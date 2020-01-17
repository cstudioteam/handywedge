package com.handywedge.pushnotice.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Authorization {

  protected static Logger logger = LogManager.getLogger("PushService");


  public static String getUserId(String token) {

    String userId = null;
    PreparedStatement stat = null;
    ResultSet rs = null;

    try (Connection con = DBUtil.getConnection()) {
      stat = con.prepareStatement("SELECT id FROM fw_api_token WHERE TOKEN=?");
      stat.setString(1, token);
      rs = stat.executeQuery();
      if (rs.next()) {
        userId = rs.getString("id");
      } else {
        logger.info("Authorization  Invalid token [{}].", token);
      }
    } catch (SQLException | NamingException e) {
      logger.error("getUserId", e);
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception e) {
        }
      }
      if (stat != null) {
        try {
          stat.close();
        } catch (Exception e) {
        }
      }
    }

    logger.debug("Authorization token={} userId={}", token, userId);

    return userId;
  }
}
