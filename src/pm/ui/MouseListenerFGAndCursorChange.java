package pm.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseListenerFGAndCursorChange implements MouseListener {

    Cursor offFocusCursor = Cursor.getDefaultCursor();
    Cursor onFocusCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    Color offFocusColor, onFocusColor;

    public MouseListenerFGAndCursorChange(Color offFocusColor, Color onFocusColor) {
        this.offFocusColor = offFocusColor;
        this.onFocusColor = onFocusColor;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        if (isValidArea(e)) {
            setEffect(e, onFocusCursor, onFocusColor);
        }
    }

    protected boolean isValidArea(MouseEvent e) {
        return true;
    }

    public void mouseExited(MouseEvent e) {
        if (isValidArea(e)) {
            setEffect(e, offFocusCursor, offFocusColor);
        }
    }

    void setEffect(MouseEvent e, Cursor cursor, Color color) {
        Component component = e.getComponent();
        component.setCursor(cursor);
        component.setForeground(color);
    }
}
