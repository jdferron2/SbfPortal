package com.jdf.SbfPortal.backend.data;

public class SbfKeeper {
	private Integer leagueId;
	private Integer sbfId;
	private Integer playerId;
	private Integer round;
	
	public SbfKeeper(int leagueId, int sbfId, int playerId, int round) {
		this.leagueId = leagueId;
		this.sbfId = sbfId;
		this.playerId = playerId;
		this.round = round;
	}
	public Integer getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(Integer leagueId) {
		this.leagueId = leagueId;
	}
	public Integer getSbfId() {
		return sbfId;
	}
	public void setSbfId(Integer sbfId) {
		this.sbfId = sbfId;
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
