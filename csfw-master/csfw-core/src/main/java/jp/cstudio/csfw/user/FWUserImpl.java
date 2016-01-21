package jp.cstudio.csfw.user;

import java.util.Locale;

import javax.enterprise.context.Dependent;

import jp.cstudio.csfw.user.FWFullUser;
import lombok.Data;

@Data
@Dependent
public class FWUserImpl implements FWFullUser {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private Locale language;

}
