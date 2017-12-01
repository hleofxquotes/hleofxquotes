package com.hungle.msmoney.core.jna;

import org.apache.log4j.Logger;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;

// TODO: Auto-generated Javadoc
/**
 * The Class CheckMsMoneyWindow.
 */
public class CheckMsMoneyWindow {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(Logger.class);

    /**
     * The Class CountChildWindows.
     */
    private static final class CountChildWindows implements WNDENUMPROC {
        
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
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        User32 user32 = User32.INSTANCE;
        String className = null;
        String windowName = null;

        HWND hWnd = null;
        
        // FindWindow(TEXT("MSMoney Frame"), NULL) – Is Money actually running?
        className = "MSMoney Frame";
        windowName = null;
        hWnd = user32.FindWindow(className, windowName);
        LOGGER.info("Is Money actually running? hWnd=" + hWnd);

        
//FindWindow(L"#32770″, "Microsoft Money") – dialog that appears when Money is not running during import.
//Respond with:
//wParam = 7; // BN_CLICKED
//lParam = 90552L; // IDNO
//PostMessage(hwndMoneyDlg, WM_COMMAND, wParam, lParam)
        
        // NO
//        >>>> Window <<<<
//        Title:  Microsoft Money
//        Class:  #32770
//        Position:       723, 529
//        Size:   479, 159
//        Style:  0x94C801C5
//        ExStyle:        0x00010101
//        Handle: 0x000608B0
//
//        >>>> Control <<<<
//        Class:  Button
//        Instance:       2
//        ClassnameNN:    Button2
//        Name:   
//        Advanced (Class):       [CLASS:Button; INSTANCE:2]
//        ID:     7
//        Text:   &No
//        Position:       376, 96
//        Size:   88, 26
//        ControlClick Coords:    64, 17
//        Style:  0x50010001
//        ExStyle:        0x00000004
//        Handle: 0x00080850
//
//        >>>> Mouse <<<<
//        Position:       1166, 665
//        Cursor ID:      2
//        Color:  0x8F8F8F
//
//        >>>> StatusBar <<<<
//
//        >>>> ToolsBar <<<<
//
//        >>>> Visible Text <<<<
//        &Yes
//        &No
//        The information has been received and will be imported the next time you run Money.  Do you want to start Money now?
//
//
//        >>>> Hidden Text <<<<

        // YES
//        >>>> Window <<<<
//        Title:  Microsoft Money
//        Class:  #32770
//        Position:       723, 529
//        Size:   479, 159
//        Style:  0x94C801C5
//        ExStyle:        0x00010101
//        Handle: 0x000608B0
//
//        >>>> Control <<<<
//        Class:  Button
//        Instance:       1
//        ClassnameNN:    Button1
//        Name:   
//        Advanced (Class):       [CLASS:Button; INSTANCE:1]
//        ID:     6
//        Text:   &Yes
//        Position:       280, 96
//        Size:   88, 26
//        ControlClick Coords:    56, 6
//        Style:  0x50030000
//        ExStyle:        0x00000004
//        Handle: 0x00080694
//
//        >>>> Mouse <<<<
//        Position:       1062, 654
//        Cursor ID:      2
//        Color:  0x8F8F8F
//
//        >>>> StatusBar <<<<
//
//        >>>> ToolsBar <<<<
//
//        >>>> Visible Text <<<<
//        &Yes
//        &No
//        The information has been received and will be imported the next time you run Money.  Do you want to start Money now?
//
//
//        >>>> Hidden Text <<<<
        
        className = "#32770";
        windowName = "Microsoft Money";
        hWnd = user32.FindWindow(className, windowName);
        LOGGER.info("Dialog to start MsMoney? hWnd=" + hWnd);
        
        
//        >>>> Window <<<<
//        Title:  Import a file
//        Class:  #32770
//        Position:       630, 416
//        Size:   348, 310
//        Style:  0x94C000C4
//        ExStyle:        0x00010101
//        Handle: 0x00170D20
//
//        >>>> Control <<<<
//        Class:  ClassMoneyPushbutton
//        Instance:       1
//        ClassnameNN:    ClassMoneyPushbutton1
//        Name:   
//        Advanced (Class):       [CLASS:ClassMoneyPushbutton; INSTANCE:1]
//        ID:     2
//        Text:   &OK
//        Position:       245, 254
//        Size:   84, 20
//        ControlClick Coords:    72, 4
//        Style:  0x50010101
//        ExStyle:        0x00000004
//        Handle: 0x00230E14
//
//        >>>> Mouse <<<<
//        Position:       950, 697
//        Cursor ID:      2
//        Color:  0xF4F4F4
//
//        >>>> StatusBar <<<<
//
//        >>>> ToolsBar <<<<
//
//        >>>> Visible Text <<<<
//        Import summary
//        &OK
//        <P STYLE="margin-left:5;margin-top:7"><img src="money://rsrc/inc/images/info.gif">&nbsp;&nbsp;Import Complete</P>
//
//
//        >>>> Hidden Text <<<<

        // FindWindow(L"#32770″, "Import a file") – the original dialog for
        // import.
        className = "#32770";
        windowName = "Import a file";
        hWnd = user32.FindWindow(className, windowName);
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
        LOGGER.info("Children window count=" + counter.getCount());
        if (counter.getCount() < 7) {
            LOGGER.info("Import a file dialog? hWnd=" + hWnd);
            boolean autoclick = false;
            if (autoclick) {
                // #define WM_COMMAND 0x0111
                int msg = 0x0111;
                WPARAM wParam = new WPARAM(2);
                LPARAM lParam = new LPARAM(0);
                // PostMessage(hwndMoneyDlg, WM_COMMAND, 2, 0); // Simulate
                // pressing OK button
                user32.PostMessage(hWnd, msg, wParam, lParam);
            }
        }        
        
        // Import new
//        >>>> Window <<<<
//        Title:  Import a file
//        Class:  #32770
//        Position:       585, 379
//        Size:   438, 385
//        Style:  0x94C000C4
//        ExStyle:        0x00010101
//        Handle: 0x000C06CA
//
//        >>>> Control <<<<
//        Class:  ClassMoneyPushbutton
//        Instance:       4
//        ClassnameNN:    ClassMoneyPushbutton4
//        Name:   
//        Advanced (Class):       [CLASS:ClassMoneyPushbutton; INSTANCE:4]
//        ID:     2
//        Text:   Cancel
//        Position:       350, 325
//        Size:   68, 20
//        ControlClick Coords:    16, 6
//        Style:  0x50010100
//        ExStyle:        0x00000004
//        Handle: 0x00290E06
//
//        >>>> Mouse <<<<
//        Position:       954, 733
//        Cursor ID:      2
//        Color:  0xF0F1EC
//
//        >>>> StatusBar <<<<
//
//        >>>> ToolsBar <<<<
//
//        >>>> Visible Text <<<<
//        Select a Money account for your imported file
//        To import the information into an account that isn't listed, click New.
//        <b>Money accounts:</b>
//        Ne&w...
//        <b>About your imported file</b>
//        <b>Statement type:</b>
//        Active Statement File
//        <b>Account type:</b>
//        Broker
//        <b>Account number:</b>
//        XXXXXX6789
//        To import into an account in a different Money file, click Import Later.
//        &Import Later
//        Next >
//        Cancel
//
//
//        >>>> Hidden Text <<<<
//        <b>Account number:</b>
         counter = new CountChildWindows();

        className = "#32770";
        windowName = "Import a file";
        hWnd = user32.FindWindow(className, windowName);
        if (hWnd != null) {
            // An application-defined value to be passed to the callback function. 
            Pointer pointer = null;
            user32.EnumChildWindows(hWnd, counter, pointer);

        }
        LOGGER.info("Children window count=" + counter.getCount());
        if (counter.getCount() > 6) {
            LOGGER.info("(NO ACCOUNT) Import a file dialog? hWnd=" + hWnd);
        }
    }

}
