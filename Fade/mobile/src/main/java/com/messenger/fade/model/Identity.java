package com.messenger.fade.model;

public class Identity {

    private String identityId;
    private String UserId;
    private String type;
    private String typeId;
    private String AccessToken;
    private String TokenSecret;

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(final String identityId) {
        this.identityId = identityId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(final String userId) {
        UserId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(final String typeId) {
        this.typeId = typeId;
    }

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(final String accessToken) {
        AccessToken = accessToken;
    }

    public String getTokenSecret() {
        return TokenSecret;
    }

    public void setTokenSecret(final String tokenSecret) {
        TokenSecret = tokenSecret;
    }
}
