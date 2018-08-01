package com.jdf.SbfPortal.authentication;

import java.util.List;

import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.SbfServiceFactory;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.data.SbfUser;
import com.jdf.SbfPortal.utility.LeagueInfoManager;

/**
 * Default mock implementation of {@link AccessControl}. This implementation
 * accepts any string as a password, and considers the user "admin" as the only
 * administrator.
 */
public class BasicAccessControl implements AccessControl {


	private SbfLeagueService leagueService = 
			SbfServiceFactory.createLeagueService();
	private List<SbfLeague> leagues;

	public boolean signIn(String username, String password) {
		SbfUser u = leagueService.getSbfUserByName(username);
		if (u == null) 
			//user name wrong
			return false;

		if (!u.getPassword().equals(password)) 
			//password wrong
			return false;

		//        if (leagueService.getSbfTeamBySbfId(u.getSbfId(), leagueId) == null)
		//        	//user does not have a team in selected league
		//        	return false;

		PlayerService playerService = SbfServiceFactory.createPlayerService();
		SbfDraftService draftService = SbfServiceFactory.createDraftService();

		leagues = leagueService.getLeaguesForUser(u);
		SbfLeague curLeague = null;
		if(!leagues.isEmpty()){
			curLeague = leagues.get(leagues.size()-1);
		}
		UserSessionVars.setCurrentLeague(curLeague);
		UserSessionVars.setCurrentUser(u);

		UserSessionVars.setPlayerService(playerService);
		UserSessionVars.setLeagueService(leagueService);
		UserSessionVars.setDraftService(draftService);
		UserSessionVars.resetRankSetToDefault();
		UserSessionVars.setLeagueManager(new LeagueInfoManager());

		return true;
	}

	public boolean isUserSignedIn() {
		return !(UserSessionVars.getCurrentUser() == null);
	}

	public boolean isUserInRole(String role) {
		if (!UserSessionVars.getCurrentUser().getRole().equals(role))
			return false;

		return true;
	}

	public boolean isUserLeagueManager(){
		if (isUserInRole("admin")) return true;
		if (UserSessionVars.getCurrentUser().getUserId() == UserSessionVars.getCurrentLeague().getLeagueManager())
			return true;

		return false;
	}

	public String getPrincipalName() {
		return UserSessionVars.getCurrentUser().getUserName();
	}

}
