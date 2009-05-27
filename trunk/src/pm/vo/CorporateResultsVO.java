/*
 * Created on 11-Feb-2005
 *
 */
package pm.vo;

import pm.util.AppConst.CORP_RESULT_TIMELINE;
import pm.util.ApplicationException;
import pm.util.PMDate;
import pm.util.PMDateFormatter;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * @author thiyagu1
 */
public class CorporateResultsVO implements Serializable {
    public static final String BASIC_EPS_IN_RS = "Basic EPS (in Rs.)";

    private static final long serialVersionUID = 42L;

    private String ticker;
    private transient PMDate startDate;
    private transient PMDate endDate;
    private transient CORP_RESULT_TIMELINE timeline;
    private transient int period;
    private transient boolean auditedFlag;
    private transient boolean consolidatedFlag;
    private transient boolean bankingFlag;
    private transient int year;
    private HashMap financialData = new HashMap();
    private HashMap segmentwiseData = new HashMap();
    private String ctrlString;

    public CorporateResultsVO(String url) throws ApplicationException {

        if (url == null || url.length() < 94) throw new ApplicationException("invalid url in CorporateResultsVO");

        int st = url.indexOf("?") + 1;
        int ed = st + 29;
        this.ticker = URLDecoder.decode(url.substring(ed));
        this.ctrlString = url.substring(st, ed);
        init();
    }

    public CorporateResultsVO(String ticker, String ctrlString) {
        this.ticker = ticker;
        this.ctrlString = ctrlString;
        init();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init();
    }

    void init() {
        try {
            this.startDate = PMDateFormatter.parseDD_MMM_YYYY(ctrlString.substring(0, 11));
            this.endDate = PMDateFormatter.parseDD_MMM_YYYY(ctrlString.substring(11, 22));
            char chTimeline = ctrlString.charAt(22);
            this.timeline = (chTimeline == 'A') ? CORP_RESULT_TIMELINE.Annual
                    : (chTimeline == 'H') ? CORP_RESULT_TIMELINE.HalfYearly
                    : (chTimeline == 'Q') ? CORP_RESULT_TIMELINE.Quaterly
                    : CORP_RESULT_TIMELINE.Other;
            this.period = (timeline == CORP_RESULT_TIMELINE.Annual ||
                    timeline == CORP_RESULT_TIMELINE.Other) ? 0 : Integer.parseInt(ctrlString.substring(23, 24));
            this.auditedFlag = (ctrlString.charAt(24) == 'A');
            this.consolidatedFlag = (ctrlString.charAt(27) == 'C');
            this.year = startDate.getYear();
            if (timeline == CORP_RESULT_TIMELINE.Quaterly && startDate.getMonth() == 1 && period == 4)
                year--; //1/1/2004 - 31/3/2004 is 2003Q4 result.
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }

    public void addFinancialDetails(String key, Object value) {
        financialData.put(key, value);
    }

    public Set getFinancialDetailKeys() {
        return financialData.keySet();
    }

    public Collection getFinancialDetails() {
        return financialData.values();
    }

    public Object getFinancialDetail(String key) {
        return financialData.get(key);
    }

    public void addSegmentDetails(String key, Object value) {
        segmentwiseData.put(key, value);
    }

    public Set getSegmentDetailKeys() {
        return segmentwiseData.keySet();
    }

    public Collection getSegmentDetails() {
        return segmentwiseData.values();
    }

    public Object getSegmentDetail(String key) {
        return segmentwiseData.get(key);
    }

    /**
     * @return Returns the auditedFlag.
     */
    public boolean isAuditedFlag() {
        return auditedFlag;
    }

    /**
     * @return Returns the consolidatedFlag.
     */
    public boolean isConsolidatedFlag() {
        return consolidatedFlag;
    }

    /**
     * @return Returns the period.
     */
    public int getPeriod() {
        return period;
    }

    /**
     * @return Returns the ticker.
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * @return Returns the timeline.
     */
    public CORP_RESULT_TIMELINE getTimeline() {
        return timeline;
    }


    /**
     * @return Returns the bankingFlag.
     */
    public boolean isBanking() {
        return bankingFlag;
    }

    /**
     * @param bankingFlag The bankingFlag to set.
     */
    public void setBanking(boolean bankingFlag) {
        this.bankingFlag = bankingFlag;
    }

    /**
     * @return Returns the ctrlString.
     */
    public String getCtrlString() {
        return ctrlString;
    }

    /**
     * @param ctrlString The ctrlString to set.
     */
    public void setCtrlString(String ctrlString) {
        this.ctrlString = ctrlString;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CorporateResultsVO->").append(ticker).append(",");
        sb.append(ctrlString).append(",");
        sb.append(year).append(",");
        sb.append(timeline).append(",");
        sb.append(period).append(",");
        sb.append(auditedFlag).append(",");
        sb.append(consolidatedFlag).append(",");
        sb.append(bankingFlag).append(",");
        sb.append(financialData).append(",");
        sb.append(segmentwiseData).append("\n");
        return sb.toString();
    }

    public PMDate getEndDate() {
        return endDate;
    }


    public PMDate getStartDate() {
        return startDate;
    }

    public float getPEValue() {
        String val = (String) financialData.get(BASIC_EPS_IN_RS);
        return Float.parseFloat(val);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        CorporateResultsVO target = (CorporateResultsVO) obj;
        return this.ticker.equals(target.ticker) && this.ctrlString.equals(target.ctrlString);
    }

    @Override
    public int hashCode() {
        return this.ticker.hashCode() + this.ctrlString.hashCode();
    }

    public void applyPriceFactor(float priceFactor) {
        // does only for PE
        String val = (String) financialData.get(BASIC_EPS_IN_RS);
        float pe = Float.parseFloat(val);
        pe *= priceFactor;
        financialData.put(BASIC_EPS_IN_RS, Float.toString(pe));
    }

    public int getYear() {
        return year;
    }
}
