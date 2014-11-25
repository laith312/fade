package com.messenger.fade.model;

import org.json.JSONObject;

import java.sql.Timestamp;

public final class User extends DomainObject {
	
	private int id;
	private String username;
	private String password;
	private String fullname;
	private String email;
	private String website;
	private byte age=18;
	private String gender;
	private String bio;
	private byte locationVisibility=1;
	private Timestamp tstamp;
	private int followsCount;
	private int followersCount;
	private String location;
	private double lat;
	private double lon;
	private int mediaCount;
	private String profilePicUrl;
	private byte privacyLevel;

	public User() {
		super();
	}

	private static final long serialVersionUID = 3121394108242466120L;	

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLocation() {
		return location;
	}
	
	public String getAnonymousLocation() {
		return stripStreetAddress(location);
	}

	public void setLocation(String location) {
		if (location != null && location.length() > 128){
			location = location.substring(0,128);
		}
		this.location = location;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		if (website != null && website.length() > 128){
			website = website.substring(0,128);
		}
		this.website = website;
	}

	public byte getAge() {
		return age;
	}

	public void setAge(byte age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		if (bio != null && bio.length() > 512){
			bio = bio.substring(0,512);
		}
		this.bio = bio;
	}

	public byte getLocationVisibility() {
		return locationVisibility;
	}

	public void setLocationVisibility(byte locationVisibility) {
		this.locationVisibility = locationVisibility;
	}

	public Timestamp getTstamp() {
		return tstamp;
	}

	public void setTstamp(Timestamp tstamp) {
		this.tstamp = tstamp;
	}

	public int getFollowsCount() {
		return followsCount;
	}

	public void setFollowsCount(int followsCount) {
		this.followsCount = followsCount;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	/**
	 * Converts the location stored in the database to a user-friendly location
	 * string
	 * 
	 * @param loc
	 * @return
	 */
	private static String stripStreetAddress(final String loc) {
		if (isBlank(loc)) {
			return "";
		}
		final String[] a = loc.split("\\|");
		if (a.length >= 2) {
			final String part1 = stripOutNumbers(a[a.length - 2]);
			final String part2 = stripOutNumbers(a[a.length - 1]);
			return part1 + "|" + part2;
		}
		return "";
	}

	public int getMediaCount() {
		return mediaCount;
	}

	public void setMediaCount(final int mediaCount) {
		this.mediaCount = mediaCount;
	}

	public String getProfilePicUrl() {
		return profilePicUrl;
	}

	public void setProfilePicUrl(String profilePicUrl) {
		if (profilePicUrl != null && profilePicUrl.length() > 128){
			profilePicUrl = null;
		}
		this.profilePicUrl = profilePicUrl;
	}

	/**
	 * 
	 * @param inUser
	 */
	public void copyFrom(final User inUser) {
		super.copyFrom(inUser);
	}
	
	public static int determineGender(final String gender) {
		if (gender == null) {
			return 0;
		}
		if (gender.equals("m")) {
			return 1;
		}
		if (gender.equals("f")) {
			return 2;
		}
		return 0;
	}

	public byte getPrivacyLevel() {
		return privacyLevel;
	}

	public void setPrivacyLevel(byte privacyLevel) {
		this.privacyLevel = privacyLevel;
	}
	
	public static User from(final JSONObject object) {
		final User user = new User();
		user.setAge((byte)object.optInt("age"));
		user.setId(object.optInt("id"));
		user.setUsername(object.optString("username"));
		user.setFullname(object.optString("fullname"));
		user.setEmail(object.optString("email"));
		user.setWebsite(object.optString("website"));
		user.setGender(object.optString("gender"));
		user.setLocation(object.optString("location"));
		user.setBio(object.optString("bio"));
		user.setLocationVisibility((byte)object.optInt("locationVisibility"));
		user.setPrivacyLevel((byte)object.optInt("privacyLevel"));
		user.setFollowersCount(object.optInt("followersCount"));
		user.setFollowsCount(object.optInt("followsCount"));
		user.setLat(object.optDouble("lat"));
		user.setLon(object.optDouble("lon"));
		user.setProfilePicUrl(object.optString("profilePicUrl"));
		user.setMediaCount(object.optInt("mediaCounts"));
		return user;
	}
	
	private static String stripOutNumbers(final String str) {
		if (isEmpty(str)) {
			return str;
		}
		final StringBuilder sb = new StringBuilder(str.length());
		for (int i=0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				continue;
			} else {
				sb.append(str.charAt(i));
			}
		}
		return sb.toString();
	}
	
	private static boolean isEmpty(final String s) {
		return s == null || s.equals("");
	}
	
	private static boolean isBlank(final String s) {
		return s == null || s.trim().equals("");
	}

}

