/*
// $Id: PMDaoTemplate.java,v 1.2 2007/12/15 16:10:29 tpalanis Exp $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2004-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package pm.dao.ibatis.dao;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import pm.vo.PortfolioDetailsVO;
import pm.vo.TradingAccountVO;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thiyagu
 * @version $Id: PMDaoTemplate.java,v 1.2 2007/12/15 16:10:29 tpalanis Exp $
 * @since 15-Aug-2007
 */
public class PMDaoTemplate extends SqlMapDaoTemplate {
    protected final String PORTFOLIO_ID = "PortfolioID";
    protected final String TRADINGACC_ID = "TradingAccID";

    public PMDaoTemplate(DaoManager daoManager) {
        super(daoManager);
    }

    protected void addAccountIDToMap(int portfolioID, int tradingAccID, Map<String, Integer> params) {
        if (portfolioID != -1) params.put(PORTFOLIO_ID, portfolioID);
        if (tradingAccID != -1) params.put(TRADINGACC_ID, tradingAccID);
    }

    protected Map<String, Integer> addIDsToMap(TradingAccountVO tradingAccount, PortfolioDetailsVO portfolioDetails) {
        Map<String, Integer> inputMap = new HashMap<String, Integer>();
        int portfolioID = portfolioDetails != null ? portfolioDetails.getId() : -1;
        int tradingID = tradingAccount != null ? tradingAccount.getId() : -1;
        addAccountIDToMap(portfolioID, tradingID, inputMap);
        return inputMap;
    }
}
