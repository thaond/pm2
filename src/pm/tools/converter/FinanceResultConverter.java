package pm.tools.converter;

import pm.bo.StockMasterBO;
import pm.dao.CompanyDAO;
import pm.vo.CorpResultVO;
import pm.vo.CorporateResultsVO;
import pm.vo.StockVO;

import java.util.Vector;

/*
 * This class converts Raw / Downloaded financial details to
 * CorpResultVO
 */
public class FinanceResultConverter {

    public void convertAll() {
        Vector<StockVO> stockList = new StockMasterBO().getStockList(false);
        CompanyDAO companyDAO = new CompanyDAO();
        for (StockVO stockVO : stockList) {
            Vector<CorporateResultsVO> financialData = companyDAO.getFinancialData(stockVO.getStockCode());
            Vector<CorpResultVO> processedFinancialData = new Vector<CorpResultVO>();
            for (CorporateResultsVO resultsVO : financialData) {
                CorpResultVO corpResultVO = convert(resultsVO);
                if (corpResultVO != null) {
                    processedFinancialData.add(corpResultVO);
                }
            }
            if (!processedFinancialData.isEmpty()) {
//                companyDAO.storeProcessedFinancialData(stockVO.getStock(), processedFinancialData);
                throw new RuntimeException("Not implemented");
            }
        }

    }

    private CorpResultVO convert(CorporateResultsVO resultsVO) {
        if (resultsVO.getFinancialDetails().isEmpty()) return null;

        CorpResultVO resultVO = new CorpResultVO(resultsVO.getTicker(), resultsVO.getStartDate(), resultsVO.getEndDate(),
                resultsVO.getTimeline(), resultsVO.getPeriod(), resultsVO.isAuditedFlag(), resultsVO.isConsolidatedFlag(),
                resultsVO.isBanking(), resultsVO.getYear());


        for (Object object : resultsVO.getFinancialDetailKeys()) {
            String key = object.toString();
            float data = 0f;
            try {
                data = Float.parseFloat(resultsVO.getFinancialDetail(key).toString());
            } catch (NumberFormatException e) {
                continue;
            }
            if (key.startsWith("Earnings Per Share")) {
                resultVO.setEps(data);
            } else if (key.startsWith("Diluted EPS")) {
                resultVO.setEps(data);
            } else if (key.startsWith("Paid-up Equity")) {
                resultVO.setPaidUpEquityShareCapital(data);
            } else if (key.startsWith("Net Profit")) {
                resultVO.setNetProfit(data);
            } else if (key.startsWith("Non Recurring Income")) {
                resultVO.setNonRecurringIncome(data);
            } else if (key.startsWith("Non Recurring Expenses")) {
                resultVO.setNonRecurringExpense(data);
            } else if (key.startsWith("Adjusted Net Profit")) {
                resultVO.setAdjustedNetProfit(data);
            } else if (key.startsWith("Interest")) {
                resultVO.setInterestCost(data);
            } else if (key.startsWith("Face Value")) {
                resultVO.setFaceValue(data);
            }
        }

        return resultVO;
    }


}
