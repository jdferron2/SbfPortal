package com.jdf.SbfPortal.backend.data;

import java.sql.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="players")
public class Player {
	private int playerId;
	private int active;
	private int jersey;
	private String lname;
	private String fname;
	private String displayName;
	private String team;
	private String position;
	private String height;
	private int weight;
	private Date dob;
	private int proRank;
	private String dobString;


	public Player() {}
	public Player(int playerId, int jersey, String lname, String fname, String displayName, String team, 
			String position, String height, int weight, Date dob, int proRank){
		this.playerId = playerId;
		this.jersey = jersey;
		this.lname = lname;
		this.fname = fname;
		this.displayName = displayName;
		this.team = team;
		this.position = position;
		this.height = height;
		this.weight = weight;
		this.dob = dob;
		this.proRank = proRank;
		if (dob != null)
			this.dobString = dob.toString();

	}

	@XmlAttribute (name="active")
	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	@XmlAttribute (name="jersey")
	public int getJersey() {
		return jersey;
	}

	public void setJersey(int jersey) {
		this.jersey = jersey;
	}

	@XmlAttribute  (name="lname")
	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	@XmlAttribute  (name="fname")
	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	@XmlAttribute  (name="displayName")
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@XmlAttribute  (name="team")
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}

	@XmlAttribute  (name="position")
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@XmlAttribute (name="height")
	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	@XmlAttribute (name="weight")
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}


	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
		dobString=dob.toString();
	}

	@XmlAttribute (name="dob")
	public String getDobString(){
		return dobString;
	}

	public void setDobString(String dobString){
		this.dobString =  dobString;
		if (dobString == null || dobString.equals("") || dobString.equals("0000-00-00")){
			dobString = "1900-01-01";
		}
		setDob(Date.valueOf(dobString));
	}

	@XmlAttribute  (name="playerId")
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	@XmlAttribute  (name="proRank")
	public int getProRank() {
		return proRank;
	}
	public void setProRank(int proRank) {
		this.proRank = proRank;
	}

	//	public SbfRank getSbfRank() {
	//		return sbfRank;
	//	}
	//	public void setSbfRank(SbfRank sbfRank) {
	//		this.sbfRank = sbfRank;
	//	}
}