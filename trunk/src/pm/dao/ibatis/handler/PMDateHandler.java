/*
// $Id: PMDateHandler.java,v 1.3 2007/12/30 15:18:02 tpalanis Exp $
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
import pm.util.PMDate;

import java.sql.SQLException;

/**
 * @author Thiyagu
 * @version $Id: PMDateHandler.java,v 1.3 2007/12/30 15:18:02 tpalanis Exp $
 * @since 15-Aug-2007
 */
public class PMDateHandler implements TypeHandlerCallback {

    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
        setter.setInt(((PMDate) parameter).getIntVal());
    }

    public Object getResult(ResultGetter getter) throws SQLException {
        int intVal = getter.getInt();
        return intVal != 0 ? new PMDate(intVal) : null;
    }

    public Object valueOf(String s) {
        return new PMDate(Integer.parseInt(s));
    }
}