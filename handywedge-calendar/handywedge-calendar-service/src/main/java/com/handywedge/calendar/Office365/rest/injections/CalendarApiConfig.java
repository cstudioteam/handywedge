package com.handywedge.calendar.Office365.rest.injections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handywedge.calendar.Office365.graph.service.config.GraphProxyInfo;
import com.handywedge.calendar.Office365.graph.service.config.GraphApiInfo;
import com.handywedge.calendar.Office365.graph.service.config.GraphAuthInfo;
import com.handywedge.calendar.Office365.rest.exception.CalendarApiException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
    public void initiate() throws CalendarApiException {
        graphApiInfo = new GraphApiInfo();
        loadProperties();
    }

    private void loadProperties() throws CalendarApiException {
        final Properties appProperties = new Properties();
        try {
            InputStream is = CalendarApiConfig.class.getResourceAsStream(PROPERTY_NAME);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(isr);
            appProperties.load( reader );
            logger.debug( "{}ファイルを読み込みました。", PROPERTY_NAME );
        } catch (IOException e) {
            logger.error( "{}の設定ファイルが読み込めません。", PROPERTY_NAME );
            throw new CalendarApiException(500, String.format("%sの設定ファイルが読み込めません。", PROPERTY_NAME) );
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

        String appId = getEnv("AUTH_APP_ID");
        String secret = getEnv("AUTH_SECRET");
        String tenant = getEnv("AUTH_TENANT");
        String scope = getEnv("AUTH_SCOPE");

        if(StringUtils.isAnyEmpty(appId, secret, tenant, scope)){
            appId = properties.getProperty( "auth.app_id" );
            secret = properties.getProperty( "auth.secret" );
            tenant = properties.getProperty( "auth.tenant" );
            scope = properties.getProperty( "auth.scope" );
        }

        graphAuthInfo.setApplicationId( appId );
        graphAuthInfo.setClientSecret( secret );
        graphAuthInfo.setTenant( tenant );
        graphAuthInfo.setScope( scope );
        graphApiInfo.setAuthInfo(graphAuthInfo);

        logger.debug( "OAuth認証情報: {}", gson.toJson( graphAuthInfo ) );
    }

    /**
     * プロキシ設定情報読み込み
     * @param properties
     */
    public void setGraphProxyInfo(Properties properties) {
        GraphProxyInfo proxyInfo = new GraphProxyInfo();

        String envUseProxy = getEnv( "PROXY_USE" );
        if(StringUtils.isEmpty(envUseProxy)) {
            String propUseProxy = properties.getProperty( "proxy.use" );
            if(StringUtils.isBlank(propUseProxy) || StringUtils.equalsIgnoreCase( propUseProxy, "false" )){
                graphApiInfo.setUseProxy(false);
            }else{
                graphApiInfo.setUseProxy(true);
                proxyInfo.setType( transProxyType(properties.getProperty("proxy.type")) );
                proxyInfo.setHost( properties.getProperty( "proxy.host" ) );
                proxyInfo.setPort( Integer.parseInt( properties.getProperty("proxy.port")) );
                graphApiInfo.setProxyInfo( proxyInfo );
            }
        }else if(StringUtils.equalsIgnoreCase( envUseProxy, "false" )){
            graphApiInfo.setUseProxy(false);
        }else {
            graphApiInfo.setUseProxy(true);
            proxyInfo.setType( transProxyType(getEnv("PROXY_TYPE")) );
            proxyInfo.setHost( getEnv( "PROXY_HOST" ) );
            proxyInfo.setPort( Integer.parseInt( getEnv("PROXY_PORT")) );
            graphApiInfo.setProxyInfo( proxyInfo );
        }

        logger.debug( "プロキシー利用有無: {}", graphApiInfo.isUseProxy() );
        logger.debug( "プロキシー情報: {}", gson.toJson( proxyInfo ) );
    }

    private Proxy.Type transProxyType(String type){
        Proxy.Type returnType = Proxy.Type.DIRECT;
        if(StringUtils.isEmpty(type)){
            returnType = Proxy.Type.DIRECT;
        } else if(type.equalsIgnoreCase( "HTTP" )){
            returnType = Proxy.Type.HTTP;
        }else if(type.equalsIgnoreCase( "SOCKS" )){
            returnType = Proxy.Type.SOCKS;
        }else if(type.equalsIgnoreCase( "DIRECT" )){
            returnType = Proxy.Type.DIRECT;
        }else{
            returnType = Proxy.Type.DIRECT;
        }

        return returnType;
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

        graphApiInfo.setRequestTimeoutForBatch( Integer.parseInt( properties.getProperty("graph.api.request.timeout.batch")));
        graphApiInfo.setRequestTimeoutForRead( Integer.parseInt( properties.getProperty("graph.api.request.timeout.read")));
        graphApiInfo.setRequestTimeoutForWrite( Integer.parseInt( properties.getProperty("graph.api.request.timeout.write")));

        logger.debug( "バッチ: リクエストタイムアウト[{}]", graphApiInfo.getRequestTimeoutForBatch() );
        logger.debug( "参照系: リクエストタイムアウト[{}]", graphApiInfo.getRequestTimeoutForRead() );
        logger.debug( "更新系: リクエストタイムアウト[{}]", graphApiInfo.getRequestTimeoutForWrite() );


        String userNumber = getEnv("GETSCHEDULE_USERS" );
        String requestNumber =  getEnv("GETSCHEDULE_REQUESRS");

        if(!StringUtils.isNumeric(userNumber)){
            userNumber = properties.getProperty("graph.api.getschedule.users" );
            if(!StringUtils.isNumeric(userNumber)){
                userNumber = "5";
            }
        }
        if(!StringUtils.isNumeric(requestNumber)){
            requestNumber = properties.getProperty("graph.api.getschedule.requests" );
            if(!StringUtils.isNumeric(requestNumber)){
                requestNumber = "5";
            }
        }
        graphApiInfo.setUserNumber( Integer.parseInt( userNumber));
        graphApiInfo.setRequestNumber( Integer.parseInt( requestNumber));


        String delegate = getEnv("GETSCHEDULE_DELEGATE");
        if(StringUtils.isEmpty(delegate)){
            delegate = properties.getProperty("graph.api.getschedule.delegate");
        }

        graphApiInfo.setDelegate( delegate );

        logger.debug( "GetSchedule情報: ユーザ数/バッチリクエスト[{}]", graphApiInfo.getUserNumber() );
        logger.debug( "GetSchedule情報情報: リクエスト数/バッチリクエスト[{}]", graphApiInfo.getRequestNumber() );
        logger.debug( "GetSchedule情報情報: 代理者ユーザ[{}]", graphApiInfo.getDelegate() );
    }

    private String getEnv(String key){
        logger.debug( "{}: {}", key, System.getenv(key) );
        return System.getenv(key);
    }
}
