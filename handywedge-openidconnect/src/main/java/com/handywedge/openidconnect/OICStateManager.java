/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.openidconnect;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author takeuchi
 */
public class OICStateManager {

  private static final Map<String, OICState> STATE_HOLDER = new ConcurrentHashMap<>();
  private static final long REFRESH_SIZE =
      OICProperties.getLong(OICConst.STATE_HOLDER_REFRESH_SIZE);
  private static final long HOLD_TERM = OICProperties.getLong(OICConst.STATE_HOLDER_TERM_SEC);

  public static OICState createState(String url) {

    OICState state = new OICState(url);
    STATE_HOLDER.put(state.getId(), state);
    if (REFRESH_SIZE > 0 && STATE_HOLDER.size() > REFRESH_SIZE) {
      refresh(HOLD_TERM);
    }

    return state;
  }

  public static OICState getState(String id) {

    return STATE_HOLDER.get(id);
  }

  public static void remove(String id) {

    STATE_HOLDER.remove(id);
  }

  public static void clear() {

    STATE_HOLDER.clear();
  }

  public static void refresh(long term) {

    long xTime = System.currentTimeMillis() - (term * 1000);

    Iterator<String> i = STATE_HOLDER.keySet().iterator();
    while (i.hasNext()) {
      String id = i.next();
      OICState s = STATE_HOLDER.get(id);
      if (s.getCreateTime() < xTime) {
        STATE_HOLDER.remove(id);
      }
    }
  }
}
