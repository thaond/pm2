/*
// $Id: FundTransactionReasonHandler.java,v 1.2 2007/12/15 16:10:45 tpalanis Exp $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2004-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package pm.dao.ibatis.handler;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;
import static pm.util.AppConst.FUND_TRANSACTION_REASON;

import java.sql.SQLException;

/**
 * @author Thiyagu
 * @version $Id: FundTransactionReasonHandler.java,v 1.2 2007/12/15 16:10:45 tpalanis Exp $
 * @since 15-Aug-2007
 */
public class FundTransactionReasonHandler implements TypeHandlerCallback {

    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
        setter.setInt(((FUND_TRANSACTION_REASON) parameter).ordinal());
    }

    public Object getResult(ResultGetter getter) throws SQLException {
        int intVal = getter.getInt();
        return FUND_TRANSACTION_REASON.values()[intVal];
    }

    public Object valueOf(String s) {
        return FUND_TRANSACTION_REASON.valueOf(s);
    }
}