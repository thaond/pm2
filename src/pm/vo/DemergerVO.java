package pm.vo;

import static pm.util.AppConst.DELIMITER2;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.StringTokenizer;

public class DemergerVO implements Serializable {

    private String newStockCode;

    private float bookValueRatio;

    private int baseID;

    public DemergerVO(String newStockCode, float bookValueRatio) {
        super();
        this.newStockCode = newStockCode;
        this.bookValueRatio = bookValueRatio;
    }

    public DemergerVO() {
    }

    public DemergerVO(String line) throws ParseException {
        StringTokenizer stk = new StringTokenizer(line, DELIMITER2);
        newStockCode = stk.nextToken();
        bookValueRatio = NumberFormat.getInstance().parse(stk.nextToken())
                .floatValue();
    }

    public int getBaseID() {
        return baseID;
    }

    public void setBaseID(int baseID) {
        this.baseID = baseID;
    }

    public float getBookValueRatio() {
        return bookValueRatio;
    }

    public void setBookValueRatio(float bookValueRatio) {
        this.bookValueRatio = bookValueRatio;
    }

    public String getNewStockCode() {
        return newStockCode;
    }

    public void setNewStockCode(String newStockCode) {
        this.newStockCode = newStockCode;
    }

    public boolean isComplete() {
        return newStockCode != null && bookValueRatio > 0;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result
                + ((newStockCode == null) ? 0 : newStockCode.hashCode());
        result = PRIME * result + Float.floatToIntBits(bookValueRatio);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DemergerVO other = (DemergerVO) obj;
        if (newStockCode == null) {
            if (other.newStockCode != null)
                return false;
        } else if (!newStockCode.equals(other.newStockCode))
            return false;
        if (Float.floatToIntBits(bookValueRatio) != Float
                .floatToIntBits(other.bookValueRatio))
            return false;
        return true;
    }

    public String toWrite() {
        StringBuffer sb = new StringBuffer();
        if (isComplete()) {
            sb.append(newStockCode).append(DELIMITER2).append(bookValueRatio);
        }
        return sb.toString();
    }

}
