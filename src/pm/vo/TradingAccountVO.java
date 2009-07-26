package pm.vo;

import pm.util.enumlist.BROKERAGETYPE;

import java.util.StringTokenizer;

public class TradingAccountVO extends Account {

    private static String DELIMITOR = ",";

    private BROKERAGETYPE brokeragetype;

    public static TradingAccountVO ALL = new TradingAccountVO("All", -1);

    public TradingAccountVO() {
    }

    public TradingAccountVO(String accountName, BROKERAGETYPE brokeragetype) {
        this.name = accountName;
        this.brokeragetype = brokeragetype;
    }

    public TradingAccountVO(String line) {
        StringTokenizer stk = new StringTokenizer(line, DELIMITOR);
        name = stk.nextToken();
        brokeragetype = BROKERAGETYPE.valueOf(stk.nextToken());
    }

    private TradingAccountVO(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public BROKERAGETYPE getBrokeragetype() {
        return brokeragetype;
    }

    public void setBrokeragetype(BROKERAGETYPE brokeragetype) {
        this.brokeragetype = brokeragetype;
    }

    public String toWritable() {
        StringBuffer sb = new StringBuffer();
        sb.append(name).append(DELIMITOR).append(brokeragetype);
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TradingAccountVO that = (TradingAccountVO) o;

        if (!name.equals(that.name)) return false;
        if (brokeragetype != that.brokeragetype) return false;

        return true;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setBrokerageTypeName(String name) {
        this.brokeragetype = BROKERAGETYPE.valueOf(name);
    }

    public String getBrokerageTypeName() {
        return this.brokeragetype.name();
    }

    public boolean isAll() {
        return this == ALL;
    }
}
