package com.jdf.SbfPortal.utility;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.data.SbfPickTrade;
import com.jdf.SbfPortal.backend.data.SbfRank;
import com.jdf.SbfPortal.backend.data.SbfRankSet;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.jdf.SbfPortal.backend.data.SbfUser;
import com.jdf.SbfPortal.backend.data.SbfUserTeam;

public class LeagueInfoManager {
	public static final int	NUMBER_OF_TEAMS = 12;
	public static final int	NUMBER_OF_ROUNDS =17;
	//	Integer leagueId;
	//	Integer sbfId;
	//	Integer UserSessionVars.getRankSet().getRankSetId();
	PlayerService playerService;
	SbfDraftService draftService;
	SbfLeagueService leagueService;

	public LeagueInfoManager(){
		initialize();
	}

	private void initialize(){
		draftService = UserSessionVars.getDraftService();
		leagueService = UserSessionVars.getLeagueService();
		playerService = UserSessionVars.getPlayerService();
		//leagueId = UserSessionVars.getCurrentLeague().getLeagueId();
		//UserSessionVars.getRankSet().getRankSetId() = UserSessionVars.getRankSet().getRankSetId();
	}

	//	public void saveLeagueInfo(){
	////		RestAPIUtils.getInstance().marshalFantasyTeams(league, LEAGUE_FILE_NAME);
	////		RestAPIUtils.getInstance().marshalPlayerList(players, PLAYERS_FILE_NAME);
	//	}
	//
	//	public FantasyLeague getLeague(){
	//		return league;
	//	}
	//
	//	public Players getPlayers(){
	//		return players;
	//	}

	public synchronized int getCurrentPick(){
		int currentPick = 1;
		List<SbfDraftRecord> draftList = new ArrayList<>(draftService.getAllDraftRecords(UserSessionVars.getCurrentLeague().getLeagueId()));
		draftList.sort((s1,s2)->Integer.compare(s1.getSlotDrafted(), s2.getSlotDrafted()));
		if (draftService.getAllDraftRecords(UserSessionVars.getCurrentLeague().getLeagueId()).isEmpty()) return 1;
		for (SbfDraftRecord rec : draftList){
			if(rec.getSlotDrafted() > currentPick){
				return currentPick;
			}
			currentPick++;
		}
		return currentPick;
		//		return draftService.getAllDraftRecords(leagueId).stream().max(
		//				(s1,s2)->Integer.compare(s1.getSlotDrafted(), s2.getSlotDrafted())
		//				).get().getSlotDrafted() + 1;
	}
	public synchronized int getRound(){
		return getRound(getCurrentPick());
	}

	public synchronized int getRound(int pick){
		int round = 1;
		if (pick%NUMBER_OF_TEAMS ==0){
			round = pick/NUMBER_OF_TEAMS;
		}else{
			round = pick/NUMBER_OF_TEAMS + 1;
		}
		return round;
	}

	public synchronized int getPickInRound(){
		return getPickInRound(getCurrentPick());
	}

	public synchronized int getPickInRound(int pick){
		if (pick <= NUMBER_OF_TEAMS) return pick;
		if (pick % NUMBER_OF_TEAMS == 0) return NUMBER_OF_TEAMS;
		return (pick % NUMBER_OF_TEAMS) ;
		//return 0;
	}

	public synchronized SbfTeam getTeamOnTheClock(){
		int round = getRound();
		int startingDraftSlot;
		if (round % 2 == 0){ //even Round
			startingDraftSlot = (NUMBER_OF_TEAMS+1) - getPickInRound();
		}else{
			startingDraftSlot = getPickInRound();
		}
		Integer ownerId = draftService.getPickOwnerId(getCurrentPick(), UserSessionVars.getCurrentLeague().getLeagueId());
		if (ownerId != null){
			return leagueService.getSbfTeamByTeamId(ownerId, UserSessionVars.getCurrentLeague().getLeagueId());
		}

		for (SbfTeam currentTeam : leagueService.getAllSbfTeamsForLeague(UserSessionVars.getCurrentLeague().getLeagueId())){
			if (currentTeam.getDraftSlot() == startingDraftSlot) return currentTeam;
		}
		return null;
	}

	public synchronized void undoLastDraftPick(){
		if (draftService.getAllDraftRecords(UserSessionVars.getCurrentLeague().getLeagueId()).isEmpty()) return;
		SbfDraftRecord lastDraftedPlayer = 
				draftService.getAllDraftRecords(UserSessionVars.getCurrentLeague().getLeagueId()).stream().max(
						(s1,s2)->Integer.compare(s1.getSlotDrafted(), s2.getSlotDrafted())
						).get();
		draftService.deleteSbfDraftRecord(lastDraftedPlayer);
	}

	public synchronized SbfDraftRecord draftPlayer(Player player, int leagueId){
		if (draftService.getSbfDraftRecordByPlayerId(player.getPlayerId(), leagueId) == null){
			SbfDraftRecord draftRecord = new SbfDraftRecord(
					leagueId,
					getTeamOnTheClock().getTeamId(),player.getPlayerId(),
					getCurrentPick(),new Timestamp(System.currentTimeMillis()));
			draftService.addSbfDraftRecord(draftRecord);
			return draftRecord;
		}
		return null;
	}

	public synchronized void movePlayerUp(Player player){
		SbfRank sbfRank = playerService.getSbfRankById(player.getPlayerId(), UserSessionVars.getRankSet().getRankSetId());
		int newRank = sbfRank.getRank()-1;
		if (newRank < 1) return;
		Player existingPlayer;
		if ((existingPlayer=playerService.getPlayerBySbfRank(newRank, UserSessionVars.getRankSet().getRankSetId())) != null){
			playerService.getSbfRankById(existingPlayer.getPlayerId(), UserSessionVars.getRankSet().getRankSetId()).setRank(newRank+1);
			playerService.addRankToUpdateList(playerService.getSbfRankById(existingPlayer.getPlayerId(), UserSessionVars.getRankSet().getRankSetId()));
		}else{
			for(int i = newRank; i>0; i--){//player added that wasnt ranked and given 1000 default rank
				existingPlayer=playerService.getPlayerBySbfRank(i, UserSessionVars.getRankSet().getRankSetId());
				if (existingPlayer != null){
					playerService.getSbfRankById(existingPlayer.getPlayerId(), UserSessionVars.getRankSet().getRankSetId()).setRank(i+1);
					playerService.addRankToUpdateList(playerService.getSbfRankById(existingPlayer.getPlayerId(), UserSessionVars.getRankSet().getRankSetId()));
					newRank = i;
					break;
				}
			}
		}
		sbfRank.setRank(newRank);
		playerService.addRankToUpdateList(sbfRank);
	}

	public synchronized void setNewRankValue(SbfRank rank, int newRank){
		if (newRank <= 0) return;

		SbfRank oldRank = playerService.getSbfRankObjByRank(newRank, UserSessionVars.getRankSet().getRankSetId());
		oldRank.setRank(rank.getRank());
		rank.setRank(newRank);

		playerService.addRankToUpdateList(rank);
		playerService.addRankToUpdateList(oldRank);
	}

	public synchronized void movePlayerDown(Player player){
		SbfRank sbfRank = playerService.getSbfRankById(player.getPlayerId(),UserSessionVars.getRankSet().getRankSetId());
		int newRank = sbfRank.getRank()+1;
		if (newRank < 1) return;
		Player existingPlayer;
		if ((existingPlayer=playerService.getPlayerBySbfRank(newRank, UserSessionVars.getRankSet().getRankSetId())) != null){
			playerService.getSbfRankById(existingPlayer.getPlayerId(), UserSessionVars.getRankSet().getRankSetId()).setRank(newRank-1);
			playerService.addRankToUpdateList(playerService.getSbfRankById(existingPlayer.getPlayerId(), UserSessionVars.getRankSet().getRankSetId()));
		}
		sbfRank.setRank(newRank);
		playerService.addRankToUpdateList(sbfRank);
	}

	public synchronized List<Integer> getPicksForTeam(int teamId){
		List<SbfPickTrade> pickTrades = draftService.getAllSbfPickTrades(UserSessionVars.getCurrentLeague().getLeagueId());
		SbfTeam team = leagueService.getSbfTeamByTeamId(teamId, UserSessionVars.getCurrentLeague().getLeagueId());
		int numTeams = leagueService
				.getLeagueById(UserSessionVars.getCurrentLeague().getLeagueId())
				.getNumTeams();

		ArrayList<Integer> picks = new ArrayList<Integer>();
		int currentPick;
		Integer testTeamId;
		for(int i = 1; i<=NUMBER_OF_ROUNDS; i++){
			if (i%2 == 0){//even round
				currentPick = i*numTeams - team.getDraftSlot() + 1;
			}else{
				currentPick = (i-1)*numTeams + team.getDraftSlot();
			}
			testTeamId = draftService.getPickOwnerId(currentPick, UserSessionVars.getCurrentLeague().getLeagueId());
			if(testTeamId == null || testTeamId == teamId){
				picks.add(currentPick);	
			}
		}
		for(SbfPickTrade pickTrade : pickTrades){
			if(!picks.contains(pickTrade.getPick())){
				if(teamId == draftService.getPickOwnerId(pickTrade.getPick(), UserSessionVars.getCurrentLeague().getLeagueId())){
					picks.add(pickTrade.getPick());
				}
			}
		}
		picks.sort((p1,p2)->Integer.compare(p1, p2));
		return picks;
	}

	public void addTradeRecord(SbfTeam toTeam, SbfTeam fromTeam, int leagueId, Integer pick){
		SbfPickTrade trade = new SbfPickTrade();
		trade.setLeagueId(leagueId);
		trade.setPick(pick);
		trade.setToTeamId(toTeam.getTeamId());
		trade.setFromTeamId(fromTeam.getTeamId());
		draftService.addSbfPickTrade(trade);
	}

	public void resetCheatsheetToDefaultRanks(SbfRankSet c){
		for(Player player : playerService.getAllPlayers()){
			if(player.getProRank() < 500 && player.getProRank() != 0){
				SbfRank rank = new SbfRank(c.getRankSetId(), player.getPlayerId(), player.getProRank());
				playerService.insertSbfRank(rank);
			}

		}
	}

	public void setCheatsheetAsDefaultForLeagueAndUser (SbfRankSet c, SbfLeague l, SbfUser u){
		SbfUserTeam ut = leagueService.getSbfUserTeamForLeagueAndUser(UserSessionVars.getCurrentUser(), 
				UserSessionVars.getCurrentLeague());
		if (ut == null){
			ut = new SbfUserTeam();
			ut.setDefaultRankSetId(c.getRankSetId());
			ut.setUserId(u.getUserId());
			ut.setLeagueId(UserSessionVars.getCurrentLeague().getLeagueId());
			leagueService.insertSbfUserTeam(ut);
		}else{
			ut.setDefaultRankSetId(c.getRankSetId());
			leagueService.updateSbfUserTeam(ut);
		}
		UserSessionVars.resetRankSetToDefault();
	}
}
