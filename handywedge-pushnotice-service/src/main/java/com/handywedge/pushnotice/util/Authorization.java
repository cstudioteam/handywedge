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
    try (Connection con = DBUtil.getConnection();
        PreparedStatement stat =
            con.prepareStatement("SELECT id FROM fw_api_token WHERE TOKEN=?");) {
      stat.setString(1, token);
      try (ResultSet rs = stat.executeQuery()) {
        if (rs.next()) {
          userId = rs.getString("id");
        } else {
          logger.info("Authorization  Invalid token [{}].", token);
        }
      }
    } catch (SQLException | NamingException e) {
      logger.error("getUserId", e);
    }

    logger.debug("Authorization token={} userId={}", token, userId);

    return userId;
  }
}
