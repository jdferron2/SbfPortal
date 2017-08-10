package com.jdf.SbfPortal.backend;

import java.util.ArrayList;
import java.util.List;

import com.jdf.SbfPortal.backend.DAO.PlayerDAO;
import com.jdf.SbfPortal.backend.DAO.SbfRankDAO;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfRank;

//@Path("/PlayerService") 

public class PlayerService {
	private List<Player> players;
	private List<SbfRank> sbfRanks;
	private List<SbfRank> sbfRankUpdateList;
	private PlayerDAO playerDao;
	private SbfRankDAO sbfRankDao;

	PlayerService(PlayerDAO pDao, SbfRankDAO sDao){
		this.playerDao = pDao;
		this.sbfRankDao = sDao;
	}

	//	@GET 
	//	@Path("/players") 
	//	@Produces(MediaType.APPLICATION_XML) 
	public List<Player> getAllPlayers() {
		if (players == null) {
			players = playerDao.getAllPlayers();
		}
		return players;
	}
	
	public List<Player> getTopNPlayers(int maxRank) {
		List<Player> rankedPlayers = new ArrayList<Player>();
		for(Player p : getAllPlayers()){
			if (p.getProRank() < maxRank && p.getProRank() != 0) rankedPlayers.add(p);			
		}
		return rankedPlayers;
	}

	public List<SbfRank> getAllSbfRanks(Integer sbfId) {
		if (sbfRanks == null) {
			sbfRanks = sbfRankDao.getAllSbfRanks(sbfId);
		}
		return sbfRanks;
	}
	
	public List<SbfRank> getTopNSbfRanks(int maxRank, Integer sbfId) {
		List<SbfRank> topNRanks = new ArrayList<SbfRank>();
		for(SbfRank r : getAllSbfRanks(sbfId)){
			if (r.getRank() < maxRank) topNRanks.add(r);			
		}
		return topNRanks;
	}

	public synchronized Player getPlayerBySbfRank(int rank, int sbfId){
		return getAllPlayers().stream().filter(
				p->getSbfRankById(p.getPlayerId(), sbfId).getRank()==rank).findFirst().orElse(null);
	}

	public synchronized Player getPlayerById(int playerId) {
		return getAllPlayers().stream().filter(
				p->p.getPlayerId()==playerId).findFirst().orElse(null);
	}	

	public synchronized int getMaxProRank() {
		Player player = getAllPlayers().stream().max(
				(p1,p2)->Integer.compare(p1.getProRank(), p2.getProRank())
				).orElse(null);
		if (player != null){
			return player.getProRank();
		}else{
			return 0;
		}
	}

	public synchronized int getMaxSbfRank(int sbfId) {
		SbfRank rank = getAllSbfRanks(sbfId).stream().max(
				(r1,r2)->Integer.compare(r1.getRank(), r2.getRank())
				).orElse(null);
		if (rank != null){
			return rank.getRank();
		}else{
			return 0;
		}
	}	

	public synchronized SbfRank getSbfRankById(int playerId, int sbfId) {
		return getAllSbfRanks(sbfId).stream().filter(
				s->s.getPlayerId()==playerId).findFirst().orElse(null);
	}
	
	public synchronized SbfRank getSbfRankObjByRank(int rank, int sbfId){
		return getAllSbfRanks(sbfId).stream().filter(
				s->s.getRank()==rank).findFirst().orElse(null);
	}
	
	public synchronized void addRankToUpdateList(SbfRank rank){
		if(sbfRankUpdateList == null){
			sbfRankUpdateList = new ArrayList<SbfRank>();
		}
		sbfRankUpdateList.add(rank);
	}
	
	public synchronized void updateFlaggedRanks(){
		if (sbfRankUpdateList==null){
			return ;
		}
		for(SbfRank rank : sbfRankUpdateList){
			sbfRankDao.updateSbfRank(rank);
		}
		sbfRankUpdateList=null;
	}
	
	public synchronized void deleteAllPlayers(){
		for (Player p : getAllPlayers()){
			playerDao.deletePlayer(p);
		}
		players = null;
	}

	public void insertPlayer(Player player) {
		playerDao.insertPlayer(player);	
	}

	public void updatePlayer(Player player) {
		playerDao.updatePlayer(player);	
	}

	public void insertSbfRank(SbfRank rank) {
		sbfRankDao.insertSbfRank(rank);
		getAllSbfRanks(rank.getSbfId()).add(rank);
	}

	public void deleteAllSbfRanks(int sbfId) {
		for (SbfRank r : getAllSbfRanks(sbfId)){
			sbfRankDao.deleteSbfRank(r);
		}
		sbfRanks = null;
	}
	
	public void deleteSbfRank(SbfRank r){
		sbfRankDao.deleteSbfRank(r);
		sbfRanks.remove(r);
	}
	

}
