package com.jdf.SbfPortal.utility;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfDraftPick;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.backend.data.SbfRank;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.vaadin.ui.UI;

public class LeagueInfoManager {
	public static final int	NUMBER_OF_TEAMS = 12;
	public static final int	NUMBER_OF_ROUNDS =17;
	Integer leagueId;
	Integer sbfId;
	PlayerService playerService;
	SbfDraftService draftService;
	SbfLeagueService leagueService;

	public LeagueInfoManager(){
		initialize();
	}

	private void initialize(){
		sbfId = (Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.SBF_ID);
		leagueId = (Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_ID);
		playerService = (PlayerService) UI.getCurrent().getSession().getAttribute(SessionAttributes.PLAYER_SERVICE);
		draftService = (SbfDraftService) UI.getCurrent().getSession().getAttribute(SessionAttributes.DRAFT_SERVICE);
		leagueService = (SbfLeagueService) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_SERVICE);
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
		List<SbfDraftRecord> draftList = new ArrayList<>(draftService.getAllDraftRecords(leagueId));
		draftList.sort((s1,s2)->Integer.compare(s1.getSlotDrafted(), s2.getSlotDrafted()));
		if (draftService.getAllDraftRecords(leagueId).isEmpty()) return 1;
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
		Integer ownerId = draftService.getPickOwnerId(getCurrentPick(), leagueId);
		if (ownerId != null){
			return leagueService.getSbfTeamBySbfId(ownerId, leagueId);
		}

		for (SbfTeam currentTeam : leagueService.getAllSbfTeams(leagueId)){
			if (currentTeam.getDraftSlot() == startingDraftSlot) return currentTeam;
		}
		return null;
	}

	public synchronized void undoLastDraftPick(){
		if (draftService.getAllDraftRecords(leagueId).isEmpty()) return;
		SbfDraftRecord lastDraftedPlayer = 
				draftService.getAllDraftRecords(leagueId).stream().max(
						(s1,s2)->Integer.compare(s1.getSlotDrafted(), s2.getSlotDrafted())
						).get();
		draftService.deleteSbfDraftRecord(lastDraftedPlayer);
	}

	public synchronized void draftPlayer(Player player, int leagueId){
		if (draftService.getSbfDraftRecordByPlayerId(player.getPlayerId(), leagueId) == null){
			SbfDraftRecord draftRecord = new SbfDraftRecord(
					leagueId,
					getTeamOnTheClock().getSbfId(),player.getPlayerId(),
					getCurrentPick(),new Timestamp(System.currentTimeMillis()));
			draftService.addSbfDraftRecord(draftRecord);
		}
	}
	
	public synchronized void movePlayerUp(Player player){
		SbfRank sbfRank = playerService.getSbfRankById(player.getPlayerId(), sbfId);
		int newRank = sbfRank.getRank()-1;
		if (newRank < 1) return;
		Player existingPlayer;
		if ((existingPlayer=playerService.getPlayerBySbfRank(newRank, sbfId)) != null){
			playerService.getSbfRankById(existingPlayer.getPlayerId(), sbfId).setRank(newRank+1);
			playerService.addRankToUpdateList(playerService.getSbfRankById(existingPlayer.getPlayerId(), sbfId));
		}else{
			for(int i = newRank; i>0; i--){//player added that wasnt ranked and given 1000 default rank
				existingPlayer=playerService.getPlayerBySbfRank(i, sbfId);
				if (existingPlayer != null){
					playerService.getSbfRankById(existingPlayer.getPlayerId(), sbfId).setRank(i+1);
					playerService.addRankToUpdateList(playerService.getSbfRankById(existingPlayer.getPlayerId(), sbfId));
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
		
		SbfRank oldRank = playerService.getSbfRankObjByRank(newRank, sbfId);
		oldRank.setRank(rank.getRank());
		rank.setRank(newRank);

		playerService.addRankToUpdateList(rank);
		playerService.addRankToUpdateList(oldRank);
	}

	public synchronized void movePlayerDown(Player player){
		SbfRank sbfRank = playerService.getSbfRankById(player.getPlayerId(),sbfId);
		int newRank = sbfRank.getRank()+1;
		if (newRank < 1) return;
		Player existingPlayer;
		if ((existingPlayer=playerService.getPlayerBySbfRank(newRank, sbfId)) != null){
			playerService.getSbfRankById(existingPlayer.getPlayerId(), sbfId).setRank(newRank-1);
			playerService.addRankToUpdateList(playerService.getSbfRankById(existingPlayer.getPlayerId(), sbfId));
		}
		sbfRank.setRank(newRank);
		playerService.addRankToUpdateList(sbfRank);
	}

	public synchronized List<Integer> getPicksForTeam(int teamId){
		List<SbfDraftPick> extraPicks = draftService.getAllSbfDraftPicks(leagueId);
		SbfTeam team = leagueService.getSbfTeamBySbfId(teamId, leagueId);
		int numTeams = leagueService
				.getLeagueById(leagueId)
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
			testTeamId = draftService.getPickOwnerId(currentPick, leagueId);
			if(testTeamId == null || testTeamId == teamId){
				picks.add(currentPick);	
			}
		}
		for(SbfDraftPick pick : extraPicks){
			if(!picks.contains(pick.getPick()) && pick.getSbfId() == teamId){
				picks.add(pick.getPick());
			}
		}
		picks.sort((p1,p2)->Integer.compare(p1, p2));
		return picks;
	}

	public void addPickToTeam(SbfTeam team, int leagueId, Integer pick) {
		SbfDraftPick sbfDraftPick = draftService.getPickBySbfIdPick(pick, leagueId);
		if(sbfDraftPick != null){
			//pick already traded at some point, update the record to point to the new team
			sbfDraftPick.setSbfId(team.getSbfId());
			draftService.updateSbfDraftPick(sbfDraftPick);
		}else{
			//pick was with original owner, create a new record
			sbfDraftPick = new SbfDraftPick(
					leagueId,
					team.getSbfId(), 
					pick);
			draftService.addSbfDraftPick(sbfDraftPick);
		}
	}
}
