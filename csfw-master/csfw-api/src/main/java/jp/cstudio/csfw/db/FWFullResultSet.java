package jp.cstudio.csfw.db;

import java.sql.SQLException;
import java.sql.SQLWarning;

public interface FWFullResultSet extends FWResultSet {

    SQLWarning getWarnings() throws SQLException;

    void clearWarnings() throws SQLException;

    String getCursorName() throws SQLException;

}
