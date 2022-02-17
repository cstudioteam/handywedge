package com.handywedge.skeleton.api.v1.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.handywedge.skeleton.db.dto.Skeleton;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SkeletonModel extends Skeleton {

  public SkeletonModel(long id, String value, Timestamp createDate) {
    super(id, value, createDate);
  }

  public Long getCreateTs() {
    if (getCreateDate() != null) {
      return getCreateDate().getTime();
    } else {
      return null;
    }
  }
}
