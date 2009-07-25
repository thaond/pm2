package pm.ui;

import pm.action.Controller;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IciciNseCodeMappingView extends JPanel {
    private final boolean showAll;

    public IciciNseCodeMappingView(boolean showAll) {
        this.showAll = showAll;
        init();
    }

    private void init() {
        Map<String, String> iciciNseMappings = Controller.getIciciMapping();
        final List<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(iciciNseMappings.entrySet());

        JTable table = UIHelper.createTable(new AbstractTableModel() {
            public int getRowCount() {
                return entries.size();
            }

            public int getColumnCount() {
                return 2;
            }

            public Object getValueAt(int row, int column) {
                Map.Entry entry = entries.get(row);
                return (column == 0 ? entry.getKey() : entry.getValue());
            }
        });
        JPanel panel = UIHelper.createChildPanel();
        panel.add(table);
        JScrollPane pane = new JScrollPane(panel);
        pane.setMaximumSize(new Dimension(50, 50));
        add(pane);
    }
}
