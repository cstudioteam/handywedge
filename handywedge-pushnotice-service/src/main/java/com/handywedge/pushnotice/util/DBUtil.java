package com.handywedge.pushnotice.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBUtil {

  protected static final String JDBC_JNDI_NAME = Property.get("JDBC_JNDI_NAME");


  public static Connection getConnection() throws NamingException, SQLException {

    Connection con = null;

    InitialContext context = new InitialContext();
    DataSource ds = (DataSource) context.lookup("java:comp/env/" + JDBC_JNDI_NAME);
    if (ds != null) {
      con = ds.getConnection();
    }

    return con;
  }

  public static void closeConnection(Connection con) {

    try {
      if (con != null) {
        con.close();
      }
    } catch (Exception e) {
    }

  }
}
