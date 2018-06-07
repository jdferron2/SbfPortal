package com.jdf.SbfPortal.utility;

import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.jdf.SbfPortal.backend.data.SbfUser;
import com.jdf.SbfPortal.backend.data.SbfUserTeam;

public class AccountInfoManager {

	public AccountInfoManager(){
		initialize();
	}
	
	private void initialize(){
		
	}
	
	public boolean createUserAccount(String userName, String password, String email){
		UserSessionVars.getLeagueService().insertSbfUser(new SbfUser(0, userName, password, email, "user"));
		return true;
	}
	
	public boolean createLeague(String leagueName, Integer numTeams, Integer leagueManager){
		SbfLeague l = new SbfLeague();
		l.setLeagueManager(leagueManager);
		l.setLeagueName(leagueName);
		l.setNumTeams(numTeams);
		UserSessionVars.getLeagueService().insertSbfLeague(l);
		
		int leagueId = l.getLeagueId();
		SbfTeam t = new SbfTeam();
		t.setDraftSlot(1);
		t.setLeagueId(leagueId);
		t.setOwnerName(UserSessionVars.getCurrentUser().getUserName());
		t.setTeamName("new team 1");
		UserSessionVars.getLeagueService().insertSbfTeam(t);
		
		for (int i = 2; i<=numTeams; i++){
			SbfTeam t2 = new SbfTeam();
			t2.setLeagueId(leagueId);
			t2.setOwnerName("new owner " + i);
			t2.setTeamName("new team " + i);
			t2.setDraftSlot(i);
			UserSessionVars.getLeagueService().insertSbfTeam(t2);
		}
		
		SbfUserTeam ut = new SbfUserTeam();
		ut.setDefaultRankSetId(UserSessionVars.getRankSet().getRankSetId());
		ut.setLeagueId(l.getLeagueId());
		ut.setTeamId(t.getTeamId());
		ut.setUserId(UserSessionVars.getCurrentUser().getUserId());
		UserSessionVars.getLeagueService().insertSbfUserTeam(ut);
		
		return true;
	}
	
	public boolean copyLeague(SbfLeague o){
		SbfLeague l = new SbfLeague(-1, o.getLeagueName(), o.getNumTeams(), o.getLeagueManager());
		UserSessionVars.getLeagueService().insertSbfLeague(l);
		for (SbfTeam t : UserSessionVars.getLeagueService().getAllSbfTeamsForLeague(o.getLeagueId())){
			SbfTeam newTeam = new SbfTeam();
			newTeam.setDraftSlot(t.getDraftSlot());
			newTeam.setLeagueId(l.getLeagueId());
			newTeam.setOwnerName(t.getOwnerName());
			newTeam.setTeamName(t.getTeamName());
			newTeam.setUserId(t.getUserId());
			UserSessionVars.getLeagueService().getSbfUserTeamForLeagueAndUser(u, l)
		}
		
		for (SbfUserTeam t : UserSessionVars.getLeagueService().getAllSbfUserTeams()){
			if (o.getLeagueId() != t.getLeagueId()){
				continue;
			}
			SbfUserTeam newUT = new SbfUserTeam();
			newUT.setDefaultRankSetId(t.getDefaultRankSetId());
			newUT.setLeagueId(l.getLeagueId());
			newUT.setTeamId(teamId);
		}
		return true;
	}
}
