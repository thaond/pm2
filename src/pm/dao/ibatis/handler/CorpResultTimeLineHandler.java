/*
// $Id: CorpResultTimeLineHandler.java,v 1.1 2007/12/31 03:36:00 tpalanis Exp $
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
import pm.util.AppConst;

import java.sql.SQLException;

/**
 * @author Thiyagu
 * @version $Id: CorpResultTimeLineHandler.java,v 1.1 2007/12/31 03:36:00 tpalanis Exp $
 * @since 15-Aug-2007
 */
public class CorpResultTimeLineHandler implements TypeHandlerCallback {

    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
        setter.setInt(((AppConst.CORP_RESULT_TIMELINE) parameter).ordinal());
    }

    public Object getResult(ResultGetter getter) throws SQLException {
        int intVal = getter.getInt();
        return AppConst.CORP_RESULT_TIMELINE.values()[intVal];
    }

    public Object valueOf(String s) {
        return AppConst.CORP_RESULT_TIMELINE.valueOf(s);
    }
}