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
/**
 * 期間種類
 *
 * @author cstudioteam
 *
 */
@JsonClassDescription("PeriodType")
public class PeriodType {
	/**
	 * 本日データ
	 */
	public static String Today = "today";

	/**
	 * 現在時刻よりデータ
	 */
	public static String Current = "current";

}