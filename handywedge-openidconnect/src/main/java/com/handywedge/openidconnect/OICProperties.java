package com.handywedge.openidconnect;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OICProperties {

    private static final String REGEX = "#\\{.+?\\}";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("openid-connect");
    private static final Logger LOGGER = LoggerFactory.getLogger(OICProperties.class);


    public static String get(String key) {

        if (BUNDLE.containsKey(key)) {
            String val = BUNDLE.getString(key);
            LOGGER.trace("get key:[" + key + "]["+ val + "]");
            List<String> list = new ArrayList<>();
            Pattern pattern = Pattern.compile(REGEX);
            Matcher matcher = pattern.matcher(val);
            while (matcher.find()) {
                list.add(matcher.group(0));
            }
            for(int i = 0; i < list.size(); i++) {
                String subKey = list.get(i);
                subKey = subKey.substring(2, subKey.length() - 1);
                val = val.replaceAll(Pattern.quote(list.get(i)), get(subKey));
                LOGGER.trace("replace:[" + key + "]["+ val + "]");
            }

            return val;
	} else {
            LOGGER.debug("get key:[" + key + "][null]");
            return null;
	}
    }

    public static int getInt(String key) {

        int retVal = 0;
        
        String s = get(key);
        if (s != null && !s.isEmpty()) {
            retVal = Integer.parseInt(s);
	}
        
        return retVal;
    }

    public static long getLong(String key) {

        long retVal = 0L;
        
        String s = get(key);
        if (s != null && !s.isEmpty()) {
            retVal = Long.parseLong(s);
	}
        
        return retVal;
    }
}
