package pm.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Date: Dec 30, 2006
 * Time: 3:51:33 PM
 */
public class ImageMenuItem extends JMenuItem {

    private static Dimension fixedSize = new Dimension(20, 20);


    public ImageMenuItem() {
        super();
    }

    public ImageMenuItem(String iconName) {
        setIcon(new ImageIcon(ImageButton.class.getClassLoader().getResource("pm/ui/resource/" + iconName)));
    }

    public ImageMenuItem(String iconaName, String actionCommand) {
        this(iconaName);
        setActionCommand(actionCommand);
    }

    public Dimension getMinimumSize() {
        return fixedSize;
    }

    public Dimension getMaximumSize() {
        return fixedSize;
    }

    public Dimension getPreferredSize() {
        return fixedSize;
    }
}