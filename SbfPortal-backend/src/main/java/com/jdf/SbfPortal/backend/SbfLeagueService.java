package com.jdf.SbfPortal.backend;

import java.util.List;

import com.jdf.SbfPortal.backend.DAO.SbfKeeperDAO;
import com.jdf.SbfPortal.backend.DAO.SbfLeagueDAO;
import com.jdf.SbfPortal.backend.DAO.SbfTeamDAO;
import com.jdf.SbfPortal.backend.data.SbfKeeper;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.data.SbfTeam;

public class SbfLeagueService {
	protected SbfLeagueDAO sbfLeagueDao;
	protected SbfTeamDAO sbfTeamDao;
	protected SbfKeeperDAO sbfKeeperDao;
	protected List<SbfLeague> sbfLeagues;
	protected List<SbfTeam> sbfTeams;
	protected List<SbfKeeper> sbfKeepers;
	
	protected SbfLeagueService(SbfLeagueDAO leagueDao, SbfTeamDAO teamDao, SbfKeeperDAO keeperDao){
		this.sbfLeagueDao = leagueDao;
		this.sbfTeamDao = teamDao;
		this.sbfKeeperDao = keeperDao;
	}

	public synchronized List<SbfLeague> getAllSbfLeagues(){
		if (sbfLeagues == null) {
			sbfLeagues = sbfLeagueDao.getAllSbfLeagues();
		}
		return sbfLeagues;
	}
	
	public synchronized List<SbfTeam> getAllSbfTeams(int leagueId){
		if (sbfTeams == null) {
			sbfTeams = sbfTeamDao.getAllTeams(leagueId);
		}
		return sbfTeams;
	}
	
	public synchronized SbfLeague getLeagueById(int id) {
		return getAllSbfLeagues().stream().filter(
				l->l.getLeagueId()==id).findFirst().orElse(null);
	}	
	
	public synchronized SbfTeam getSbfTeamBySbfId(int id, int leagueId){
		return getAllSbfTeams(leagueId).stream().filter(
				t->t.getSbfId()==id).findFirst().orElse(null);
	}
	
	public synchronized SbfTeam getSbfTeamByName(String name, int leagueId){
		return getAllSbfTeams(leagueId).stream().filter(
				t->t.getOwnerName().toUpperCase().equals(name.toUpperCase())).findFirst().orElse(null);
	}
	
	public synchronized SbfTeam getSbfTeamByDraftSlot(int draftSlot, int leagueId){
		return getAllSbfTeams(leagueId).stream().filter(
				t->t.getDraftSlot()==draftSlot).findFirst().orElse(null);
	}
	
	public synchronized List<SbfKeeper> getAllSbfKeepers(int leagueId){
		if (sbfKeepers == null) {
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
}
