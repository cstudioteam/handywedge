package com.handywedge.calendar.Office365.graph.service.extension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handywedge.calendar.Office365.graph.exceptions.GraphApiException;
import com.handywedge.calendar.Office365.rest.models.ScheduleInformation;
import com.handywedge.calendar.Office365.rest.models.ScheduleSummaryItem;
import com.microsoft.graph.content.MSBatchResponseContent;
import com.handywedge.calendar.Office365.graph.service.utils.Constant;
import com.handywedge.calendar.Office365.graph.service.requests.GraphExtendGetScheduleRequest;
import com.handywedge.calendar.Office365.graph.service.config.GraphApiInfo;

import okhttp3.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class GraphExtendGetScheduleApi extends GraphExtendBaseApi {

    private static final Logger logger = LogManager.getLogger( );

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public final static String GET_SCHEDULE_REQUEST_URI = "/users/%s/calendar/getSchedule";

    private List<ExtendBatchRequestStep> batchRequestSteps = new ArrayList<>();

    private GraphExtendGetScheduleRequest requestInfo;

    public GraphExtendGetScheduleApi(GraphApiInfo api){
        super(api);
    }

    private String getURL(String delegate) {
        String requestURL = Constant.GRAPH_API_BASE_URL
                + String.format( GET_SCHEDULE_REQUEST_URI, delegate );
        return requestURL;
    }


    private RequestBody setPostBody() {
        Map<String, Object> startTime = new HashMap<>(  );
        startTime.put( "dateTime", requestInfo.getStartTime());
        startTime.put( "timeZone", getTimeZone() );

        Map<String, Object> endTime = new HashMap<>(  );
        endTime.put( "dateTime", requestInfo.getEndTime());
        endTime.put( "timeZone", getTimeZone() );


        Map<String, Object> bodyMap = new HashMap<>( );
        bodyMap.put( "availabilityViewInterval", requestInfo.getAvailabilityViewInterval() );
        bodyMap.put( "schedules", requestInfo.getSchedules() );
        bodyMap.put( "startTime", startTime);
        bodyMap.put( "endTime", endTime);

        JSONObject bodyJson = new JSONObject(bodyMap);
        RequestBody postBody = RequestBody.create( MediaType.parse("application/json"), bodyJson.toString());
        return postBody;
    }

    public Request getRequest(){
        Request request = new Request.Builder()
                .url(getURL(getDelegate()))
                .headers( getHeaders() )
                .post( setPostBody() )
                .build();
        return request;
    }

    public List<ScheduleInformation> extractScheduleInformation(MSBatchResponseContent responseContent) throws GraphApiException {
        List<ScheduleInformation> scheduleInformation = new ArrayList<>( );


        for(ExtendBatchRequestStep batchRequestStep : getBatchRequestSteps()){
            Response response = null;
            if(null != responseContent) {
                response = responseContent.getResponseById( batchRequestStep.getRequestId() );
            }

            logger.debug( "BatchResponseContent: {}", gson.toJson( response ) );

            List<ScheduleInformation> subScheduleInformation = new ArrayList<>(  );
            if(null != response && response.isSuccessful()){
                String jsonResponse = null;
                try {
                    jsonResponse = response.body().string();
                } catch (IOException e) {
                    logger.warn(e.getMessage(), e);
                    throw new GraphApiException( String.valueOf(response.code()), response.message() );
                }finally {
                    response.body().close();
                }

                subScheduleInformation = extractGetScheduleResponse(jsonResponse);

                scheduleInformation.addAll( subScheduleInformation );
            }else{
                logger.warn( "バッチ処理エラー。メールアドレス：{}; CODE: {}; MESSAGE: {}"
                        , batchRequestStep.getRequestUsers()
                        , response.code()
                        , response.message()
                );

                subScheduleInformation = extractGetScheduleErrorResponse(response, batchRequestStep.getRequestUsers());
                scheduleInformation.addAll( subScheduleInformation );
            }
        }
        return scheduleInformation;
    }

    private List<ScheduleInformation> extractGetScheduleErrorResponse(Response response, List<String> requestUsers) {
        List<ScheduleInformation> scheduleInformation = new ArrayList<ScheduleInformation>();

        for(String user : requestUsers){
            ScheduleInformation subScheduleInformation = new ScheduleInformation();

            subScheduleInformation.setScheduleId( user );
            subScheduleInformation.setHasError( true );
            subScheduleInformation.setErrorCode( String.valueOf( response.code() ) );

            scheduleInformation.add( subScheduleInformation );
        }

        return scheduleInformation;
    }

    private List<ScheduleInformation> extractGetScheduleResponse(String jsonResponse){

        List<ScheduleInformation> scheduleInformation = new ArrayList<ScheduleInformation>();

        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray valueJsonArray = jsonObject.getJSONArray("value");
        for (int i=0; i < valueJsonArray.length(); i++) {
            JSONObject jObjectScheduleInformation = valueJsonArray.getJSONObject( i );

            ScheduleInformation subScheduleInformation = new ScheduleInformation();
            subScheduleInformation.setScheduleId(jObjectScheduleInformation.getString("scheduleId" ) );

            if(jObjectScheduleInformation.has( "error" )){
                logger.warn( "予定表取得エラー。取得対象:{}, エラーコード：{}",
                        jObjectScheduleInformation.getString( "scheduleId" ),
                        jObjectScheduleInformation.getJSONObject("error").getString("responseCode" ));

                subScheduleInformation.setHasError( true );
                subScheduleInformation.setErrorCode( jObjectScheduleInformation.getJSONObject("error").getString("responseCode" ));
                scheduleInformation.add( subScheduleInformation );
                continue;
            }
            List<ScheduleSummaryItem> scheduleSummaryItems = new ArrayList<ScheduleSummaryItem>();

            JSONArray scheduleItemJsonArray = jObjectScheduleInformation.getJSONArray("scheduleItems");
            for (int j=0; j < scheduleItemJsonArray.length(); j++) {
                ScheduleSummaryItem scheduleSummaryItem = new ScheduleSummaryItem();
                JSONObject jObjectScheduleItem = scheduleItemJsonArray.getJSONObject( j );

                if(ObjectUtils.isEmpty( jObjectScheduleItem )){
                    scheduleSummaryItems.add( scheduleSummaryItem );
                    continue;
                }

                if(jObjectScheduleItem.has( "subject" )) {
                    scheduleSummaryItem.setSubject( jObjectScheduleItem.getString( "subject" ) );
                }
                scheduleSummaryItem.setStatus( jObjectScheduleItem.getString( "status" ) );
                scheduleSummaryItem.setStartTime( jObjectScheduleItem.getJSONObject( "start").getString( "dateTime" ) );
                scheduleSummaryItem.setEndTime( jObjectScheduleItem.getJSONObject( "end").getString( "dateTime" ) );
                scheduleSummaryItems.add( scheduleSummaryItem );
            }

            subScheduleInformation.setScheduleSummaryItems( scheduleSummaryItems );
            scheduleInformation.add( subScheduleInformation );

            logger.debug( "ユーザー: {}  予約件数：{}",
                    subScheduleInformation.getScheduleId(),
                    subScheduleInformation.getScheduleSummaryItems().size());
        }

        return scheduleInformation;
    }

    /**
     * バッチリクエスト情報作成
     */
    public List<ExtendBatchRequestStep> getBatchRequestSteps(){
        return this.batchRequestSteps;
    }


    /**
     * バッチリクエスト情報作成
     * @param requestInfo
     */
    public void buildBatchRequestSteps(GraphExtendGetScheduleRequest requestInfo) {

        String delegate = getDelegate();
        if(StringUtils.isBlank( delegate )){
            delegate = requestInfo.getSchedules().stream().findAny().get();
        }

        Request request = new Request.Builder()
                .url(getURL(delegate))
                .headers( getHeaders() )
                .post( makeBatchRequestBody(requestInfo) )
                .build();

        List<String> denpendsOns = null;
        String requestId = String.format( "request_%2d", getBatchRequestSteps().size() + 1 );
        ExtendBatchRequestStep batchRequestStep = new ExtendBatchRequestStep(requestId, request, denpendsOns, requestInfo.getSchedules());

        getBatchRequestSteps().add( batchRequestStep );
    }

    private RequestBody makeBatchRequestBody(GraphExtendGetScheduleRequest requestInfo) {
        Map<String, Object> startTime = new HashMap<>(  );
        startTime.put( "dateTime", requestInfo.getStartTime());
        startTime.put( "timeZone", getTimeZone() );

        Map<String, Object> endTime = new HashMap<>(  );
        endTime.put( "dateTime", requestInfo.getEndTime());
        endTime.put( "timeZone", getTimeZone() );


        Map<String, Object> bodyMap = new HashMap<>( );
        bodyMap.put( "availabilityViewInterval", requestInfo.getAvailabilityViewInterval() );
        bodyMap.put( "schedules", requestInfo.getSchedules() );
        bodyMap.put( "startTime", startTime);
        bodyMap.put( "endTime", endTime);

        JSONObject bodyJson = new JSONObject(bodyMap);
        RequestBody postBody = RequestBody.create( MediaType.parse("application/json"), bodyJson.toString());
        return postBody;
    }
}
