package pm.dao.ibatis.dao;

import pm.vo.IPOVO;

import java.util.List;

/**
 * Date: Aug 26, 2006
 * Time: 4:57:38 PM
 */
public interface IIPODAO {

    public List<IPOVO> getIPOTransaction(int portfolioID, int tradingAccID);

    public void insertIPOApply(IPOVO ipovo);

    public void updateIPO(IPOVO ipovo);

}
