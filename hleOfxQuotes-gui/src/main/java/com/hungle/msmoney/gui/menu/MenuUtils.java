package com.hungle.msmoney.gui.menu;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import com.hungle.msmoney.qs.QuoteSourceListener;

public class MenuUtils {
    private static final Logger LOGGER = Logger.getLogger(MenuUtils.class);

    /**
     * Creates the samples menu.
     *
     * @return the j menu
     */
    public static final JMenu createSamplesMenu(QuoteSourceListener quoteSourceListener, ExecutorService threadPool) {
        JMenu menu;
        JMenuItem menuItem;
        menu = new JMenu("Samples");

        menuItem = new JMenuItem(new LoadNYSETask("NYSE", quoteSourceListener, threadPool));
        menu.add(menuItem);

        menuItem = new JMenuItem(new LoadNASDAQTask("NASDAQ", quoteSourceListener, threadPool));
        menu.add(menuItem);

        menuItem = new JMenuItem(new LoadUSMutualFundsTask("US Mutual Funds", quoteSourceListener, threadPool));
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem(new LoadLSETask("London Stock Exchange", quoteSourceListener, threadPool));
        menu.add(menuItem);

        menu.addSeparator();
        // Dogs of the Dow
        menuItem = new JMenuItem(new LoadDogTask("Dogs of the Dow", quoteSourceListener, threadPool));
        menu.add(menuItem);
        return menu;
    }

    /**
     * Creates the bookmarks menu.
     * @param threadPool 
     * @param quoteSourceListener 
     *
     * @return the j menu
     */
    public static final JMenu createBookmarksMenu(QuoteSourceListener quoteSourceListener, ExecutorService threadPool) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> createBookmarksMenu()");
        }
    
        File dir = new File("bookmarks");
        if (!dir.isDirectory()) {
            return null;
        }
    
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }
        if (files.length <= 0) {
            return null;
        }
    
        JMenu menu;
        JMenuItem menuItem;
        menu = new JMenu("Bookmarks");
    
        Arrays.sort(files);
        for (File file : files) {
            menuItem = new JMenuItem(new LoadBookmarkAction(file, quoteSourceListener, threadPool));
            menu.add(menuItem);
        }
        return menu;
    }

}
