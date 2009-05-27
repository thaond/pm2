/*
 * Created on Jan 11, 2005
 *
 */
package pm.ui;

import pm.action.Controller;
import static pm.ui.UIHelper.createButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author thiyagu1
 */
public class PMProgressBar {

    private JProgressBar progressBar;
    private boolean runFlag = true;
    private int _WIDTH = 300;
    private int _HEIGHT = 60;
    private JDialog dialog;
    JButton cancelButton = createButton("Cancel");
    private static PMProgressBar instance = null;

    private PMProgressBar() {
        JFrame parentFrame = PortfolioManager.getInstance();
        dialog = new JDialog(parentFrame);
        dialog.setUndecorated(true);
        int x = parentFrame.getX() + parentFrame.getWidth() / 2 - _WIDTH / 2;
        int y = parentFrame.getY() + parentFrame.getHeight() / 2 - _HEIGHT / 2;
        dialog.setBounds(x, y, _WIDTH, _HEIGHT);
        JPanel panel = new JPanel();
        progressBar = new JProgressBar(0, 100);
        progressBar.setFont(UIHelper.FONT_TITLE_PROGRESSBAR);
        progressBar.setBackground(UIHelper.COLOR_BG_PROGRESSBAR);
        progressBar.setString("Retrieving Data...");
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        panel.add(progressBar);
        panel.add(getCancelButton());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        dialog.getContentPane().add(panel);
        dialog.pack();
    }

    private Component getCancelButton() {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Controller.doCancel();
            }
        });
        return cancelButton;
    }

    public static PMProgressBar getInstance() {
        if (instance == null) {
            instance = new PMProgressBar();
        }
        return instance;
    }

    public void start(boolean showCancel) {
        cancelButton.setEnabled(showCancel);
        dialog.setVisible(true);
    }

    public void stop() {
        dialog.setVisible(false);
    }
}
