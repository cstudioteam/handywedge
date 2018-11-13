package com.handywedge.o365.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonClassDescription("MeetingCancel")
/**
 * 会議室キャンセル情報クラス
 * @author cstudioteam
 *
 */
public class MeetingCancel extends Meeting {
	/**
	 * 会議イベントにあわせて送信するメッセージ
	 */
	@JsonProperty(value = "message")
	private String message;

	/**
	 * 会議強制キャンセル
	 */
	@JsonProperty(value = "forced", required = true, defaultValue = "false")
	private Boolean forced;
}
