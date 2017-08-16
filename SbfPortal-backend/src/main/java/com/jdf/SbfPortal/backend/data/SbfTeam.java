package com.jdf.SbfPortal.backend.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class SbfTeam {
	private String ownerName;
	private int draftSlot;
	private int teamId;
	private int leagueId;
	private String teamName;
	private int userId;
	
	public SbfTeam() {}
	public SbfTeam(int leagueId, String owner, int draftPosition, int id, String teamName, int userId){
		this.leagueId = leagueId;
		this.ownerName = owner;
		this.draftSlot = draftPosition;
		this.teamId = id;
		this.teamName = teamName;
		this.setUserId(userId);
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String owner) {
		this.ownerName = owner;
	}
	
	public int getDraftSlot() {
		return draftSlot;
	}
	public void setDraftSlot(int draftPosition) {
		this.draftSlot = draftPosition;
	}
	
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public int getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

}
