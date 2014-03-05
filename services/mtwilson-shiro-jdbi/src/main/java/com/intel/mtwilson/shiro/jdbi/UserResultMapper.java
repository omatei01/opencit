/*
 * Copyright (C) 2013 Intel Corporation
 * All rights reserved.
 */
package com.intel.mtwilson.shiro.jdbi;

import com.intel.dcsg.cpg.i18n.LocaleUtil;
import com.intel.mtwilson.shiro.jdbi.model.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.intel.dcsg.cpg.io.UUID;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * 
 * @author jbuhacoff
 */
public class UserResultMapper implements ResultSetMapper<User> {

    @Override
    public User map(int i, ResultSet rs, StatementContext sc) throws SQLException {
//        UUID uuid = UUID.valueOf(rs.getBytes("id")); // use this when uuid is a binary(mysql) or uuid(postgresql) type in database
//        UUID uuid = UUID.valueOf(rs.getString("id")); // use this when uuid is a char type in database
        User user = new User();
//        role.setId(UUID.valueOf(rs.getBytes("id"))); // would work for mysql if using binary(16) for uuid field
        user.setId(UUID.valueOf((java.util.UUID)rs.getObject("id"))); // works for postgresql  when using uuid field
        user.setUsername(rs.getString("username"));
        user.setComment(rs.getString("comment"));
        if( rs.getString("locale") != null ) {
            user.setLocale(LocaleUtil.forLanguageTag(rs.getString("locale")));
        }
        user.setEnabled(rs.getBoolean("enabled"));
        user.setStatus(Status.valueOf(rs.getString("status")));
        return user;
    }
    
}