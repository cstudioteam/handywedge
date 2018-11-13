
package com.handywedge.o365.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum MeetingFreeBusyStatusEnum {
	Free("Free", "利用空き"),
	Wait("Wait", "利用待ち"),
	Busy("Busy", "利用中");

	private final String id;
    private final String description;

    private MeetingFreeBusyStatusEnum(final String id, final String description) {
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
     * Gets a MeetingFreeBusyStatusEnum from id or <tt>null</tt> if the requested type doesn't exist.
     * @param id String
     * @return MyEnumType
     */
    public static MeetingFreeBusyStatusEnum fromId(final String id) {
        if (id != null) {
            for (MeetingFreeBusyStatusEnum type : MeetingFreeBusyStatusEnum.values()) {
                if (id.equalsIgnoreCase(type.id)) {
                    return type;
                }
            }
        }
        return null;
    }
 }
