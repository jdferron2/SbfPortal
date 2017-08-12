package com.jdf.SbfPortal.views;


import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.UiComponents.DraftBoardPopupUI;
import com.jdf.SbfPortal.UiComponents.DraftDisplayPopupUI;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Audio;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class DraftDayView extends HorizontalLayout implements View {
	private Label onTheClock =  new Label();
	private Grid<Player> availableGrid;
	private Grid<SbfDraftRecord> draftedGrid;
	private ListDataProvider<Player> playersDataProvider;
	private ListDataProvider<SbfDraftRecord> draftedPlayersDataProvider;
	List<Player> playerList;
	private String availPlayerNameFilterValue="";
	private String availPositionFilterValue="";
	private String availIsDraftedFilterValue="Available";
	private boolean viewBuilt = false;
	private boolean icingEnabled;
	private SbfDraftService draftService;
	private PlayerService playerService;
	private SbfLeagueService leagueService;
	private LeagueInfoManager leagueMgr;
	private Integer leagueId;
	private Integer sbfId;
	private Random rand = new Random();
	private boolean isAWinner = false;
	BrowserWindowOpener draftDisplayOpener;
	
	BrowserWindowOpener draftBoardDisplayOpener;
	
	private Audio PICKISINSOUND = new Audio(null, new ThemeResource("audio/pickIsInChime.mp3"));
	Window resumeWindow = new Window("Resume Draft");

	private final Command filterCommand = new Command() {
		@Override
		public void menuSelected(final MenuItem selectedItem) {
			String filter = selectedItem.getParent().getDescription();
			String filterValue = selectedItem.getText();
			if(filter != null ){
				if (filter.equals("Position Filter")){
					setAvailPositionFilterValue(filterValue);
					selectedItem.getParent().setText(filterValue);
				}
				if (filter.equals("Is Drafted Filter")){
					setAvailIsDraftedFilterValue(filterValue);
					selectedItem.getParent().setText(filterValue);
				}
			}
			playersDataProvider.refreshAll();
		}};

		public DraftDayView(){

		}

		public void enter(ViewChangeEvent event) {
			if(playerService==null){
				playerService = 
						(PlayerService) UI.getCurrent().getSession().getAttribute(SessionAttributes.PLAYER_SERVICE);
			}
			if(draftService==null){
				draftService = 
						(SbfDraftService) UI.getCurrent().getSession().getAttribute(SessionAttributes.DRAFT_SERVICE);
			}
			if(leagueService==null){
				leagueService = 
						(SbfLeagueService) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_SERVICE);
			}
			if(leagueMgr==null){
				leagueMgr =
						(LeagueInfoManager) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_MANAGER);
			}
			if(leagueId == null){
				leagueId = 
						(Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_ID);
			}
			if(sbfId == null){
				sbfId = 
						(Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.SBF_ID);
			}
			if(UI.getCurrent().getSession().getAttribute(SessionAttributes.ICING_ENABLED) == null){
				UI.getCurrent().getSession().setAttribute(SessionAttributes.ICING_ENABLED, true);
				icingEnabled = true;
			}else{
				icingEnabled = (boolean) UI.getCurrent().getSession().getAttribute(SessionAttributes.ICING_ENABLED);
			}
			
			if (!viewBuilt) {
				buildView();
				viewBuilt=true;
			}

			playersDataProvider.refreshAll();
			draftedPlayersDataProvider.refreshAll();
		}

		protected void buildView(){
			playerList =  playerService.getTopNPlayers(500);
			onTheClock.setCaptionAsHtml(true);
			setOnTheClockCaption();
			setSizeFull();


			final VerticalLayout layout = new VerticalLayout();
			layout.setMargin(false);
			layout.setSizeFull();
			layout.setSpacing(false);

			final HorizontalLayout bannerLayout = new HorizontalLayout();
			bannerLayout.setSizeFull();
			PICKISINSOUND.setShowControls(false); PICKISINSOUND.setSizeUndefined();

			final HorizontalLayout gridsLayout = new HorizontalLayout();
			gridsLayout.setSizeFull();

			availableGrid = configureAvailableGrid(playerList);
			availableGrid.setSizeFull();

			draftedGrid = configureDraftedGrid();

			createResumeWindow();

			Button pickIsInButton = new Button("Pick is In!!!");
			pickIsInButton.addClickListener(new Button.ClickListener()
			{ @Override public void buttonClick(Button.ClickEvent clickEvent)
			{
				for(UI t : getSession().getUIs()){
					if(t.getClass().equals(DraftDisplayPopupUI.class)){
						((DraftDisplayPopupUI) t).pickIsIn();
					}
				}
				PICKISINSOUND.play();
			} });

			final Button launchDraftDisplay = new Button("Launch Draft Display");
			draftDisplayOpener = new BrowserWindowOpener(DraftDisplayPopupUI.class);
			draftDisplayOpener.setFeatures("Height=500, width=500");
			draftDisplayOpener.extend(launchDraftDisplay);
			
			final Button launchDraftBoard = new Button("Launch Draft Board");
			draftBoardDisplayOpener = new BrowserWindowOpener(DraftBoardPopupUI.class);
			draftBoardDisplayOpener.setFeatures("Height=500, width=500");
			draftBoardDisplayOpener.extend(launchDraftBoard);

			bannerLayout.addComponents(PICKISINSOUND,onTheClock,pickIsInButton, launchDraftDisplay, launchDraftBoard);
			bannerLayout.setHeight("70px");
			bannerLayout.setExpandRatio(PICKISINSOUND, 0);
			bannerLayout.setExpandRatio(onTheClock, 1);

			gridsLayout.addComponents(availableGrid,draftedGrid);
			layout.addComponents(bannerLayout,gridsLayout);
			layout.setExpandRatio(gridsLayout, 1);
			addComponent(layout);
		}
		@SuppressWarnings("unchecked")
		public Grid<SbfDraftRecord> configureDraftedGrid(){
			Grid<SbfDraftRecord> draftedGrid = new Grid<>();
			draftedGrid.setItems(draftService.getAllDraftRecords(leagueId));
			draftedGrid.setSizeFull();
			draftedGrid.setSelectionMode(SelectionMode.SINGLE);
			draftedGrid.addColumn(s->playerService.getPlayerById(s.getPlayerId()).getDisplayName())
			.setCaption("Player Name");
			draftedGrid.addColumn(s->playerService.getPlayerById(s.getPlayerId()).getPosition())
			.setCaption("Position");
			draftedGrid.addColumn(s->leagueService.getSbfTeamBySbfId(s.getSbfId(), leagueId).getOwnerName())
			.setCaption("Drafted By");
			draftedGrid.addColumn(SbfDraftRecord::getSlotDrafted).setCaption("Drafted").setId("DraftedColumn");
			draftedGrid.addColumn(player->"Undo", undoButtonRenderer()).setId("UndoColumn");
			draftedPlayersDataProvider = (ListDataProvider<SbfDraftRecord>) draftedGrid.getDataProvider();
			draftedGrid.sort("DraftedColumn");
			draftedGrid.getColumn("UndoColumn").setStyleGenerator(p -> {
				if (leagueService.getSbfKeeperByPlayerId(p.getPlayerId(), leagueId)!= null) {
					return "hidden" ;
				}else{
					return null;
				}
			});

			return draftedGrid;
		}

		@SuppressWarnings("unchecked")
		public Grid<Player> configureAvailableGrid(List<Player> players){
			Grid<Player> availableGrid = new Grid<>();
			availableGrid.setItems(players);

			availableGrid.setSizeFull();
			availableGrid.setSelectionMode(SelectionMode.SINGLE);
			availableGrid.addColumn(p->playerService.getSbfRankById(p.getPlayerId(), sbfId) == null ? 1000 : 
				playerService.getSbfRankById(p.getPlayerId(), sbfId).getRank()
					).setCaption("My Rank").setId("RankColumn");
			availableGrid.addColumn(Player::getPosition).setCaption("Position").setId("PositionColumn");
			availableGrid.addColumn(Player::getDisplayName).setCaption("Name").setId("PlayerNameColumn");
			availableGrid.addColumn(Player::getTeam).setCaption("Team");
			availableGrid.addColumn(player->"Draft!", draftedButtonRenderer()).setId("DraftedColumn");

			playersDataProvider = (ListDataProvider<Player>) availableGrid.getDataProvider();

			playersDataProvider.setFilter(p->p, p -> availableGridFilter(p));
			HeaderRow filterRow = availableGrid.appendHeaderRow();

			TextField availPlayerNameFilter = getTextFilter();
			availPlayerNameFilter.addValueChangeListener(event -> {
				setAvailPlayerNameFilterValue(event.getValue());
				playersDataProvider.refreshAll();
			});

			availableGrid.getColumn("DraftedColumn").setStyleGenerator(p -> {
				if (draftService.getSbfDraftRecordByPlayerId(p.getPlayerId(), leagueId)!= null) {
					return "hidden" ;
				}else{
					return null;
				}
			});

			MenuBar availPositionFilter = getPositionFilter();

			MenuBar availIsDraftedFilter = getIsDraftedFilter();

			filterRow.getCell("PlayerNameColumn").setComponent(availPlayerNameFilter);
			filterRow.getCell("PositionColumn").setComponent(availPositionFilter);
			filterRow.getCell("DraftedColumn").setComponent(availIsDraftedFilter);

			availableGrid.sort("RankColumn");

			return availableGrid;

		}

		public boolean availableGridFilter(Player player){
			//Player Name
			if (availPlayerNameFilterValue != null && !availPlayerNameFilterValue.equals("")){
				String playerLower = player.getDisplayName().toLowerCase(Locale.ENGLISH);
				if(!playerLower.contains(availPlayerNameFilterValue)) return false;
			}
			//are they drafted?
			if (availIsDraftedFilterValue.equals("Available")){
				if (draftService.getSbfDraftRecordByPlayerId(player.getPlayerId(), leagueId) != null) {
					return false;
				}
			}else if (availIsDraftedFilterValue.equals("Drafted")){
				if (draftService.getSbfDraftRecordByPlayerId(player.getPlayerId(), leagueId) == null) {
					return false;
				}
			}

			//player position
			if (availPositionFilterValue != null && !availPositionFilterValue.equals("") && !availPositionFilterValue.equalsIgnoreCase("All")){
				if (!player.getPosition().toLowerCase().equals(availPositionFilterValue)) return false;
			}
			return true;
		}

		public void setAvailPlayerNameFilterValue(String name){
			this.availPlayerNameFilterValue = name.toLowerCase();
		}

		public void setAvailPositionFilterValue(String position){
			if (position == null) {
				this.availPositionFilterValue = position;
			}
			else {
				this.availPositionFilterValue = position.toLowerCase();
			}
		}

		public void setAvailIsDraftedFilterValue(String isDraftedFilterSelection){
			this.availIsDraftedFilterValue = isDraftedFilterSelection;
		}


		public TextField getTextFilter(){
			TextField filter = new TextField();
			filter.setWidth("100%");
			filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
			filter.setPlaceholder("Filter");
			return filter;
		}


		public MenuBar getPositionFilter(){
			MenuBar posFilterMenuBar = new MenuBar();
			posFilterMenuBar.addStyleName("borderless");
			MenuItem availPlayerPosFilter = posFilterMenuBar.addItem("Filter", null);
			availPlayerPosFilter.addItem("All", filterCommand);
			availPlayerPosFilter.addItem("QB", filterCommand);
			availPlayerPosFilter.addItem("RB", filterCommand);
			availPlayerPosFilter.addItem("WR", filterCommand);
			availPlayerPosFilter.addItem("TE", filterCommand);
			availPlayerPosFilter.addItem("K", filterCommand);
			availPlayerPosFilter.addItem("DEF", filterCommand);

			availPlayerPosFilter.setDescription("Position Filter");
			return posFilterMenuBar;
		}

		public MenuBar getIsDraftedFilter(){
			MenuBar isDraftedMenubar = new MenuBar();
			isDraftedMenubar.addStyleName("borderless");
			MenuItem availIsDraftedItem = isDraftedMenubar.addItem("Filter", null);
			availIsDraftedItem.addItem("Drafted", filterCommand);
			availIsDraftedItem.addItem("Available", filterCommand);
			availIsDraftedItem.addItem("All", filterCommand);
			availIsDraftedItem.setDescription("Is Drafted Filter");
			return isDraftedMenubar;
		}

		@SuppressWarnings({ "rawtypes" })
		public ButtonRenderer draftedButtonRenderer (){
			ButtonRenderer<Object> draftButtonRenderer = new ButtonRenderer<Object>();
			draftButtonRenderer.addClickListener(clickEvent -> {
				Player selectedPlayer = (Player)clickEvent.getItem();
				leagueMgr.draftPlayer(selectedPlayer, leagueId);
				//availableGrid.getDataProvider().refreshAll();
				draftedGrid.getDataProvider().refreshAll();
				//availableGrid.getDataProvider().refreshItem(selectedPlayer);
				playersDataProvider.refreshAll();
				int randomInt = rand.nextInt(99) + 1;
				if (randomInt < 4 && icingEnabled) isAWinner=true;
				setOnTheClockCaption();
			});
			return draftButtonRenderer;
		}

		@SuppressWarnings({ "rawtypes" })
		public ButtonRenderer undoButtonRenderer (){
			ButtonRenderer<Object> undoButtonRenderer = new ButtonRenderer<Object>();
			undoButtonRenderer.addClickListener(clickEvent -> {
				SbfDraftRecord rec = (SbfDraftRecord)clickEvent.getItem();
				draftService.deleteSbfDraftRecord(rec);
				draftedGrid.getDataProvider().refreshAll();
				Player p = playerService.getPlayerById(rec.getPlayerId());
				//availableGrid.getDataProvider().refreshItem(p);
				playersDataProvider.refreshAll();

				setOnTheClockCaption();
			});
			return undoButtonRenderer;
		}

		public void setOnTheClockCaption(){
			String teamOnTheClock = leagueMgr.getTeamOnTheClock().getOwnerName();
			int round = leagueMgr.getRound();
			int pickInRound =leagueMgr.getPickInRound();
			onTheClock.setCaption("<h2>On The Clock: " + teamOnTheClock + 
					"<br/>Round " + round + ", pick " + pickInRound + "</h2>");

			for(UI t : getSession().getUIs()){
				if(t.getClass().equals(DraftDisplayPopupUI.class)){
					((DraftDisplayPopupUI) t).processPick(isAWinner);
				}else if(t.getClass().equals(DraftBoardPopupUI.class)){
					SbfDraftRecord r = draftService.getSbfDraftRecordByPickNum(leagueMgr.getCurrentPick()-1, leagueId);
					((DraftBoardPopupUI) t).addDraftSelection(r);
				}
			}
			if(isAWinner){
				UI.getCurrent().addWindow(resumeWindow);
				isAWinner=false;
			}				
		}

		private void createResumeWindow(){
			resumeWindow.setModal(true);
			resumeWindow.setClosable(false);
			VerticalLayout layout = new VerticalLayout();
			layout.setMargin(true);
			layout.setSpacing(true);
			Label message = new Label("Resume Draft.");
			layout.addComponent(message);
			resumeWindow.setContent(layout);
			Button resumeButton = new Button("Resume Draft");
			resumeButton.addClickListener(new Button.ClickListener()
			{ @Override public void buttonClick(Button.ClickEvent clickEvent)
			{
				for(UI t : getSession().getUIs()){
					if(t.getClass().equals(DraftDisplayPopupUI.class)){
						((DraftDisplayPopupUI) t).processPick(false);
					}			
				}
				resumeWindow.close();
			}
			});

			layout.addComponent(resumeButton);
			layout.setComponentAlignment(resumeButton, Alignment.MIDDLE_RIGHT);

		}
}