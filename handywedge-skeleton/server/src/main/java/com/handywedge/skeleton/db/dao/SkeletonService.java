package com.handywedge.skeleton.db.dao;

import java.sql.SQLException;

import com.handywedge.context.FWRESTContext;
import com.handywedge.db.FWConnectionManager;
import com.handywedge.db.FWPreparedStatement;
import com.handywedge.db.FWResultSet;
import com.handywedge.db.FWTransactional;
import com.handywedge.db.FWTransactional.FWTxType;
import com.handywedge.skeleton.api.v1.model.SkeletonModel;
import com.handywedge.skeleton.db.dto.Skeleton;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class SkeletonService {

  @Inject
  private FWConnectionManager cm;
  @Inject
  private FWRESTContext context;

  // SQL予約語は大文字、その他のテーブル名などは小文字。
  @FWTransactional(FWTxType.READ_ONLY)
  public SkeletonModel read(long id) throws SQLException {
    String sql = "SELECT * FROM skeleton WHERE id = ?";
    // Connectionのクローズは不要。
    // StatementやResultSetはクローズ。
    try (FWPreparedStatement ps = cm.getConnection().prepareStatement(sql)) {
      int idx = 1;
      ps.setLong(idx++, id);
      try (FWResultSet rs = ps.executeQuery()) {
        SkeletonModel result = null;
        if (rs.next()) {
          result = new SkeletonModel(rs.getLong("id"), rs.getString("value"),
              rs.getTimestamp("create_datetime"));
        }
        return result;
      }
    }
  }

  // 一番最初の@FWTransactionalがトランザクション開始点となる。
  // @FWTransactionalをネストして呼び出した場合は最初のトランザクションで処理される。
  // commitやrollback操作も不要。例外がスローされた場合はHWでロールバック。
  @FWTransactional
  public SkeletonModel create(Skeleton dto) throws SQLException {
    // クエリーが長くなる場合などはStringBuilderで見やすく区切る。
    StringBuilder sql = new StringBuilder();
    sql.append("INSERT INTO skeleton (");
    sql.append("value");
    sql.append(") VALUES (");
    sql.append("?");
    sql.append(")");
    try (FWPreparedStatement ps = cm.getConnection().prepareStatement(sql.toString(),
        FWPreparedStatement.RETURN_GENERATED_KEYS)) {
      int idx = 1;
      ps.setString(idx++, dto.getValue());
      ps.executeUpdate();
      try (FWResultSet rs = ps.getGeneratedKeys()) {
        rs.next();
        long id = rs.getLong(1);
        return read(id);
      }
    }
  }
}
