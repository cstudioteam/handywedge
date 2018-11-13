package com.handywedge.o365.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonClassDescription("RoomFreeBusy")
/**
 * 会議室の利用状況情報クラス
 *
 * @author cstudioteam
 *
 */
public class RoomFreeBusy extends EmailInfo {
	/**
	 * コンテント
	 */
	@JsonProperty(value = "content")
	private String content;

//	/**
//	 * コンストラクタ
//	 */
//	public RoomFreeBusy() {
//	}
//
//	/**
//	 * コンストラクタ
//	 *
//	 * @param content
//	 *            Json型の会議利用状況
//	 */
//	public RoomFreeBusy(String content) {
//		this.content = content;
//	}

	/**
	 * コンストラクター
	 */
	public RoomFreeBusy(String name, String emailAddress) {
		super(name, emailAddress);
	}

	/**
	 * コンストラクター
	 */
	public RoomFreeBusy() {
		super();
	}
	/**
	 * 利用状況
	 */
	@JsonProperty(value = "status", required = true)
	private String status;

	/**
	 * 間隔（単位：分）
	 */
	@JsonProperty(value = "duration")
	@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
	private int duration;

	/**
	 * 空き時間問い合わせ区間開始時刻
	 */
	@JsonProperty(value = "startTime")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "JST")
	private Date startTime;
	/**
	 * 空き時間問い合わせ区間終了時刻
	 */
	@JsonProperty(value = "endTime")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "JST")
	private Date endTime;

	/**
	 * 利用状況
	 */
	@JsonProperty(value = "meetingFreeBusies")
	private List<MeetingFreeBusy> meetingFreeBusies;

}
