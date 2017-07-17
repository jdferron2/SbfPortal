package com.jdf.SbfPortal.backend;

import java.util.List;

import com.jdf.SbfPortal.backend.DAO.SbfLeagueDAO;
import com.jdf.SbfPortal.backend.DAO.SbfTeamDAO;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.data.SbfTeam;

public class SbfLeagueService {
	protected SbfLeagueDAO sbfLeagueDao;
	protected SbfTeamDAO sbfTeamDao;
	protected List<SbfLeague> sbfLeagues;
	protected List<SbfTeam> sbfTeams;
	
	protected SbfLeagueService(SbfLeagueDAO leagueDao, SbfTeamDAO teamDao){
		this.sbfLeagueDao = leagueDao;
		this.sbfTeamDao = teamDao;
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
	
	public synchronized SbfTeam getSbfTeamByName(String name, int leaugeId){
		return getAllSbfTeams(leaugeId).stream().filter(
				t->t.getOwnerName().toUpperCase().equals(name.toUpperCase())).findFirst().orElse(null);
	}
}
