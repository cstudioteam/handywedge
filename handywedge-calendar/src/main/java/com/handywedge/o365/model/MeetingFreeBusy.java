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
@JsonClassDescription("MeetingFreeBusy")
/**
 * 会議室利用状況情報クラス
 *
 * @author cstudioteam
 */
public class MeetingFreeBusy extends Meeting {
	/**
	 * ステータス
	 */
	@JsonProperty(value = "status", required = true)
	private String status;
}
