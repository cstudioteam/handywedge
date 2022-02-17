package jp.cstudio.handywedge.test.app.common.view;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import com.handywedge.context.FWContext;
import com.handywedge.log.FWLogger;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
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

    long startTime = logger.perfStart("init");
    model = new DefaultMenuModel();

    // ログは実験menu
    DefaultSubMenu menu1 = DefaultSubMenu.builder().label("データベース").build();
    DefaultMenuItem item1 = DefaultMenuItem.builder().value("マスタ参照")
        .outcome("/contents/db/master.xhtml").icon("fa fa-database").build();
    menu1.getElements().add(item1);
    model.getElements().add(menu1);

    DefaultSubMenu menu2 = DefaultSubMenu.builder().label("プロパティ").build();
    DefaultMenuItem item2 = DefaultMenuItem.builder().value("プロパティ一覧")
        .outcome("/contents/config/resources.xhtml").icon("fa fa-wrench").build();
    menu2.getElements().add(item2);
    model.getElements().add(menu2);

    DefaultSubMenu menu3 = DefaultSubMenu.builder().label("PDF").build();
    DefaultMenuItem item3 = DefaultMenuItem.builder().value("請求書サンプル")
        .outcome("/contents/report/pdf/seikyusho.xhtml").icon("fa fa-file-pdf-o").build();
    menu3.getElements().add(item3);
    model.getElements().add(menu3);

    DefaultSubMenu menu4 = DefaultSubMenu.builder().label("暗号化").build();
    DefaultMenuItem item4 = DefaultMenuItem.builder().value("文字列")
        .outcome("/contents/crypto/crypto.xhtml").icon("fa fa-key").build();
    menu4.getElements().add(item4);
    model.getElements().add(menu4);

    DefaultSubMenu menu5 = DefaultSubMenu.builder().label("メール").build();
    DefaultMenuItem item5 = DefaultMenuItem.builder().value("メール送信")
        .outcome("/contents/mail/mail.xhtml").icon("fa fa-envelope-o").build();
    menu5.getElements().add(item5);
    model.getElements().add(menu5);


    DefaultSubMenu menu6 = DefaultSubMenu.builder().label("ワークフロー").build();
    DefaultMenuItem item6 = DefaultMenuItem.builder().value("ワークフローサンプル")
        .outcome("/contents/workflow/workflowView.xhtml").icon("fa fa-users").build();
    menu6.getElements().add(item6);
    DefaultMenuItem item7 = DefaultMenuItem.builder().value("ワークフローサンプル2")
        .outcome("/contents/workflow2/workflow2View.xhtml").icon("fa fa-users").build();
    menu6.getElements().add(item7);
    model.getElements().add(menu6);

    // 実験menu
    // new Thread(new AppLogReader(1000)).start();
    // new Thread(new ErrorLogReader(5000)).start();
    logger.perfEnd("init", startTime);
  }

  public String setting() {

    System.out.println("設定");
    return "";
  }

  public List<String> getLanguages() {
    return Arrays.asList("JP", "US");
  }

  private void setLanguage(String val) {
    logger.debug("setLocale val=" + val);
    switch (val) {
      case "JP":
        ctx.getUser().setLocale(Locale.JAPAN);
        break;
      case "US":
        ctx.getUser().setLocale(Locale.US);
        break;
      default:
        ctx.getUser().setLocale(Locale.getDefault());
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
    return ctx.getUser().getLocale().getCountry();
  }
}
