package com.jdf.SbfPortal.views;

import java.util.ArrayList;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.UiComponents.ConfirmButton;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.DraftRank;
import com.jdf.SbfPortal.backend.data.DraftRankings;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.Players;
import com.jdf.SbfPortal.backend.data.SbfRank;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.jdf.SbfPortal.backend.data.SbfUserTeam;
import com.jdf.SbfPortal.backend.utility.RestAPIUtils;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class AdminView extends VerticalLayout implements View  {
	public static final String NAME = "Admin Functions";
	private ComboBox<Integer> team1PickSelector;
	private ComboBox<Integer> team2PickSelector;
	private ComboBox<SbfTeam> team1Selector;
	private ComboBox<SbfTeam> team2Selector;
	private Button processTrade = new Button("Process Trade");
	private GridLayout tradeLayout = new GridLayout(3,3);
	private boolean viewBuilt = false;
	private Integer leagueId;
	private Integer rankSetId;
	private Integer userId;
	private PlayerService playerService;
	private SbfLeagueService leagueService;
	private LeagueInfoManager leagueMgr;
	private boolean icingEnabled;
	private boolean themeSongsEnabled;
	public AdminView(){

	}
	@Override
	public void enter(ViewChangeEvent event) {
		userId = UserSessionVars.getCurrentUser().getUserId();
		rankSetId = UserSessionVars.getRankSet().getRankSetId();
		leagueId = UserSessionVars.getCurrentLeague().getLeagueId();
		leagueMgr = UserSessionVars.getLeagueManager();
		playerService = UserSessionVars.getPlayerService();
		leagueService = UserSessionVars.getLeagueService();
		if(UI.getCurrent().getSession().getAttribute(SessionAttributes.ICING_ENABLED) == null){
			UI.getCurrent().getSession().setAttribute(SessionAttributes.ICING_ENABLED, true);
			icingEnabled = true;
		}else{
			icingEnabled = (boolean) UI.getCurrent().getSession().getAttribute(SessionAttributes.ICING_ENABLED);
		}
		
		if(UI.getCurrent().getSession().getAttribute(SessionAttributes.THEME_SONGS_ENABLED) == null){
			UI.getCurrent().getSession().setAttribute(SessionAttributes.THEME_SONGS_ENABLED, true);
			themeSongsEnabled = true;
		}else{
			themeSongsEnabled = (boolean) UI.getCurrent().getSession().getAttribute(SessionAttributes.ICING_ENABLED);
		}
		if(!viewBuilt){
			buildView();
			viewBuilt=true;
		}	
	}

	private void buildView(){
		SbfUserTeam currentUT = UserSessionVars.getLeagueService().getSbfUserTeamForLeagueAndUser(
				UserSessionVars.getCurrentUser(),UserSessionVars.getCurrentLeague());
		team1PickSelector = createTradeBox(currentUT.getTeamId());
		team2PickSelector = createTradeBox(currentUT.getTeamId());
		team1Selector =  this.createTeamSelectorCB("Team 1", team1PickSelector);
		team2Selector =  this.createTeamSelectorCB("Team 2", team2PickSelector);
		CheckBox enableIcing = new CheckBox("Icing Enabled");
		enableIcing.setValue(icingEnabled);

		enableIcing.addValueChangeListener(event ->
		UI.getCurrent().getSession().setAttribute(SessionAttributes.ICING_ENABLED, enableIcing.getValue())
				);
		
		CheckBox enableThemeSongs = new CheckBox("Theme Songs Enabled");
		enableThemeSongs.setValue(themeSongsEnabled);

		enableThemeSongs.addValueChangeListener(event ->
		UI.getCurrent().getSession().setAttribute(SessionAttributes.THEME_SONGS_ENABLED, enableThemeSongs.getValue())
				);

		setSpacing(true);
		setMargin(true);
		Button resetPlayerList = new Button("Reset Players Table");
		resetPlayerList.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				//delete current player table
				playerService.deleteAllPlayers();

				//re-load active players from api
				Players players = RestAPIUtils.getInstance().invokeQueryPlayers();
				for(Player player : players.getPlayers()){
					//if (player.getActive() == 1){
					playerService.insertPlayer(player);
					//}			
				}

				//set pro ranks for all players
				DraftRankings ranks = RestAPIUtils.getInstance().invokeQueryRanks();
				for(DraftRank rank: ranks.getDraftRanks()){
					Player player = playerService.getPlayerById(rank.getPlayerId());
					if (player != null){
						player.setProRank(rank.getProRank());
						playerService.updatePlayer(player);
					}
				}

				for (Player p : playerService.getAllPlayers()){
					if (p.getProRank() == 0){
						p.setProRank(9999);
						playerService.updatePlayer(p);
					}
				}
				//				ArrayList<SbfRank> ranksToDelete = new ArrayList<SbfRank>();
				//				for (SbfRank r : playerService.getAllSbfRanks(sbfId)){
				//					if (playerService.getPlayerById(r.getPlayerId()) == null){
				//						//System.out.println("DELETE RANK FOR " + r.getPlayerId());
				//						ranksToDelete.add(r);
				//					}
				//				}
				//				
				//				for (SbfRank r : ranksToDelete){
				//					playerService.deleteSbfRank(r);
				//				}


				Notification.show("Player list update successfully!");;		
			}

		});

		ConfirmButton resetMyRanks = new ConfirmButton("Reset My Ranks");
		resetMyRanks.setConfirmationText("This will reset custom ranks to the default pro ranks.");
		resetMyRanks.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				//delete current ranks
				playerService.deleteAllSbfRanks(rankSetId);

				//add default ranks based on pro ranks
				for(Player player : playerService.getAllPlayers()){
					if(player.getProRank() < 500 && player.getProRank() != 0){
						SbfRank rank = new SbfRank(rankSetId, player.getPlayerId(), player.getProRank());
						playerService.insertSbfRank(rank);
					}

				}

				Notification.show("Ranks updated successfully!");
			}

		});
		
		ConfirmButton resetDefaultRanks = new ConfirmButton("Reset Default Ranks");
		resetDefaultRanks.setConfirmationText("This will reset default ranks to the current pro ranks.");
		resetDefaultRanks.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				//delete current ranks
				playerService.deleteAllSbfRanks(0);

				//add default ranks based on pro ranks
				for(Player player : playerService.getAllPlayers()){
					if(player.getProRank() < 500 && player.getProRank() != 0){
						SbfRank rank = new SbfRank(0, player.getPlayerId(), player.getProRank());
						playerService.insertSbfRank(rank);
					}

				}

				Notification.show("Ranks updated successfully!");
			}

		});

		processTrade.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				if(team2PickSelector.getValue()!= null ){
					leagueMgr.addTradeRecord(team1Selector.getValue(), team2Selector.getValue(), leagueId, team2PickSelector.getValue());
				}
				if(team1PickSelector.getValue()!= null ){
					leagueMgr.addTradeRecord(team2Selector.getValue(), team1Selector.getValue(), leagueId, team1PickSelector.getValue());
				}
				team1PickSelector.clear();
				team2PickSelector.clear();
				team1Selector.clear();
				team2Selector.clear();
				Notification.show("Trade Processed successfully!");
			}

		});
		processTrade.setVisible(false);
		tradeLayout.addComponent(team1Selector,0,0);
		tradeLayout.addComponent(team2Selector,1,0);
		tradeLayout.addComponent(processTrade, 1,2);
		team1PickSelector.setVisible(false);
		team2PickSelector.setVisible(false);
		tradeLayout.addComponent(team1PickSelector,0,1);
		tradeLayout.addComponent(team2PickSelector,1,1);
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.addComponents(resetPlayerList, resetMyRanks,resetDefaultRanks);
		addComponents(enableIcing, enableThemeSongs, buttonLayout, new Label("PROCESS TRADE"), tradeLayout);
	}

	public ComboBox<Integer> createTradeBox(int teamId){
		ComboBox<Integer> teamPicksCB = new ComboBox<Integer>("Picks");
		teamPicksCB.setItemCaptionGenerator(
				i->"Pick: " + Integer.toString(i) + " (r" + 
						Integer.toString(leagueMgr.getRound(i))+
						" p" + leagueMgr.getPickInRound(i) + ")");
		teamPicksCB.setItems(leagueMgr.getPicksForTeam(teamId));
		teamPicksCB.addValueChangeListener(event-> {
			setSubmitVisible();
		});
		return teamPicksCB;
	}

	public ComboBox<SbfTeam> createTeamSelectorCB(String name, ComboBox<Integer> connectedBox){
		ComboBox<SbfTeam> teamCB = new ComboBox<SbfTeam>(name);
		teamCB.setItems(leagueService.getAllSbfTeamsForLeague(leagueId));
		teamCB.setItemCaptionGenerator(SbfTeam::getOwnerName);
		teamCB.addValueChangeListener(event-> {
			if(event.getValue()!= null){
				connectedBox.clear();
				connectedBox.setItems(leagueMgr.getPicksForTeam(event.getValue().getTeamId()));
				connectedBox.setVisible(true);
				setSubmitVisible();
			}else{
				connectedBox.clear();
				connectedBox.setVisible(false);
				setSubmitVisible();
			}
		});

		return teamCB;
	}

	public void setSubmitVisible(){
		if ((team1PickSelector.getValue() != null || team2PickSelector.getValue() != null) &&
				(team1Selector.getValue() !=null && team2Selector.getValue() !=null)){
			processTrade.setVisible(true);
		}else{
			processTrade.setVisible(false);
		}
	}

}
