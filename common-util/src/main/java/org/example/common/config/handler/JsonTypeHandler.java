package org.example.common.config.handler;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author: 李红兵
 * @date: 2021/4/6 16:30
 * @description: 数据库json类型处理器泛型类
 * 继承该类，指定泛型类型，数据源配置handler的扫描包即可自动转换指定类型数据
 * 在resultMap和@TableField注解中指定typeHandler为该类(mp高版本才支持)，或者具体实现类
 */
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.parseObject(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.parseObject(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.parseObject(cs.getString(columnIndex));
    }

    private T parseObject(String value) {
        return StringUtils.isEmpty(value) ? null : JSON.parseObject(value, this.getRawType());
    }
}
