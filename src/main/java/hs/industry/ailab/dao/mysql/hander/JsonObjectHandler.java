package hs.industry.ailab.dao.mysql.hander;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/9 10:18
 */
public class JsonObjectHandler implements TypeHandler<JSONObject> {
    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, JSONObject jsonObject, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i,jsonObject.toJSONString());
    }

    @Override
    public JSONObject getResult(ResultSet resultSet, String s) throws SQLException {
        return JSONObject.parseObject(resultSet.getString(s));
//        return null;
    }

    @Override
    public JSONObject getResult(ResultSet resultSet, int i) throws SQLException {
        return JSONObject.parseObject(resultSet.getString(i));
//        return null;
    }

    @Override
    public JSONObject getResult(CallableStatement callableStatement, int i) throws SQLException {
        return JSONObject.parseObject(callableStatement.getString(i));
//        return null;
    }
}
