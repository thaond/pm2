package pm.vo;

/**
 * Date: Sep 14, 2006
 * Time: 9:25:54 PM
 */
public class ActionMapping {

    private int actionId;
    private int transactionId;


    public ActionMapping() {
    }

    public ActionMapping(int actionId, int transactionId) {
        this.actionId = actionId;
        this.transactionId = transactionId;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionMapping that = (ActionMapping) o;

        if (actionId != that.actionId) return false;
        if (transactionId != that.transactionId) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = actionId;
        result = 31 * result + transactionId;
        return result;
    }
}
