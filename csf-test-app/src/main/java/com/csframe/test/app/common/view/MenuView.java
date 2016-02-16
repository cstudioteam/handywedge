package com.csframe.test.app.common.view;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import com.csframe.context.FWContext;
import com.csframe.log.FWLogger;
import com.csframe.test.app.log.AppLogReader;
import com.csframe.test.app.log.ErrorLogReader;

import lombok.Getter;

@ViewScoped
@Named
public class MenuView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private transient FWLogger logger;

  @Getter
  @Inject
  private FWContext ctx;

  @Getter
  private MenuModel model;

  private String lang;

  @PostConstruct
  public void init() {

    model = new DefaultMenuModel();

    DefaultSubMenu menu = new DefaultSubMenu("ログ");
    DefaultMenuItem item = new DefaultMenuItem("アプリケーションログ");
    item.setIcon("fa fa-file-text");
    item.setOutcome("/contents/log/app_log.xhtml");
    menu.addElement(item);

    item = new DefaultMenuItem("サーバログ");
    item.setIcon("fa fa-file-text");
    menu.addElement(item);

    item = new DefaultMenuItem("エラーログ");
    item.setIcon("fa fa-ban");
    item.setOutcome("/contents/log/error_log.xhtml");
    menu.addElement(item);
    model.addElement(menu);

    menu = new DefaultSubMenu("データベース");
    item = new DefaultMenuItem("マスタ参照");
    item.setOutcome("/contents/db/master.xhtml");
    item.setIcon("fa fa-database");
    menu.addElement(item);
    model.addElement(menu);

    menu = new DefaultSubMenu("プロパティ");
    item = new DefaultMenuItem("プロパティ一覧");
    item.setOutcome("/contents/config/resources.xhtml");
    item.setIcon("fa fa-wrench");
    menu.addElement(item);
    model.addElement(menu);

    new Thread(new AppLogReader(1000)).start();
    new Thread(new ErrorLogReader(5000)).start();
  }

  public String setting() {

    System.out.println("設定");
    return "";
  }

  public String logout() {

    System.out.println("ログアウト");
    return "";
  }

  public List<String> getLanguages() {
    return Arrays.asList("JP", "US");
  }

  private void setLanguage(String val) {
    logger.debug("setLocale val=" + val);
    switch (val) {
      case "JP":
        ctx.getUser().setLanguage(Locale.JAPAN);
        break;
      case "US":
        ctx.getUser().setLanguage(Locale.US);
        break;
      default:
        ctx.getUser().setLanguage(Locale.getDefault());
        break;
    }
  }

  public void change() {

    setLanguage(lang);
  }

  public void setLang(String lang) {

    this.lang = lang;
  }

  public String getLang() {
    return ctx.getUser().getLanguage().getCountry();
  }
}
