/*
 * Created on Jan 6, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package pm.analyzer.bo;

import pm.util.PMDate;
import pm.util.QuoteIterator;

import java.util.Map;

/**
 * @author thiyagu
 *         <p/>
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IStockAnalyzerBO {

    /**
     * @param quoteIterator
     * @param stDate
     * @param enDate
     * @param negative
     * @param positive
     * @return
     */
    boolean markData(QuoteIterator quoteIterator, PMDate stDate, PMDate enDate, boolean positive, boolean negative);

    Map<String, Float> getFactors();

    void setFactors(Map<String, Float> factors);

    boolean hasFactors();
}
