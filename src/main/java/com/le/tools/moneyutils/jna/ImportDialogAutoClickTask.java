package com.le.tools.moneyutils.jna;

import org.apache.log4j.Logger;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;

class ImportDialogAutoClickTask implements Runnable {
    private static final Logger log = Logger.getLogger(ImportDialogAutoClickTask.class);

    private final ImportDialogAutoClickService serivce;

    private final class CountChildWindows implements WNDENUMPROC {
        private int count = 0;

        @Override
        public boolean callback(HWND hwnd, Pointer pointer) {
            if (log.isDebugEnabled()) {
                log.debug("Child window: " + hwnd);
            }
            count++;
            return true;
        }

        public int getCount() {
            return count;
        }
    }

    /**
     * @param importDialogAutoClickService
     */
    ImportDialogAutoClickTask(ImportDialogAutoClickService importDialogAutoClickService) {
        this.serivce = importDialogAutoClickService;
    }

    @Override
    public void run() {
        try {
            this.serivce.getSemaphore().acquire();
            try {
                if (!this.serivce.isEnable()) {
                    if (log.isDebugEnabled()) {
                        log.debug("SKIP ImportDialogAutoClickTask - not enabled");
                    }
                    return;
                }

                if (this.serivce.isShuttingDown()) {
                    log.info("SKIP ImportDialogAutoClickTask - isShuttingDown");
                    return;
                }

                autoclick();
            } finally {
                this.serivce.getSemaphore().release();
            }
        } catch (InterruptedException e) {
            log.error(e, e);
        }
    }

    private void autoclick() {
        User32 user32 = User32.INSTANCE;
        String className = null;
        String windowName = null;
        if (!this.serivce.isEnable()) {
            if (log.isDebugEnabled()) {
                log.debug("SKIP ImportDialogAutoClickTask - not enabled");
            }
            return;
        }

        HWND hWnd = null;
        if (this.serivce.isShuttingDown()) {
            log.info("SKIP ImportDialogAutoClickTask - isShuttingDown");
            return;
        }
        className = "#32770";
        windowName = "Import a file";
        hWnd = user32.FindWindow(className, windowName);
        if (hWnd == null) {
            if (log.isDebugEnabled()) {
                log.debug("SKIP ImportDialogAutoClickTask, cannot find matching dialog");
            }
            return;
        }
        int childWindowsCount = getChildWindowsCount(user32, hWnd);
        log.info("Dialog children window count=" + childWindowsCount);
        if (childWindowsCount > 6) {
            return;
        }

        log.info("> START - Found dialog, attempt to auto-click");

        try {
            if (!this.serivce.isEnable()) {
                if (log.isDebugEnabled()) {
                    log.debug("SKIP ImportDialogAutoClickTask - not enabled");
                }
                return;
            }

            if (this.serivce.isShuttingDown()) {
                log.info("SKIP ImportDialogAutoClickTask - isShuttingDown");
                return;
            }
            // #define WM_COMMAND 0x0111
            int msg = 0x0111;
            WPARAM wParam = new WPARAM(2);
            LPARAM lParam = new LPARAM(0);
            // PostMessage(hwndMoneyDlg, WM_COMMAND, 2, 0); // Simulate
            // pressing OK button
            user32.PostMessage(hWnd, msg, wParam, lParam);
        } finally {
            log.info("< DONE - auto-click");
        }

    }

    private int getChildWindowsCount(User32 user32, HWND hWnd) {
        int count = 0;
        if (hWnd == null) {
            return count;
        }
        // A pointer to an application-defined callback function. For more
        // information, see EnumChildProc.
        CountChildWindows counter = null;

        counter = new CountChildWindows();
        if (hWnd != null) {
            // An application-defined value to be passed to the callback
            // function.
            Pointer pointer = null;
            user32.EnumChildWindows(hWnd, counter, pointer);
        }
        return counter.getCount();
    }
}