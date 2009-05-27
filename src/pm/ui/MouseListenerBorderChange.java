package pm.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class MouseListenerBorderChange implements MouseListener {

    Border offBorder, onBorder;

    public MouseListenerBorderChange(Border offBorder, Border onBorder) {
        this.offBorder = offBorder;
        this.onBorder = onBorder;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        ((JComponent) e.getSource()).setBorder(onBorder);
    }

    public void mouseExited(MouseEvent e) {
        ((JComponent) e.getSource()).setBorder(offBorder);
    }

}
