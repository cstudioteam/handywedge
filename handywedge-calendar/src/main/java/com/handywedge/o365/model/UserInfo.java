
package com.handywedge.o365.model;


import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonClassDescription("UserInfo")
public class UserInfo extends EmailInfo {
	/**
	 * コンストラクター
	 */
	public UserInfo(String name, String emailAddress) {
		super(name, emailAddress);
	}
}