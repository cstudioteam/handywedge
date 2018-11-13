package com.handywedge.o365.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handywedge.o365.model.Meeting;
import com.handywedge.o365.model.MeetingFreeBusy;
import com.handywedge.o365.model.MeetingFreeBusyStatusEnum;
import com.handywedge.o365.model.PeriodType;
import com.handywedge.o365.model.RoomFreeBusy;
import com.handywedge.o365.model.RoomInfo;
import com.handywedge.o365.service.EWSMApiService;
import com.handywedge.o365.util.DateTimeCalculat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

@Path("/api/room")
@Produces({ "application/json" })
@Server(description = "Dev(localhost)",
        url = "http://localhost:8080/handywedge-calendar/api")
public class RoomResource{
	private static final Logger logger = LoggerFactory.getLogger(RoomResource.class);
	private EWSMApiService service = new EWSMApiService();

	@GET
	@Path("/FreeBusyTime")
	@Consumes({ "application/json" })
	@Operation(summary = "会議室利用状況確認", tags = { "Room" }, responses = {
			@ApiResponse(description = "会議室利用状況一覧情報", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RoomFreeBusy.class)))),
			@ApiResponse(responseCode = "400", description = "Invalid parameter supplied"),
			@ApiResponse(responseCode = "401", description = "Authentication error"),
			@ApiResponse(responseCode = "404", description = "Room not found") })
	public Response FreeBusyTime(
			@Parameter(description = "空き時間問い合わせ会議室グループ", required = false) @QueryParam("group") String group,
			@Parameter(description = "空き時間問い合わせ会議室", required = true) @QueryParam("room") String room,
			@Parameter(description = "期間種類", required = true) @DefaultValue("Today") @QueryParam("period") String period) {
		logger.info("[START] {0} controller={1}, action={2}, queryString={3}", 0, 1);

		List<RoomFreeBusy> rFreeBusyList = new ArrayList<RoomFreeBusy>();
		int duration = 60; // 30分間隔で取得(30~1440)

		DateTime start = DateTime.now();
		if (PeriodType.Current.equals(period)) {
			// 現在時刻を挟む1日分を取得
			start = DateTimeCalculat.roundDown(DateTime.now(), duration);
		} else {
			// 本日０時を取得
			start = DateTimeCalculat.getDayTime(DateTime.now());
		}
		DateTime end = DateTimeCalculat.roundDown(DateTime.now().plusDays(1), duration);

		try {
			rFreeBusyList = service.getFreeBusyTime(room, duration, start, end);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return Response.status(Status.FORBIDDEN).build();
		}

		final Date startDate = start.toDate();
		final Date endDate = end.toDate();
		List<RoomFreeBusy> newFreeBusy = rFreeBusyList.stream()
				.filter(r -> r != null && r.getMeetingFreeBusies() != null).map(r -> {
					RoomFreeBusy rfb = new RoomFreeBusy();
					rfb.setName(r.getName());
					rfb.setEmailAddress(r.getEmailAddress());
					rfb.setDuration(r.getDuration());
					rfb.setStartTime(startDate);
					rfb.setEndTime(endDate);
					rfb.setMeetingFreeBusies(r.getMeetingFreeBusies().stream()
							.filter(f -> (f != null) && (f.getStartTime().getTime() <= startDate.getTime())
									&& (f.getEndTime().getTime() >= endDate.getTime()))
							.map(f -> f).collect(Collectors.toList()));
					return rfb;
				}).collect(Collectors.toList());

		List<RoomFreeBusy> resultFreeBusyList = new ArrayList<RoomFreeBusy>();
		for (RoomFreeBusy roomFreeBusy : newFreeBusy) {
			List<MeetingFreeBusy> tmpMeetingFreeBusies = new ArrayList<MeetingFreeBusy>();
			DateTime tmpStart = start;
			DateTime tmpEnd = end;

			// ------------------------------
			// 会議室の会議予約情報を取得する。
			// ------------------------------
			List<RoomInfo> resources = new ArrayList<RoomInfo>();
			resources.add(new RoomInfo(roomFreeBusy.getName(), roomFreeBusy.getEmailAddress()));

			Meeting mtg = new Meeting();
			mtg.setResources(resources);
			mtg.setStartTime(start.toDate());
			mtg.setEndTime(end.toDate());

			List<Meeting> roomMeetings = service.getRoomMeetingList(mtg);

			for (MeetingFreeBusy meeting : roomFreeBusy.getMeetingFreeBusies()) {
				MeetingFreeBusy tmpMeetFreeBusy = new MeetingFreeBusy();
				tmpMeetFreeBusy.setStatus(MeetingFreeBusyStatusEnum.Busy.name());
				tmpMeetFreeBusy.setStartTime(meeting.getStartTime());
				tmpMeetFreeBusy.setEndTime(meeting.getEndTime());

				Meeting meet = roomMeetings.stream()
						.filter(rm -> rm != null
								&& rm.getStartTime().getTime() == tmpMeetFreeBusy.getStartTime().getTime()
								&& rm.getEndTime().getTime() == tmpMeetFreeBusy.getEndTime().getTime())
						.map(rm -> rm).findFirst().orElse(new Meeting());

				if (meet != null && !StringUtils.isEmpty(meet.getId())) {
					tmpMeetFreeBusy.setId(meet.getId());
					tmpMeetFreeBusy.setSubject(meet.getSubject());
					tmpMeetFreeBusy.setLocation(meet.getLocation());
					tmpMeetFreeBusy.setOrganizer(meet.getOrganizer());
					tmpMeetFreeBusy.setSensibility(meet.getSensibility());
					tmpMeetFreeBusy.setRequiredAttendees(meet.getRequiredAttendees());
					tmpMeetFreeBusy.setOptionalAttendees(meet.getOptionalAttendees());
					tmpMeetFreeBusy.setResources(meet.getResources());

					tmpMeetingFreeBusies.add(tmpMeetFreeBusy);
				}

				roomMeetings.remove(meet);
				tmpStart = new DateTime(meeting.getEndTime());
			}

			// 新しい利用状況で置換え
			roomFreeBusy.getMeetingFreeBusies().addAll(tmpMeetingFreeBusies);
			resultFreeBusyList.add(roomFreeBusy);
		}

		logger.info("[E N D] controller={0}, action={1}, elapsed time={2}", 0, 1);
		return Response.ok().entity(resultFreeBusyList).build();

	}
}
