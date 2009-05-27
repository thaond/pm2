package pm.dao.ibatis.dao;

import pm.vo.CorpResultVO;

import java.util.List;

/**
 * @author Thiyagu
 * @version $Id: ICompanyResultDAO.java,v 1.1 2007/12/31 03:36:00 tpalanis Exp $
 * @since 30-Dec-2007
 */
public interface ICompanyResultDAO {

    void save(CorpResultVO resultVO);

    List<CorpResultVO> get(int stockID);

    void updateStockId(int fromStockId, int toStockId);
}
