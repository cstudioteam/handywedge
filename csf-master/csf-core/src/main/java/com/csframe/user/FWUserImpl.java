package com.csframe.user;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;

import lombok.Data;

@Data
@SessionScoped
public class FWUserImpl implements FWFullUser {

  private static final long serialVersionUID = 1L;

  private String id;
  private String name;
  private Locale language;

  @PostConstruct
  public void init() {
    language = Locale.getDefault();
  }

}
