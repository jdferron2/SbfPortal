package com.jdf.SbfPortal.views;


import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.UiComponents.DraftBoardPopupUI;
import com.jdf.SbfPortal.UiComponents.DraftDisplayPopupUI;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.backend.data.SbfRank;
import com.jdf.SbfPortal.backend.utility.BroadcastCommands;
import com.jdf.SbfPortal.backend.utility.Broadcaster;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.annotations.Push;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Audio;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

@Push
@SuppressWarnings("serial")
public class DraftDayView extends HorizontalLayout implements View {
	public final static String NAME = "Draft Day";
	private Label onTheClock =  new Label();
	private Grid<SbfRank> availableGrid;
	private Grid<SbfDraftRecord> draftedGrid;
	private ListDataProvider<SbfRank> ranksDataProvider;
	private ListDataProvider<SbfDraftRecord> draftedPlayersDataProvider;
	List<SbfRank> rankList;
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
	private Random rand = new Random();
	private boolean isAWinner = false;
	BrowserWindowOpener draftDisplayOpener;
	private Integer icePercent;
	private Integer shotPercent;
	private Integer shotgunPercent;

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
			ranksDataProvider.refreshAll();
		}};

		public DraftDayView(){

		}

		public void enter(ViewChangeEvent event) {
			//PICKISINSOUND.setSource(new ExternalResource("http://k003.kiwi6.com/hotlink/q0rm2z6jes/Brandon.mp3"));
			leagueId = UserSessionVars.getCurrentLeague().getLeagueId();
			leagueMgr = UserSessionVars.getLeagueManager();
			playerService = UserSessionVars.getPlayerService();
			leagueService = UserSessionVars.getLeagueService();
			draftService = UserSessionVars.getDraftService();
			if(UI.getCurrent().getSession().getAttribute(SessionAttributes.ICING_ENABLED) == null){
				UI.getCurrent().getSession().setAttribute(SessionAttributes.ICING_ENABLED, true);
				icingEnabled = true;
			}else{
				icingEnabled = (boolean) UI.getCurrent().getSession().getAttribute(SessionAttributes.ICING_ENABLED);
			}
			
			if(UI.getCurrent().getSession().getAttribute(SessionAttributes.ICE_PERCENT) == null){
				UI.getCurrent().getSession().setAttribute(SessionAttributes.ICE_PERCENT, AdminView.DEFAULT_ICE_PERCENT);
				icePercent = AdminView.DEFAULT_ICE_PERCENT;
			}else{
				icePercent = (Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.ICE_PERCENT);
			}
			
			if(UI.getCurrent().getSession().getAttribute(SessionAttributes.SHOT_PERCENT) == null){
				UI.getCurrent().getSession().setAttribute(SessionAttributes.SHOT_PERCENT, AdminView.DEFAULT_SHOT_PERCENT);
				shotPercent = AdminView.DEFAULT_SHOT_PERCENT;
			}else{
				shotPercent = (Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.SHOT_PERCENT);
			}
			
			if(UI.getCurrent().getSession().getAttribute(SessionAttributes.SHOTGUN_PERCENT) == null){
				UI.getCurrent().getSession().setAttribute(SessionAttributes.SHOTGUN_PERCENT, AdminView.DEFAULT_SHOTGUN_PERCENT);
				shotgunPercent = AdminView.DEFAULT_SHOTGUN_PERCENT;
			}else{
				shotgunPercent = (Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.SHOTGUN_PERCENT);
			}

			if (!viewBuilt) {
				buildView();
				viewBuilt=true;
			}

			ranksDataProvider.refreshAll();
			draftedPlayersDataProvider.refreshAll();
		}

		protected void buildView(){
			rankList =  playerService.getAllSbfRanks(UserSessionVars.getRankSet().getRankSetId());
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

			availableGrid = configureAvailableGrid(rankList);
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
			
			if (!UserSessionVars.getAccessControl().isUserLeagueManager()){
				pickIsInButton.setVisible(false);
			}

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
			.setCaption("Player Name").setId("NameColumn");
			draftedGrid.addColumn(s->playerService.getPlayerById(s.getPlayerId()).getPosition())
			.setCaption("Position");
			draftedGrid.addColumn(s->leagueService.getSbfTeamByTeamId(s.getTeamId(), leagueId).getOwnerName())
			.setCaption("Drafted By");
			draftedGrid.addColumn(SbfDraftRecord::getSlotDrafted).setCaption("Drafted").setId("DraftedColumn");
			if(UserSessionVars.getAccessControl().isUserLeagueManager()){
				draftedGrid.addColumn(player->"Undo", undoButtonRenderer()).setId("UndoColumn");
				draftedGrid.getColumn("UndoColumn").setStyleGenerator(p -> {
					if (leagueService.getSbfKeeperByPlayerId(p.getPlayerId(), leagueId)!= null) {
						return "hidden" ;
					}else{
						return null;
					}
				});
				FooterRow footer = draftedGrid.prependFooterRow();
				footer.getCell("NameColumn").setComponent(manualEntryButton() );
			}
			draftedPlayersDataProvider = (ListDataProvider<SbfDraftRecord>) draftedGrid.getDataProvider();
			
			draftedGrid.sort("DraftedColumn");
			

			return draftedGrid;
		}
		
		private Button manualEntryButton (){
			Button button = new Button("Manual Lookup");
			button.setStyleName(ValoTheme.BUTTON_LINK);
			button.addClickListener(clickEvent -> {
				UI.getCurrent().addWindow(getManualEntryWindow());
			});
			return button;
		}
		
		private Window getManualEntryWindow(){
			Window subWindow = new Window("Manual Lookup");
			VerticalLayout subContent = new VerticalLayout();
			ComboBox<Player> playersLookup = new ComboBox<Player>("Player");
			playersLookup.setItems(UserSessionVars.getPlayerService().getAllPlayers());
			playersLookup.setItemCaptionGenerator(Player::getDisplayName);
			subContent.setMargin(true);

			Button submitButton = new Button("Submit");
			submitButton.addClickListener(new Button.ClickListener()
			{ @Override public void buttonClick(Button.ClickEvent clickEvent)
			{
				Player selectedPlayer = playersLookup.getValue();
				if(selectedPlayer!=null) {
					if(UserSessionVars.getDraftService().getSbfDraftRecordByPlayerId(selectedPlayer.getPlayerId(), 
							UserSessionVars.getCurrentLeague().getLeagueId()) == null) {
						//ok to draft the player
						processDraftPick(selectedPlayer);
					}else {
						Notification.show("Player already drafted", Type.WARNING_MESSAGE);
					}
				}
				subWindow.close();

			} });

			subContent.addComponents(playersLookup, submitButton);
			subWindow.setContent(subContent);
			subWindow.center();

			//	subWindow.setClosable(false);
			return subWindow;
		}

		@SuppressWarnings("unchecked")
		public Grid<SbfRank> configureAvailableGrid(List<SbfRank> ranks){
			Grid<SbfRank> availableGrid = new Grid<>();
			availableGrid.setItems(ranks);

			availableGrid.setSizeFull();
			availableGrid.setSelectionMode(SelectionMode.SINGLE);
			availableGrid.addColumn(SbfRank::getRank
					).setCaption("My Rank").setId("RankColumn");
			availableGrid.addColumn(r->playerService.getPlayerById(r.getPlayerId()).getPosition()).setCaption("Position").setId("PositionColumn");
			availableGrid.addColumn(r->playerService.getPlayerById(r.getPlayerId()).getDisplayName()).setCaption("Name").setId("PlayerNameColumn");
			availableGrid.addColumn(r->playerService.getPlayerById(r.getPlayerId()).getTeam()).setCaption("Team");
			availableGrid.addColumn(SbfRank::getTier).setCaption("Tier");
			if(UserSessionVars.getAccessControl().isUserLeagueManager()){
				availableGrid.addColumn(rank->"Draft!", draftedButtonRenderer()).setId("DraftedColumn");
				availableGrid.getColumn("DraftedColumn").setStyleGenerator(r -> {
					if (draftService.getSbfDraftRecordByPlayerId(r.getPlayerId(), leagueId)!= null) {
						return "hidden" ;
					}else{
						return null;
					}
				});
			}			
			availableGrid.setStyleGenerator(rank -> {
				int tier = rank.getTier();
				if (tier>0) {
					return "T"+tier;
				} else {
					return null;
				}
			});

			ranksDataProvider = (ListDataProvider<SbfRank>) availableGrid.getDataProvider();

			ranksDataProvider.setFilter(r->r, r -> availableGridFilter(r));
			HeaderRow filterRow = availableGrid.appendHeaderRow();

			TextField availPlayerNameFilter = getTextFilter();
			availPlayerNameFilter.addValueChangeListener(event -> {
				setAvailPlayerNameFilterValue(event.getValue());
				ranksDataProvider.refreshAll();
			});

			MenuBar availPositionFilter = getPositionFilter();

			MenuBar availIsDraftedFilter = getIsDraftedFilter();

			filterRow.getCell("PlayerNameColumn").setComponent(availPlayerNameFilter);
			filterRow.getCell("PositionColumn").setComponent(availPositionFilter);
			if(UserSessionVars.getAccessControl().isUserLeagueManager()){
				filterRow.getCell("DraftedColumn").setComponent(availIsDraftedFilter);
			}
			
			availableGrid.sort("RankColumn");

			return availableGrid;

		}

		public boolean availableGridFilter(SbfRank rank){
			Player player = playerService.getPlayerById(rank.getPlayerId());
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
				SbfRank selection = (SbfRank)clickEvent.getItem();
				Player selectedPlayer = playerService.getPlayerById(selection.getPlayerId());
				processDraftPick(selectedPlayer);
				
			});
			return draftButtonRenderer;
		}

		@SuppressWarnings({ "rawtypes" })
		public ButtonRenderer undoButtonRenderer (){
			ButtonRenderer<Object> undoButtonRenderer = new ButtonRenderer<Object>();
			undoButtonRenderer.addClickListener(clickEvent -> {
				SbfDraftRecord r = (SbfDraftRecord)clickEvent.getItem();
				draftService.deleteSbfDraftRecord(r);
				draftedGrid.getDataProvider().refreshAll();
				ranksDataProvider.refreshAll();
				for(UI t : getSession().getUIs()){
					if(t.getClass().equals(DraftDisplayPopupUI.class)){
						((DraftDisplayPopupUI) t).processPick("none" ,true);
					}else if(t.getClass().equals(DraftBoardPopupUI.class)){
						((DraftBoardPopupUI) t).removeDraftSelection(r);
					}
				}
				Broadcaster.broadcast(UserSessionVars.getCurrentLeague().getLeagueId(),UI.getCurrent().getSession(), BroadcastCommands.UNDO_DRAFT_PICK, new Object[] {r});

				setOnTheClockCaption();
			});
			return undoButtonRenderer;
		}

		private void processDraftPick(Player p) {
			SbfDraftRecord r = leagueMgr.draftPlayer(p, leagueId);
			draftedGrid.getDataProvider().refreshAll();
			ranksDataProvider.refreshAll();
			String prize = "none";
			if(icingEnabled) {
				int randomInt = rand.nextInt(99) + 1;
				
				isAWinner=true;
				if (randomInt <= icePercent) {
					prize = "ice";
				}else if  (randomInt >icePercent && randomInt <= icePercent + shotPercent) {
					prize = "shot";
				}else if(randomInt >icePercent + shotPercent && randomInt <= icePercent + shotPercent + shotgunPercent) {
					prize = "shotgun";
				}else {
					isAWinner=false;
				}	
			}
			
			
			for(UI t : getSession().getUIs()){
				if(t.getClass().equals(DraftDisplayPopupUI.class)){
					((DraftDisplayPopupUI) t).processPick(prize, false);
				}else if(t.getClass().equals(DraftBoardPopupUI.class)){
					((DraftBoardPopupUI) t).addDraftSelection(r);
				}
			}
			Broadcaster.broadcast(UserSessionVars.getCurrentLeague().getLeagueId(),UI.getCurrent().getSession(), BroadcastCommands.DRAFT_PLAYER, new Object[] {r, isAWinner});
			setOnTheClockCaption();
		}
		public void setOnTheClockCaption(){
			String teamOnTheClock = leagueMgr.getTeamOnTheClock().getOwnerName();
			int round = leagueMgr.getRound();
			int pickInRound =leagueMgr.getPickInRound();
			onTheClock.setCaption("<h2>On The Clock: " + teamOnTheClock + 
					"<br/>Round " + round + ", pick " + pickInRound + "</h2>");

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
						((DraftDisplayPopupUI) t).processPick("none", false);
					}			
				}
				Broadcaster.broadcast(UserSessionVars.getCurrentLeague().getLeagueId(),UI.getCurrent().getSession(), BroadcastCommands.RESUME_DRAFT,null);
				resumeWindow.close();
			}
			});

			layout.addComponent(resumeButton);
			layout.setComponentAlignment(resumeButton, Alignment.MIDDLE_RIGHT);

		}

		public void refreshPage() {
			UI.getCurrent().access(new Runnable() {
				@Override
				public void run() {      
					ranksDataProvider.refreshAll();
					draftedPlayersDataProvider.refreshAll();
					setOnTheClockCaption();
//					Notification notify = new Notification("Something changed.");
//					notify.setDelayMsec(2000);
//					notify.show(Page.getCurrent());
				}
			});			
		}
}