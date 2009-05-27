/*
 * Created on Nov 4, 2004
 *
 */
package pm.ui;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author thiyagu1
 */
public class PMKeyAdapter extends KeyAdapter {
    private StringBuffer prefix = new StringBuffer();

    public PMKeyAdapter() {
    }

    public void reset() {
        prefix.delete(0, prefix.length());
    }

    public void keyTyped(KeyEvent e) {

        char ch = e.getKeyChar();
        if (Character.isLetter(ch) || ch == '\b' || ch == '\\') {
            if (ch == '\b') {
                if (prefix.length() != 0)
                    prefix.deleteCharAt(prefix.length() - 1);
            } else if (ch == '\\') {
                reset();
            } else {
                prefix.append(Character.toUpperCase(ch));
            }

            ListModel masterList = null;
            if (e.getComponent() instanceof JList) {
                masterList = ((JList) e.getComponent()).getModel();
            } else if (e.getComponent() instanceof JComboBox) {
                masterList = ((JComboBox) e.getComponent()).getModel();
            }
            if (masterList == null) return;
            int len = masterList.getSize();
            for (int i = 0; i < len; i++) {
                String item = masterList.getElementAt(i).toString();
                if (item.startsWith(prefix.toString())) {
                    if (e.getComponent() instanceof JList) {
                        ((JList) e.getComponent()).setSelectedIndex(i);
                        ((JList) e.getComponent()).ensureIndexIsVisible(i);
                    } else if (e.getComponent() instanceof JComboBox) {
                        ((JComboBox) e.getComponent()).setSelectedIndex(i);
                    }
                    break;
                }
            }
        }
    }

}
