package jp.cstudio.handywedge.test.app.common.view;

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

import com.handywedge.context.FWContext;
import com.handywedge.log.FWLogger;

import jp.cstudio.handywedge.test.app.log.AppLogReader;
import jp.cstudio.handywedge.test.app.log.ErrorLogReader;
import jp.cstudio.handywedge.test.app.role.Role;
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
  private Role role;

  @Getter
  private MenuModel model;

  private String lang;

  @PostConstruct
  public void init() {

    long startTime = logger.perfStart("init");
    model = new DefaultMenuModel();

    role = Role.toEnum(ctx.getUser().getRole());
    // ログは実験menu
    DefaultSubMenu menu;
    DefaultMenuItem item;
    if (role == Role.ADMIN) {
      menu = new DefaultSubMenu("ログ（管理者メニュー）");
      item = new DefaultMenuItem("アプリケーションログ");
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
    }

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

    menu = new DefaultSubMenu("PDF");
    item = new DefaultMenuItem("請求書サンプル");
    item.setOutcome("/contents/report/pdf/seikyusho.xhtml");
    item.setIcon("fa fa-file-pdf-o");
    menu.addElement(item);
    model.addElement(menu);

    menu = new DefaultSubMenu("暗号化");
    item = new DefaultMenuItem("文字列");
    item.setOutcome("/contents/crypto/crypto.xhtml");
    item.setIcon("fa fa-key");
    menu.addElement(item);
    model.addElement(menu);

    menu = new DefaultSubMenu("メール");
    item = new DefaultMenuItem("メール送信");
    item.setOutcome("/contents/mail/mail.xhtml");
    item.setIcon("fa fa-envelope-o");
    menu.addElement(item);
    model.addElement(menu);

    menu = new DefaultSubMenu("ワークフロー");
    item = new DefaultMenuItem("ワークフローサンプル");
    item.setOutcome("/contents/workflow/workflowView.xhtml");
    item.setIcon("fa fa-users");
    menu.addElement(item);
    model.addElement(menu);

    // 実験menu
    new Thread(new AppLogReader(1000)).start();
    new Thread(new ErrorLogReader(5000)).start();
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
