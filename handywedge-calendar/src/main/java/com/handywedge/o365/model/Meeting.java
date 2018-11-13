package com.handywedge.o365.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 会議情報クラス
 *
 * @author cstudioteam
 *
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonClassDescription("Meeting")
public class Meeting {
	/**
	 * 会議ID(予約者側)
	 */
	@JsonProperty(value = "id", required = true)
	private String Id;
	/**
	 * 会議標題
	 */
	@JsonProperty(value = "subject")
	private String Subject;
	/**
	 * 会議室
	 */
	@JsonProperty(value = "location")
	private String location;
	/**
	 * 会議開始時間
	 */
	@JsonProperty(value = "startTime")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "JST")
	private Date startTime;
	/**
	 * 会議終了時間
	 */
	@JsonProperty(value = "endTime")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "JST")
	private Date endTime;
	/**
	 * 会議主催者
	 */
	@JsonProperty(value = "organizer")
	private UserInfo organizer;
	/**
	 * 必須参加者
	 */
	@JsonProperty(value = "requiredAttendees")
	private List<UserInfo> requiredAttendees = new ArrayList<UserInfo>();
	/**
	 * 会議任意参加者(予備)
	 */
	@JsonProperty(value = "optionalAttendees")
	private List<UserInfo> optionalAttendees = new ArrayList<UserInfo>();

	/**
	 * リソース（会議室）(予備)
	 */
	@JsonProperty(value = "resources")
	private List<RoomInfo> resources = new ArrayList<RoomInfo>();

	/**
	 * 会議延長時間(mm分)
	 */
	@JsonProperty(value = "extensionTime")
	@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
	private int extensionTime;

	/**
	 * 会議か否か
	 */
	@JsonProperty(value = "isMeeting")
	@JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
	private boolean isMeeting = false;

	/**
	 * 会議キャンセルか否か
	 */
	@JsonProperty(value = "isCancelled")
	@JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
	private boolean isCancelled = false;

	/**
	 *  非公開
	 * Normal : 通常
	 * Personal : 個人
	 * Private : プライベート
	 * Confidential : 機密
	 */
	@JsonProperty(value = "sensibility", defaultValue = "Normal")
//	@JsonDeserialize(using = SensibilityEnum.class)
	private String Sensibility;
}
