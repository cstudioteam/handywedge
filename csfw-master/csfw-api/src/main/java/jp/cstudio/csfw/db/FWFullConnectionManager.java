package jp.cstudio.csfw.db;

public interface FWFullConnectionManager extends FWConnectionManager {

    FWFullConnection getConnection(String dataSource);

    @Override
    FWFullConnection getConnection();

    void addStatement(FWStatement statement);

    void addResltSet(FWResultSet resultSet);

    void close();
}
