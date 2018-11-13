package com.handywedge.o365.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;


/**
 * 会議室グループクラス
 * @author cstudioteam
 *
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonClassDescription("GroupFreeBusy")
public class GroupFreeBusy extends EmailInfo {
	/**
	 * 場所
	 */
	@JsonProperty("location")
	private String location;

	/**
	 * 会議室一覧
	 */
	@JsonProperty("roomFreeBusies")
	private List<RoomFreeBusy> roomFreeBusies;

	/**
	 * コンストラクター
	 */
	public GroupFreeBusy(String name, String emailAddress) {
		super(name, emailAddress);
	}
}
