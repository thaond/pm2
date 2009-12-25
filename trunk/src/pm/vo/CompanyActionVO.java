package pm.vo;

import org.apache.log4j.Logger;
import pm.util.*;
import pm.util.AppConst.COMPANY_ACTION_TYPE;

import static pm.util.AppConst.COMPANY_ACTION_TYPE.Merger;
import static pm.util.AppConst.DELIMITER_COMMA;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class CompanyActionVO implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CompanyActionVO.class);

    private PMDate exDate;

    private String stockCode;

    private COMPANY_ACTION_TYPE action;

    private float dsbValue;

    private float base;

    private List<DemergerVO> demergerData = new Vector<DemergerVO>();

    private boolean percentageValue;

    private transient float valueAtCurrentPrice = 0f;

    private String parentEntity;

    private int id;

    public CompanyActionVO() {
    }

    /**
     * @param action
     * @param date
     * @param stockCode
     * @param dsbValue
     * @param base
     */
    public CompanyActionVO(COMPANY_ACTION_TYPE action, PMDate date,
                           String stockCode, float dsbValue, float base) {
        this.action = action;
        this.exDate = date;
        this.stockCode = stockCode;
        this.dsbValue = dsbValue;
        this.base = base;
    }

    public CompanyActionVO(String line) throws Exception {
        StringTokenizer stk = new StringTokenizer(line, DELIMITER_COMMA);
        try {
            this.exDate = PMDateFormatter.parseYYYYMMDD(stk.nextToken());
        } catch (ApplicationException e) {
            logger.error(e, e);
        }
        this.stockCode = stk.nextToken();
        this.dsbValue = NumberFormat.getInstance().parse(stk.nextToken())
                .floatValue();
        this.base = NumberFormat.getInstance().parse(stk.nextToken())
                .floatValue();
        this.action = COMPANY_ACTION_TYPE.valueOf(stk.nextToken());
        demergerData = new Vector<DemergerVO>();
        if (stk.hasMoreTokens()) {
            percentageValue = Boolean.parseBoolean(stk.nextToken());
        }
        if (action == Merger) {
            parentEntity = stk.nextToken();
        } else {
            while (stk.hasMoreTokens()) {
                demergerData.add(new DemergerVO(stk.nextToken()));
            }
        }
    }

    public CompanyActionVO(COMPANY_ACTION_TYPE action, PMDate date,
                           String stockCode, Vector<DemergerVO> data) {
        this.action = action;
        this.exDate = date;
        this.stockCode = stockCode;
        demergerData = data;
    }

    public CompanyActionVO(COMPANY_ACTION_TYPE action, PMDate exDate, String stockCode, float dsbValue, float base, String parentEntity) {

        this.action = action;
        this.exDate = exDate;
        this.stockCode = stockCode;
        this.dsbValue = dsbValue;
        this.base = base;
        this.parentEntity = parentEntity;
    }

    public COMPANY_ACTION_TYPE getAction() {
        return action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public void setBase(float base) {
        this.base = base;
    }

    public void setActionVal(String val) {
        action = COMPANY_ACTION_TYPE.valueOf(val);
    }

    public float getDsbValue() {
        return dsbValue;
    }

    public PMDate getExDate() {
        return exDate;
    }

    public int getExDateVal() {
        return exDate.getIntVal();
    }

    public void setExDateVal(int val) {
        exDate = new PMDate(val);
    }

    public String getStockCode() {
        return stockCode;
    }

    public float getBase() {
        return base;
    }

    public List<DemergerVO> getDemergerData() {
        return demergerData;
    }

    public void setDemergerData(List<DemergerVO> demergerData) {
        this.demergerData = demergerData;
    }

    public String toWrite() {
        StringBuffer sb = new StringBuffer();
        sb.append(PMDateFormatter.formatYYYYMMDD(exDate)).append(DELIMITER_COMMA);
        sb.append(stockCode).append(DELIMITER_COMMA);
        sb.append(dsbValue).append(DELIMITER_COMMA);
        sb.append(base).append(DELIMITER_COMMA);
        sb.append(action).append(DELIMITER_COMMA);
        sb.append(percentageValue).append(DELIMITER_COMMA);
        if (action == Merger) {
            sb.append(parentEntity).append(DELIMITER_COMMA);
        } else {
            for (DemergerVO demergerVO : demergerData) {
                sb.append(demergerVO.toWrite()).append(DELIMITER_COMMA);
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(toWrite());
        sb.append("\n");
        return sb.toString();
    }

    /*
      * It doesn't consider dsb / demerger values for comparison
      */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((exDate == null) ? 0 : exDate.hashCode());
        result = PRIME * result
                + ((stockCode == null) ? 0 : stockCode.hashCode());
        result = PRIME * result + ((action == null) ? 0 : action.hashCode());
        result = PRIME * result + Float.floatToIntBits(dsbValue);
        result = PRIME * result + Float.floatToIntBits(base);
        result = PRIME * result
                + ((demergerData == null) ? 0 : demergerData.hashCode());
        result = PRIME * result + ((parentEntity == null) ? 0 : parentEntity.hashCode());
        return result;
    }

    /*
      * It doesn't consider dsb / demerger values for comparison
      */
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
        final CompanyActionVO other = (CompanyActionVO) obj;
        if (exDate == null) {
            if (other.exDate != null) {
                return false;
            }
        } else if (!exDate.equals(other.exDate)) {
            return false;
        }
        if (stockCode == null) {
            if (other.stockCode != null) {
                return false;
            }
        } else if (!stockCode.equals(other.stockCode)) {
            return false;
        }
        if (action == null) {
            if (other.action != null) {
                return false;
            }
        } else if (!action.equals(other.action)) {
            return false;
        }
        if (Float.floatToIntBits(dsbValue) != Float
                .floatToIntBits(other.dsbValue)) {
            return false;
        }
        if (Float.floatToIntBits(base) != Float.floatToIntBits(other.base)) {
            return false;
        }
        if (percentageValue != other.percentageValue) {
            return false;
        }
        if ((parentEntity == null && other.parentEntity != null) || (parentEntity != null && !parentEntity.equals(other.parentEntity))) {
            return false;
        }
        if (demergerData == null) {
            if (other.demergerData != null) {
                return false;
            }
        } else if (!compareDemergerData(other)) {
            return false;
        }
        return true;
    }

    private boolean compareDemergerData(final CompanyActionVO other) {
        if (other.demergerData == null) {
            return false;
        }
        if (demergerData.size() != other.demergerData.size()) {
            return false;
        }
        for (int i = 0; i < demergerData.size(); i++) {
            if (!other.demergerData.contains(demergerData.get(i))) {
                return false;
            }
        }
        return true;
    }


    public boolean isPercentageValue() {
        return percentageValue;
    }

    public void setPercentageValue(boolean percentageValue) {
        this.percentageValue = percentageValue;
    }

    public void setDsbValue(float bonus) {
        this.dsbValue = bonus;
    }

    public String getDisplayMsgWithStockCode() {
        StringBuffer sb = new StringBuffer();
        sb.append(stockCode).append(" - ");
        sb.append(action.toString()).append(getActionDetails());
        if (action == COMPANY_ACTION_TYPE.Divident) {
            sb.append(" {").append(Helper.formatFloat(valueAtCurrentPrice)).append("}");
        }
        return sb.toString();
    }

    public String getDisplayMsgWithDate() {
        StringBuffer sb = new StringBuffer();
        sb.append(PMDateFormatter.displayFormat(exDate)).append(" : ");
        sb.append(action.toString()).append(getActionDetails());
        return sb.toString();
    }

    public StringBuffer getActionDetails() {
        StringBuffer sb = new StringBuffer(" ");
        sb.append("[");
        switch (action) {
            case Divident:
                sb.append(dsbValue);
                if (percentageValue) {
                    sb.append("%");
                } else {
                    sb.append(" Rs.");
                }
                break;
            case Bonus:
                sb.append("").append(dsbValue).append(" For ").append(base);
                break;
            case Split:
                sb.append("Rs.").append(base).append(" To Rs.").append(dsbValue);
                break;
            case Demerger:
                break;
        }
        sb.append("]");
        return sb;
    }

    public float getPriceFactor() {
        switch (action) {
            case Bonus:
                return base / (base + dsbValue);
            case Split:
                return dsbValue / base;
            case Demerger:
                for (DemergerVO demergerVO : demergerData) {
                    if (demergerVO.getNewStockCode().equals(stockCode)) {
                        return demergerVO.getBookValueRatio() / 100f;
                    }
                }
        }
        return 0;
    }

    public float getValueAtCurrentPrice() {
        return valueAtCurrentPrice;
    }

    public void setValueAtCurrentPrice(float valueAtCurrentPrice) {
        this.valueAtCurrentPrice = valueAtCurrentPrice;
    }


    public void setExDate(PMDate exDate) {
        this.exDate = exDate;
    }

    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    public CompanyActionVO clone() {
        try {
            return (CompanyActionVO) super.clone();
        } catch (CloneNotSupportedException e) {
            logger.error(e, e);
        }
        return null;
    }

    public boolean normalize(float faceValue) {
        if ((action == COMPANY_ACTION_TYPE.Divident) && !percentageValue) {
            float percentage = dsbValue / base / faceValue * 100f;
            dsbValue = Helper.getRoundedOffValue(percentage, 4);
            percentageValue = true;
            return true;
        }
        return false;
    }

    public String getParentEntity() {
        return parentEntity;
    }

    public void setParentEntity(String parentEntity) {
        this.parentEntity = parentEntity;
    }
}
