
package com.handywedge.o365.model;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonClassDescription("Eメールアドレス情報")
public class EmailInfo {

	/**
	 * 名前
	 */
	@JsonProperty("name")
	@JsonPropertyDescription("名前")
	private String name;

	/**
	 * Eメールアドレス
	 */
	@JsonProperty("emailAddress")
	@JsonPropertyDescription("メールアドレス")
	private String emailAddress;

	/**
	 * コンストラクター
	 */
	public EmailInfo(String name, String emailAddress) {
		this.name = name;
		this.emailAddress = emailAddress;
	}
	/**
	 * コンストラクター
	 */
	public EmailInfo() {
	}
}
