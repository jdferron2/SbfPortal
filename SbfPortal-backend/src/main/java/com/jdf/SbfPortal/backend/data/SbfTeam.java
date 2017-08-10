package com.jdf.SbfPortal.backend.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class SbfTeam {
	private String ownerName;
	private int draftSlot;
	private int sbfId;
	private int leagueId;
	private String teamName;
	
	public SbfTeam() {}
	public SbfTeam(int leagueId, String owner, int draftPosition, int id, String teamName){
		this.leagueId = leagueId;
		this.ownerName = owner;
		this.draftSlot = draftPosition;
		this.sbfId = id;
		this.teamName = teamName;
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
	
	public int getSbfId() {
		return sbfId;
	}
	public void setSbfId(int sbfId) {
		this.sbfId = sbfId;
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

}
