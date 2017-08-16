package com.jdf.SbfPortal.backend.data;

public class SbfLeague {
	private String leagueName;
	private int leagueId;
	private int numTeams;
	private int leagueManager;

	public SbfLeague() {}
	public SbfLeague(int leagueId, String leagueName, int numTeams, int mgr){
		this.setLeagueId(leagueId);
		this.setLeagueName(leagueName);
		this.setNumTeams(numTeams);
		this.setLeagueManager(mgr);
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
	public int getLeagueManager() {
		return leagueManager;
	}
	public void setLeagueManager(int leagueManager) {
		this.leagueManager = leagueManager;
	}
	


}
