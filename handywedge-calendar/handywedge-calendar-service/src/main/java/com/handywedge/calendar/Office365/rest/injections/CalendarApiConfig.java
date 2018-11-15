package com.handywedge.calendar.Office365.rest.injections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handywedge.calendar.Office365.graph.service.config.GraphProxyInfo;
import com.handywedge.calendar.Office365.graph.service.config.GraphApiInfo;
import com.handywedge.calendar.Office365.graph.service.config.GraphAuthInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.Proxy;
import java.util.Properties;

public class CalendarApiConfig {

    private static final Logger logger = LogManager.getLogger();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final String PROPERTY_NAME = "/handywedge-calendar-service.properties";

    private GraphApiInfo graphApiInfo;

    @PostConstruct
    public void initiate() {
        graphApiInfo = new GraphApiInfo();
        loadProperties();
    }

    private void loadProperties() {
        final Properties appProperties = new Properties();
        try {
            InputStream is = CalendarApiConfig.class.getResourceAsStream(PROPERTY_NAME);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(isr);
            appProperties.load( reader );
            logger.info( "{}ファイルを読み込みました。", PROPERTY_NAME );
        } catch (IOException e) {
            logger.error( "{}の設定ファイルが読み込めません。",PROPERTY_NAME );
            return;
        }

        setGraphProxyInfo(appProperties);
        setGraphAuthInfo(appProperties);
        setGraphApiInfo(appProperties);
    }


    /**
     * Graph Api 認証用情報読み込み
     * @param properties
     */
    public void setGraphAuthInfo(Properties properties){
        GraphAuthInfo graphAuthInfo = new GraphAuthInfo();
        graphAuthInfo.setApplicationId( properties.getProperty( "auth.app_id" ) );
        graphAuthInfo.setClientSecret( properties.getProperty( "auth.secret" ) );
        graphAuthInfo.setTenant( properties.getProperty( "auth.tenant" ) );
        graphAuthInfo.setScope( properties.getProperty( "auth.scope" ) );
        graphApiInfo.setAuthInfo(graphAuthInfo);

        logger.debug( "OAuth認証情報: {}", gson.toJson( graphAuthInfo ) );
    }

    /**
     * プロキシ設定情報読み込み
     * @param properties
     */
    public void setGraphProxyInfo(Properties properties){
        GraphProxyInfo proxyInfo = new GraphProxyInfo();

        String useProxy = properties.getProperty( "proxy.use" );
        if(StringUtils.isBlank(useProxy) || StringUtils.equalsIgnoreCase( useProxy, "false" )){
            graphApiInfo.setUseProxy(false);
        }else{
            graphApiInfo.setUseProxy(true);

            proxyInfo.setType( Proxy.Type.valueOf(properties.getProperty("proxy.type")) );
            proxyInfo.setHost( properties.getProperty( "proxy.host" ) );
            proxyInfo.setPort( Integer.parseInt( properties.getProperty("proxy.port")) );
        }
        logger.debug( "プロキシー利用有無: {}", useProxy );
        logger.debug( "プロキシー情報: {}", gson.toJson( proxyInfo ) );
    }

    /**
     * GraphApi設定情報取得
     * @return
     */
    public GraphApiInfo getGraphApiInfo(){
        return graphApiInfo;
    }

    public void setGraphApiInfo(Properties properties){
        graphApiInfo.setTimeZone( properties.getProperty( "graph.api.timezone" ) );
        logger.debug( "その他情報: TimeZone[{}]", graphApiInfo.getTimeZone() );

        graphApiInfo.setUserNumber( Integer.parseInt( properties.getProperty("graph.api.getschedule.users" )));
        graphApiInfo.setRequestNumber( Integer.parseInt( properties.getProperty("graph.api.getschedule.requests")));
        graphApiInfo.setDelegate( properties.getProperty("graph.api.getschedule.delegate"));

        logger.debug( "GetSchedule情報: ユーザ数/バッチリクエスト[{}]", graphApiInfo.getUserNumber() );
        logger.debug( "GetSchedule情報情報: リクエスト数/バッチリクエスト[{}]", graphApiInfo.getRequestNumber() );
        logger.debug( "GetSchedule情報情報: 代理者ユーザ[{}]", graphApiInfo.getDelegate() );
    }
}
