package jp.cstudio.csfw.user;

import java.io.Serializable;
import java.util.Locale;

public interface FWUser extends Serializable {

    String getId();

    String getName();

    Locale getLanguage();

    void setLanguage(Locale language);

}
