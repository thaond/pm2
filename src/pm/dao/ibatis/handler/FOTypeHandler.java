package pm.dao.ibatis.handler;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;
import pm.util.enumlist.FOTYPE;

import java.sql.SQLException;

public class FOTypeHandler implements TypeHandlerCallback {

    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
        setter.setInt(((FOTYPE) parameter).ordinal());
    }

    public Object getResult(ResultGetter getter) throws SQLException {
        int intVal = getter.getInt();
        return FOTYPE.values()[intVal];
    }

    public Object valueOf(String s) {
        return FOTYPE.valueOf(s);
    }
}