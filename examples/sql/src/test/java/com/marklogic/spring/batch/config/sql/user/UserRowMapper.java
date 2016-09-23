package com.marklogic.spring.batch.config.sql.user;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt(1));
        user.setName(rs.getString(2));
        user.setEmail(rs.getString(3));
        String comment = rs.getString(4);
        if (comment != null) {
            user.getComments().add(comment);
        }
        return user;
    }

}
