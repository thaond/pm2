package pm.bo;

import org.apache.log4j.Logger;
import pm.dao.ibatis.dao.DAOManager;
import pm.dao.ibatis.dao.IStockDAO;
import pm.util.BusinessLogger;
import pm.util.PMDate;
import pm.util.enumlist.SERIESTYPE;
import pm.vo.StockVO;

import java.util.*;

/**
 * Date: 23-Jul-2006
 * Time: 11:25:21
 */
public class StockMasterBO {

    private static Logger logger = Logger.getLogger(StockMasterBO.class);
    public static final String DUMMYISIN_PREFIX = "PM_";

    public boolean storeStockList(List<StockVO> newStockList) {
        try {
            DAOManager.getDaoManager().startTransaction();
            IStockDAO stockDAO = getDAO();
            eliminateDuplicateUpdateModified(newStockList, stockDAO.getStockList(false));
            addNewStocks(newStockList, stockDAO.getStockList(false));
            updateDelistedStocks(newStockList, stockDAO.getStockList(false));
            DAOManager.getDaoManager().commitTransaction();
        } finally {
            DAOManager.getDaoManager().endTransaction();
        }
        return true;
    }

    void updateDelistedStocks(List<StockVO> newStockList, List<StockVO> stockList) {
        List<StockVO> delistedList = new ArrayList<StockVO>();
        Set<StockVO> newStockSet = new HashSet<StockVO>(newStockList);
        for (StockVO stockVO : stockList) {
            if (!newStockSet.contains(stockVO) && !isAutoCreated(stockVO)) {
                stockVO.setListed(false);
                delistedList.add(stockVO);
            }
        }
        updateStockList(delistedList);
    }

    void updateStockList(List<StockVO> delistedList) {
        getDAO().updateStockList(delistedList);
    }

    private boolean isAutoCreated(StockVO stockVO) {
        return stockVO.getIsin().startsWith(DUMMYISIN_PREFIX);
    }

    void addNewStocks(List<StockVO> incomingStockList, List<StockVO> stockList) {
        List<StockVO> newToPMList = new ArrayList<StockVO>();
        Set<StockVO> existingStockSet = new HashSet<StockVO>(stockList);
        for (StockVO stockVO : incomingStockList) {
            if (!existingStockSet.contains(stockVO)) {
                newToPMList.add(stockVO);
            }
        }
        insertStockList(newToPMList);
    }

    void insertStockList(List<StockVO> newToPMList) {
        applySizeRestrictions(newToPMList);
        getDAO().insertStockList(newToPMList);
    }

    void eliminateDuplicateUpdateModified(List<StockVO> newStockList, List<StockVO> stockList) {
        Hashtable<String, StockVO> htStockListByISIN = new Hashtable<String, StockVO>();
        Hashtable<String, StockVO> htStockListByStockCode = new Hashtable<String, StockVO>();
        for (StockVO stockVO : stockList) {
            htStockListByISIN.put(stockVO.getIsin(), stockVO);
            htStockListByStockCode.put(stockVO.getStockCode(), stockVO);
        }
        for (StockVO newStockVO : newStockList) {
            StockVO stockVO = htStockListByISIN.remove(newStockVO.getIsin());
            if (!newStockVO.equals(stockVO) && stockVO != null) {
                newStockVO.setId(stockVO.getId());
                identifyRemoveDuplicate(htStockListByStockCode, newStockVO, stockVO);
                updateStock(newStockVO);
            }
        }
    }

    private void identifyRemoveDuplicate(Hashtable<String, StockVO> htStockListByStockCode, StockVO newStockVO, StockVO stockVO) {
        if (!stockVO.getStockCode().equals(newStockVO.getStockCode())) {
            StockVO duplicateStockVO = htStockListByStockCode.remove(newStockVO.getStockCode());
            if (!stockVO.equals(duplicateStockVO) && duplicateStockVO != null) {
                removeDuplicate(duplicateStockVO, newStockVO);
            }
        }
    }

    void updateStock(StockVO stockVO) {
        getDAO().updateStock(stockVO);
    }

    private void applySizeRestrictions(List<StockVO> newStockList) {
        for (StockVO stockVO : newStockList) {
            if (stockVO.getCompanyName().length() > 50) {
                stockVO.setCompanyName(stockVO.getCompanyName().substring(0, 50));
            }
        }
    }

    private IStockDAO getDAO() {
        return DAOManager.getStockDAO();
    }

    public Vector<StockVO> getStockList(boolean incIndex) {
        List<StockVO> stockList = getDAO().getStockList(incIndex);
        return new Vector<StockVO>(stockList);
    }

    public StockVO getListedStockDetails(String stockCode) {
        return getDAO().getStock(stockCode);
    }

    public void insertMissingStockCodes(Set<String> stockCodes) {
        IStockDAO dao = getDAO();
        try {
            DAOManager.getDaoManager().startTransaction();
            List<StockVO> stockList = dao.getStockList(false);
            for (StockVO stockVO : stockList) {
                stockCodes.remove(stockVO.getStockCode());
            }
            for (String stockCode : stockCodes) {
                insertNewStock(stockCode);
            }
            DAOManager.getDaoManager().commitTransaction();
        } finally {
            DAOManager.getDaoManager().endTransaction();
        }
    }

    public void insertNewStock(String stockCode) {
        insertNewStock(stockCode, "");
    }

    public void insertNewStock(String stockCode, String companyName) {
        getDAO().insertStock(new StockVO(stockCode, companyName, 10f, SERIESTYPE.equity, 10f, (short) 1, DUMMYISIN_PREFIX + stockCode, new PMDate(), true));
    }

    public void doSymbolChange(String oldStockCode, String newStockCode) {
        BusinessLogger.logSymbolChange(oldStockCode, newStockCode);
        IStockDAO dao = getDAO();
        StockVO stockVO = dao.getStock(oldStockCode);
        if (stockVO != null) {
            try {
                StockVO newStockVO = (StockVO) stockVO.clone();
                newStockVO.setStockCode(newStockCode);
                dao.updateStock(newStockVO);
            } catch (CloneNotSupportedException e) {
                logger.error(e, e);
            }
        }
    }

    public void removeDuplicate(StockVO duplicateStockVO, StockVO originalStockVO) {
        try {
            DAOManager.getDaoManager().startTransaction();
            new PortfolioBO().handleDuplicateInStopLoss(duplicateStockVO, originalStockVO);
            new WatchlistBO().handleDuplicate(duplicateStockVO.getId(), originalStockVO.getId());
            DAOManager.getCompanyActionDAO().updateStockId(duplicateStockVO.getId(), originalStockVO.getId());
            DAOManager.getTransactionDAO().updateStockId(duplicateStockVO.getId(), originalStockVO.getId());
            DAOManager.getQuoteDAO().updateStockId(duplicateStockVO.getId(), originalStockVO.getId());
            DAOManager.companyResultDAO().updateStockId(duplicateStockVO.getId(), originalStockVO.getId());
            DAOManager.getStockDAO().delete(duplicateStockVO.getId());
            DAOManager.getDaoManager().commitTransaction();
        } finally {
            DAOManager.getDaoManager().endTransaction();
        }
    }
}
