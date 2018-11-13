package com.handywedge.o365.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 会議予約情報クラス
 *
 * @author cstudioteam
 *
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonClassDescription("MeetingReservInfo")
public class MeetingReservInfo {
	/**
	 * 会議室（SMTPメールアドレス）
	 */
	@JsonProperty(value = "room", required = true)
	private String room;
	/**
	 * 予約者（SMTPメールアドレス）
	 */
	@JsonProperty(value = "subscriber", required = true)
	private String subscriber;

	/**
	 * 会議標題
	 */
	@JsonProperty(value = "subject", required = true)
	private String Subject;

	/**
	 * 会議開始時間
	 */
	@JsonProperty(value = "startTime")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "JST")
	private String startTime;
	/**
	 * 会議終了時間
	 */
	@JsonProperty(value = "endTime")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "JST")
	private String endTime;
}
