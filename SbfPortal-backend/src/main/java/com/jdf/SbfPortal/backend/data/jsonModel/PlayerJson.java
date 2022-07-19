package com.jdf.SbfPortal.backend.data.jsonModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerJson {
	@JsonProperty("player_id")
	private Integer playerId;
	
	@JsonProperty("name")
    private String 	fullName;

    private String 	position;
    private String 	team;
    
    private Float 	adp;
    
    @JsonProperty("adp_formatted")
    private Float 	adpFormatted;
    
    @JsonProperty("times_drafted")
    private Integer timesDrafted;
    
    @JsonProperty("high")
    private Short 	highRank;
    
    @JsonProperty("low")
    private Short 	lowRank;
    
    @JsonProperty("stdev")
    private Float 	stDeviation;
    
    private Short 	bye;
    
    public PlayerJson() {
    	
    }
    
	public Integer getPlayerId() {
		return playerId;
	}
	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public Float getAdp() {
		return adp;
	}
	public void setAdp(Float adp) {
		this.adp = adp;
	}
	public Float getAdpFormatted() {
		return adpFormatted;
	}
	public void setAdpFormatted(Float adpFormatted) {
		this.adpFormatted = adpFormatted;
	}
	public Integer getTimesDrafted() {
		return timesDrafted;
	}
	public void setTimesDrafted(Integer timesDrafted) {
		this.timesDrafted = timesDrafted;
	}
	public Short getHighRank() {
		return highRank;
	}
	public void setHighRank(Short highRank) {
		this.highRank = highRank;
	}
	public Short getLowRank() {
		return lowRank;
	}
	public void setLowRank(Short lowRank) {
		this.lowRank = lowRank;
	}
	public Float getStDeviation() {
		return stDeviation;
	}
	public void setStDeviation(Float stDeviation) {
		this.stDeviation = stDeviation;
	}
	public Short getBye() {
		return bye;
	}
	public void setBye(Short bye) {
		this.bye = bye;
	}
    
}
