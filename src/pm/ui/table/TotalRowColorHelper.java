package pm.ui.table;

import pm.ui.UIHelper;

import java.awt.*;

/**
 * @author Thiyagu
 * @version $Id: TotalRowColorHelper.java,v 1.1 2008/01/23 15:39:24 tpalanis Exp $
 * @since 01-Jan-2008
 */
public class TotalRowColorHelper {

    public static void setColor(Component component) {
        component.setFont(UIHelper.TOTAL_FONT);
        component.setBackground(UIHelper.TOTAL_BACKGROUND_COLOR);
    }
}