package com.handywedge.calendar.Office365.rest.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * freeBusyStatus:
 *  * free      - 空き時間
 *  * tentative - 仮の予定
 *  * busy      - ビジー
 *  * oof       - 外出中
 *  * workingElsewhere - 別の場所で作業
 */
public enum FreeBusyStatusEnum {
    free("free"),
    tentative("tentative"),
    busy("busy"),
    oof("oof"),
    workingElsewhere("workingElsewhere");

//    UNKNOWN("unknown");

    private String value;

    FreeBusyStatusEnum(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static FreeBusyStatusEnum fromValue(String text) {
        for (FreeBusyStatusEnum b : FreeBusyStatusEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}