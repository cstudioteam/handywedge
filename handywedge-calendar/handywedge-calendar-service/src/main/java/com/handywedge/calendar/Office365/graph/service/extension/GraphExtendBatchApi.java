package com.handywedge.calendar.Office365.graph.service.extension;

import com.microsoft.graph.content.MSBatchRequestContent;
import com.microsoft.graph.content.MSBatchResponseContent;
import com.handywedge.calendar.Office365.graph.service.utils.Constant;
import com.handywedge.calendar.Office365.graph.service.requests.GraphExtendBatchRequest;

import com.handywedge.calendar.Office365.graph.service.config.GraphApiInfo;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GraphExtendBatchApi extends GraphExtendBaseApi {

    private static final Logger logger = LogManager.getLogger( );

    public final static String BATCH_REQUEST_URI = "/$batch";

    private GraphExtendBatchRequest requestInfo;

    public GraphExtendBatchApi(GraphApiInfo api, GraphExtendBatchRequest graphExtendBatchRequest){
        super(api);
        requestInfo = graphExtendBatchRequest;
    }

    private String getURL() {
        String requestURL = Constant.GRAPH_API_BASE_URL + String.format( BATCH_REQUEST_URI );
        logger.debug( "Request URL: {}", requestURL );
        return requestURL;
    }

    @Override
    public Headers getHeaders() {
        Map headerMap = new HashMap(  );
        headerMap.put( "Content-Type", "application/json" );
        headerMap.put( "Content-Encoding", "gzip, deflate" );
        headerMap.put( "Connection", "Keep-Alive");
        headerMap.put( "Prefer", "IdType=\"ImmutableId\"" );

        Headers headers = Headers.of(headerMap);
        return headers;
    }

    public RequestBody setRequestBody() {
        MSBatchRequestContent requestContent = new MSBatchRequestContent( requestInfo.getRequestSteps() );
        String content = requestContent.getBatchRequestContent();
        RequestBody requestBody = RequestBody.create( MediaType.parse("application/json"), content);
        return requestBody;
    }

    public Request getRequest() {
        Request request = new Request.Builder()
                .url(getURL())
                .headers( getHeaders() )
                .post( setRequestBody() )
                .build();
        return request;
    }

    public MSBatchResponseContent getResponseContent() {

        Response response = null;
        try {
            Request request = getRequest();
            long startTime = System.currentTimeMillis();

            response = getGraphClient(getBatchRequestTimeout()).newCall(request).execute();

            long endTime = System.currentTimeMillis();
            logger.info("[バッチ処理] 処理時間：{}ms", (endTime - startTime));

        } catch (IOException e) {
            logger.error( "バッチ処理エラー。MESSAGE: {}",  e.getMessage());
            e.printStackTrace();
        }

        MSBatchResponseContent responseContent = null;
        if(response.isSuccessful()){
            responseContent = new MSBatchResponseContent(response);
        }else{
            logger.error( "バッチ処理エラー。CODE: {}; MESSAGE: {}", response.code(), response.message());
            response.close();
        }
        return responseContent;
    }

}
