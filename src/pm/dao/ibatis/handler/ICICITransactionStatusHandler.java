package pm.dao.ibatis.handler;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;
import pm.util.enumlist.ICICITransactionStatus;

import java.sql.SQLException;

public class ICICITransactionStatusHandler implements TypeHandlerCallback {

    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
        setter.setInt(((ICICITransactionStatus) parameter).ordinal());
    }

    public Object getResult(ResultGetter getter) throws SQLException {
        int intVal = getter.getInt();
        return ICICITransactionStatus.values()[intVal];
    }

    public Object valueOf(String s) {
        return ICICITransactionStatus.valueOf(s);
    }
}