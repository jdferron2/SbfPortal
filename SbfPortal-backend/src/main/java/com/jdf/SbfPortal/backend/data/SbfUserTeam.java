package com.jdf.SbfPortal.backend.data;

public class SbfUserTeam {
	private int teamId;
	private int leagueId;
	private int userId;
	private int defaultRankSetId;

	public SbfUserTeam() {}
	public SbfUserTeam(int leagueId, int teamId, int userId, int defaultRankSetId){
		this.leagueId = leagueId;
		this.teamId = teamId;
		this.setUserId(userId);
		this.setDefaultRankSetId(defaultRankSetId);
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

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getDefaultRankSetId() {
		return defaultRankSetId;
	}
	public void setDefaultRankSetId(int defaultRankSetId) {
		this.defaultRankSetId = defaultRankSetId;
	}

}
