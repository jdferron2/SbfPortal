package com.jdf.SbfPortal.backend.data;

import javax.xml.bind.annotation.XmlElement;

public class DraftRank {
	private int playerId;
	private String lname;
	private String fname;
	private String displayName;
	private String team;
	private String position;
	private int proRank;


	public DraftRank() {}
	public DraftRank(int playerId, String lname, String fname, String displayName, String team, 
			String position, int proRank){
		this.playerId = playerId;
		this.lname = lname;
		this.fname = fname;
		this.displayName = displayName;
		this.team = team;
		this.position = position;
		this.proRank = proRank;

	}

	@XmlElement  (name="lname")
	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	@XmlElement  (name="fname")
	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	@XmlElement  (name="displayName")
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@XmlElement  (name="team")
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}

	@XmlElement  (name="position")
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@XmlElement  (name="playerId")
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	@XmlElement  (name="overallRank")
	public int getProRank() {
		return proRank;
	}
	public void setProRank(int proRank) {
		this.proRank = proRank;
	}
}