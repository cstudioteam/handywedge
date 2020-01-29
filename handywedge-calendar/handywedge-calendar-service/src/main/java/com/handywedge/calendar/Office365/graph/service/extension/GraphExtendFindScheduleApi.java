package com.handywedge.calendar.Office365.graph.service.extension;

import com.handywedge.calendar.Office365.graph.exceptions.GraphApiException;
import com.handywedge.calendar.Office365.rest.models.ScheduleDetailItem;
import com.handywedge.calendar.Office365.graph.service.utils.Constant;
import com.handywedge.calendar.Office365.graph.service.requests.GraphExtendFindScheduleRequest;
import com.handywedge.calendar.Office365.graph.service.requests.GraphExtendFindScheduleResponse;
import com.handywedge.calendar.Office365.graph.service.config.GraphApiInfo;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphExtendFindScheduleApi extends GraphExtendBaseApi {

    private static final Logger logger = LogManager.getLogger( );

    public final static String SEARCH_SCHEDULE_REQUEST_URI = "/users/%s/calendar/calendarView";

    private GraphExtendFindScheduleRequest requestInfo;

    public GraphExtendFindScheduleApi(GraphApiInfo app){
        super(app);
    }

    public GraphExtendFindScheduleApi(GraphApiInfo app, GraphExtendFindScheduleRequest findScheduleRequest){
        super(app);
        requestInfo = findScheduleRequest;
    }

    private String getURL() {
        String requestURL = Constant.GRAPH_API_BASE_URL
                + String.format( SEARCH_SCHEDULE_REQUEST_URI, requestInfo.getOrganizer() )
                + "?"
                + String.format( "startDateTime=%s", requestInfo.getStartTime())
                + "&"
                + String.format( "endDateTime=%s", requestInfo.getEndTime())
                + "&"
                + getSelect();

        logger.debug( "Request URL: {}", requestURL );
        return requestURL;
    }

    private String getSelect(){
        List select = Arrays.asList( "id", "organizer", "showAs", "subject", "start", "end" );

        return String.format( "$select=%s" , String.join( ",", select));
    }

    public Request getRequest(){
        Request request = new Request.Builder()
                .url(getURL())
                .headers( getHeaders() )
                .get()
                .build();
        return request;
    }

    public GraphExtendFindScheduleResponse getResponse() throws GraphApiException {

        Response response = null;
        try {
            logger.debug( "検索処理: {}",  getRequest());
            Request request = getRequest();

            long startTime = System.currentTimeMillis();

            response = getGraphClient().newCall(request).execute();

            long endTime = System.currentTimeMillis();
            logger.info("[検索処理] 処理時間：{}ms", (endTime - startTime));

        } catch (IOException e) {
            logger.error( "検索処理エラー。MESSAGE: {}",  response.message());
            e.printStackTrace();
            throw new GraphApiException( String.valueOf(response.code()), response.message() );
        }

        GraphExtendFindScheduleResponse findScheduleResponse = null;
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

            findScheduleResponse = makeScheduleResponse(jsonResponse);
            logger.info("[検索処理完了] ユーザー：{}   件名：{} 開始時刻〜終了時刻：{}〜{}",
                    requestInfo.getOrganizer(),
                    requestInfo.getSubject(),
                    requestInfo.getStartTime(),
                    requestInfo.getEndTime()
                    );
        }else{
            logger.error( "検索処理エラー。CODE: {}; MESSAGE: {}", response.code(), response.message());
            throw new GraphApiException( String.valueOf(response.code()), response.message() );
        }
        return findScheduleResponse;
    }

    private GraphExtendFindScheduleResponse makeScheduleResponse(String jsonResponse){
        GraphExtendFindScheduleResponse findScheduleResponse = new GraphExtendFindScheduleResponse();

        List<ScheduleDetailItem> scheduleItems = new ArrayList<ScheduleDetailItem>();

        // Todo: nextLink
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray jObjectScheduleItems = jsonObject.getJSONArray("value");
        for (int i=0; i < jObjectScheduleItems.length(); i++) {

            ScheduleDetailItem scheduleItem = new ScheduleDetailItem();
            JSONObject jObjectScheduleItem = jObjectScheduleItems.getJSONObject( i );

            scheduleItem.setId( jObjectScheduleItem.getString( "id" ) );
            scheduleItem.setOrganizer( jObjectScheduleItem.getJSONObject( "organizer" ).getJSONObject( "emailAddress" ).getString( "address" ) );
            if(jObjectScheduleItem.has( "subject" )) {
                scheduleItem.setSubject( jObjectScheduleItem.getString( "subject" ) );
            }
            scheduleItem.setStatus( jObjectScheduleItem.getString( "showAs" ) );
            scheduleItem.setStartTime( jObjectScheduleItem.getJSONObject( "start").getString( "dateTime" ) );
            scheduleItem.setEndTime( jObjectScheduleItem.getJSONObject( "end").getString( "dateTime" ) );
            scheduleItems.add( scheduleItem );

            logger.info( "ユーザー: {}  ID：{}", scheduleItem.getOrganizer(), scheduleItem.getId());
        }
        findScheduleResponse.setScheduleItems(scheduleItems);

        return findScheduleResponse;
    }

}
