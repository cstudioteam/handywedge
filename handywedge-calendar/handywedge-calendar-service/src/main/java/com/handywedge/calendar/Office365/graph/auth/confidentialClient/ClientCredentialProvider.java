package com.handywedge.calendar.Office365.graph.auth.confidentialClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handywedge.calendar.Office365.graph.auth.BaseAuthentication;
import com.handywedge.calendar.Office365.graph.exceptions.GraphApiException;
import com.handywedge.calendar.Office365.graph.service.config.GraphApiInfo;
import com.handywedge.calendar.Office365.graph.auth.AuthConstants;
import com.handywedge.calendar.Office365.graph.auth.ProxyConnectionClient;
import com.handywedge.calendar.Office365.graph.auth.enums.NationalCloud;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.httpcore.ICoreAuthenticationProvider;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import java.util.Arrays;
import java.util.List;

public class ClientCredentialProvider extends BaseAuthentication implements IAuthenticationProvider, ICoreAuthenticationProvider{

    private static final Logger logger = LogManager.getLogger();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ClientCredentialProvider(GraphApiInfo info){

        this(
                info.getAuthInfo().getApplicationId(),
                Arrays.asList(info.getAuthInfo().getScope()),
                info.getAuthInfo().getClientSecret(),
                info.getAuthInfo().getTenant(),
                NationalCloud.Global,
                info
        );

    }
    /*
     * Client credential provider instance using client secret
     *
     * @param clientId Client ID of application
     * @param scopes Scopes that application need to access protected resources
     * @param clientSecret Client secret of application
     * @param tenant The tenant GUID or friendly name format or common
     *
     */
    public ClientCredentialProvider(String clientId,
                                    List<String> scopes,
                                    String clientSecret,
                                    String tenant,
                                    NationalCloud nationalCloud,
                                    GraphApiInfo info) {
        super(	scopes,
                clientId,
                GetAuthority(nationalCloud == null? NationalCloud.Global: nationalCloud, tenant),
                null,
                nationalCloud == null? NationalCloud.Global: nationalCloud,
                tenant,
                clientSecret,
                info);
    }

    @Override
    public void authenticateRequest(IHttpRequest request) {
        String accessToken = getAcccessToken();
        request.addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + accessToken);
    }

    @Override
    public Request authenticateRequest(Request request) {
        String accessToken = getAcccessToken();
        return request.newBuilder().addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER + accessToken).build();
    }

    String getAcccessToken() {
        String accessToken = "";
        try {
            long duration = System.currentTimeMillis() - getStartTime();
            if(getResponse()!=null && duration>0 && duration< getResponse().getExpiresIn()*1000) {
                accessToken = getResponse().getAccessToken();
            } else {
                OAuthClientRequest authRequest = getTokenRequestMessage();
                accessToken = getAccessTokenNewRequest(authRequest);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return accessToken;
    }

    /*
     * Create the token request message
     *
     * @return The token request message
     */
    public OAuthClientRequest getTokenRequestMessage() throws OAuthSystemException {
        String tokenUrl = getAuthority() + AuthConstants.TOKEN_ENDPOINT;
        TokenRequestBuilder token = OAuthClientRequest.
                tokenLocation(tokenUrl)
                .setClientId(getClientId())
                .setGrantType(GrantType.CLIENT_CREDENTIALS)
                .setScope(getScopesAsString());
        if(getClientSecret() != null) {
            token.setClientSecret(getClientSecret());
        }
        return token.buildBodyMessage();
    }

    /*
     * Call using request to get response containing access token
     *
     * @param request The request to execute
     * @return The access token in response
     */
    public String getAccessTokenNewRequest(OAuthClientRequest request) throws OAuthSystemException, OAuthProblemException {
        OAuthClient oAuthClient = null;
        logger.debug( "apiInfo: {}", gson.toJson( getGraphApiConfigInfo() ) );
        if(getGraphApiConfigInfo().isUseProxy()) {
            oAuthClient = new OAuthClient(new ProxyConnectionClient(getGraphApiConfigInfo().getProxyInfo()));
        }else {
            oAuthClient = new OAuthClient( new URLConnectionClient() );
        }

        setStartTime(System.currentTimeMillis());
        setResponse(oAuthClient.accessToken(request));
        return getResponse().getAccessToken();
    }
}
