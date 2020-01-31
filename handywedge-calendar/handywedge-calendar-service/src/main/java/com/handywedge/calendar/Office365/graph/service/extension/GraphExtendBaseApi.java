package com.handywedge.calendar.Office365.graph.service.extension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handywedge.calendar.Office365.graph.service.config.GraphProxyInfo;
import com.handywedge.calendar.Office365.graph.auth.confidentialClient.ClientCredentialProvider;
import com.handywedge.calendar.Office365.graph.service.interceptors.GzipRequestInterceptor;
import com.microsoft.graph.httpcore.AuthenticationHandler;
import com.microsoft.graph.httpcore.TelemetryHandler;

import com.handywedge.calendar.Office365.graph.service.config.GraphApiInfo;
import com.handywedge.calendar.Office365.graph.service.config.GraphAuthInfo;
import okhttp3.ConnectionPool;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GraphExtendBaseApi {
    private static final Logger logger = LogManager.getLogger( );

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final long GRAPH_DEFAULT_TIMEOUT = 15;

    GraphApiInfo apiInfo = null;
    private GraphAuthInfo authInfo = null;
    private GraphProxyInfo proxyInfo = null;

    public GraphExtendBaseApi(GraphApiInfo api) {
        this.apiInfo = api;
        this.authInfo = api.getAuthInfo();
        this.proxyInfo = api.getProxyInfo();
    }

    public ClientCredentialProvider getAuthenticationProvider(){
        long startTime = System.currentTimeMillis();

        ClientCredentialProvider clientCredentialProvider = new ClientCredentialProvider( apiInfo );

        long endTime = System.currentTimeMillis();
        logger.info("[AzureAD認証処理] 処理時間：{}ms", (endTime - startTime));

        return clientCredentialProvider;
    }

    public String getTimeZone(){
        return apiInfo.getTimeZone();
    }

    public Headers getHeaders() {
        String[] headerArray = new String[6];
        headerArray[0] = "Content-Type";
        headerArray[1] = "application/json";
        headerArray[2] = "Prefer";
        headerArray[3] = String.format("outlook.timezone=\"%s\"", getTimeZone());
        headerArray[4] = "Prefer";
        headerArray[5] = "IdType=\"ImmutableId\"";

        Headers headers = Headers.of(headerArray);
        logger.debug( "Request Header: {}", headers );
        return headers;
    }

    public OkHttpClient getGraphClient(long timeout){

        OkHttpClient client = new OkHttpClient();

        AuthenticationHandler authenticationHandler = new AuthenticationHandler( getAuthenticationProvider());
        Interceptor[] interceptors = null;
        if(ObjectUtils.isEmpty( getHeaders().get( "Content-Encoding" ))
                || !getHeaders().get( "Content-Encoding" ).contains( "gzip" )){
            interceptors = new Interceptor[]{authenticationHandler, new TelemetryHandler()};
        }else{
            interceptors = new Interceptor[]{authenticationHandler, new TelemetryHandler(), new GzipRequestInterceptor()};
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (interceptors != null) {
            int interceptorNum = interceptors.length;

            for(int i = 0; i < interceptorNum; ++i) {
                Interceptor interceptor = interceptors[i];
                if (interceptor != null) {
                    builder.addInterceptor(interceptor);
                }
            }
        }
        logger.debug("### DEBUG ### Graphリクエスストタイムアウト={}", timeout);
        builder.connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(20, 5L, TimeUnit.MINUTES))
                .followRedirects(false);

        if(apiInfo.isUseProxy()) {
            builder.proxy( getProxy() );
        }

        return builder.build();
    }

    public long getBatchRequestTimeout(){
        return apiInfo.getRequestTimeoutForBatch();
    }

    public long getReadRequestTimeout(){
        return apiInfo.getRequestTimeoutForRead();
    }

    public long getWriteRequestTimeout(){
        return apiInfo.getRequestTimeoutForWrite();
    }

    /**
     * Proxyオブジェクト生成
     * @return
     */
    private Proxy getProxy(){
        return new Proxy(proxyInfo.getType(), new InetSocketAddress( proxyInfo.getHost(), proxyInfo.getPort() ));
    }

    /**
     * 設定ファイルより代理人ユーザを取得
     * @return　代理人ユーザ
     */
    public String getDelegate(){
        return apiInfo.getDelegate();
    }
}
