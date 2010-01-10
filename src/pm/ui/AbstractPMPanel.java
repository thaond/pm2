/*
 * Created on 22-Feb-2005
 *
 */
package pm.ui;

import org.apache.log4j.Logger;
import pm.util.SwingWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author thiyagu1
 */
public abstract class AbstractPMPanel extends JPanel implements ActionListener {
    protected static Logger logger = Logger.getLogger(AbstractPMPanel.class);
    protected boolean flagShowProgressBar = false;
    protected boolean flagShowCancel = true;

    public void doAction(final String actionCommand) {
        final SwingWorker worker = new SwingWorker() {
            Object retVal = null;

            public Object construct() {
                if (flagShowProgressBar) {
                    PortfolioManager.getInstance().setEnabled(false);
                    PMProgressBar.getInstance().start(flagShowCancel);
//            		PortfolioManager.getInstance().getProgressPane().start();
//            		PortfolioManager.getInstance().progress.setVisible(true);
                }
                try {
                    retVal = getData(actionCommand);
                } catch (Exception e) {
                    logger.error(e, e);
                    PortfolioManager.displayAppInfo(e.getMessage(), "Error");
                } finally {
                    if (flagShowProgressBar) {
                        PortfolioManager.getInstance().setEnabled(true);
                        PMProgressBar.getInstance().stop();
                        //PortfolioManager.getInstance().setVisible(true);
//            		PortfolioManager.getInstance().getProgressPane().stop();
//            		PortfolioManager.getInstance().progress.setVisible(false);
                    }
                }
                return retVal;
            }

            public void finished() {
                doDisplay(retVal, actionCommand);
            }
        };
        worker.start();

    }

    /**
     * This method takecare of displaying the result
     *
     * @param retVal
     * @param actionCommand
     */
    protected abstract void doDisplay(Object retVal, String actionCommand);

    /**
     * This method is responsible to communicate to the Controller
     *
     * @param actionCommand
     * @return
     */
    protected abstract Object getData(String actionCommand);

    /* (non-Javadoc)
      * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
      */

    public void actionPerformed(ActionEvent e) {
        doAction(e.getActionCommand());
    }

    protected JButton getActionButton(String text) {
        JButton saveButton = UIHelper.createButton(text);
        saveButton.addActionListener(this);
        return saveButton;
    }

    protected void addComponentWithTitle(GridBagConstraints gbc, String title, JComponent component) {
        UIHelper.addComponentWithTitle(this, gbc, title, component);
    }
}
