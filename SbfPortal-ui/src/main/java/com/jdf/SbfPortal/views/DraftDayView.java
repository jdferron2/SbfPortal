package com.jdf.SbfPortal.views;


import java.util.List;
import java.util.Locale;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
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
	private SbfDraftService draftService;
	private PlayerService playerService;
	private SbfLeagueService leagueService;
	private LeagueInfoManager leagueMgr;
	private Integer leagueId;

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
			if (!viewBuilt) {
				buildView();
				viewBuilt=true;
			}
			
			playersDataProvider.refreshAll();
			draftedPlayersDataProvider.refreshAll();
		}

		protected void buildView(){
			playerList =  playerService.getAllPlayers();
			onTheClock.setCaptionAsHtml(true);
			setOnTheClockCaption();
			setSizeFull();


			final VerticalLayout layout = new VerticalLayout();
			layout.setMargin(false);
			layout.setSizeFull();
			layout.setSpacing(false);

			final HorizontalLayout bannerLayout = new HorizontalLayout();
			bannerLayout.setSizeFull();

			final HorizontalLayout gridsLayout = new HorizontalLayout();
			gridsLayout.setSizeFull();

			availableGrid = configureAvailableGrid(playerList);
			availableGrid.setSizeFull();

			draftedGrid = configureDraftedGrid();

			Button undoButton = new Button("Undo");
			undoButton.addClickListener(new Button.ClickListener()
			{ @Override public void buttonClick(Button.ClickEvent clickEvent)
			{
				leagueMgr.undoLastDraftPick();
				draftedPlayersDataProvider.refreshAll();
				playersDataProvider.refreshAll();
				setOnTheClockCaption();
			} });

			bannerLayout.addComponents(onTheClock, undoButton);
			bannerLayout.setComponentAlignment(undoButton, Alignment.TOP_RIGHT);
			bannerLayout.setHeight("70px");
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
			draftedGrid.addColumn(SbfDraftRecord::getSlotDrafted).setCaption("Drafted");
			draftedPlayersDataProvider = (ListDataProvider<SbfDraftRecord>) draftedGrid.getDataProvider();
			return draftedGrid;
		}

		@SuppressWarnings("unchecked")
		public Grid<Player> configureAvailableGrid(List<Player> players){
			Grid<Player> availableGrid = new Grid<>();
			availableGrid.setItems(players);

			availableGrid.setSizeFull();
			availableGrid.setSelectionMode(SelectionMode.SINGLE);
			availableGrid.addColumn(p->playerService.getSbfRankById(p.getPlayerId(), leagueId).getRank()).setCaption("My Rank");
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
			ButtonRenderer<Object> test = new ButtonRenderer<Object>();
			test.addClickListener(clickEvent -> {
				Player selectedPlayer = (Player)clickEvent.getItem();
				leagueMgr.draftPlayer(selectedPlayer, leagueId);
				availableGrid.getDataProvider().refreshAll();
				draftedGrid.getDataProvider().refreshAll();

				setOnTheClockCaption();
			});
			return test;
		}

		public void setOnTheClockCaption(){
			String teamOnTheClock = leagueMgr.getTeamOnTheClock().getOwnerName();
			int round = leagueMgr.getRound();
			int pickInRound =leagueMgr.getPickInRound();
			onTheClock.setCaption("<h2>On The Clock: " + teamOnTheClock + 
					"<br/>Round " + round + ", pick " + pickInRound + "</h2>");
		}
}