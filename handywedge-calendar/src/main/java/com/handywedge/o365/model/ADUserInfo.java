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
@JsonClassDescription("ADUserInfo")
public class ADUserInfo extends EmailInfo{
    /**
     * 姓
     */
	@JsonProperty("surName")
	@JsonPropertyDescription("姓")
	private String surName;

	/**
	 * 名
	 */
	@JsonProperty("givenName")
	@JsonPropertyDescription("名")
	private String givenName;

	/**
	 * 姓　名（読み）
	 */
	@JsonProperty("phoneticName")
	@JsonPropertyDescription("姓　名（読み）")
	private String phoneticName;

	/**
	 * 姓（読み）
	 */
	@JsonProperty("phoneticFirstName")
	@JsonPropertyDescription("姓（読み）")
	private String phoneticFirstName;

	/**
	 * 名（読み)
	 */
	@JsonProperty("phoneticLastName")
	@JsonPropertyDescription("名（読み)")
	private String phoneticLastName;

	/**
	 * 電話番号
	 */
	@JsonProperty("phoneNumber")
	@JsonPropertyDescription("電話番号")
	private String phoneNumber;

	/**
	 * 事務所場所
	 */
	@JsonProperty("location")
	@JsonPropertyDescription("事務所場所")
	private String location;

	/**
	 * 部署
	 */
	@JsonProperty("department")
	@JsonPropertyDescription("部署")
	private String department;

	/**
	 * コンストラクター
	 */
	public ADUserInfo() {
		super();
	}

	/**
	 * コンストラクター
	 */
	public ADUserInfo(String name, String emailAddress) {
		super(name, emailAddress);
	}
}
