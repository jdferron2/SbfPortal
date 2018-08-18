package com.jdf.SbfPortal.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jdf.SbfPortal.backend.DAO.SbfKeeperDAO;
import com.jdf.SbfPortal.backend.DAO.SbfLeagueDAO;
import com.jdf.SbfPortal.backend.DAO.SbfTeamDAO;
import com.jdf.SbfPortal.backend.DAO.SbfUserDAO;
import com.jdf.SbfPortal.backend.DAO.SbfUserTeamsDAO;
import com.jdf.SbfPortal.backend.data.SbfKeeper;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.jdf.SbfPortal.backend.data.SbfUser;
import com.jdf.SbfPortal.backend.data.SbfUserTeam;

public class SbfLeagueService {
	protected SbfLeagueDAO sbfLeagueDao;
	protected SbfTeamDAO sbfTeamDao;
	protected SbfKeeperDAO sbfKeeperDao;
	protected SbfUserDAO sbfUserDao;
	protected SbfUserTeamsDAO sbfUserTeamsDao;
	protected List<SbfLeague> sbfLeagues;
	protected List<SbfTeam> sbfTeams;
	protected List<SbfKeeper> sbfKeepers;
	protected List<SbfUser> sbfUsers;
	protected List<SbfUserTeam> sbfUserTeams;
	
	protected Integer lastRetrievedLeagueIdKeepers;
	
	
	protected SbfLeagueService(SbfLeagueDAO leagueDao, SbfTeamDAO teamDao, SbfKeeperDAO keeperDao, 
			SbfUserDAO userDao, SbfUserTeamsDAO userTeamsDao){
		this.sbfLeagueDao = leagueDao;
		this.sbfTeamDao = teamDao;
		this.sbfKeeperDao = keeperDao;
		this.sbfUserDao = userDao;
		this.sbfUserTeamsDao = userTeamsDao;
	}

	public synchronized List<SbfLeague> getAllSbfLeagues(){
		if (sbfLeagues == null) {
			sbfLeagues = sbfLeagueDao.getAllSbfLeagues();
		}
		return sbfLeagues;
	}
	
	public synchronized List<SbfLeague> getAllSbfLeaguesManagedByUser(int userId){
		List<SbfLeague> leagues = new ArrayList<SbfLeague>();
		for (SbfLeague l : getAllSbfLeagues()){
			if (l.getLeagueManager() == userId){
				leagues.add(l);
			}
		}
		return leagues;
	}
	
	public synchronized List<SbfTeam> getAllSbfTeams(){
		if (sbfTeams == null) {
			sbfTeams = sbfTeamDao.getAllTeams();
		}
		return sbfTeams;
	}
	
	public synchronized List<SbfUserTeam> getAllSbfUserTeams(){
		if (sbfUserTeams == null) {
			sbfUserTeams = sbfUserTeamsDao.getAllSBfUserTeams();
		}
		return sbfUserTeams;
	}
	
	public synchronized List<SbfTeam> getAllSbfTeamsForLeague(int leagueId){
		List<SbfTeam> teams = new ArrayList<SbfTeam>();
		for (SbfTeam t : getAllSbfTeams()){
			if (t.getLeagueId() == leagueId){
				teams.add(t);
			}
		}
		return teams;
	}
	
	public synchronized List<SbfUser> getAllSbfUsers(){
		if (sbfUsers == null) {
			sbfUsers = sbfUserDao.getAllSbfUsers();
		}
		return sbfUsers;
	}
	
	public synchronized SbfUser getSbfUserByName(String name){
		return getAllSbfUsers().stream().filter(
				u->u.getUserName().toUpperCase().equals(name.toUpperCase())).findFirst().orElse(null);
	}
	
	public synchronized SbfUser getSbfUserById(int id){
		return getAllSbfUsers().stream().filter(
				u->u.getUserId().equals(id)).findFirst().orElse(null);
	}
	
	public synchronized SbfLeague getLeagueById(int id) {
		return getAllSbfLeagues().stream().filter(
				l->l.getLeagueId()==id).findFirst().orElse(null);
	}	
	
	public synchronized SbfTeam getSbfTeamByTeamId(int id, int leagueId){
		return getAllSbfTeams().stream().filter(
				t->t.getTeamId()==id && t.getLeagueId() == leagueId).findFirst().orElse(null);
	}
	
	public synchronized SbfTeam getSbfTeamByName(String name, int leagueId){
		return getAllSbfTeams().stream().filter(
				t->t.getLeagueId() == leagueId && t.getOwnerName().toUpperCase().equals(name.toUpperCase())).findFirst().orElse(null);
	}
	
	public synchronized SbfTeam getSbfTeamByDraftSlot(int draftSlot, int leagueId){
		return getAllSbfTeams().stream().filter(
				t->t.getLeagueId() == leagueId && t.getDraftSlot()==draftSlot).findFirst().orElse(null);
	}
	
	public synchronized List<SbfKeeper> getAllSbfKeepers(int leagueId){
		if (sbfKeepers == null || lastRetrievedLeagueIdKeepers != leagueId) {
			lastRetrievedLeagueIdKeepers = leagueId;
			sbfKeepers = sbfKeeperDao.getAllSbfKeepers(leagueId);
		}
		return sbfKeepers;
	}
	
	public synchronized SbfKeeper getSbfKeeperByPlayerId(int playerId, int leagueId){
		return getAllSbfKeepers(leagueId).stream().filter(
				k->k.getPlayerId().equals(playerId)).findFirst().orElse(null);
	}
	
	public synchronized void deleteSbfKeeper(SbfKeeper sbfKeeper){
		sbfKeeperDao.deleteSbfKeeper(sbfKeeper);
		sbfKeepers.remove(sbfKeeper);
	}
	
	public synchronized void insertSbfKeeper(SbfKeeper k){
		sbfKeeperDao.insertSbfKeeper(k);
		sbfKeepers.add(k);
	}
	
	public synchronized void updateSbfTeam(SbfTeam t){
		sbfTeamDao.updateTeam(t);
	}
	public synchronized void deleteSbfTeam(SbfTeam t){
		sbfTeamDao.deleteTeam(t);
		this.getAllSbfTeams().remove(t);
	}
	
	public synchronized void updateSbfUserTeam(SbfUserTeam t){
		sbfUserTeamsDao.updateSbfUserTeam(t);
	}
	
	public synchronized void insertSbfUserTeam(SbfUserTeam t){
		sbfUserTeamsDao.insertSbfUserTeam(t);
		getAllSbfUserTeams().add(t);
	}
	
	public synchronized void deleteSbfUserTeam(SbfUserTeam t){
		sbfUserTeamsDao.deleteSbfUserTeam(t);
		this.getAllSbfUserTeams().remove(t);
	}
	
	public synchronized void insertSbfTeam(SbfTeam t){
		sbfTeamDao.insertTeam(t);
		getAllSbfTeams().add(t);
	}
	
	public synchronized void insertSbfUser(SbfUser u){
		sbfUserDao.insertSbfUser(u);
		getAllSbfUsers().add(u);
	}
	
	public synchronized void insertSbfLeague(SbfLeague l){
		sbfLeagueDao.insertSbfLeague(l);
		getAllSbfLeagues().add(l);
	}
	
	public synchronized void updateSbfLeague(SbfLeague l){
		sbfLeagueDao.updateSbfLeague(l);
		getAllSbfLeagues().add(l);
	}
	
	public synchronized void deleteSbfLeague(SbfLeague l){
		sbfLeagueDao.deleteSbfLeague(l);
		getAllSbfLeagues().remove(l);
	}
//	public synchronized List<SbfTeam> getAllSbfTeamsForUser(SbfUser u){
//		List<SbfTeam> teams = new ArrayList<SbfTeam>();
//		for (SbfTeam t : getAllSbfTeams()){
//			if(t.getTeamId() == u.getUserId()){
//				teams.add(t);
//			}
//		}
//		return teams;
//	}
	
	public synchronized List<SbfLeague> getLeaguesForUser(SbfUser u){
		List<SbfLeague> leagues = new ArrayList<SbfLeague>();
		SbfLeague l;
		for(SbfUserTeam t : getAllSbfUserTeams()){
			if(t.getUserId() == u.getUserId()){
				l = getLeagueById(t.getLeagueId());
				if (!leagues.contains(l)){
					leagues.add(l);
				}
			}
		}
		return leagues;
	}
	
	public synchronized Integer getDefaultRankSetForLeagueAndUser(SbfUser u, SbfLeague l){
		for(SbfUserTeam t : getAllSbfUserTeams()){
			if (t.getUserId() == u.getUserId()){
				if (t.getLeagueId() == l.getLeagueId()){
					return t.getDefaultRankSetId();
				}
			}
		}
		return 0;
	}
	
	public synchronized void removeRankSetAsDefault(int rankSetId){
		for(SbfUserTeam t : getAllSbfUserTeams()){
			if(t.getDefaultRankSetId() == rankSetId){
				t.setDefaultRankSetId(0);
				this.updateSbfUserTeam(t);
			}
		}
	}
	
	public synchronized SbfUserTeam getSbfUserTeamForLeagueAndUser(SbfUser u, SbfLeague l){
//		for(SbfUserTeam t : getAllSbfUserTeams()){
//			if (t.getUserId() == u.getUserId() && t.getLeagueId() == l.getLeagueId()){
//				return t;
//			}
//		}
//		return null;
		return this.getAllSbfUserTeams().stream().filter(
				ut->ut.getUserId()==u.getUserId() && ut.getLeagueId() == l.getLeagueId()).findFirst().orElse(null);
	}
	
	public synchronized List<SbfUserTeam> getSbfUserTeamForLeague(SbfLeague l){
		List<SbfUserTeam> userTeams = new ArrayList<SbfUserTeam>();
		for (SbfUserTeam ut : getAllSbfUserTeams()){
			if (ut.getLeagueId() == l.getLeagueId()){
				userTeams.add(ut);
			}
		}
		return userTeams;
	}
}
