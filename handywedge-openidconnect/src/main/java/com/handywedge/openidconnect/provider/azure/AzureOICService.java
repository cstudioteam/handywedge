package com.handywedge.openidconnect.provider.azure;

import com.handywedge.openidconnect.OICException;
import com.handywedge.openidconnect.entity.OICJwt;
import com.handywedge.openidconnect.OICService;
import com.handywedge.openidconnect.entity.OICUserInfo;

/** Azure AD 認証用のサービスクラス。
 * 
*/
public class AzureOICService extends OICService {

    public AzureOICService() throws OICException {
        super.loadConfig();
    }

    @Override
    protected Class getConfigClass() {
        return AzureOICProviderMetadata.class;
    }
    
    @Override
    protected OICUserInfo getUserInfo(OICJwt jwt) throws OICException {
        
        OICUserInfo userInfo = new OICUserInfo();

        String id = jwt.getPayload().getClaim("upn");
        String name = jwt.getPayload().getClaim("name");
        if (id == null || id.isEmpty()) {
            throw new OICException("UserID is empty.");
        }
        if (name == null || name.isEmpty()) {
            throw new OICException("User name is empty.");
        }
        
        userInfo.setId(id);
        userInfo.setName(name);

        return userInfo;
    }
}
