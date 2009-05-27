package pm.vo;

import pm.util.AppConst.CORP_RESULT_TIMELINE;
import static pm.util.AppConst.DELIMITER_COMMA;
import pm.util.ApplicationException;
import pm.util.PMDate;
import pm.util.PMDateFormatter;

import java.util.StringTokenizer;

public class CorpResultVO {
    private int id;
    private String stockCode;
    private PMDate startDate;
    private PMDate endDate;
    private CORP_RESULT_TIMELINE timeline;
    private int period; // Q1, Q2 .. or H1, H2
    private boolean auditedFlag;
    private boolean consolidatedFlag;
    private boolean bankingFlag;
    private int year;
    private float eps;
    private float faceValue;
    private float paidUpEquityShareCapital;
    private float netProfit;
    private float nonRecurringIncome;
    private float nonRecurringExpense;
    private float adjustedNetProfit;
    private float interestCost;
    private float netSales;

    public CorpResultVO() {
    }

    public CorpResultVO(String stockCode, PMDate startDate, PMDate endDate, CORP_RESULT_TIMELINE timeline, int period, boolean auditedFlag, boolean consolidatedFlag, boolean bankingFlag, int year) {
        this.stockCode = stockCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeline = timeline;
        this.period = period;
        this.auditedFlag = auditedFlag;
        this.consolidatedFlag = consolidatedFlag;
        this.bankingFlag = bankingFlag;
        this.year = year;
    }

    public CorpResultVO(String line) throws ApplicationException {
        StringTokenizer stk = new StringTokenizer(line, DELIMITER_COMMA);
        stockCode = stk.nextToken();
        startDate = PMDateFormatter.parseYYYYMMDD(stk.nextToken());
        endDate = PMDateFormatter.parseYYYYMMDD(stk.nextToken());
        timeline = CORP_RESULT_TIMELINE.valueOf(stk.nextToken());
        period = Integer.parseInt(stk.nextToken());
        auditedFlag = Boolean.parseBoolean(stk.nextToken());
        consolidatedFlag = Boolean.parseBoolean(stk.nextToken());
        bankingFlag = Boolean.parseBoolean(stk.nextToken());
        year = Integer.parseInt(stk.nextToken());
        eps = Float.parseFloat(stk.nextToken());
        faceValue = Float.parseFloat(stk.nextToken());
        paidUpEquityShareCapital = Float.parseFloat(stk.nextToken());
        netProfit = Float.parseFloat(stk.nextToken());
        nonRecurringIncome = Float.parseFloat(stk.nextToken());
        nonRecurringExpense = Float.parseFloat(stk.nextToken());
        adjustedNetProfit = Float.parseFloat(stk.nextToken());
        interestCost = Float.parseFloat(stk.nextToken());
        if (stk.hasMoreTokens()) {
            netSales = Float.parseFloat(stk.nextToken());
        }
    }

    public void setFinancialData(float eps, float faceValue, float paidUpEquityShareCapital, float netProfit,
                                 float nonRecurringIncome, float nonRecurringExpense, float adjustedNetProfit,
                                 float interestCost, float netSales) {
        this.eps = eps;
        this.faceValue = faceValue;
        this.paidUpEquityShareCapital = paidUpEquityShareCapital;
        this.netProfit = netProfit;
        this.nonRecurringIncome = nonRecurringIncome;
        this.nonRecurringExpense = nonRecurringExpense;
        this.adjustedNetProfit = adjustedNetProfit;
        this.interestCost = interestCost;
        this.netSales = netSales;
    }


    public String toWrite() {
        StringBuffer sb = new StringBuffer();
        sb.append(stockCode).append(DELIMITER_COMMA);
        sb.append(PMDateFormatter.formatYYYYMMDD(startDate)).append(DELIMITER_COMMA);
        sb.append(PMDateFormatter.formatYYYYMMDD(endDate)).append(DELIMITER_COMMA);
        sb.append(timeline.toString()).append(DELIMITER_COMMA);
        sb.append(period).append(DELIMITER_COMMA);
        sb.append(auditedFlag).append(DELIMITER_COMMA);
        sb.append(consolidatedFlag).append(DELIMITER_COMMA);
        sb.append(bankingFlag).append(DELIMITER_COMMA);
        sb.append(year).append(DELIMITER_COMMA);
        sb.append(eps).append(DELIMITER_COMMA);
        sb.append(faceValue).append(DELIMITER_COMMA);
        sb.append(paidUpEquityShareCapital).append(DELIMITER_COMMA);
        sb.append(netProfit).append(DELIMITER_COMMA);
        sb.append(nonRecurringIncome).append(DELIMITER_COMMA);
        sb.append(nonRecurringExpense).append(DELIMITER_COMMA);
        sb.append(adjustedNetProfit).append(DELIMITER_COMMA);
        sb.append(interestCost).append(DELIMITER_COMMA);
        sb.append(netSales).append(DELIMITER_COMMA);
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return toWrite();
    }

    public float getAdjustedNetProfit() {
        return adjustedNetProfit;
    }

    public void setAdjustedNetProfit(float adjustedNetProfit) {
        this.adjustedNetProfit = adjustedNetProfit;
    }

    public boolean isAuditedFlag() {
        return auditedFlag;
    }

    public void setAuditedFlag(boolean auditedFlag) {
        this.auditedFlag = auditedFlag;
    }

    public boolean isBankingFlag() {
        return bankingFlag;
    }

    public void setBankingFlag(boolean bankingFlag) {
        this.bankingFlag = bankingFlag;
    }

    public boolean isConsolidatedFlag() {
        return consolidatedFlag;
    }

    public void setConsolidatedFlag(boolean consolidatedFlag) {
        this.consolidatedFlag = consolidatedFlag;
    }

    public PMDate getEndDate() {
        return endDate;
    }

    public void setEndDate(PMDate endDate) {
        this.endDate = endDate;
    }

    public float getEps() {
        return eps;
    }

    public void setEps(float eps) {
        this.eps = eps;
    }

    public float getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(float faceValue) {
        this.faceValue = faceValue;
    }

    public float getInterestCost() {
        return interestCost;
    }

    public void setInterestCost(float interestCost) {
        this.interestCost = interestCost;
    }

    public float getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(float netProfit) {
        this.netProfit = netProfit;
    }

    public float getNonRecurringExpense() {
        return nonRecurringExpense;
    }

    public void setNonRecurringExpense(float nonRecurringExpense) {
        this.nonRecurringExpense = nonRecurringExpense;
    }

    public float getNonRecurringIncome() {
        return nonRecurringIncome;
    }

    public void setNonRecurringIncome(float nonRecurringIncome) {
        this.nonRecurringIncome = nonRecurringIncome;
    }

    public float getPaidUpEquityShareCapital() {
        return paidUpEquityShareCapital;
    }

    public void setPaidUpEquityShareCapital(float paidUpEquityShareCapital) {
        this.paidUpEquityShareCapital = paidUpEquityShareCapital;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public PMDate getStartDate() {
        return startDate;
    }

    public void setStartDate(PMDate startDate) {
        this.startDate = startDate;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public CORP_RESULT_TIMELINE getTimeline() {
        return timeline;
    }

    public void setTimeline(CORP_RESULT_TIMELINE timeline) {
        this.timeline = timeline;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((stockCode == null) ? 0 : stockCode.hashCode());
        result = PRIME * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = PRIME * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = PRIME * result + ((timeline == null) ? 0 : timeline.hashCode());
        result = PRIME * result + period;
        result = PRIME * result + (auditedFlag ? 1231 : 1237);
        result = PRIME * result + (consolidatedFlag ? 1231 : 1237);
        result = PRIME * result + (bankingFlag ? 1231 : 1237);
        result = PRIME * result + year;
//		result = PRIME * result + Float.floatToIntBits(eps);
//		result = PRIME * result + Float.floatToIntBits(faceValue);
//		result = PRIME * result + Float.floatToIntBits(paidUpEquityShareCapital);
//		result = PRIME * result + Float.floatToIntBits(netProfit);
//		result = PRIME * result + Float.floatToIntBits(nonRecurringIncome);
//		result = PRIME * result + Float.floatToIntBits(nonRecurringExpense);
//		result = PRIME * result + Float.floatToIntBits(adjustedNetProfit);
//		result = PRIME * result + Float.floatToIntBits(interestCost);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CorpResultVO other = (CorpResultVO) obj;
        if (stockCode == null) {
            if (other.stockCode != null) {
                return false;
            }
        } else if (!stockCode.equals(other.stockCode)) {
            return false;
        }
        if (startDate == null) {
            if (other.startDate != null) {
                return false;
            }
        } else if (!startDate.equals(other.startDate)) {
            return false;
        }
        if (endDate == null) {
            if (other.endDate != null) {
                return false;
            }
        } else if (!endDate.equals(other.endDate)) {
            return false;
        }
        if (timeline == null) {
            if (other.timeline != null) {
                return false;
            }
        } else if (!timeline.equals(other.timeline)) {
            return false;
        }
        if (period != other.period) {
            return false;
        }
        if (auditedFlag != other.auditedFlag) {
            return false;
        }
        if (consolidatedFlag != other.consolidatedFlag) {
            return false;
        }
        if (bankingFlag != other.bankingFlag) {
            return false;
        }
        if (year != other.year) {
            return false;
        }
//		if (Float.floatToIntBits(eps) != Float.floatToIntBits(other.eps))
//			return false;
//		if (Float.floatToIntBits(faceValue) != Float.floatToIntBits(other.faceValue))
//			return false;
//		if (Float.floatToIntBits(paidUpEquityShareCapital) != Float.floatToIntBits(other.paidUpEquityShareCapital))
//			return false;
//		if (Float.floatToIntBits(netProfit) != Float.floatToIntBits(other.netProfit))
//			return false;
//		if (Float.floatToIntBits(nonRecurringIncome) != Float.floatToIntBits(other.nonRecurringIncome))
//			return false;
//		if (Float.floatToIntBits(nonRecurringExpense) != Float.floatToIntBits(other.nonRecurringExpense))
//			return false;
//		if (Float.floatToIntBits(adjustedNetProfit) != Float.floatToIntBits(other.adjustedNetProfit))
//			return false;
//		if (Float.floatToIntBits(interestCost) != Float.floatToIntBits(other.interestCost))
//			return false;
        return true;
    }

    public boolean equalsIncAll(Object obj) {
        if (!this.equals(obj)) return false;

        final CorpResultVO other = (CorpResultVO) obj;
        if (Float.floatToIntBits(eps) != Float.floatToIntBits(other.eps))
            return false;
        if (Float.floatToIntBits(faceValue) != Float.floatToIntBits(other.faceValue))
            return false;
        if (Float.floatToIntBits(paidUpEquityShareCapital) != Float.floatToIntBits(other.paidUpEquityShareCapital))
            return false;
        if (Float.floatToIntBits(netProfit) != Float.floatToIntBits(other.netProfit))
            return false;
        if (Float.floatToIntBits(nonRecurringIncome) != Float.floatToIntBits(other.nonRecurringIncome))
            return false;
        if (Float.floatToIntBits(nonRecurringExpense) != Float.floatToIntBits(other.nonRecurringExpense))
            return false;
        if (Float.floatToIntBits(adjustedNetProfit) != Float.floatToIntBits(other.adjustedNetProfit))
            return false;
        if (Float.floatToIntBits(interestCost) != Float.floatToIntBits(other.interestCost))
            return false;
        return true;
    }

    public String getDisplay() {
        StringBuffer sb = new StringBuffer();
        sb.append(year);
        sb.append(timeline.getCode());
        if (timeline != CORP_RESULT_TIMELINE.Annual) {
            sb.append(period);
        }
        return sb.toString();
    }

    public float getNetSales() {
        return netSales;
    }

    public void setNetSales(float netSales) {
        this.netSales = netSales;
    }


}
