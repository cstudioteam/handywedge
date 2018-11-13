package com.handywedge.o365.model;

import java.util.List;

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
@JsonClassDescription("RoomGroupInfo")
/**
 * 会議室グループ情報クラス
 * @author cstudioteam
 *
 */
public class RoomGroupInfo extends EmailInfo {
	/**
	 * 場所
	 */
	@JsonProperty(value = "location")
	private String location;

	/**
	 * 会議室一覧
	 */
	@JsonProperty(value = "rooms")
	private List<RoomInfo> rooms;

	/**
	 * コンストラクター
	 */
	public RoomGroupInfo(String name, String emailAddress) {
		super(name, emailAddress);
	}
}
