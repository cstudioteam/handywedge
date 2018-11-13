package com.handywedge.o365.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * 会議状態更新ステータス定義クラス
 * @author cstudioteam
 *
 */
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum UpdateStatusEnum
{
	Reservation("Reservation", "予約"),
	Start("Start", "予約開始"),
	Extension("Extension", "予約延長"),
	End("End", "予約終了"),
	Cancel("Cancel", "予約キャンセル"),
	Change("Change", "予約変更");
	private final String id;
    private final String description;

    private UpdateStatusEnum(final String id, final String description) {
        this.id = id;
        this.description = description;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * Gets a UpdateStatusEnum from id or <tt>null</tt> if the requested type doesn't exist.
     * @param id String
     * @return UpdateStatusEnum
     */
    public static UpdateStatusEnum fromId(final String id) {
        if (id != null) {
            for (UpdateStatusEnum type : UpdateStatusEnum.values()) {
                if (id.equalsIgnoreCase(type.id)) {
                    return type;
                }
            }
        }
        return null;
    }
}
