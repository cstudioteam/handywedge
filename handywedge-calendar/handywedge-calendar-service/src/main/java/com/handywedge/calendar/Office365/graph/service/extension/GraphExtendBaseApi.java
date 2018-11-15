package com.handywedge.calendar.Office365.graph.service.extension;

import com.handywedge.calendar.Office365.graph.service.config.GraphProxyInfo;
import com.handywedge.calendar.Office365.graph.auth.confidentialClient.ClientCredentialProvider;
import com.handywedge.calendar.Office365.graph.service.interceptors.GzipRequestInterceptor;
import com.microsoft.graph.httpcore.AuthenticationHandler;
import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.httpcore.TelemetryHandler;

import com.handywedge.calendar.Office365.graph.service.config.GraphApiInfo;
import com.handywedge.calendar.Office365.graph.service.config.GraphAuthInfo;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CORBA.TIMEOUT;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GraphExtendBaseApi {
    private static final Logger logger = LogManager.getLogger( );

    private static final Integer GRAPH_CONNECT_TIMEOUT = 15;
    private static final Integer GRAPH_READ_TIMEOUT = 15;
    private static final Integer GRAPH_WRITE_TIMEOUT = 15;

    GraphApiInfo apiInfo = null;
    GraphAuthInfo authInfo = null;
    GraphProxyInfo proxyInfo = null;

    public GraphExtendBaseApi(GraphApiInfo api) {
        this.apiInfo = api;
        this.authInfo = api.getAuthInfo();
        this.proxyInfo = api.getProxyInfo();
    }

    public ClientCredentialProvider getProvider(){
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
        Map headerMap = new HashMap(  );
        headerMap.put( "Content-Type", "application/json" );
        headerMap.put( "Prefer", String.format("outlook.timezone=\"%s\"", getTimeZone()) );

        Headers headers = Headers.of(headerMap);
        return headers;
    }

    public OkHttpClient getGraphClient(){

        AuthenticationHandler authenticationHandler = new AuthenticationHandler(getProvider());
        Interceptor[] interceptors = {authenticationHandler, new GzipRequestInterceptor()};
        if(true) {
            OkHttpClient client = HttpClients.createFromInterceptors( interceptors );
            return client;
        } else{
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

            return builder
                    .connectTimeout(GRAPH_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(GRAPH_WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(GRAPH_READ_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(new TelemetryHandler())
                    .proxy( getProxy() )
                    .build();
        }
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
