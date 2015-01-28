package com.messenger.fade.model;

import java.io.Serializable;
import java.util.Date;

public final class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2179651363590969834L;
	public int sqlliteid; //primary key of db entry
	public String containerid; //my id + '_' partner id
    public String partnerusername; //my partner's username (i already know my username)
    public int partneruserid; //my partner's username (i already know my username)
	public String text; //message from either me or my partner
	public String filekey; //amazon file key from either me or my partner
	public String uniqueid; //unique message id
	public Date read; //the date my partner read the message
	public Date createdate;//the date this message was created by either me or my partner
	
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