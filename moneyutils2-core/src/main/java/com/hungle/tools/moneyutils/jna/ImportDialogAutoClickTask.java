package com.hungle.tools.moneyutils.jna;

import org.apache.log4j.Logger;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;

// TODO: Auto-generated Javadoc
/**
 * The Class ImportDialogAutoClickTask.
 */
class ImportDialogAutoClickTask implements Runnable {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(ImportDialogAutoClickTask.class);

    /** The serivce. */
    private final ImportDialogAutoClickService serivce;

    /**
     * The Class CountChildWindows.
     */
    private final class CountChildWindows implements WNDENUMPROC {
        
        /** The count. */
        private int count = 0;

        /* (non-Javadoc)
         * @see com.sun.jna.platform.win32.WinUser.WNDENUMPROC#callback(com.sun.jna.platform.win32.WinDef.HWND, com.sun.jna.Pointer)
         */
        @Override
        public boolean callback(HWND hwnd, Pointer pointer) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Child window: " + hwnd);
            }
            count++;
            return true;
        }

        /**
         * Gets the count.
         *
         * @return the count
         */
        public int getCount() {
            return count;
        }
    }

    /**
     * Instantiates a new import dialog auto click task.
     *
     * @param importDialogAutoClickService the import dialog auto click service
     */
    ImportDialogAutoClickTask(ImportDialogAutoClickService importDialogAutoClickService) {
        this.serivce = importDialogAutoClickService;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            this.serivce.getSemaphore().acquire();
            try {
                if (!this.serivce.isEnable()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("SKIP ImportDialogAutoClickTask - not enabled");
                    }
                    return;
                }

                if (this.serivce.isShuttingDown()) {
                    LOGGER.info("SKIP ImportDialogAutoClickTask - isShuttingDown");
                    return;
                }

                autoclick();
            } finally {
                this.serivce.getSemaphore().release();
            }
        } catch (InterruptedException e) {
            LOGGER.error(e, e);
        }
    }

    /**
     * Autoclick.
     */
    private void autoclick() {
        User32 user32 = User32.INSTANCE;
        String className = null;
        String windowName = null;
        if (!this.serivce.isEnable()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("SKIP ImportDialogAutoClickTask - not enabled");
            }
            return;
        }

        HWND hWnd = null;
        if (this.serivce.isShuttingDown()) {
            LOGGER.info("SKIP ImportDialogAutoClickTask - isShuttingDown");
            return;
        }
        className = "#32770";
        windowName = "Import a file";
        hWnd = user32.FindWindow(className, windowName);
        if (hWnd == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("SKIP ImportDialogAutoClickTask, cannot find matching dialog");
            }
            return;
        }
        int childWindowsCount = getChildWindowsCount(user32, hWnd);
        LOGGER.info("Dialog children window count=" + childWindowsCount);
        if (childWindowsCount > 6) {
            return;
        }

        LOGGER.info("> START - Found dialog, attempt to auto-click");

        try {
            if (!this.serivce.isEnable()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("SKIP ImportDialogAutoClickTask - not enabled");
                }
                return;
            }

            if (this.serivce.isShuttingDown()) {
                LOGGER.info("SKIP ImportDialogAutoClickTask - isShuttingDown");
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
            LOGGER.info("< DONE - auto-click");
        }

    }

    /**
     * Gets the child windows count.
     *
     * @param user32 the user 32
     * @param hWnd the h wnd
     * @return the child windows count
     */
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