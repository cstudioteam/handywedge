package jp.cstudio.csfw.db;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Struct;

public interface FWConnection {

    FWStatement createStatement() throws SQLException;

    FWPreparedStatement prepareStatement(String sql) throws SQLException;

    FWCallableStatement prepareCall(String sql) throws SQLException;

    String nativeSQL(String sql) throws SQLException;

    Clob createClob() throws SQLException;

    Blob createBlob() throws SQLException;

    NClob createNClob() throws SQLException;

    SQLXML createSQLXML() throws SQLException;

    Array createArrayOf(String typeName, Object[] elements) throws SQLException;

    Struct createStruct(String typeName, Object[] attributes) throws SQLException;

}
