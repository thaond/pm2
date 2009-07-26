package pm.net.icici;

import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.ITransactionDAO;
import pm.vo.ICICITransaction;
import pm.vo.TransactionVO;

import java.util.List;

public class TransactionSynchronizer {

    public static void main(String[] args) {
        new TransactionSynchronizer().sync();
    }

    public void sync() {
        ITransactionDAO iTransactionDAO = DAOManager.getTransactionDAO();
        List<ICICITransaction> iciciTransactions = iTransactionDAO.iciciTransactions();
        List<TransactionVO> transactions = iTransactionDAO.getTransactionList("ICICI_Direct", null);
        for (TransactionVO transaction : transactions) {
            boolean matched = false;
            for (ICICITransaction iciciTransaction : iciciTransactions) {
                if (match(iciciTransaction, transaction)) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
//                iciciTransaction.setPortfolio("Thiyagu");
//                iciciTransaction.setTradingAc("ICICI_Direct");
                System.out.println(transaction.getDetails());
            }
        }
    }

    private boolean match(ICICITransaction i1, TransactionVO t1) {
        return i1.getStockCode().equals(t1.getStockCode()) && i1.getDate().equals(t1.getDate()) && i1.getAction() == t1.getAction()
                && i1.getPrice() == t1.getPrice();
    }
}
