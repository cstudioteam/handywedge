package jp.cstudio.csfw.common;

public class FWException extends Exception {

  private static final long serialVersionUID = 1L;

  private String code;
  private Object[] args;

  public FWException(String code, Throwable cause, Object... args) {
    super(cause);
    this.code = code;
    this.args = args;
  }

  public FWException(String code, Object... args) {
    super();
    this.code = code;
    this.args = args;
  }

  @Override
  public String getMessage() {

    // TODO エラーコードからメッセージ取得する仕組みを作る
    return super.getMessage();
  }

}
