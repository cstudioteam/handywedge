package com.handywedge.pushnotice.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Property {

  protected static final String REGEX = "#\\{.+?\\}";
  protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle("hw-pushnotice");

  protected static Logger logger = LogManager.getLogger("PushService");


  static {
    Enumeration<String> e = BUNDLE.getKeys();
    while (e.hasMoreElements()) {
      String key = e.nextElement();
      logger.info("[Property] " + key + ": [" + BUNDLE.getString(key) + "]");
    }
  }


  public static String get(String key) {

    String retVal = getProperty(key);

    return retVal;
  }

  public static int getInt(String key) {

    int retVal = 0;

    String s = getProperty(key);
    if (s != null && !s.isEmpty()) {
      retVal = Integer.parseInt(s);
    }

    return retVal;
  }

  public static long getLong(String key) {

    long retVal = 0L;

    String s = getProperty(key);
    if (s != null && !s.isEmpty()) {
      retVal = Long.parseLong(s);
    }

    return retVal;
  }

  public static boolean getBool(String key) {

    boolean retVal = false;

    String s = getProperty(key);
    if (s != null && !s.isEmpty()) {
      retVal = Boolean.parseBoolean(s);
    }

    return retVal;
  }

  private static String getProperty(String key) {

    String retVal = null;

    if (BUNDLE.containsKey(key)) {
      retVal = BUNDLE.getString(key);
      List<String> list = new ArrayList<>();
      Pattern pattern = Pattern.compile(REGEX);
      Matcher matcher = pattern.matcher(retVal);
      while (matcher.find()) {
        list.add(matcher.group(0));
      }
      for (int i = 0; i < list.size(); i++) {
        String subKey = list.get(i);
        subKey = subKey.substring(2, subKey.length() - 1);
        retVal = retVal.replaceAll(Pattern.quote(list.get(i)), get(subKey));
      }
    }

    return retVal;
  }
}
