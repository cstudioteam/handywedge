package com.handywedge.calendar.Office365.rest.injections;

import com.handywedge.calendar.Office365.graph.exceptions.GraphApiException;
import com.handywedge.calendar.Office365.graph.service.GraphCalendarService;
import com.handywedge.calendar.Office365.graph.service.requests.GraphExtendFindByIdScheduleRequest;
import com.handywedge.calendar.Office365.graph.service.requests.GraphExtendGetScheduleRequest;
import com.handywedge.calendar.Office365.graph.service.requests.GraphExtendRegisterScheduleRequest;
import com.handywedge.calendar.Office365.rest.models.ScheduleDetailItem;
import com.handywedge.calendar.Office365.rest.models.ScheduleInformation;
import com.handywedge.calendar.Office365.rest.models.ScheduleSummaryItem;
import com.handywedge.calendar.Office365.rest.requests.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarApiService {
    private static final Logger logger = LogManager.getLogger();

    @Inject
    CalendarApiConfig apiConfig;

    GraphCalendarService graphService;

    @PostConstruct
    public void initiate(){
        graphService = new GraphCalendarService(apiConfig.getGraphApiInfo());
    }


    /**
     * 予定表取得サービス
     * @param request
     * @param response
     * @throws GraphApiException
     */
    public void getSchedule(GetScheduleRequest request, GetScheduleResponse response) throws GraphApiException {

        GraphExtendGetScheduleRequest graphExtendGetScheduleRequest = new GraphExtendGetScheduleRequest();
        graphExtendGetScheduleRequest.setSchedules( request.getEmails() );
        graphExtendGetScheduleRequest.setAvailabilityViewInterval( 30 );

        graphExtendGetScheduleRequest.setStartTime(request.getStartTime());
        graphExtendGetScheduleRequest.setEndTime( request.getEndTime());

        List<ScheduleInformation> scheduleInformation = graphService.getScheduleByBatch( graphExtendGetScheduleRequest );

        List<Object> filterScheduleInformation = scheduleInformation.stream()
                .map( si -> {
                    if (si.getHasError()) {
                        return si;
                    } else {
                        ScheduleInformation newSI = new ScheduleInformation();
                        newSI = si;

                        List<ScheduleSummaryItem> filterItem = si.getScheduleSummaryItems().stream()
                                .filter( item -> {
                                    if (null == item) {
                                        return false;
                                    }
                                    if(null == request.getStatuses() || request.getStatuses().size() == 0){
                                        return true;
                                    }
                                    return request.getStatuses().contains( item.getStatus() );
                                } ).collect( Collectors.toList() );
                        newSI.getScheduleSummaryItems().clear();
                        newSI.getScheduleSummaryItems().addAll( filterItem );
                        return newSI;
                    }
                } ).collect( Collectors.toList() );

        response.getScheduleInformation().addAll( scheduleInformation );
    }


    /**
     * 予定表登録サービス
     * @param request
     * @param response
     * @throws GraphApiException
     */
    public void registerSchedule(RegisterScheduleRequest request, RegisterScheduleResponse response) throws GraphApiException{

        GraphExtendRegisterScheduleRequest graphExtendRegisterScheduleRequest = new GraphExtendRegisterScheduleRequest();

        graphExtendRegisterScheduleRequest.setOrganizer( request.getOrganizer() );
        graphExtendRegisterScheduleRequest.setSubject( request.getSubject() );
        graphExtendRegisterScheduleRequest.setBody( request.getBody() );
        graphExtendRegisterScheduleRequest.getAttendees().addAll( request.getAttendees() );
        graphExtendRegisterScheduleRequest.setLocations( request.getLocations() );
        graphExtendRegisterScheduleRequest.setStartTime(request.getStartTime());
        graphExtendRegisterScheduleRequest.setEndTime( request.getEndTime());
        graphExtendRegisterScheduleRequest.setStatus( request.getStatus() );

        ScheduleDetailItem scheduleDetailItem = graphService.registerSchedule( graphExtendRegisterScheduleRequest );

        response.setId(scheduleDetailItem.getId());
    }

    /**
     * 予定表削除サービス
     * @param request
     * @param response
     * @throws GraphApiException
     */
    public void deleteSchedule(DeleteScheduleRequest request, DeleteScheduleResponse response) throws GraphApiException {
        GraphExtendFindByIdScheduleRequest graphExtendDeleteScheduleRequest = new GraphExtendFindByIdScheduleRequest();

        graphExtendDeleteScheduleRequest.setId( request.getId() );
        graphExtendDeleteScheduleRequest.setOrganizer( request.getOrganizer() );

        graphService.deleteSchedule( graphExtendDeleteScheduleRequest );

    }


    /**
     * IDによる予定表検索サービス
     * @param request
     * @param response
     * @throws GraphApiException
     */
    public void findSchedule(FindScheduleByIdRequest request, FindScheduleByIdResponse response) throws GraphApiException {
        GraphExtendFindByIdScheduleRequest graphExtendFindByIdScheduleRequest = new GraphExtendFindByIdScheduleRequest();

        graphExtendFindByIdScheduleRequest.setId( request.getId() );
        graphExtendFindByIdScheduleRequest.setOrganizer( request.getOrganizer() );

        // ToDo:
//        GraphExtendFindByIdScheduleReponse  graphExtendFindByIdScheduleReponse = graphService.deleteSchedule( graphExtendFindByIdScheduleRequest );
//
//        response.getScheduleInformation();
    }

    /**
     * 予約期間より予定表検索サービス
     * @param request
     * @param response
     * @throws GraphApiException
     */
    public void findSchedule(FindScheduleByPeriodRequest request, FindScheduleByPeriodResponse response) throws GraphApiException {
        //TODO:

    }
}
