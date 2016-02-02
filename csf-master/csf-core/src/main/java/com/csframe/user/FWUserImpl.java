package com.csframe.user;

import java.util.Locale;

import javax.enterprise.context.Dependent;

import com.csframe.user.FWFullUser;

import lombok.Data;

@Data
@Dependent
public class FWUserImpl implements FWFullUser {

  private static final long serialVersionUID = 1L;

  private String id;
  private String name;
  private Locale language;

}
