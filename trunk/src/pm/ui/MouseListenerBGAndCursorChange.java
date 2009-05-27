package pm.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseListenerBGAndCursorChange implements MouseListener {

    Cursor offFocusCursor = Cursor.getDefaultCursor();
    Cursor onFocusCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    Color offFocusColor, onFocusColor;

    public MouseListenerBGAndCursorChange(Color offFocusColor, Color onFocusColor) {
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
        Component component = e.getComponent();
        component.setCursor(onFocusCursor);
        component.setBackground(onFocusColor);

    }

    public void mouseExited(MouseEvent e) {
        Component component = e.getComponent();
        component.setCursor(offFocusCursor);
        component.setBackground(offFocusColor);
    }

}
