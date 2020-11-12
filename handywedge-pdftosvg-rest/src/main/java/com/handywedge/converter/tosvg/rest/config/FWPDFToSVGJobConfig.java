package com.handywedge.converter.tosvg.rest.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import java.io.*;
import java.util.Properties;

@Path("/file")
public class FWPDFToSVGJobConfig {
  private static final String PROPERTY_NAME = "/handywedge-pdftosvg-rest.properties";

  private static final Logger logger = LogManager.getLogger(FWPDFToSVGJobConfig.class);

  final Properties appProperties = new Properties();

  @PostConstruct
  public void initiate() {
    loadProperties();
  }

  private void loadProperties() {
    try {
      InputStream is = FWPDFToSVGJobConfig.class.getResourceAsStream(PROPERTY_NAME);
      InputStreamReader isr = new InputStreamReader(is, "UTF-8");
      BufferedReader reader = new BufferedReader(isr);
      appProperties.load(reader);
      logger.debug("{} loaded", PROPERTY_NAME);
    } catch (IOException e) {
      logger.error("{} loadding error.", PROPERTY_NAME);
    }
  }

  /**
   * 変換ページしきい値
   * 
   * @return 変換ページしきい値
   */
  public int getPageThreshold() {
    logger.info(" [converter.page.threshold: {}]",
        appProperties.getProperty("converter.page.threshold"));
    return NumberUtils.toInt(appProperties.getProperty("converter.page.threshold"));
  }

  /**
   * 変換スレッド数
   * 
   * @return 変換ページしきい値
   */
  public int getMultithreadNumber() {
    logger.info(" [converter.multithread.number: {}]",
        appProperties.getProperty("converter.multithread.number"));
    return NumberUtils.toInt(appProperties.getProperty("converter.multithread.number"));
  }
}
