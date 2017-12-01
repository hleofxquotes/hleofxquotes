/**
 * 
 */
package com.hungle.msmoney.gui;

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

    /** The popup. */
    private JPopupMenu popup;

    /**
     * Instantiates a new popup listener.
     *
     * @param popup the popup
     */
    public PopupListener(JPopupMenu popup) {
        super();
        this.popup = popup;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    /**
     * Maybe show popup.
     *
     * @param e the e
     */
    protected void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if (popup != null) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}