package com.jdf.SbfPortal.authentication;

import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.SbfServiceFactory;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.data.SbfRankSet;
import com.jdf.SbfPortal.backend.data.SbfUser;
import com.jdf.SbfPortal.utility.AccountInfoManager;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.UI;

public class UserSessionVars {
	public static final String CURRENT_USER = "CURRENTUSER";
	public static final String CURRENT_LEAGUE = "CURRENTLEAGUE";
	public static final String DRAFT_SERVICE = "DRAFTSERVICE";
	public static final String PLAYER_SERVICE = "PLAYERSERVICE";
	public static final String LEAGUE_SERVICE = "LEAGUESERVICE";
	public static final String LEAGUE_MGR = "LEAGUEMGR";
	public static final String ACCOUNT_MGR = "ACCTMGR";
	public static final String RANK_SET = "RANKSET";
	private static final String ACCESS_CONTROL = "ACCESSCONTROL";
	private static final String CURRENT_VIEW = "CURRENTVIEW";

	public static SbfUser getCurrentUser() {
		SbfUser currentUser = (SbfUser) UI.getCurrent().getSession()
				.getAttribute(CURRENT_USER);
		return currentUser;
	}

	public static void setCurrentUser(SbfUser currentUser) {
		if (currentUser == null) {
			removeAttribute(
					CURRENT_USER);
		} else {
			UI.getCurrent().getSession().setAttribute(
					CURRENT_USER, currentUser);
		}
	}
	
	public static View getCurrentView() {
		View v = (View) UI.getCurrent().getSession()
				.getAttribute(CURRENT_VIEW);
		return v;
	}

	public static void setCurrentView(View v) {
		if (v == null) {
			removeAttribute(
					CURRENT_VIEW);
		} else {
			UI.getCurrent().getSession().setAttribute(
					CURRENT_VIEW, v);
		}
	}

	public static SbfLeague getCurrentLeague() {
		SbfLeague currentLeague = (SbfLeague) UI.getCurrent().getSession()
				.getAttribute(CURRENT_LEAGUE);
		return currentLeague;
	}

	public static void setCurrentLeague(SbfLeague currentLeague) {
		if (currentLeague == null) {
			removeAttribute(
					CURRENT_LEAGUE);
		} else {
			UI.getCurrent().getSession().setAttribute(
					CURRENT_LEAGUE, currentLeague);
		}
	}
	
	public static LeagueInfoManager getLeagueManager() {
		LeagueInfoManager mgr = (LeagueInfoManager) UI.getCurrent().getSession()
				.getAttribute(LEAGUE_MGR);
		if(mgr == null){
			setLeagueManager(mgr = new LeagueInfoManager());
		}
		return mgr;
	}

	public static void setLeagueManager(LeagueInfoManager mgr) {
		if (mgr == null) {
			removeAttribute(
					LEAGUE_MGR);
		} else {
			UI.getCurrent().getSession().setAttribute(
					LEAGUE_MGR, mgr);
		}
	}
	
	public static AccountInfoManager getAccountInfoManager() {
		AccountInfoManager mgr = (AccountInfoManager) UI.getCurrent().getSession()
				.getAttribute(ACCOUNT_MGR);
		if(mgr == null){
			setAccountInfoManager(mgr = new AccountInfoManager());
		}
		return mgr;
	}
	
	public static void setAccountInfoManager(AccountInfoManager mgr) {
		if (mgr == null) {
			removeAttribute(
					ACCOUNT_MGR);
		} else {
			UI.getCurrent().getSession().setAttribute(
					ACCOUNT_MGR, mgr);
		}
	}
	
	public static PlayerService getPlayerService() {
		PlayerService service = (PlayerService) UI.getCurrent().getSession()
				.getAttribute(PLAYER_SERVICE);
		if(service == null){
			setPlayerService(service = SbfServiceFactory.createPlayerService());
		}
		return service;
	}

	public static void setPlayerService(PlayerService s) {
		if (s == null) {
			removeAttribute(
					PLAYER_SERVICE);
		} else {
			UI.getCurrent().getSession().setAttribute(
					PLAYER_SERVICE, s);
		}
	}
	
	public static SbfDraftService getDraftService() {
		SbfDraftService service = (SbfDraftService) UI.getCurrent().getSession()
				.getAttribute(DRAFT_SERVICE);
		if(service == null){
			setDraftService(service = SbfServiceFactory.createDraftService());
		}
		return service;
	}

	public static void setDraftService(SbfDraftService s) {
		if (s == null) {
			removeAttribute(
					DRAFT_SERVICE);
		} else {
			UI.getCurrent().getSession().setAttribute(
					DRAFT_SERVICE, s);
		}
	}
	
	public static void resetRankSetToDefault(){
		if (getLeagueService() == null || getPlayerService() == null){
			return;
		}
		if(getCurrentLeague() == null){//user must not be associated to any leagues, grab their default cheatsheet
			for(SbfRankSet r : getPlayerService().getAllSbfRankSets(getCurrentUser().getUserId())){
				setRankSet(r);
				return;
			}
		}
		Integer rankSetId = getLeagueService().getDefaultRankSetForLeagueAndUser(getCurrentUser(),
    			getCurrentLeague());
        
        if (rankSetId == 0){//get default set
        	setRankSet(
        		getPlayerService().getGlobalDefaultRankSet());
        }
        else{
        	setRankSet(
        			getPlayerService().getSbfRankSetByIdAndUser(rankSetId, getCurrentUser().getUserId()));
        }
	}
	
	public static SbfLeagueService getLeagueService() {
		SbfLeagueService service = (SbfLeagueService) UI.getCurrent().getSession()
				.getAttribute(LEAGUE_SERVICE);
		if(service == null){
			setLeagueService(service = SbfServiceFactory.createLeagueService());
		}
		return service;
	}
	

	public static void setLeagueService(SbfLeagueService s) {
		if (s == null) {
			removeAttribute(
					LEAGUE_SERVICE);
		} else {
			UI.getCurrent().getSession().setAttribute(
					LEAGUE_SERVICE, s);
		}
	}
	
	public static AccessControl getAccessControl() {
		AccessControl a = (AccessControl) UI.getCurrent().getSession()
				.getAttribute(ACCESS_CONTROL);
		return a;
	}

	public static void setAccessControl(AccessControl a) {
		if (a == null) {
			removeAttribute(
					ACCESS_CONTROL);
		} else {
			UI.getCurrent().getSession().setAttribute(
					ACCESS_CONTROL, a);
		}
	}
	
	public static SbfRankSet getRankSet() {
		SbfRankSet rankSet = (SbfRankSet) UI.getCurrent().getSession()
				.getAttribute(RANK_SET);
		return rankSet;
	}

	public static void setRankSet(SbfRankSet set) {
		if (set == null) {
			removeAttribute(
					RANK_SET);
		} else {
			UI.getCurrent().getSession().setAttribute(
					RANK_SET, set);
		}
	}

	private static VaadinRequest getCurrentRequest() {
		VaadinRequest request = VaadinService.getCurrentRequest();
		if (request == null) {
			throw new IllegalStateException(
					"No request bound to current thread");
		}
		return request;
	}
	
	private static void removeAttribute(String attribute){
		UI.getCurrent().getSession().setAttribute(attribute, null);
	}
}


