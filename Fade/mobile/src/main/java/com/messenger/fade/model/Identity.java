package com.messenger.fade.model;

import java.sql.Timestamp;

import org.json.JSONObject;

/**
 * each User object can have one or more identities,
 * which are 3rd party identities, e.g. fb, twitter, 
 * ig, etc
 * @author kkawai
 *
 */
public final class Identity extends DomainObject{
	
	public static final int IDENTITY_FB = 0;
	public static final int IDENTITY_GPLUS = 1;
	public static final int IDENTITY_IG = 2;
	public static final int IDENTITY_TWITTER = 3;
	public static final int IDENTITY_KIK = 4;
	
	private long id;
	private long userId;
	private int type;
	private String accessToken;
	private String tokenSecret;
	private int status;
	private String email;
	private String username;
	private String thirdPartyId;
	private Timestamp tstamp;
	
	
	public Identity() {
		super();
	}

	private static final long serialVersionUID = 3121394108242111220L;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getTokenSecret() {
		return tokenSecret;
	}
	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Timestamp getTstamp() {
		return tstamp;
	}
	public void setTstamp(Timestamp tstamp) {
		this.tstamp = tstamp;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setThirdPartyId(final String thirdPartyId) {
		this.thirdPartyId = thirdPartyId;
	}
	public String getThirdPartyId() {
		return thirdPartyId;
	}
	
	public static Identity from(final JSONObject object) {
		final Identity identity = new Identity();
		identity.setAccessToken(object.optString("accessToken"));
		identity.setTokenSecret(object.optString("tokenSecret"));
		identity.setEmail(object.optString("email"));
		identity.setId(object.optLong("id"));
		identity.setStatus(object.optInt("status"));
		identity.setThirdPartyId(object.optString("thirdPartyId"));
		identity.setType(object.optInt("type"));
		identity.setUserId(object.optLong("userId"));
		identity.setUsername(object.optString("username"));
		return identity;
	}	

}
