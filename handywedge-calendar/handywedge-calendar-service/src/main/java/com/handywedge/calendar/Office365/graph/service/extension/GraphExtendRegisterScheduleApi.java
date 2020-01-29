package com.handywedge.calendar.Office365.graph.service.extension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.handywedge.calendar.Office365.graph.exceptions.GraphApiException;
import com.handywedge.calendar.Office365.rest.models.ScheduleDetailItem;
import com.handywedge.calendar.Office365.rest.models.ScheduleInformation;
import com.handywedge.calendar.Office365.rest.models.ScheduleSummaryItem;
import com.microsoft.graph.models.extensions.Location;
import com.handywedge.calendar.Office365.graph.service.utils.Constant;
import com.handywedge.calendar.Office365.graph.service.requests.GraphExtendRegisterScheduleRequest;
import com.handywedge.calendar.Office365.graph.service.requests.GraphExtendRegisterScheduleResponse;
import com.handywedge.calendar.Office365.graph.service.config.GraphApiInfo;
import com.microsoft.graph.models.generated.AttendeeType;
import com.microsoft.graph.models.generated.BodyType;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphExtendRegisterScheduleApi extends GraphExtendBaseApi {

    private static final Logger logger = LogManager.getLogger( );

    public final static String REGISTER_SCHEDULE_REQUEST_URI = "/users/%s/calendar/events";

    private GraphExtendRegisterScheduleRequest requestInfo;

    public GraphExtendRegisterScheduleApi(GraphApiInfo api){
        super(api);
    }

    public GraphExtendRegisterScheduleApi(GraphApiInfo api, GraphExtendRegisterScheduleRequest registerScheduleRequest){
        super(api);
        requestInfo = registerScheduleRequest;
    }

    private String getURL() {
        String requestURL = Constant.GRAPH_API_BASE_URL
                + String.format( REGISTER_SCHEDULE_REQUEST_URI, requestInfo.getOrganizer() );
        logger.debug( "Request URL: {}", requestURL );
        return requestURL;
    }

    private RequestBody setPostBody() {
        Map<String, Object> startTime = new HashMap<>(  );
        startTime.put( "dateTime", requestInfo.getStartTime());
        startTime.put( "timeZone", getTimeZone() );

        Map<String, Object> endTime = new HashMap<>(  );
        endTime.put( "dateTime", requestInfo.getEndTime());
        endTime.put( "timeZone", getTimeZone() );

        Map<String, Object> body = new HashMap<String, Object>(){
            {
                put("contentType", StringUtils.lowerCase( BodyType.HTML.toString()));
                put("content", requestInfo.getBody());
            }
        };

        List<Map> attendees = new ArrayList<>();
        for(String attendee : requestInfo.getAttendees()) {
            Map<String, Object> attendeeMap = new HashMap<String, Object>() {
                {
                    put( "type",StringUtils.lowerCase(AttendeeType.REQUIRED.toString()) );
                    Map<String, Object> addressMap = new HashMap<String, Object>(){
                        {put( "address", attendee );}
                    };
                    put( "emailAddress", addressMap );
                }
            };
            attendees.add(attendeeMap);
        }

        // リソース（会議室等）も参加者として登録
        for(String location : requestInfo.getLocations()) {
            Map<String, Object> locationMap = new HashMap<String, Object>() {
                {
                    put( "type", StringUtils.lowerCase(AttendeeType.RESOURCE.toString()) );
                    Map<String, Object> addressMap = new HashMap<String, Object>(){
                        {put( "address", location );}
                    };
                    put( "emailAddress", addressMap );
                }
            };
            attendees.add(locationMap);
        }

        Map<String, Object> bodyMap = new HashMap<>( );
        bodyMap.put( "subject", requestInfo.getSubject() );
        bodyMap.put("start", startTime);
        bodyMap.put("end", endTime);
        bodyMap.put( "body", body);
        bodyMap.put( "showAs", StringUtils.lowerCase( requestInfo.getStatus().toString()));
        bodyMap.put( "attendees", attendees );


        JSONObject bodyJson = new JSONObject(bodyMap);
        logger.debug( "Request Body: {}", bodyJson );
        RequestBody postBody = RequestBody.create( MediaType.parse("application/json"), bodyJson.toString());

        return postBody;
    }

    public Request getRequest(){
        Request request = new Request.Builder()
                .url(getURL())
                .headers( getHeaders() )
                .post( setPostBody() )
                .build();
        return request;
    }

    public ScheduleDetailItem getResponse() throws GraphApiException {

        ScheduleDetailItem scheduleDetailItem = null;
        Response response = null;
        try {

            Request request = getRequest();
            logger.debug( "登録処理: {}",  request);

            long startTime = System.currentTimeMillis();

            response = getGraphClient().newCall(request).execute();

            long endTime = System.currentTimeMillis();
            logger.info("[予定表登録処理] 処理時間：{}ms", (endTime - startTime));

            logger.debug( "GraphExtendBatchResponse: {}",  response);
        } catch (IOException e) {
//            logger.error( "登録処理エラー。MESSAGE: {}",  response.message());
            e.printStackTrace();
            throw new GraphApiException( "", response.message());
        }


        GraphExtendRegisterScheduleResponse registerScheduleResponse = null;
        if(response.isSuccessful()){
            String jsonResponse = null;
            try {
                jsonResponse = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                throw new GraphApiException( "", e.getMessage() );
            }finally {
                response.body().close();
            }

            scheduleDetailItem = extractScheduleInformationResponse(jsonResponse);
            logger.info("[登録処理完了] ユーザー：{}   ID：{} 開始時刻〜終了時刻：{}〜{}",
                    requestInfo.getOrganizer(),
                    scheduleDetailItem.getId(),
                    requestInfo.getStartTime(),
                    requestInfo.getEndTime()
            );
        }else{
            logger.error( "登録処理エラー。CODE: {}; MESSAGE: {}", response.code(), response.message());
            throw new GraphApiException( String.valueOf(response.code()), response.message() );
        }
        return scheduleDetailItem;
    }

    private ScheduleDetailItem extractScheduleInformationResponse(String jsonResponse){

        ScheduleDetailItem scheduleDetailItem = new ScheduleDetailItem();
        JSONObject jObjectScheduleItem = new JSONObject(jsonResponse);

        scheduleDetailItem.setId( jObjectScheduleItem.getString("id" ) );
        scheduleDetailItem.setStartTime( jObjectScheduleItem.getJSONObject( "start").getString( "dateTime" ) );
        scheduleDetailItem.setEndTime( jObjectScheduleItem.getJSONObject( "end").getString( "dateTime" ) );
        scheduleDetailItem.setStatus( jObjectScheduleItem.getString( "status" ) );

        if(jObjectScheduleItem.has( "subject" )) {
            scheduleDetailItem.setSubject( jObjectScheduleItem.getString( "subject" ) );
        }else{
            scheduleDetailItem.setSubject("");
        }

        if(jObjectScheduleItem.has( "body" )) {
            scheduleDetailItem.setBody( jObjectScheduleItem.getJSONObject( "body").getString( "content" ) );
        }else{
            scheduleDetailItem.setSubject("");
        }

        if(jObjectScheduleItem.has( "attendees" )) {
            JSONArray attendees = jObjectScheduleItem.getJSONArray( "attendees" );

            for (int i=0; i < attendees.length(); i++) {
                JSONObject attendee = attendees.getJSONObject(i);
                if(AttendeeType.RESOURCE.equals(attendee.getString( "type" ))){
                    scheduleDetailItem.getLocations().add( attendee.getJSONObject( "emailAddress" ).getString( "address" ) );
                }else{
                    scheduleDetailItem.getAttendees().add( attendee.getJSONObject( "emailAddress" ).getString( "address" ) );
                }
            }
        }


        return scheduleDetailItem;
    }

}
