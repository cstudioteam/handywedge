package jp.cstudio.csframe.test.app.config;

import java.io.Serializable;
import java.util.Locale;
import java.util.Set;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.csframe.config.FWMessageResources;

@ViewScoped
@Named
public class MessageResourcesAction implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private FWMessageResources resources;


  public Set<String> getKeySet() {

    return resources.keySet();
  }

  public String getValue(String key) {

    return resources.get(key);
  }

  public Set<String> getJPKeySet() {

    return resources.keySet(Locale.JAPAN);
  }

  public String getJPValue(String key) {

    return resources.get(key, Locale.JAPAN);
  }

  public Set<String> getUSKeySet() {

    return resources.keySet(Locale.US);
  }

  public String getUSValue(String key) {

    return resources.get(key, Locale.US);
  }
}
