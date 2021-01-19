package hs.industry.ailab.dao.mysql.hander;

import com.alibaba.fastjson.JSONArray;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/9 10:18
 */
public class JsonArrayHandler implements TypeHandler<JSONArray> {
    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, JSONArray objects, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, objects.toJSONString());
    }

    @Override
    public JSONArray getResult(ResultSet resultSet, String s) throws SQLException {
        String rs = resultSet.getString(s);
        return JSONArray.parseArray(rs);
    }

    @Override
    public JSONArray getResult(ResultSet resultSet, int i) throws SQLException {
        return JSONArray.parseArray(resultSet.getString(i));
    }

    @Override
    public JSONArray getResult(CallableStatement callableStatement, int i) throws SQLException {
        return JSONArray.parseArray(callableStatement.getString(i));
    }
}
