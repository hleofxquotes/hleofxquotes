package com.hungle.tools.moneyutils.ofx.statement;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving popup events.
 * The class that is interested in processing a popup
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPopupListener<code> method. When
 * the popup event occurs, that object's appropriate
 * method is invoked.
 *
 * @see PopupEvent
 */
public class PopupListener extends MouseAdapter {
    
    /**
     * Instantiates a new popup listener.
     *
     * @param popupMenu the popup menu
     */
    public PopupListener(JPopupMenu popupMenu) {
        super();
        this.popupMenu = popupMenu;
    }

    /** The popup menu. */
    private JPopupMenu popupMenu;

    /* (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        showPopup(e);
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    /**
     * Show popup.
     *
     * @param e the e
     */
    private void showPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

}
