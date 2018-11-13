package com.handywedge.o365.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonClassDescription("SendMailInfo")
/**
 * メール送信サービス
 * @author cstudioteam
 *
 */
public class SendMailInfo {

	/**
	 * 差出人（SMTPメールアドレス）
	 */
	@JsonProperty(value = "from", required = true)
	private String from;

	/**
	 * 宛先一覧（SMTPメールアドレス）
	 */
	@JsonProperty(value = "recipients", required = true)
	private List<String> recipients;

	/**
	 * メール件名
	 */
	@JsonProperty(value = "subject")
	private String subject;

	/**
	 * メール種類（0:HTML、1:TEXT）
	 */
	@JsonProperty(value = "type")
	private BodyType type;

	/**
	 * メール本文
	 */
	@JsonProperty(value = "body")
	private String body;

}
