/**
 *  Copyright 2016 SmartBear Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.handywedge.o365.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handywedge.o365.exception.ApiException;
import com.handywedge.o365.model.Meeting;
import com.handywedge.o365.model.MeetingCancelInfo;
import com.handywedge.o365.model.MeetingEndInfo;
import com.handywedge.o365.model.MeetingExtensionInfo;
import com.handywedge.o365.model.MeetingReservInfo;
import com.handywedge.o365.model.MeetingStartInfo;
import com.handywedge.o365.model.RoomInfo;
import com.handywedge.o365.model.UserInfo;
import com.handywedge.o365.service.EWSMApiService;
import com.handywedge.o365.util.DateTimeCalculat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

@Path("/api/meeting")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@Server(description = "Dev(localhost)",
        url = "http://localhost:8080/handywedge-calendar/api")
public class MeetingResource {
	private static final Logger logger = LoggerFactory.getLogger(MeetingResource.class);
	private EWSMApiService service = new EWSMApiService();

	@POST
	@Path("/reservation")
	@Operation(summary = "会議を予約する", tags = { "Meeting" }, responses = {
			@ApiResponse(responseCode = "201", description = "Send mail Successed"),		// TODO
			@ApiResponse(responseCode = "400", description = "Invalid MeetingReservInfo supplied"),
			@ApiResponse(responseCode = "404", description = "EmailAddress not found"),
			@ApiResponse(responseCode = "415", description = "HTTP 415 Unsupported Media Type")})
	public Response reservation(
			@RequestBody(
					description = "会議予約情報", required = true,
					content = @Content( schema = @Schema(implementation = MeetingReservInfo.class))) final MeetingReservInfo reservInfo)
			throws ApiException {
		logger.info("[START] {} controller={}, action={}, queryString={}", 0, 1);

		Meeting upMeeting = new Meeting();
        upMeeting.setSubject(reservInfo.getSubject());

        DateTimeFormatter dtFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        DateTime start = DateTime.parse(reservInfo.getStartTime(), dtFormat);
        DateTime end = DateTime.parse(reservInfo.getEndTime(), dtFormat);
        upMeeting.setStartTime(start.toDate());
        upMeeting.setEndTime(end.toDate());

        if( start.getMillis() > end.getMillis() ){
            logger.error ("パラメータ不正。StartTime={}, EndTime={}",reservInfo.getStartTime(), reservInfo.getEndTime());
            return Response.status(Status.BAD_REQUEST).entity("パラメータ不正").build();
        }

        upMeeting.setOrganizer(getUserInfo(reservInfo.getSubscriber()));
        List<RoomInfo> rmInfos = new ArrayList<RoomInfo>();
        rmInfos.add(getRoomInfo(reservInfo.getRoom()));
        upMeeting.getResources().addAll(rmInfos);

        // 会議室がすでに予約中かチェック
        List<Meeting> roomUesdMeeting =  null;
        try{
            roomUesdMeeting = service.getRoomMeetingList(upMeeting);
        }catch (Exception e){
            logger.error (e.getMessage());
            return Response.status(Status.SERVICE_UNAVAILABLE).entity("サービスエラー").build();
        }

        if( roomUesdMeeting != null && roomUesdMeeting.isEmpty() ){
            String subMessage = String.format("Room={}, StartTime={}, EndTime={}",
            		reservInfo.getRoom(), reservInfo.getStartTime(), reservInfo.getEndTime());
            logger.error( "The meeting room is in use. {}", subMessage);

            return Response.status(Status.NOT_FOUND).entity("未検出エラー").build();
        }
        // 会議予約処理
        Meeting resultMeeting = new Meeting();
        try{
            service.reservationMeeting(upMeeting);
        }
        catch (Exception e){
        	     logger.error (e.getMessage());
            return Response.status(Status.SERVICE_UNAVAILABLE).entity("予約エラー").build();
        }

		logger.info("[E N D] controller={}, action={}, elapsed time={}", 0, 1);
		return Response.ok().entity(resultMeeting).build();
	}


	@POST
	@Path("/start")
//	@Consumes({ "application/json" })
//	@Produces({ "application/json" })
	@Operation(summary = "会議を利用開始する", tags = { "Meeting" }, responses = {
			@ApiResponse(responseCode = "201", description = "Metting start Successed"),	//Todo
			@ApiResponse(responseCode = "400", description = "Invalid MeetingReservInfo supplied"),
			@ApiResponse(responseCode = "404", description = "EmailAddress not found"),
			@ApiResponse(responseCode = "415", description = "HTTP 415 Unsupported Media Type")})
	public Response start(
			@RequestBody(
					description = "会議開始情報", required = true,
					content = @Content( schema = @Schema(implementation = MeetingStartInfo.class))) final MeetingStartInfo startInfo)
			throws ApiException {
		logger.info("[START] {} controller={}, action={}, queryString={}", 0, 1);

		Meeting srhMeeting = new Meeting();
        if(StringUtils.isBlank(startInfo.getId())){
            logger.error ("ID is Empty.");
            return Response.status(Status.BAD_REQUEST).entity("ID is Empty.").build();
        }
        srhMeeting.setId(startInfo.getId());

        // 登録済みの会議を検索
        Meeting srhRsltMeeting = null;
        try{
            srhRsltMeeting = service.getMeetingById(srhMeeting);
        } catch (Exception e) {
        	   logger.error ("Invalid Id specified." + e.getMessage());
        	   return Response.status(Status.SERVICE_UNAVAILABLE).entity("サービスエラー").build();
        }

        if(null == srhRsltMeeting){
            logger.error ("not found.");
            return Response.status(Status.NOT_FOUND).entity("未検出エラー").build();
        }

        // 会議開始
        Meeting stMeeting = new Meeting();
        	stMeeting.setId(srhRsltMeeting.getId());
        	stMeeting.setOrganizer(srhRsltMeeting.getOrganizer());
        	stMeeting.getRequiredAttendees().addAll( srhRsltMeeting.getRequiredAttendees());
        	stMeeting.getResources().addAll(srhRsltMeeting.getResources());
        	stMeeting.setSensibility(srhRsltMeeting.getSensibility());

		logger.info("[E N D] controller={}, action={}, elapsed time={}", 0, 1);

        stMeeting.setSubject(srhRsltMeeting.getSubject());
        stMeeting.setStartTime(srhRsltMeeting.getStartTime());
        stMeeting.setEndTime(srhRsltMeeting.getEndTime());

		return Response.ok().entity(stMeeting).build();
	}

	@POST
	@Path("/end")
	@Operation(summary = "会議を利用終了する", tags = { "Meeting" }, responses = {
			@ApiResponse(responseCode = "201", description = "Metting end Successed"),	//Todo
			@ApiResponse(responseCode = "400", description = "Invalid MeetingEndInfo supplied"),
			@ApiResponse(responseCode = "404", description = "EmailAddress not found"),
			@ApiResponse(responseCode = "415", description = "HTTP 415 Unsupported Media Type")})
	public Response end(
			@RequestBody(
					description = "会議終了情報", required = true,
					content = @Content( schema = @Schema(implementation = MeetingEndInfo.class))) final MeetingEndInfo endInfo)
			throws ApiException {
		logger.info("[START] {} controller={}, action={}, queryString={}", 0, 1);

		Meeting srhMeeting = new Meeting();
        if(StringUtils.isBlank(endInfo.getId())){
            logger.error ("ID is Empty.");
            return Response.status(Status.BAD_REQUEST).entity("ID is Empty.").build();
        }
        srhMeeting.setId(endInfo.getId());

        // 登録済みの会議を検索
        Meeting srhRsltMeeting = null;
        try{
            srhRsltMeeting = service.getMeetingById(srhMeeting);
        } catch (Exception e) {
        	   logger.error ("Invalid Id specified." + e.getMessage());
        	   return Response.status(Status.SERVICE_UNAVAILABLE).entity("サービスエラー").build();
        }

        if(null == srhRsltMeeting){
            logger.error ("not found.");
            return Response.status(Status.NOT_FOUND).entity("未検出エラー").build();
        }

        // 会議開始
        Meeting edMeeting = new Meeting();
        edMeeting.setId(srhRsltMeeting.getId());
        edMeeting.setOrganizer(srhRsltMeeting.getOrganizer());
        edMeeting.getRequiredAttendees().addAll( srhRsltMeeting.getRequiredAttendees());
        edMeeting.getResources().addAll(srhRsltMeeting.getResources());
        edMeeting.setSensibility(srhRsltMeeting.getSensibility());

        try{
            service.endMeeting(edMeeting);
        }
        catch (Exception e){
        	     logger.error (e.getMessage());
            return Response.status(Status.SERVICE_UNAVAILABLE).entity("終了エラー").build();
        }

		logger.info("[E N D] controller={}, action={}, elapsed time={}", 0, 1);

		edMeeting.setSubject(srhRsltMeeting.getSubject());
		edMeeting.setStartTime(srhRsltMeeting.getStartTime());
		edMeeting.setEndTime(DateTimeCalculat.CurrentTimeWithRest().toDate());

		return Response.ok().entity(edMeeting).build();
	}

	@POST
	@Path("/extension")
	@Operation(summary = "会議を利用延長する", tags = { "Meeting" }, responses = {
			@ApiResponse(responseCode = "201", description = "Metting extension Successed"),	//Todo
			@ApiResponse(responseCode = "400", description = "Invalid MeetingExtensionInfo supplied"),
			@ApiResponse(responseCode = "404", description = "EmailAddress not found"),
			@ApiResponse(responseCode = "415", description = "HTTP 415 Unsupported Media Type")})
	public Response extension(
			@RequestBody(
					description = "会議延長情報", required = true,
					content = @Content( schema = @Schema(implementation = MeetingExtensionInfo.class))) final MeetingExtensionInfo extendInfo)
			throws ApiException {
		logger.info("[START] {} controller={}, action={}, queryString={}", 0, 1);

		Meeting srhMeeting = new Meeting();
        if(StringUtils.isBlank(extendInfo.getId())){
            logger.error ("ID is Empty.");
            return Response.status(Status.BAD_REQUEST).entity("ID is Empty.").build();
        }
        srhMeeting.setId(extendInfo.getId());

        // 登録済みの会議を検索
        Meeting srhRsltMeeting = null;
        try{
            srhRsltMeeting = service.getMeetingById(srhMeeting);
        } catch (Exception e) {
        	   logger.error ("Invalid Id specified." + e.getMessage());
        	   return Response.status(Status.SERVICE_UNAVAILABLE).entity("サービスエラー").build();
        }

        if(null == srhRsltMeeting){
            logger.error ("not found.");
            return Response.status(Status.NOT_FOUND).entity("未検出エラー").build();
        }

        // 会議開始
        Meeting extdMeeting = new Meeting();
        extdMeeting.setId(srhRsltMeeting.getId());
        extdMeeting.setExtensionTime(extendInfo.getExtensionTime());
        extdMeeting.setOrganizer(srhRsltMeeting.getOrganizer());
        extdMeeting.getRequiredAttendees().addAll( srhRsltMeeting.getRequiredAttendees());
        extdMeeting.getResources().addAll(srhRsltMeeting.getResources());
        extdMeeting.setSensibility(srhRsltMeeting.getSensibility());

        // 会議予約処理
        Meeting updMeeting = new Meeting();
        try{
            service.extensionMeeting(extdMeeting);
        }
        catch (Exception e){
        	     logger.error (e.getMessage());
            return Response.status(Status.SERVICE_UNAVAILABLE).entity("延長エラー").build();
        }

		logger.info("[E N D] controller={}, action={}, elapsed time={}", 0, 1);

		Date newEndTime = DateTimeCalculat.plusMinutes(srhRsltMeeting.getEndTime(), extendInfo.getExtensionTime());
		extdMeeting.setSubject(srhRsltMeeting.getSubject());
		extdMeeting.setStartTime(srhRsltMeeting.getStartTime());
		extdMeeting.setEndTime(newEndTime);

		return Response.ok().entity(extdMeeting).build();
	}

	@POST
	@Path("/cancel")
	@Operation(summary = "会議を強制キャンセルする", tags = { "Meeting" }, responses = {
			@ApiResponse(responseCode = "201", description = "Metting extension Successed"),	//Todo
			@ApiResponse(responseCode = "400", description = "Invalid MeetingCancelInfo supplied"),
			@ApiResponse(responseCode = "404", description = "EmailAddress not found"),
			@ApiResponse(responseCode = "415", description = "HTTP 415 Unsupported Media Type")})
	public Response cancel(
			@RequestBody(
					description = "会議キャンセル情報", required = true,
					content = @Content( schema = @Schema(implementation = MeetingCancelInfo.class))) final MeetingCancelInfo cancelInfo)
			throws ApiException {
		logger.info("[START] {} controller={}, action={}, queryString={}", 0, 1);

		Meeting clMeeting = new Meeting();
        if(StringUtils.isBlank(cancelInfo.getId())){
            logger.error ("ID is Empty.");
            return Response.status(Status.BAD_REQUEST).entity("ID is Empty.").build();
        }
        clMeeting.setId(cancelInfo.getId());

        // 登録済みの会議を検索
        Meeting srhRsltMeeting = null;
        try{
            srhRsltMeeting = service.getMeetingById(clMeeting);
        } catch (Exception e) {
        	   logger.error ("Invalid Id specified." + e.getMessage());
        	   return Response.status(Status.SERVICE_UNAVAILABLE).entity("サービスエラー").build();
        }

        clMeeting.setStartTime(srhRsltMeeting.getStartTime());
        clMeeting.setEndTime(srhRsltMeeting.getEndTime());
        clMeeting.setOrganizer(srhRsltMeeting.getOrganizer());
        clMeeting.getRequiredAttendees().addAll( srhRsltMeeting.getRequiredAttendees());
        clMeeting.getResources().addAll(srhRsltMeeting.getResources());
        clMeeting.setSensibility(srhRsltMeeting.getSensibility());

        try{
            service.extensionMeeting(clMeeting);
        }
        catch (Exception e){
        	     logger.error (e.getMessage());
            return Response.status(Status.SERVICE_UNAVAILABLE).entity("キャンセルエラー").build();
        }

		logger.info("[E N D] controller={}, action={}, elapsed time={}", 0, 1);

        clMeeting.setSubject(srhRsltMeeting.getSubject());

		return Response.ok().entity(clMeeting).build();
	}


    private UserInfo getUserInfo(String user)
    {
        UserInfo organizer = new UserInfo("", "");

        if(!StringUtils.isBlank(user)){
             organizer.setEmailAddress(user);
        }

        return organizer;
    }

    private RoomInfo getRoomInfo(String resource)
    {
        RoomInfo roomInfo = new RoomInfo("","");

        if(!StringUtils.isBlank(resource)){
            roomInfo.setEmailAddress(resource);
        }

        return roomInfo;
    }
}
