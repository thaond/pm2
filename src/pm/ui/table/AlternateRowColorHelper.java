package pm.ui.table;

import pm.ui.UIHelper;

import java.awt.*;

/**
 * @author Thiyagu
 * @version $Id: AlternateRowColorHelper.java,v 1.1 2008/01/02 11:49:14 tpalanis Exp $
 * @since 01-Jan-2008
 */
public class AlternateRowColorHelper {

    private static Color evenRowColor = Color.WHITE;
    private static Color oddRowColor = UIHelper.COLOR_QUICKSILVER;

    public static void setColor(Component component, int row) {
        Color backGroundColor = row % 2 == 0 ? evenRowColor : oddRowColor;
        component.setBackground(backGroundColor);
    }
}
