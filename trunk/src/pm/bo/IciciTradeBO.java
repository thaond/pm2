package pm.bo;

import pm.dao.ibatis.dao.DAOManager;
import pm.vo.ICICITransaction;
import pm.vo.TransactionVO;

import java.util.List;

public class IciciTradeBO {

    public void syncWithPMTrade() {
        List<ICICITransaction> iciciTransactions = DAOManager.getTransactionDAO().iciciTransactions();
        List<TransactionVO> pmTransactions = DAOManager.getTransactionDAO().getTransactionList("ICICI_Direct", null);
        for (ICICITransaction iciciTransaction : iciciTransactions) {
            if (iciciTransaction.getPortfolio() != null) continue;
            for (TransactionVO pmTransaction : pmTransactions) {
                if (pmTransaction.isDayTrading() == iciciTransaction.isDayTrading() && pmTransaction.getAction() == iciciTransaction.getAction() &&
                        pmTransaction.getPrice() == iciciTransaction.getPrice() && pmTransaction.getQty() == iciciTransaction.getQty() &&
                        pmTransaction.getStockCode().equals(iciciTransaction.getStockCode()) && pmTransaction.getDate().equals(iciciTransaction.getDate())) {
                    iciciTransaction.mapTransaction(pmTransaction.getPortfolio());
                    DAOManager.getTransactionDAO().updateOrInsertICICITransaction(iciciTransaction);
                    break;
                }
            }
        }
    }
}
