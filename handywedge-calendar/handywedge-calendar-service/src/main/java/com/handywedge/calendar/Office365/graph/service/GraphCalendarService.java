package com.handywedge.calendar.Office365.graph.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handywedge.calendar.Office365.graph.exceptions.GraphApiException;
import com.handywedge.calendar.Office365.graph.service.config.GraphApiInfo;
import com.handywedge.calendar.Office365.graph.service.extension.*;
import com.handywedge.calendar.Office365.graph.service.requests.*;
import com.handywedge.calendar.Office365.graph.service.utils.Common;
import com.handywedge.calendar.Office365.rest.models.ScheduleInformation;
import com.handywedge.calendar.Office365.rest.models.ScheduleDetailItem;
import com.microsoft.graph.content.MSBatchResponseContent;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GraphCalendarService {

    private static final Logger logger = LogManager.getLogger();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    GraphApiInfo apiInfo = null;

    public GraphCalendarService(GraphApiInfo app){
        apiInfo = app;
    }


    public void deleteSchedule(GraphExtendFindByIdScheduleRequest request) throws GraphApiException {
        GraphExtendDeleteScheduleResponse response = new GraphExtendDeleteScheduleResponse();
        logger.info( "予定表削除処理 Start" );

        GraphExtendDeleteScheduleApi deleteApi = new GraphExtendDeleteScheduleApi( apiInfo, request );
        deleteApi.getResponse();

        logger.info( "予定表削除処理 E n d" );

        return ;
    }

    public GraphExtendFindScheduleResponse findScheduleWithBatch(List<GraphExtendFindScheduleRequest> requests) throws GraphApiException{
        GraphExtendFindScheduleResponse findScheduleResponse = new GraphExtendFindScheduleResponse();
        logger.info( "予定表検索処理 Start" );

        int processCount = 0;
        for(GraphExtendFindScheduleRequest scheduleRequest: requests) {
            logger.debug( "予定表検索処理 [{}回目]", ++processCount );
            GraphExtendFindScheduleApi scheduleApi = new GraphExtendFindScheduleApi( apiInfo, scheduleRequest );
            GraphExtendFindScheduleResponse tempFindScheduleResponse = scheduleApi.getResponse();

            logger.debug( "予定表検索処理結果 [{}回目]: {}", processCount, tempFindScheduleResponse.getScheduleItems().size());
            logger.debug( "予定表検索処理結果 [{}回目]: {}", processCount, gson.toJson( tempFindScheduleResponse ));

            if(null == findScheduleResponse.getScheduleItems()){
                findScheduleResponse.setScheduleItems( tempFindScheduleResponse.getScheduleItems());
            }else{
                findScheduleResponse.getScheduleItems().addAll( tempFindScheduleResponse.getScheduleItems() );
            }

        }
        logger.info( "予定表検索処理結果（総件数）： {}件", findScheduleResponse.getScheduleItems().size() );
        logger.info( "予定表検索処理 E n d" );

        return findScheduleResponse;
    }

    public ScheduleDetailItem registerSchedule(GraphExtendRegisterScheduleRequest request) throws GraphApiException {
        GraphExtendRegisterScheduleResponse registerScheduleResponse = new GraphExtendRegisterScheduleResponse();
        logger.info( "予定表登録処理 Start" );

        GraphExtendRegisterScheduleApi scheduleApi = new GraphExtendRegisterScheduleApi( apiInfo, request );
        ScheduleDetailItem scheduleDetailItem = scheduleApi.getResponse();

        logger.info( "予定表登録処理 E n d" );
        return scheduleDetailItem;
    }

    /**
     * バッチによる予定表取得処理
     */
    public List<ScheduleInformation> getScheduleByBatch(GraphExtendGetScheduleRequest request) throws GraphApiException {
        List<ScheduleInformation> resultScheduleInformation = new ArrayList<ScheduleInformation>();

        logger.info( "予定表取得処理 Start" );
        List<List<List<String>>> newRequestList = (List<List<List<String>>>) Common.covert3DimensionList(
                request.getSchedules(),
                apiInfo.getUserNumber(),
                apiInfo.getRequestNumber());


        int processCount = 0;
        for(List<List<String>> requestList: newRequestList){
            GraphExtendGetScheduleApi scheduleApi = new GraphExtendGetScheduleApi( apiInfo );

            logger.debug( "予定表取得バッチ処理 [{}回目]", ++processCount );
            for(List<String> userList: requestList){
                GraphExtendGetScheduleRequest scheduleRequest = new GraphExtendGetScheduleRequest();
                scheduleRequest.setAvailabilityViewInterval( request.getAvailabilityViewInterval() );
                scheduleRequest.setSchedules( userList );
                scheduleRequest.setStartTime( request.getStartTime() );
                scheduleRequest.setEndTime( request.getEndTime() );

                logger.debug( "MSBatchRequestStep: {}", gson.toJson( request ) );
                scheduleApi.buildBatchRequestSteps( scheduleRequest );
            }

            GraphExtendBatchRequest graphExtendBatchRequest = new GraphExtendBatchRequest();
            graphExtendBatchRequest.setRequestSteps(scheduleApi.getBatchRequestSteps());
            GraphExtendBatchApi batchApi = new GraphExtendBatchApi( apiInfo, graphExtendBatchRequest );
            MSBatchResponseContent responseContent = batchApi.getResponseContent();

            List<ScheduleInformation> scheduleInformation = scheduleApi.extractScheduleInformation(responseContent);

            // 0の場合、リトライ中断
            if(apiInfo.getRetryTimeThreshold() != 0){
                // リトライ処理
                // リトライ対象外
                List<ScheduleInformation> normalScheduleInformation = scheduleInformation.stream()
                        .filter(schd -> {
                            if (ObjectUtils.isEmpty(schd)) {
                                return false;
                            }
                            // 閾値超えはリトライ対象外
                            if (schd.getHeaderRetryTime() > 0 && schd.getHeaderRetryTime() <= apiInfo.getRetryTimeThreshold()) {
                                return false;
                            }
                            return true;
                        })
                        .collect(Collectors.toList());

                resultScheduleInformation.addAll(normalScheduleInformation);

                // リトライ対象
                List<ScheduleInformation> retryScheduleInformation = scheduleInformation.stream()
                        .filter(schd -> {
                            if (ObjectUtils.isEmpty(schd)) {
                                return false;
                            }
                            if (schd.getHeaderRetryTime() <= 0) {
                                return false;
                            }
                            // 閾値超えはリトライ対象外
                            if (schd.getHeaderRetryTime() > apiInfo.getRetryTimeThreshold()) {
                                return false;
                            }
                            return true;
                        })
                        .collect(Collectors.toList());

                List<ScheduleInformation> resultRetryScheduleInformation = new ArrayList<ScheduleInformation>();
                if (!ObjectUtils.isEmpty(retryScheduleInformation)) {
                    resultRetryScheduleInformation = retryGetSchdule(request, retryScheduleInformation);
                }

                resultScheduleInformation.addAll(resultRetryScheduleInformation);
            }else{
                logger.info( "予定表取得処理" );
                resultScheduleInformation.addAll(scheduleInformation);
            }

            logger.debug("予定表取得バッチ処理結果 [{}回目]: {}", processCount, resultScheduleInformation.size());
            try {
                if(apiInfo.getBatchWaitTime() != 0) {
                    Thread.sleep(apiInfo.getBatchWaitTime() * 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info( "予定表取得処理結果（総件数）： {}件", resultScheduleInformation.size() );
        logger.info( "予定表取得処理 E n d" );

        return resultScheduleInformation;
    }

    private List<ScheduleInformation> retryGetSchdule(GraphExtendGetScheduleRequest request, List<ScheduleInformation> retryScheduleInformation) throws GraphApiException {
        List<ScheduleInformation> resultRetryScheduleInformation = new ArrayList<ScheduleInformation>();

        logger.info( "(リトライ)予定表取得処理 Start" );

        long retryTime = retryScheduleInformation.stream()
                .max(Comparator.comparing(ScheduleInformation::getHeaderRetryTime))
                .map(schd -> schd.getHeaderRetryTime())
                .orElse((long)0);

        logger.warn("(リトライ)予定表取得処理 待ち時間[0~{}]:　{}秒", apiInfo.getRetryTimeThreshold(), retryTime);

        try {
            if(retryTime != 0) {    // オーバーヘッド回避のため
                Thread.sleep(retryTime * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String> retryScheduleIdList = retryScheduleInformation.stream()
                .map(schd -> {
                    return schd.getScheduleId();
                })
                .collect(Collectors.toList());

        List<List<List<String>>> newRetryRequestList = (List<List<List<String>>>) Common.covert3DimensionList(
                retryScheduleIdList,
                apiInfo.getUserNumber(),
                apiInfo.getRequestNumber());


        int processCount = 0;
        for(List<List<String>> requestList: newRetryRequestList) {
            GraphExtendGetScheduleApi scheduleApi = new GraphExtendGetScheduleApi(apiInfo);

            for (List<String> userList : requestList) {
                GraphExtendGetScheduleRequest scheduleRequest = new GraphExtendGetScheduleRequest();
                scheduleRequest.setAvailabilityViewInterval(request.getAvailabilityViewInterval());
                scheduleRequest.setSchedules(userList);
                scheduleRequest.setStartTime(request.getStartTime());
                scheduleRequest.setEndTime(request.getEndTime());

                scheduleApi.buildBatchRequestSteps(scheduleRequest);
            }

            GraphExtendBatchRequest graphExtendBatchRequest = new GraphExtendBatchRequest();
            graphExtendBatchRequest.setRequestSteps(scheduleApi.getBatchRequestSteps());
            GraphExtendBatchApi batchApi = new GraphExtendBatchApi( apiInfo, graphExtendBatchRequest );
            MSBatchResponseContent responseContent = batchApi.getResponseContent();

            List<ScheduleInformation> scheduleInformation = scheduleApi.extractScheduleInformation(responseContent);

            logger.debug( "(リトライ)予定表取得バッチ処理結果 [{}回目]: {}", processCount, scheduleInformation.size());
            resultRetryScheduleInformation.addAll(scheduleInformation);
        }

        logger.info( "(リトライ)予定表取得処理結果（総件数）： {}件", resultRetryScheduleInformation.size() );
        logger.info( "(リトライ)予定表取得処理 E n d" );

        return resultRetryScheduleInformation;
    }
}
