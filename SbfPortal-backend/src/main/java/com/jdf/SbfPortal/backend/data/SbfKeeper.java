package com.jdf.SbfPortal.backend.data;

public class SbfKeeper {
	private Integer leagueId;
	private Integer teamId;
	private Integer playerId;
	private Integer round;
	
	public SbfKeeper(int leagueId, int teamId, int playerId, int round) {
		this.leagueId = leagueId;
		this.teamId = teamId;
		this.playerId = playerId;
		this.round = round;
	}
	public Integer getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(Integer leagueId) {
		this.leagueId = leagueId;
	}
	public Integer getTeamId() {
		return teamId;
	}
	public void setTeamId(Integer teamId) {
		this.teamId = teamId;
	}
	public Integer getPlayerId() {
		return playerId;
	}
	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}
	public Integer getRound() {
		return round;
	}
	public void setRound(Integer round) {
		this.round = round;
	}
}
