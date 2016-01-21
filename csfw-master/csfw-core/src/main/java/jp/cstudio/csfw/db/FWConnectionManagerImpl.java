package jp.cstudio.csfw.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import jp.cstudio.csfw.common.FWConstantCode;
import jp.cstudio.csfw.common.FWRuntimeException;
import jp.cstudio.csfw.db.FWFullConnection;
import jp.cstudio.csfw.db.FWFullConnectionManager;
import jp.cstudio.csfw.db.FWResultSet;
import jp.cstudio.csfw.db.FWStatement;
import jp.cstudio.csfw.log.FWLogger;

@RequestScoped
public class FWConnectionManagerImpl implements FWFullConnectionManager {

    // REQUIRED以外のネストしたトランザクションは考慮していない
    private FWFullConnection connection = null;
    private List<FWStatement> statements = new ArrayList<>();
    private List<FWResultSet> resultSets = new ArrayList<>();

    @Inject
    private FWLogger logger;

    @Override
    public FWFullConnection getConnection(String dataSourceName) {

        DataSource dataSource;
        try {
            dataSource = (DataSource) InitialContext.doLookup("java:comp/env/" + dataSourceName);
        } catch (NamingException e) {
            throw new FWRuntimeException(FWConstantCode.DS_LOOKUP_FAIL, e);
        }
        try {
            connection = new FWConnectionWrapper(dataSource.getConnection());
            return connection;
        } catch (SQLException e) {
            throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
        }
    }

    @Override
    public FWFullConnection getConnection() {

        return connection;
    }

    @Override
    public void addStatement(FWStatement statement) {

        statements.add(statement);
    }

    @Override
    public void addResltSet(FWResultSet resultSet) {

        resultSets.add(resultSet);
    }

    @Override
    public void close() {

        try {
            for (FWResultSet rs : resultSets) {
                if (!rs.isClosed()) {
                    logger.warn("FWResulstSet not closed.");
                    rs.close();
                }
            }
            for (FWStatement s : statements) {
                if (!s.isClosed()) {
                    logger.warn("FWStatement not closed.");
                    s.close();
                }
            }
        } catch (SQLException e) {
            throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
        }
    }

}
