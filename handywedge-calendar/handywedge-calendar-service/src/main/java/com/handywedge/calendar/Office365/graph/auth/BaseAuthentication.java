package com.handywedge.calendar.Office365.graph.auth;

import com.handywedge.calendar.Office365.graph.exceptions.GraphApiException;
import com.handywedge.calendar.Office365.graph.service.config.GraphApiInfo;
import com.handywedge.calendar.Office365.graph.auth.enums.NationalCloud;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import java.util.HashMap;
import java.util.List;

public class BaseAuthentication {

    private List<String> scopes;
    private String clientId;
    private String authority;
    private String clientSecret;
    private long startTime;
    private NationalCloud nationalCloud;
    private String tenant;
    private String redirectUri = "https://localhost:8080";
    private OAuthJSONAccessTokenResponse response;

    private static final Logger logger = LogManager.getLogger();
    public GraphApiInfo apiInfo = null;

    public BaseAuthentication(
            List<String> scopes,
            String clientId,
            String authority,
            String redirectUri,
            NationalCloud nationalCloud,
            String tenant,
            String clientSecret,
            GraphApiInfo info)
    {
        this.scopes = scopes;
        this.clientId = clientId;
        this.authority = authority;
        this.redirectUri = redirectUri;
        this.nationalCloud = nationalCloud;
        this.tenant = tenant;
        this.clientSecret = clientSecret;
        this.apiInfo = info;
    }

    protected GraphApiInfo getGraphApiConfigInfo(){
        return this.apiInfo;
    }

    protected static HashMap<String, String> CloudList = new HashMap<String, String>()
    {{
        put( "Global", "https://login.microsoftonline.com/" );
        put( "China", "https://login.chinacloudapi.cn/" );
        put( "Germany", "https://login.microsoftonline.de/" );
        put( "UsGovernment", "https://login.microsoftonline.us/" );
    }};

    protected static String GetAuthority(NationalCloud authorityEndpoint, String tenant)
    {
        return CloudList.get(authorityEndpoint.toString()) + tenant;
    }

    protected String getScopesAsString() {
        StringBuilder scopeString = new StringBuilder();
        for(String s : this.scopes) {
            scopeString.append(s);
            scopeString.append(" ");
        }
        return scopeString.toString();
    }

    protected String getAccessTokenSilent() throws GraphApiException {
        long durationPassed = System.currentTimeMillis() - startTime;
        if(this.response == null || durationPassed < 0) return null;
        try {
            if(durationPassed >= response.getExpiresIn()*1000) {
                TokenRequestBuilder token = OAuthClientRequest.
                        tokenLocation(this.authority + AuthConstants.TOKEN_ENDPOINT)
                        .setClientId(this.clientId)
                        .setScope(getScopesAsString())
                        .setRefreshToken(response.getRefreshToken())
                        .setGrantType(GrantType.REFRESH_TOKEN)
                        .setScope(getScopesAsString())
                        .setRedirectURI(redirectUri);
                if(this.clientSecret != null) {
                    token.setClientSecret(this.clientSecret);
                }

                OAuthClientRequest request = token.buildBodyMessage();

                OAuthClient oAuthClient = null;
                if(apiInfo.isUseProxy()) {
                    oAuthClient = new OAuthClient(new ProxyConnectionClient(apiInfo.getProxyInfo()));
                }else {
                    oAuthClient = new OAuthClient( new URLConnectionClient() );
                }
                this.startTime = System.currentTimeMillis();

                this.response = oAuthClient.accessToken(request);
                return response.getAccessToken();
            } else {
                return response.getAccessToken();
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new GraphApiException( "OauthError" , e.getMessage() );
        }
    }

    protected String getAuthority() {
        return this.authority;
    }

    protected String getClientId() {
        return this.clientId;
    }

    protected String getClientSecret() {
        return this.clientSecret;
    }

    protected String getRedirectUri() {
        return this.redirectUri;
    }

    protected void setResponse(OAuthJSONAccessTokenResponse response) {
        this.response = response;
    }

    protected OAuthJSONAccessTokenResponse getResponse() {
        return this.response;
    }

    protected long getStartTime() {
        return this.startTime;
    }

    protected void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    protected NationalCloud getNationalCloud() {
        return this.nationalCloud;
    }

    protected String getTenant() {
        return this.tenant;
    }

}
