package com.jdf.SbfPortal.views;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.backend.data.SbfKeeper;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class KeepersView extends VerticalLayout implements View {
	public static final String NAME = "Keepers";
	private SbfDraftService draftService;
	private PlayerService playerService;
	private SbfLeagueService leagueService;
	private ListDataProvider<Player> playersDataProvider;
	private ListDataProvider<SbfKeeper> keepersDataProvider;

	private Integer leagueId;
	private boolean viewBuilt = false;
	private String playerNameFilterValue="";

	private Grid<Player> playersGrid;
	private Grid<SbfKeeper> keepersGrid;

	List<Player> playerList;
	List<SbfKeeper> keeperList;

	@Override
	public void enter(ViewChangeEvent event) {
		leagueId = UserSessionVars.getCurrentLeague().getLeagueId();
		playerService = UserSessionVars.getPlayerService();
		leagueService = UserSessionVars.getLeagueService();
		draftService = UserSessionVars.getDraftService();

		if (!viewBuilt) {
			buildView();
			viewBuilt=true;
		}

	}

	void buildView(){
		playerList =  playerService.getAllPlayers();
		keeperList = leagueService.getAllSbfKeepers(leagueId);

		playersGrid = configurePlayerGrid(playerList);

		keepersGrid = configureKeeperGrid(keeperList);
		final HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(false);
		layout.setSpacing(true);
		//layout.setHeightUndefined();
		setSizeFull();
		layout.setSizeFull();
		layout.setWidthUndefined();
		if(UserSessionVars.getAccessControl().isUserLeagueManager()) {
			layout.addComponent(playersGrid);
			Button draftKeepersButton = new Button("Draft All Keepers");
			draftKeepersButton.addClickListener(new Button.ClickListener()
			{ @Override public void buttonClick(Button.ClickEvent clickEvent)
			{
				draftAllKeepers();
			} });
			addComponent(draftKeepersButton);
			
		}
		layout.addComponents(keepersGrid);
		updateGridSizes();
		//setSizeUndefined();
		addComponents( layout);
		setExpandRatio(layout, 1);
	}

	@SuppressWarnings("unchecked")
	public Grid<Player> configurePlayerGrid(List<Player> players){
		playersGrid = new Grid<>();
		playersGrid.setCaption("Available Players");
		playersGrid.setItems(players);

		playersGrid.setSizeFull();
		playersGrid.setWidth("600px");
		playersGrid.setSelectionMode(SelectionMode.SINGLE);
		playersGrid.addColumn(Player::getProRank).setCaption("Pro Rank").setId("ProRankColumn").setMaximumWidth(80);
		playersGrid.addColumn(Player::getPosition).setCaption("Position").setId("PositionColumn").setMaximumWidth(80);
		playersGrid.addColumn(Player::getDisplayName).setCaption("Name").setId("PlayerNameColumn");
		playersGrid.addColumn(Player::getTeam).setCaption("Team");
		
		if(UserSessionVars.getAccessControl().isUserLeagueManager()) {
			playersGrid.addColumn(player->"Set Keeper", keeperButtonRenderer()).setId("KeeperColumn");
		}
		playersDataProvider = (ListDataProvider<Player>) playersGrid.getDataProvider();

		playersDataProvider.setFilter(p->p, p -> playersGridFilter(p));
		HeaderRow filterRow = playersGrid.appendHeaderRow();

		TextField playerNameFilter = getTextFilter();
		playerNameFilter.addValueChangeListener(event -> {
			playerNameFilterValue = event.getValue();
			playersDataProvider.refreshAll();
		});

		//		playersGrid.getColumn("KeeperColumn").setStyleGenerator(p -> {
		//			if (leagueService.getSbfKeeperByPlayerId(p.getPlayerId(), leagueId)!= null) {
		//				return "hidden" ;
		//			}else{
		//				return null;
		//			}
		//		});

		filterRow.getCell("PlayerNameColumn").setComponent(playerNameFilter);

		playersGrid.sort("ProRankColumn");

		return playersGrid;

	}

	@SuppressWarnings("unchecked")
	public Grid<SbfKeeper> configureKeeperGrid(List<SbfKeeper> keepers){
		keepersGrid = new Grid<>();
		keepersGrid.setCaption("Kept Players");
		keepersGrid.setItems(keepers);

		//keepersGrid.setSizeFull();
		keepersGrid.setSelectionMode(SelectionMode.SINGLE);
		keepersGrid.addColumn(k->leagueService.getSbfTeamByTeamId(k.getTeamId(), leagueId).getOwnerName()).setCaption("Team").setId("TeamColumn");
		keepersGrid.addColumn(SbfKeeper::getRound).setCaption("Round Kept").setId("RoundKeptColumn").setMaximumWidth(70);
		keepersGrid.addColumn(k->playerService.getPlayerById(k.getPlayerId()).orElseThrow(()->new RuntimeException("Couldnt Find player " + k.getPlayerId()))
				.getDisplayName()).setCaption("Name").setId("PlayerNameColumn");

		if(UserSessionVars.getAccessControl().isUserLeagueManager()) {
			keepersGrid.addColumn(k->"Delete Keeper", deleteKeeperButtonRenderer()).setId("DeleteColumn");	
		}

		keepersDataProvider = (ListDataProvider<SbfKeeper>) keepersGrid.getDataProvider();

		keepersGrid.setSizeFull();
		keepersGrid.setWidth("600px");
		keepersGrid.sort("TeamColumn");

		return keepersGrid;

	}

	@SuppressWarnings({ "rawtypes" })
	private ButtonRenderer keeperButtonRenderer (){
		ButtonRenderer<Object> keeperButtonRenderer = new ButtonRenderer<Object>();
		keeperButtonRenderer.addClickListener(clickEvent -> {
			Player selectedPlayer = (Player)clickEvent.getItem();
			UI.getCurrent().addWindow(getSetKeeperWindow(selectedPlayer.getPlayerId()));
		});
		return keeperButtonRenderer;
	}

	@SuppressWarnings({ "rawtypes" })
	private ButtonRenderer deleteKeeperButtonRenderer (){
		ButtonRenderer<Object> deleteKeeperButtonRenderer = new ButtonRenderer<Object>();
		deleteKeeperButtonRenderer.addClickListener(clickEvent -> {
			SbfKeeper selectedKeeper = (SbfKeeper)clickEvent.getItem();
			leagueService.deleteSbfKeeper(selectedKeeper);
			SbfDraftRecord r = draftService.getSbfDraftRecordByPlayerId(selectedKeeper.getPlayerId(), leagueId);
			if (r != null) draftService.deleteSbfDraftRecord(r);
			keepersDataProvider.refreshAll();
			playersDataProvider.refreshAll();
			updateGridSizes();
		});
		return deleteKeeperButtonRenderer;
	}

	private boolean playersGridFilter(Player player){
		//Player Name
		if (playerNameFilterValue != null && !playerNameFilterValue.equals("")){
			String playerLower = player.getDisplayName().toLowerCase(Locale.ENGLISH);
			if(!playerLower.contains(playerNameFilterValue)) return false;
		}if (leagueService.getSbfKeeperByPlayerId(player.getPlayerId(), leagueId)!=null){ //already a keeper record
			return false;
		}if (draftService.getSbfDraftRecordByPlayerId(player.getPlayerId(), leagueId) !=null){//already a draft record
			return false;
		}
		return true;
	}

	private TextField getTextFilter(){
		TextField filter = new TextField();
		filter.setWidth("100%");
		filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
		filter.setPlaceholder("Filter");
		return filter;
	}

	private void draftAllKeepers(){
		for(SbfKeeper k: leagueService.getAllSbfKeepers(leagueId)){
			SbfDraftRecord r=draftService.getSbfDraftRecordByPlayerId(k.getPlayerId(), leagueId);
			if (r !=null) {
				draftService.deleteSbfDraftRecord(r);
			}
			draftKeeper(k);
		}

	}

	private boolean draftKeeper(SbfKeeper k){
		if (draftService.getSbfDraftRecordByPlayerId(k.getPlayerId(), leagueId)!=null) {
			Notification.show("Player already Kept", Type.ERROR_MESSAGE);
			return false;
		}
		int keeperPick = 0;
		for(Integer i : UserSessionVars.getLeagueManager().getPicksForTeam(k.getTeamId())) {
			if(LeagueInfoManager.getRound(i) <= k.getRound()) {
				if(i> keeperPick) {
					if (UserSessionVars.getDraftService().getSbfDraftRecordByPickNum(i, leagueId) == null) {
						keeperPick=i;
					}
				}
			}
		}
		
		if(keeperPick > 0) {
			draftService.addSbfDraftRecord(
					new SbfDraftRecord(
							leagueId, 
							k.getTeamId(), 
							k.getPlayerId(), 
							keeperPick, 
							new Timestamp(System.currentTimeMillis()),
							0)); //auction cost if i ever add an auction view
			return true;
		}else {
			if(k.getRound() < 5) {//maybe theres already a keeper taking all the spots available, so move to the next round
				for(Integer i : UserSessionVars.getLeagueManager().getPicksForTeam(k.getTeamId())) {
					if(LeagueInfoManager.getRound(i) < 6) {
						if (UserSessionVars.getDraftService().getSbfDraftRecordByPickNum(i, leagueId) == null && (i<keeperPick || keeperPick ==0) ) {
							keeperPick=i;
						}
					}
				}
			}
			if(keeperPick > 0) {
				draftService.addSbfDraftRecord(
						new SbfDraftRecord(
								leagueId, 
								k.getTeamId(), 
								k.getPlayerId(), 
								keeperPick, 
								new Timestamp(System.currentTimeMillis()),
								0)); //auction cost if i ever add an auction view
				return true;
			}else {
				Notification.show("Could not find required draft pick",Type.WARNING_MESSAGE );
				return false;
			}
			
		}
		
	}
	private Window getSetKeeperWindow(int playerId){
		Window subWindow = new Window(playerService.getPlayerById(playerId).orElseThrow(()->new RuntimeException("Couldnt Find player " + playerId))
				.getDisplayName());
		HorizontalLayout subContent = new HorizontalLayout();
		subContent.setMargin(true);
		ComboBox<SbfTeam> selectTeam =
				new ComboBox<>("Select Team");
		selectTeam.setItems(leagueService.getAllSbfTeamsForLeague(leagueId));
		selectTeam.setItemCaptionGenerator(SbfTeam::getOwnerName);

		ComboBox<Integer> selectRound =
				new ComboBox<>("Select Round");
		selectRound.setItems(new Integer[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18});

		Button submitButton = new Button("Submit");
		submitButton.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			if (selectRound.getValue() != null && selectTeam.getValue() != null){
				SbfKeeper k = new SbfKeeper(leagueId, selectTeam.getValue().getTeamId(), playerId, selectRound.getValue());
				if(UserSessionVars.getLeagueService().getAllSbfKeepersForTeam(leagueId, k.getTeamId()).size() > 4) {
					Notification.show("Selected team already has hit defined keeper limit.", Type.WARNING_MESSAGE);
				}
				else if (draftKeeper(k)) {
					leagueService.insertSbfKeeper(k);
					keepersDataProvider.refreshAll();
					playersDataProvider.refreshAll();
					updateGridSizes();
				}
				
				subWindow.close();
			}
		} });

		subContent.addComponents(selectTeam, selectRound, submitButton);
		subWindow.setContent(subContent);
		subWindow.center();
		//	subWindow.setClosable(false);
		return subWindow;

	}
	
	private void updateGridSizes() {
	//	keepersGrid.setHeightByRows(keepersDataProvider.getItems().size());
		//playersGrid.setHeightByRows(playersDataProvider.getItems().size());
	}

}
