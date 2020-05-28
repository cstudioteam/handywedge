OpenID Connect
=========================
.. index::
   single: OpenID-Connect


-------------------------------
OpenID Connect 認証サービス
-------------------------------

サービス概要
-----------------
| Handywedge OpenID Connect認証サービスは外部のOpenIDプロバイダに対して認証認可を依頼するサービスプロバイダである。
| OpenID Connectのうちのid_token認証に対応している。

HandywedgeアプリケーションからログインリクエストをリダイレクトされるとOpenIDプロバイダに認証を依頼するためリクエストは
更にOpenIDプロバイダのログインページにリダイレクトされ、ユーザが直接OpenIDプロバイダにサインインすることにより認証が行われる。
OpenIDプロバイダからのレスポンスに含まれるIDトークンからユーザIDを抽出しでHandywedgeにトークンの発行を依頼する。

.. seqdiag::
   :name: seq-openid

   seqdiag {
      span_height = 5;
              ユーザ; 業務プログラム; 認証サービス; OpenIDプロバイダ;

              ユーザ -> 業務プログラム [label="1.ログインリクエスト"];
              ユーザ <-- 業務プログラム [label="2.OpenID-Connectにリダイレクト"];
              ユーザ -> 認証サービス [label="3.ログイン要求", rightnote="14と18の戻り先をクエリで送信"];
              ユーザ <-- 認証サービス [label="4.OpenID-OpenIdプロバイダにリダイレクト"];
              ユーザ -> OpenIDプロバイダ [label="5.認可 リクエスト response_type=id_token"];
              ユーザ <-- OpenIDプロバイダ [label="6. ログイン画面"];
              ユーザ -> OpenIDプロバイダ [label="7. サインイン"];
              ユーザ <-- OpenIDプロバイダ [label="8. ID_TOKEN送信（アプリ登録先にリダイレクト）"];
              ユーザ -> 認証サービス [label="9. ID_TOKEN送信"];
              認証サービス -> OpenIDプロバイダ [label="10. 公開鍵取得"];
              認証サービス <-- OpenIDプロバイダ [label=""];
              認証サービス -> 認証サービス [label="11. ID_TOKEN検証"];
              認証サービス -> OpenIDプロバイダ [label="12. ACCESS_TOKEN取得"];
              認証サービス <-- OpenIDプロバイダ [label=""];
              認証サービス -> OpenIDプロバイダ [label="13. USER_INFO取得"];
              認証サービス <-- OpenIDプロバイダ [label=""];
              認証サービス -> 業務プログラム [label="14. Handywedgeトークン要求"];
              業務プログラム -> 業務プログラム [label="15. Handywedgeトークン発行"];
              認証サービス <-- 業務プログラム [label="16. Handywedgeトークン"];
              ユーザ <-- 認証サービス [label="17. AP Serverにリダイレクト"];
              ユーザ -> 業務プログラム [label="18. ダミーリクエスト", rightnote="クエリストリングでトークン送信"];
              ユーザ <-- 業務プログラム [label="19. トップ画面等", rightnote="トークンをCookieにセット"];

              ユーザ [shape=actor]
              業務プログラム [color=pink]
              認証サービス [color=palegreen]
              OpenIDプロバイダ [color=pink]
   }


使用方法
-----------------
| プロジェクトをビルドして作成されたopenidconnect.warをAPサーバにデプロイして使用する。
| 現バージョンではマルチプロバイダには対応していないためOpenIDプロバイダ毎にデプロイする必要がある。

| Handywedgeアプリケーションはユーザからのログインリクエストを認証サービスにリダイレクトする。
| パラメータに次の値を設定する。

.. csv-table:: パラメータ設定
   :name: tab-paramater
   :header: "パラメータ名", "内容"
   :widths: 30 70

   "api", "Handywedgeのトークン発行API（シーケンス14のURL）"
   "url", "Handywedgeのトップ画面API（シーケンス18のURL）"



プロパティ設定（openid-connect.properties)
------------------------------------------
OpenIDプロバイダや環境に合わせてopenid-connect.propertiesを設定する。

.. csv-table:: プロパティ設定
   :name: tab-properties
   :header: "プロパティ項目名", "内容", "Microsoft Azureでの設定例"
   :widths: 30 30 40

   "openid.provider.type", "OpenIDプロバイダ種別", "Azure"
   "service.class", "OpenIDプロバイダ固有の処理をするサービスクラス名", "com.handywedge.openidconnect.provider.azure.AzureOICService"
   "rp.login.path", "OpenIDプロバイダにリクエストをリダイレクトするパス名", "/login"
   "rp.auth.path", "OpenIDプロバイダからToken返却時にリダイレクトされるパス名", "/auth"
   "op.metadata.doc.url", "OpenID プロバイダメタデータドキュメント取得URL", "https://login.microsoftonline.com/#{azure.tenant.id}/.well-known/openid-configuration"
   "response.type", "OpenIDプロバイダパラメータ", "id_token"
   "response.mode", "OpenIDプロバイダパラメータ", "form_post"
   "scope", "OpenIDプロバイダパラメータ", "openid"
   "hw.sso.login.endpoint", "クライアントのログインURL", "http://ap-server:8080/fw/rest/sso/login"
   "state.holder.term.sec", "ステートホルダーのゴミ掃除するまでの秒数", "1200"
   "oic.logout.path", "ログアウト用URLにリダイレクトさせるためのPATH", "/logout"
   "azure.tenant.id", "Azure AD 固有情報（MicrosoftテナントID）", ""
