package com.csframe.test.app.common.view;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import com.csframe.test.app.log.AppLogReader;
import com.csframe.test.app.log.ErrorLogReader;

import lombok.Getter;

@ApplicationScoped
@Named
public class MenuView {

  @Getter
  private MenuModel model;

  @PostConstruct
  public void init() {

    model = new DefaultMenuModel();

    DefaultSubMenu menu = new DefaultSubMenu("ログ");

    DefaultMenuItem item = new DefaultMenuItem("アプリケーションログ");
    item.setIcon("fa fa-file-text");
    item.setUrl("/contents/log/app_log.xhtml");
    menu.addElement(item);

    item = new DefaultMenuItem("サーバログ");
    item.setIcon("fa fa-file-text");
    menu.addElement(item);

    item = new DefaultMenuItem("エラーログ");
    item.setIcon("fa fa-ban");
    item.setUrl("/contents/log/error_log.xhtml");
    menu.addElement(item);

    model.addElement(menu);

    menu = new DefaultSubMenu("データベース");

    item = new DefaultMenuItem("マスタ参照");
    item.setUrl("/contents/db/master.xhtml");
    item.setIcon("fa fa-database");
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

}
