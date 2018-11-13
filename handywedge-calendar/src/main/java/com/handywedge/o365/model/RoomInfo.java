
package com.handywedge.o365.model;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonClassDescription("RoomInfo")
/**
 * 会議室情報クラス
 * @author cstudioteam
 *
 */
public class RoomInfo extends EmailInfo {
	/**
	 * コンストラクター
	 */
	public RoomInfo(String name, String emailAddress) {
		super(name, emailAddress);
	}
}
