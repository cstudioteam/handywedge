package com.handywedge.converter.tosvg.library.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class FWConverterUtils {
  /**
   * 変換ファイル格納ディレクトリを取得
   * 
   * @return 変換ファイル格納ディレクトリ
   */
  public static String getWorkDirectory() {
    String workDirectory = System.getenv("converter_dir");
    if (StringUtils.isEmpty(workDirectory)) {
      workDirectory = FileUtils.getTempDirectory().getAbsolutePath();
    }
    return workDirectory;
  }
}
