package com.jdf.SbfPortal.backend.data;

public class SbfLeague {
	private String leagueName;
	private int leagueId;
	private int numTeams;

	public SbfLeague() {}
	public SbfLeague(int leagueId, String leagueName, int numTeams){
		this.leagueId = leagueId;
		this.leagueName = leagueName;
		this.numTeams = numTeams;
	}
	public String getLeagueName() {
		return leagueName;
	}
	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}
	public int getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}
	public int getNumTeams() {
		return numTeams;
	}
	public void setNumTeams(int numTeams) {
		this.numTeams = numTeams;
	}
	


}
