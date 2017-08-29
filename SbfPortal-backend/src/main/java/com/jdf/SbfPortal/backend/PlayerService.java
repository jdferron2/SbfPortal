package com.jdf.SbfPortal.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.jdf.SbfPortal.backend.DAO.PlayerDAO;
import com.jdf.SbfPortal.backend.DAO.SbfRankDAO;
import com.jdf.SbfPortal.backend.DAO.SbfRankSetsDAO;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfRank;
import com.jdf.SbfPortal.backend.data.SbfRankSet;

//@Path("/PlayerService") 

public class PlayerService {
	private List<Player> players;
	private List<SbfRank> sbfRanks;
	private List<SbfRank> sbfRankUpdateList;
	private List<SbfRankSet> sbfRankSets;
	//private HashMap<Integer, List<SbfRank>> sbfRanksLookup = new HashMap<Integer, List<SbfRank>>();
	private PlayerDAO playerDao;
	private SbfRankDAO sbfRankDao;
	private static Logger logger = Logger.getLogger(PlayerService.class);
	private SbfRankSetsDAO sbfRankSetsDao;
	private Integer lastLoadedRankSetId;

	PlayerService(PlayerDAO pDao, SbfRankDAO sDao, SbfRankSetsDAO rsDao){
		this.playerDao = pDao;
		this.sbfRankDao = sDao;
		this.sbfRankSetsDao = rsDao;
	}

	//	@GET 
	//	@Path("/players") 
	//	@Produces(MediaType.APPLICATION_XML) 
	public synchronized List<Player> getAllPlayers() {
		if (players == null) {
			players = playerDao.getAllPlayers();
		}
		return players;
	}
	
	public synchronized List<Player> getTopNPlayers(int maxRank) {
		List<Player> rankedPlayers = new ArrayList<Player>();
		for(Player p : getAllPlayers()){
			if (p.getProRank() < maxRank && p.getProRank() != 0) rankedPlayers.add(p);			
		}
		return rankedPlayers;
	}

	public synchronized List<SbfRank> getAllSbfRanks(Integer rankSetId) {
		if(sbfRanks == null || lastLoadedRankSetId != rankSetId){
			sbfRanks = sbfRankDao.getAllSbfRanks(rankSetId);
			lastLoadedRankSetId=rankSetId;
		}
		return sbfRanks;
	}
	
	public synchronized List<SbfRankSet> getAllSbfRankSets(Integer userId) {
		if (sbfRankSets == null) {
			sbfRankSets = sbfRankSetsDao.getAllSbfRankSets(userId);
		}
		return sbfRankSets;
	}
	
	public synchronized List<SbfRank> getTopNSbfRanks(int maxRank, Integer rankSetId) {
		List<SbfRank> topNRanks = new ArrayList<SbfRank>();
		for(SbfRank r : getAllSbfRanks(rankSetId)){
			if (r.getRank() < maxRank) topNRanks.add(r);			
		}
		return topNRanks;
	}

	public synchronized Player getPlayerBySbfRank(int rank, int rankSetId){
		return getAllPlayers().stream().filter(
				p->getSbfRankById(p.getPlayerId(), rankSetId).getRank()==rank).findFirst().orElse(null);
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

	public synchronized int getMaxSbfRank(int rankSetId) {
		SbfRank rank = getAllSbfRanks(rankSetId).stream().max(
				(r1,r2)->Integer.compare(r1.getRank(), r2.getRank())
				).orElse(null);
		if (rank != null){
			return rank.getRank();
		}else{
			return 0;
		}
	}	

	public synchronized SbfRank getSbfRankById(int playerId, int rankSetId) {
		return getAllSbfRanks(rankSetId).stream().filter(
				s->s.getPlayerId()==playerId).findFirst().orElse(null);
	}
	
	public synchronized SbfRank getSbfRankObjByRank(int rank, int rankSetId){
		return getAllSbfRanks(rankSetId).stream().filter(
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

	public synchronized void insertPlayer(Player player) {
		playerDao.insertPlayer(player);	
	}

	public synchronized void updatePlayer(Player player) {
		playerDao.updatePlayer(player);	
	}

	public synchronized void insertSbfRank(SbfRank rank) {
		getAllSbfRanks(rank.getRankSetId()).add(rank);
		sbfRankDao.insertSbfRank(rank);
	}

	public synchronized void deleteAllSbfRanks(int rankSetId) {
		for (SbfRank r : getAllSbfRanks(rankSetId)){
			sbfRankDao.deleteSbfRank(r);
		}
		sbfRanks = null;
	}
	
	public synchronized void deleteRankSet(SbfRankSet r){
		getAllSbfRankSets(r.getUserId()).remove(r);
		sbfRankSetsDao.deleteSbfRankSet(r);
		deleteAllSbfRanks(r.getRankSetId());
	}
	
	public synchronized void deleteSbfRank(SbfRank r){
		getAllSbfRanks(r.getRankSetId()).remove(r);
		sbfRankDao.deleteSbfRank(r);
	}
	
	public synchronized SbfRankSet getGlobalDefaultRankSet() {
		return sbfRankSetsDao.getAllSbfRankSets(0).stream().filter(
				s->s.getRankSetId()==0).findFirst().orElse(null);
	}
	
	public synchronized SbfRankSet getSbfRankSetByIdAndUser(int rankSetId, int userId) {
		return getAllSbfRankSets(userId).stream().filter(
				s->s.getRankSetId()==rankSetId).findFirst().orElse(null);
	}
	
	
	public synchronized void insertSbfRankSet(SbfRankSet s){
		getAllSbfRankSets(s.getUserId()).add(s);
		sbfRankSetsDao.insertSbfRankSet(s);
	}

}
