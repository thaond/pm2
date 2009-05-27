package pm.chart;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import pm.action.Controller;
import pm.ui.UIHelper;
import static pm.ui.UIHelper.createCheckBox;
import pm.vo.EODChartVO;
import pm.vo.PortfolioDetailsVO;
import pm.vo.StopLossVO;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

public class EODChartPanel extends JPanel {
    private static final String DISPLAY_TRANSACTION = "Display Transaction";

    private static final String DISPLAY_SL_T = "Display StopLoss/Target";

    private static final String DISPLAY_FIN_DETAIL = "Display FinDetail";

    private static final String DISPLAY_MOV_AVG = "Display MovingAverage";

    private static final String DISPLAY_VOLUME = "Display Volume";

    private JLabel displayLabel = new JLabel();

    private EODChartBuilder chartBuilder;

    private boolean displayTrace = false;

    private ChartPanel chartpanel;
    private Border border1 = new SoftBevelBorder(SoftBevelBorder.RAISED, Color.LIGHT_GRAY, Color.GRAY);
    private Border border2 = new SoftBevelBorder(SoftBevelBorder.LOWERED, Color.LIGHT_GRAY, Color.GRAY);

    private static Logger logger = Logger.getLogger(EODChartPanel.class);

    private JButton showTraceButton = UIHelper.createButton("");

    public EODChartPanel(EODChartVO chartVO, String stockCode, int[] days, PortfolioDetailsVO portfolioDetailsVO) {
        super(new BorderLayout());
        chartBuilder = new EODChartBuilder(chartVO, stockCode, days);
        chartpanel = new PMChartPanel(chartBuilder, portfolioDetailsVO, this);
        chartpanel.setMouseZoomable(true, false);
        chartpanel.addMouseMotionListener(getMouseListener());
        add(chartpanel, BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.NORTH);

    }

    private MouseMotionListener getMouseListener() {
        return new MouseMotionListener() {

            public void mouseMoved(MouseEvent e) {
                if (!displayTrace) return;
                PMChartPanel chartPanel = (PMChartPanel) e.getSource();
                float yaxisValue = chartPanel.convertMousePositionToPrice(e.getY());
                if (yaxisValue != -1) {
                    displayLabel.setText("Price : " + yaxisValue);
                }

            }

            public void mouseDragged(MouseEvent e) {

            }

        };
    }

    private Component getButtonPanel() {
        JPanel buttonPanel = UIHelper.createChildPanel();
        URL resource = getClass().getClassLoader().getResource("pm/ui/resource/navigator.GIF");
        if (resource != null) {
            showTraceButton.setIcon(new ImageIcon(resource));
            showTraceButton.setPreferredSize(new Dimension(30, 18));
            showTraceButton.setBorder(border1);
            showTraceButton.addMouseListener(new MouseListener() {

                public void mouseExited(MouseEvent e) {
                    if (displayTrace) showTraceButton.setBorder(border2);
                    else showTraceButton.setBorder(border1);
                }

                public void mouseEntered(MouseEvent e) {
                    if (displayTrace) showTraceButton.setBorder(border1);
                    else showTraceButton.setBorder(border2);
                }

                public void mouseReleased(MouseEvent e) {

                }

                public void mousePressed(MouseEvent e) {

                }

                public void mouseClicked(MouseEvent e) {

                }

            });
        } else
            logger.info("navigator.GIF missing");

        showTraceButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                toggleTraceDisplay();
            }

        });
        buttonPanel.add(showTraceButton);
        displayLabel.setPreferredSize(new Dimension(100, 12));
        displayLabel.setForeground(Color.RED);
        buttonPanel.add(displayLabel);

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBox checkBox = ((JCheckBox) e.getSource());
                boolean showFlag = checkBox.isSelected();
                if (checkBox.getActionCommand().equals(DISPLAY_TRANSACTION)) {
                    chartBuilder.toggleTransactionDisplay(showFlag);
                } else if (checkBox.getActionCommand().equals(
                        DISPLAY_FIN_DETAIL)) {
                    chartBuilder.toggleFinancialDetailsDisplay(showFlag);
                } else if (checkBox.getActionCommand().equals(DISPLAY_MOV_AVG)) {
                    chartBuilder.toggleMovingAverageDisplay(showFlag);
                } else if (checkBox.getActionCommand().equals(DISPLAY_SL_T)) {
                    chartBuilder.toggleStopLossTargetDisplay(showFlag);
                } else if (checkBox.getActionCommand().equals(DISPLAY_VOLUME)) {
                    chartBuilder.toggleVolumePlot(showFlag);
                }
            }
        };

        buttonPanel.add(createCheckBox(DISPLAY_TRANSACTION, true,
                actionListener));
        buttonPanel.add(createCheckBox(DISPLAY_SL_T, true, actionListener));
        buttonPanel
                .add(createCheckBox(DISPLAY_FIN_DETAIL, true, actionListener));
        buttonPanel.add(createCheckBox(DISPLAY_MOV_AVG, true, actionListener));
        buttonPanel.add(createCheckBox(DISPLAY_VOLUME, true, actionListener));

        return buttonPanel;
    }

    public void toggleTraceDisplay() {
        JButton button = showTraceButton;
        if (displayTrace) {
            displayLabel.setText("");
            button.setBorder(border1);
        } else {
            displayLabel.setText("Price :");
            button.setBorder(border2);
        }
        displayTrace = !displayTrace;
        chartpanel.setVerticalAxisTrace(displayTrace);
    }


}

class PMChartPanel extends ChartPanel {
    private EODChartBuilder builder;
    private PortfolioDetailsVO portfolioDetailsVO;

    private enum ValueType {
        StopLoss2, StopLoss1, Target1, Target2, None
    }

    ;
    private EODChartPanel basePanel;

    public PMChartPanel(EODChartBuilder builder, PortfolioDetailsVO portfolioDetailsVO, EODChartPanel basePanel) {
        super(builder.getChart());
        this.builder = builder;
        this.portfolioDetailsVO = portfolioDetailsVO;
        this.basePanel = basePanel;
    }

    public float convertMousePositionToPrice(int y) {
        double scaleY = getScaleY();
        y = (int) ((y - getInsets().top) / scaleY);
        Rectangle2D chart1ScreenDataArea = getChartRenderingInfo().getPlotInfo().getSubplotInfo(0).getPlotArea();
        CombinedDomainXYPlot domainXYPlot = (CombinedDomainXYPlot) getChart().getPlot();
        List subplots = domainXYPlot.getSubplots();
        XYPlot topPlot = (XYPlot) subplots.get(0);

        ValueAxis rangeAxis = topPlot.getRangeAxis(0);
        double domainLowerBound = rangeAxis.getLowerBound();
        double domainUpperBound = rangeAxis.getUpperBound();
        double maxY = chart1ScreenDataArea.getMaxY();
        double minY = chart1ScreenDataArea.getMinY();
        if (y < minY || y > maxY) return -1;
        double rangeDiff = domainUpperBound - domainLowerBound;
        double yDiff = maxY - minY;
        double yaxisValue = rangeDiff / yDiff * (maxY - y - 1) + domainLowerBound;
        BigDecimal bigDecimal = new BigDecimal(yaxisValue);
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int y = (int) ((e.getY() - getInsets().top) / getScaleY());
        Rectangle2D chart1ScreenDataArea = this.getChartRenderingInfo().getPlotInfo().getSubplotInfo(0).getPlotArea();
        double maxY = chart1ScreenDataArea.getMaxY();
        double minY = chart1ScreenDataArea.getMinY();
        if (y < minY || y > maxY) return;
        else super.mouseMoved(e);
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (!getVerticalAxisTrace() || !builder.isHolding()) return;

        ChartPanel panel = (ChartPanel) event.getSource();
        int cursorX = event.getX();
        int cursorY = event.getY();
        Container cont = panel.getParent();
        while (cont != null && !(cont instanceof JFrame)) {
            cont = cont.getParent();
        }
        if (cont != null) {
            final float currPriceTrace = convertMousePositionToPrice(event.getY());
            Frame frame = (Frame) cont;
            final JDialog dialog = new JDialog(frame);
            dialog.setModal(true);
            dialog.setUndecorated(true);
            int x = cursorX + frame.getX();
            int y = cursorY + panel.getY();
            dialog.setBounds(x, y, 10, 10);
            JPanel dialogPanel = new JPanel();
            dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS));
            ButtonGroup bg = new ButtonGroup();
            ActionListener listener = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    StopLossVO stopLossVO = builder.getChartVO().getStopLossVO();
                    ValueType actionCommand = ValueType.valueOf(e.getActionCommand());
                    switch (actionCommand) {
                        case StopLoss2:
                            stopLossVO.setStopLoss2(currPriceTrace);
                            break;
                        case StopLoss1:
                            stopLossVO.setStopLoss1(currPriceTrace);
                            break;
                        case Target1:
                            stopLossVO.setTarget1(currPriceTrace);
                            break;
                        case Target2:
                            stopLossVO.setTarget2(currPriceTrace);
                            break;
                    }
                    dialog.setVisible(false);
                    if (actionCommand != ValueType.None) {
                        if (Controller.saveStopLoss(stopLossVO)) {
                            basePanel.toggleTraceDisplay();
                            builder.updateStopLossDisplay(stopLossVO);
                        }
                    }

                }

            };
            JRadioButton radioButton;
            for (ValueType valueType : ValueType.values()) {
                radioButton = UIHelper.createRadioButton(valueType.name(), valueType.name());
                bg.add(radioButton);
                radioButton.addActionListener(listener);
                dialogPanel.add(radioButton);
            }
            dialogPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            dialog.getContentPane().add(dialogPanel);
            dialog.pack();
            dialog.setVisible(true);
        }

    }

}
