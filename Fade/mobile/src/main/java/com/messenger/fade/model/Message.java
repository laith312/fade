package com.messenger.fade.model;

import java.io.Serializable;
import java.util.Date;

public final class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2179651363590969834L;
	public int sqlliteid;

    public String containerid; //my user id + partner id

	public int touserid;
    public String tousername;
    public int fromuserid;
    public String fromusername;
	public boolean isVoice, isDeleteThis;
	public String text;
	public String username; 
	public String deviceid;
    public String photokey;//photo
	public String filekey; //other, e.g. media i.e. voice, video
	public String uniqueid;
	public String picurl;
	public Date read; //read date
	public Date date;//create date
	
	@Override
	public boolean equals(Object o) {
		final Message other = (Message)o;
		return sqlliteid == other.sqlliteid;
	}
	
	@Override
	public int hashCode() {
		return sqlliteid;
	}
}