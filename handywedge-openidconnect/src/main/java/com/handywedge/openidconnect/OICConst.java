package com.handywedge.openidconnect;

/**
 * OpenId Connect関連クラスで使用する定数。
 *
 * @author takeuchi
 */
public class OICConst {

    /*
     * プロパティキー定数
    */

    // Relying Party (RP)サービス情報
    static final String RP_SCHEMA = "rp.schema";
    static final String RP_HOST = "rp.host";
    static final String RP_BASE_PATH = "rp.base.path";
    static final String RP_LOGIN_PATH = "rp.login.path";
    static final String RP_CODE_PATH = "rp.code.path";
    static final String RP_AUTH_PATH = "rp.auth.path";

    static final String SERVICE_CLASS = "service.class";
    static final String OP_METADATA_DOC_URL = "op.metadata.doc.url";

    static final String RESPONSE_TYPE = "response.type";
    static final String RESPONSE_MODE = "response.mode";
    static final String SCOPE = "scope";
    static final String CLIENT_ID = "client.id";

    // Handywedge 情報
    static final String HW_SSO_LOGIN_ENDPOINT = "hw.sso.login.endpoint";

    // Stateホルダー情報
    static final String STATE_HOLDER_REFRESH_SIZE = "state_holder_refresh_size";
    static final String STATE_HOLDER_TERM_SEC = "state.holder.term.sec";

    static final String TAG_NAME_ERROR = "error";
    static final String TAG_NAME_ERROR_DESCRIPTION = "error_description";

    // ログアウトパス
    static final String OIC_LOGOUT_PATH = "oic.logout.path";

    /* 環境変数キー定数 */
    static final String ENV_AZURE_TENANT_ID = "AZURE_TENANT_ID";
    static final String ENV_CLIENT_ID = "CLIENT_ID";
    static final String ENV_RELYING_PARTY_URL = "RELYING_PARTY_URL";
    static final String ENV_HW_OIC_LOGIN_ENDPOINT_CONTEXTPATH = "HW_OIC_LOGIN_ENDPOINT_CONTEXTPATH";
    static final String ENV_OPENID_PROVIDER_TYPE = "OPENID_PROVIDER_TYPE";
}
