package com.handywedge.calendar.Office365.graph.service.extension;


import com.handywedge.calendar.Office365.graph.exceptions.GraphApiException;
import com.handywedge.calendar.Office365.graph.service.requests.GraphExtendFindByIdScheduleRequest;
import com.handywedge.calendar.Office365.graph.service.utils.Constant;
import com.handywedge.calendar.Office365.graph.service.requests.GraphExtendDeleteScheduleResponse;
import com.handywedge.calendar.Office365.graph.service.config.GraphApiInfo;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GraphExtendDeleteScheduleApi extends GraphExtendBaseApi {

    private static final Logger logger = LogManager.getLogger( );

    public final static String DELETE_SCHEDULE_REQUEST_URI = "/users/%s/calendar/events/%s";

    private GraphExtendFindByIdScheduleRequest requestInfo;

    public GraphExtendDeleteScheduleApi(GraphApiInfo api){
        super(api);
    }

    public GraphExtendDeleteScheduleApi(GraphApiInfo api, GraphExtendFindByIdScheduleRequest deleteScheduleRequest){
        super(api);
        requestInfo = deleteScheduleRequest;
    }

    private String getURL() {
        String requestURL = Constant.GRAPH_API_BASE_URL
                + String.format( DELETE_SCHEDULE_REQUEST_URI, requestInfo.getOrganizer(), requestInfo.getId() );

        logger.debug( "Request URL: {}", requestURL );
        return requestURL;
    }

    @Override
    public Headers getHeaders() {
        Map headerMap = new HashMap(  );
        headerMap.put( "Prefer", "IdType=\"ImmutableId\"" );
        Headers headers = Headers.of(headerMap);
        logger.debug( "Request Header: {}", headers );
        return headers;
    }

    public Request getRequest(){
        Request request = new Request.Builder()
                .url(getURL())
                .headers( getHeaders() )
                .delete()
                .build();
        return request;
    }

    public void getResponse() throws GraphApiException {

        Response response = null;
        try {
            Request request = getRequest();

            long startTime = System.currentTimeMillis();

            response = getGraphClient(getWriteRequestTimeout()).newCall(request).execute();

            long endTime = System.currentTimeMillis();
            logger.info("[削除処理] 処理時間：{}ms", (endTime - startTime));

        } catch (IOException e) {
            //logger.error( "削除処理エラー。MESSAGE: {}",  response.message());
            e.printStackTrace();
            throw new GraphApiException( String.valueOf(response.code()), response.message() );
        }

        GraphExtendDeleteScheduleResponse deleteScheduleResponse = null;
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

            logger.debug("[削除処理完了] ユーザー：{}   ID：{}",
                    requestInfo.getOrganizer(),
                    requestInfo.getId()
            );
        }else{
            logger.error( "削除処理エラー。CODE: {}; MESSAGE: {}", response.code(), response.message());
            throw new GraphApiException( String.valueOf(response.code()), response.message() );
        }
        return ;
    }

//    private GraphExtendDeleteScheduleResponse makeScheduleResponse(String jsonResponse){
//        return new GraphExtendDeleteScheduleResponse();
//    }

}
