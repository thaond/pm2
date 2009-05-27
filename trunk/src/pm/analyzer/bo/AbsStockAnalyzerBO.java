package pm.analyzer.bo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thiyagu
 * @version $Id: AbsStockAnalyzerBO.java,v 1.1 2008/01/03 05:46:53 tpalanis Exp $
 * @since 02-Jan-2008
 */
public abstract class AbsStockAnalyzerBO implements IStockAnalyzerBO {

    protected Map<String, Float> factors = new HashMap<String, Float>();

    public Map<String, Float> getFactors() {
        return factors;
    }

    public void setFactors(Map<String, Float> factors) {
        this.factors = factors;
    }

    public boolean hasFactors() {
        return !this.factors.isEmpty();
    }
}
