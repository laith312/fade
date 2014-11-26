package com.messenger.fade.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

public final class UserMedia extends DomainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 40442225221111L;
	
	public static final byte MEDIA_TYPE_IMAGE = 0;
	public static final byte MEDIA_TYPE_AUDIO = 1;
	public static final byte MEDIA_TYPE_VIDEO = 2;
	
	private int id;
	private int userid;
	private short num;
	private byte mediaType;
	private int likes;
	private String caption;
	private Timestamp tstamp;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public short getNum() {
		return num;
	}
	public void setNum(short num) {
		this.num = num;
	}
	public byte getMediaType() {
		return mediaType;
	}
	public void setMediaType(byte mediaType) {
		this.mediaType = mediaType;
	}
	public int getLikes() {
		return likes;
	}
	public void setLikes(int likes) {
		this.likes = likes;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public Timestamp getTstamp() {
		return tstamp;
	}
	public void setTstamp(Timestamp tstamp) {
		this.tstamp = tstamp;
	}
	
	/**
	 * Note: Not a bean method.  For now, only image media type is supported.
	 * @return
	 */
	public String getMediaUrl() {
		if (mediaType == 0) {
			return "http://pics.fade.s3.amazonaws.com/"+userid+'_'+num+".jpg";
		}
		return null;
	}
	
	public static UserMedia from(final JSONObject object) {
		final UserMedia media = new UserMedia();
		media.setId(object.optInt("id"));
		media.setCaption(object.optString("caption"));
		media.setLikes(object.optInt("likes"));
		media.setNum((short)object.optInt("num"));
		media.setMediaType((byte)object.optInt("mediaType"));
		media.setUserid(object.optInt("userid"));
		return media;
	}
	
	public static JSONArray from(final List<UserMedia> list) {
		final JSONArray array = new JSONArray();
		for (final UserMedia userMedia : list) {
			array.put(userMedia.toJSON());
		}
		return array;
	}
}
