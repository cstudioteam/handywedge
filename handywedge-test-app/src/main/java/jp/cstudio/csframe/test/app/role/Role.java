
package jp.cstudio.csframe.test.app.role;

public enum Role {

  ADMIN("管理者"), GENERAL("一般ユーザー"), STAFF("担当者"), SUPERIOR("上長"), DIRECTOR("部門長");

  private String role;

  private Role(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }

  public static Role toEnum(String role) {
    for (Role r : values()) {
      if (r.getRole().equals(role)) {
        return r;
      }
    }
    return GENERAL;// nullやノーマッチは一般ユーザー扱いにする
  }

}
