/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.openidconnect.entity;

/**
 * OpenId Providerのコンフィグレーションメタデータ。
 *
 * @author takeuchi
 */
public class OICProviderMetadata {

  private String issuer;
  private String authorization_endpoint;
  private String token_endpoint;
  private String[] token_endpoint_auth_methods_supported;
  private String[] token_endpoint_auth_signing_alg_values_supported;
  private String userinfo_endpoint;
  private String check_session_iframe;
  private String end_session_endpoint;
  private String jwks_uri;
  private String registration_endpoint;
  private String[] scopes_supported;
  private String[] response_types_supported;
  private String[] response_modes_supported;
  private String[] acr_values_supported;
  private String[] subject_types_supported;
  private String[] userinfo_signing_alg_values_supported;
  private String[] userinfo_encryption_alg_values_supported;
  private String[] userinfo_encryption_enc_values_supported;
  private String[] id_token_signing_alg_values_supported;
  private String[] id_token_encryption_alg_values_supported;
  private String[] id_token_encryption_enc_values_supported;
  private String[] request_object_signing_alg_values_supported;
  private String[] display_values_supported;
  private String[] claim_types_supported;
  private String[] claims_supported;
  private String claims_parameter_supported;
  private String service_documentation;
  private String[] ui_locales_supported;
  private String[] grant_types_supported;
  private String op_policy_uri;
  private String op_tos_uri;
  private String revocation_endpoint;
  private String revocation_endpoint_auth_methods_supported;
  private String revocation_endpoint_auth_signing_alg_values_supported;
  private String introspection_endpoint;
  private String introspection_endpoint_auth_methods_supported;
  private String[] introspection_endpoint_auth_signing_alg_values_supported;
  private String[] code_challenge_methods_supported;
  private String[] request_object_encryption_alg_values_supported;
  private String[] request_object_encryption_enc_values_supported;
  private String claims_locales_supported;
  private String request_parameter_supported;
  private String request_uri_parameter_supported;
  private String require_request_uri_registration;
  private String http_logout_supported;
  private String frontchannel_logout_supported;
  private String tenant_region_scope;
  private String cloud_instance_name;
  private String cloud_graph_host_name;
  private String rbac_url;

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public String getAuthorization_endpoint() {
    return authorization_endpoint;
  }

  public void setAuthorization_endpoint(String authorization_endpoint) {
    this.authorization_endpoint = authorization_endpoint;
  }

  public String getToken_endpoint() {
    return token_endpoint;
  }

  public void setToken_endpoint(String token_endpoint) {
    this.token_endpoint = token_endpoint;
  }

  public String[] getToken_endpoint_auth_methods_supported() {
    return token_endpoint_auth_methods_supported;
  }

  public void setToken_endpoint_auth_methods_supported(
      String[] token_endpoint_auth_methods_supported) {
    this.token_endpoint_auth_methods_supported = token_endpoint_auth_methods_supported;
  }

  public String[] getToken_endpoint_auth_signing_alg_values_supported() {
    return token_endpoint_auth_signing_alg_values_supported;
  }

  public void setToken_endpoint_auth_signing_alg_values_supported(
      String[] token_endpoint_auth_signing_alg_values_supported) {
    this.token_endpoint_auth_signing_alg_values_supported =
        token_endpoint_auth_signing_alg_values_supported;
  }

  public String getUserinfo_endpoint() {
    return userinfo_endpoint;
  }

  public void setUserinfo_endpoint(String userinfo_endpoint) {
    this.userinfo_endpoint = userinfo_endpoint;
  }

  public String getCheck_session_iframe() {
    return check_session_iframe;
  }

  public void setCheck_session_iframe(String check_session_iframe) {
    this.check_session_iframe = check_session_iframe;
  }

  public String getEnd_session_endpoint() {
    return end_session_endpoint;
  }

  public void setEnd_session_endpoint(String end_session_endpoint) {
    this.end_session_endpoint = end_session_endpoint;
  }

  public String getJwks_uri() {
    return jwks_uri;
  }

  public void setJwks_uri(String jwks_uri) {
    this.jwks_uri = jwks_uri;
  }

  public String getRegistration_endpoint() {
    return registration_endpoint;
  }

  public void setRegistration_endpoint(String registration_endpoint) {
    this.registration_endpoint = registration_endpoint;
  }

  public String[] getScopes_supported() {
    return scopes_supported;
  }

  public void setScopes_supported(String[] scopes_supported) {
    this.scopes_supported = scopes_supported;
  }

  public String[] getResponse_types_supported() {
    return response_types_supported;
  }

  public void setResponse_types_supported(String[] response_types_supported) {
    this.response_types_supported = response_types_supported;
  }

  public String[] getResponse_modes_supported() {
    return response_modes_supported;
  }

  public void setResponse_modes_supported(String[] response_modes_supported) {
    this.response_modes_supported = response_modes_supported;
  }

  public String[] getAcr_values_supported() {
    return acr_values_supported;
  }

  public void setAcr_values_supported(String[] acr_values_supported) {
    this.acr_values_supported = acr_values_supported;
  }

  public String[] getSubject_types_supported() {
    return subject_types_supported;
  }

  public void setSubject_types_supported(String[] subject_types_supported) {
    this.subject_types_supported = subject_types_supported;
  }

  public String[] getUserinfo_signing_alg_values_supported() {
    return userinfo_signing_alg_values_supported;
  }

  public void setUserinfo_signing_alg_values_supported(
      String[] userinfo_signing_alg_values_supported) {
    this.userinfo_signing_alg_values_supported = userinfo_signing_alg_values_supported;
  }

  public String[] getUserinfo_encryption_alg_values_supported() {
    return userinfo_encryption_alg_values_supported;
  }

  public void setUserinfo_encryption_alg_values_supported(
      String[] userinfo_encryption_alg_values_supported) {
    this.userinfo_encryption_alg_values_supported = userinfo_encryption_alg_values_supported;
  }

  public String[] getUserinfo_encryption_enc_values_supported() {
    return userinfo_encryption_enc_values_supported;
  }

  public void setUserinfo_encryption_enc_values_supported(
      String[] userinfo_encryption_enc_values_supported) {
    this.userinfo_encryption_enc_values_supported = userinfo_encryption_enc_values_supported;
  }

  public String[] getId_token_signing_alg_values_supported() {
    return id_token_signing_alg_values_supported;
  }

  public void setId_token_signing_alg_values_supported(
      String[] id_token_signing_alg_values_supported) {
    this.id_token_signing_alg_values_supported = id_token_signing_alg_values_supported;
  }

  public String[] getId_token_encryption_alg_values_supported() {
    return id_token_encryption_alg_values_supported;
  }

  public void setId_token_encryption_alg_values_supported(
      String[] id_token_encryption_alg_values_supported) {
    this.id_token_encryption_alg_values_supported = id_token_encryption_alg_values_supported;
  }

  public String[] getId_token_encryption_enc_values_supported() {
    return id_token_encryption_enc_values_supported;
  }

  public void setId_token_encryption_enc_values_supported(
      String[] id_token_encryption_enc_values_supported) {
    this.id_token_encryption_enc_values_supported = id_token_encryption_enc_values_supported;
  }

  public String[] getRequest_object_signing_alg_values_supported() {
    return request_object_signing_alg_values_supported;
  }

  public void setRequest_object_signing_alg_values_supported(
      String[] request_object_signing_alg_values_supported) {
    this.request_object_signing_alg_values_supported = request_object_signing_alg_values_supported;
  }

  public String[] getDisplay_values_supported() {
    return display_values_supported;
  }

  public void setDisplay_values_supported(String[] display_values_supported) {
    this.display_values_supported = display_values_supported;
  }

  public String[] getClaim_types_supported() {
    return claim_types_supported;
  }

  public void setClaim_types_supported(String[] claim_types_supported) {
    this.claim_types_supported = claim_types_supported;
  }

  public String[] getClaims_supported() {
    return claims_supported;
  }

  public void setClaims_supported(String[] claims_supported) {
    this.claims_supported = claims_supported;
  }

  public String getClaims_parameter_supported() {
    return claims_parameter_supported;
  }

  public void setClaims_parameter_supported(String claims_parameter_supported) {
    this.claims_parameter_supported = claims_parameter_supported;
  }

  public String getService_documentation() {
    return service_documentation;
  }

  public void setService_documentation(String service_documentation) {
    this.service_documentation = service_documentation;
  }

  public String[] getUi_locales_supported() {
    return ui_locales_supported;
  }

  public void setUi_locales_supported(String[] ui_locales_supported) {
    this.ui_locales_supported = ui_locales_supported;
  }

  public String[] getGrant_types_supported() {
    return grant_types_supported;
  }

  public void setGrant_types_supported(String[] grant_types_supported) {
    this.grant_types_supported = grant_types_supported;
  }

  public String getOp_policy_uri() {
    return op_policy_uri;
  }

  public void setOp_policy_uri(String op_policy_uri) {
    this.op_policy_uri = op_policy_uri;
  }

  public String getOp_tos_uri() {
    return op_tos_uri;
  }

  public void setOp_tos_uri(String op_tos_uri) {
    this.op_tos_uri = op_tos_uri;
  }

  public String getRevocation_endpoint() {
    return revocation_endpoint;
  }

  public void setRevocation_endpoint(String revocation_endpoint) {
    this.revocation_endpoint = revocation_endpoint;
  }

  public String getRevocation_endpoint_auth_methods_supported() {
    return revocation_endpoint_auth_methods_supported;
  }

  public void setRevocation_endpoint_auth_methods_supported(
      String revocation_endpoint_auth_methods_supported) {
    this.revocation_endpoint_auth_methods_supported = revocation_endpoint_auth_methods_supported;
  }

  public String getRevocation_endpoint_auth_signing_alg_values_supported() {
    return revocation_endpoint_auth_signing_alg_values_supported;
  }

  public void setRevocation_endpoint_auth_signing_alg_values_supported(
      String revocation_endpoint_auth_signing_alg_values_supported) {
    this.revocation_endpoint_auth_signing_alg_values_supported =
        revocation_endpoint_auth_signing_alg_values_supported;
  }

  public String getIntrospection_endpoint() {
    return introspection_endpoint;
  }

  public void setIntrospection_endpoint(String introspection_endpoint) {
    this.introspection_endpoint = introspection_endpoint;
  }

  public String getIntrospection_endpoint_auth_methods_supported() {
    return introspection_endpoint_auth_methods_supported;
  }

  public void setIntrospection_endpoint_auth_methods_supported(
      String introspection_endpoint_auth_methods_supported) {
    this.introspection_endpoint_auth_methods_supported =
        introspection_endpoint_auth_methods_supported;
  }

  public String[] getIntrospection_endpoint_auth_signing_alg_values_supported() {
    return introspection_endpoint_auth_signing_alg_values_supported;
  }

  public void setIntrospection_endpoint_auth_signing_alg_values_supported(
      String[] introspection_endpoint_auth_signing_alg_values_supported) {
    this.introspection_endpoint_auth_signing_alg_values_supported =
        introspection_endpoint_auth_signing_alg_values_supported;
  }

  public String[] getCode_challenge_methods_supported() {
    return code_challenge_methods_supported;
  }

  public void setCode_challenge_methods_supported(String[] code_challenge_methods_supported) {
    this.code_challenge_methods_supported = code_challenge_methods_supported;
  }

  public String[] getRequest_object_encryption_alg_values_supported() {
    return request_object_encryption_alg_values_supported;
  }

  public void setRequest_object_encryption_alg_values_supported(
      String[] request_object_encryption_alg_values_supported) {
    this.request_object_encryption_alg_values_supported =
        request_object_encryption_alg_values_supported;
  }

  public String[] getRequest_object_encryption_enc_values_supported() {
    return request_object_encryption_enc_values_supported;
  }

  public void setRequest_object_encryption_enc_values_supported(
      String[] request_object_encryption_enc_values_supported) {
    this.request_object_encryption_enc_values_supported =
        request_object_encryption_enc_values_supported;
  }

  public String getClaims_locales_supported() {
    return claims_locales_supported;
  }

  public void setClaims_locales_supported(String claims_locales_supported) {
    this.claims_locales_supported = claims_locales_supported;
  }

  public String getRequest_parameter_supported() {
    return request_parameter_supported;
  }

  public void setRequest_parameter_supported(String request_parameter_supported) {
    this.request_parameter_supported = request_parameter_supported;
  }

  public String getRequest_uri_parameter_supported() {
    return request_uri_parameter_supported;
  }

  public void setRequest_uri_parameter_supported(String request_uri_parameter_supported) {
    this.request_uri_parameter_supported = request_uri_parameter_supported;
  }

  public String getRequire_request_uri_registration() {
    return require_request_uri_registration;
  }

  public void setRequire_request_uri_registration(String require_request_uri_registration) {
    this.require_request_uri_registration = require_request_uri_registration;
  }

  public String getHttp_logout_supported() {
    return http_logout_supported;
  }

  public void setHttp_logout_supported(String http_logout_supported) {
    this.http_logout_supported = http_logout_supported;
  }

  public String getFrontchannel_logout_supported() {
    return frontchannel_logout_supported;
  }

  public void setFrontchannel_logout_supported(String frontchannel_logout_supported) {
    this.frontchannel_logout_supported = frontchannel_logout_supported;
  }

  public String getTenant_region_scope() {
    return tenant_region_scope;
  }

  public void setTenant_region_scope(String tenant_region_scope) {
    this.tenant_region_scope = tenant_region_scope;
  }

  public String getCloud_instance_name() {
    return cloud_instance_name;
  }

  public void setCloud_instance_name(String cloud_instance_name) {
    this.cloud_instance_name = cloud_instance_name;
  }

  public String getCloud_graph_host_name() {
    return cloud_graph_host_name;
  }

  public void setCloud_graph_host_name(String cloud_graph_host_name) {
    this.cloud_graph_host_name = cloud_graph_host_name;
  }

  public String getRbac_url() {
    return rbac_url;
  }

  public void setRbac_url(String rbac_url) {
    this.rbac_url = rbac_url;
  }
}
