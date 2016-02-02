package jp.cstudio.csfw.common;

public class FWRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private String code;
  private Object[] args;

  public FWRuntimeException(String code, Throwable cause, Object... args) {
    super(cause);
    this.code = code;
    this.args = args;
  }

  public FWRuntimeException(String code, Object... args) {
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
