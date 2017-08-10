package com.jdf.SbfPortal.views;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfRank;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class CheatSheetView extends HorizontalLayout implements View {
	private ListDataProvider<SbfRank> playersDataProvider;
	boolean viewBuilt = false;
	private SbfDraftService draftService;
	private PlayerService playerService;
	private SbfLeagueService leagueService;
	private LeagueInfoManager leagueMgr;
	private List<SbfRank> myRanks;
	private String positionFilterValue="";

	private Integer leagueId;
	private Integer sbfId;
	private String playerNameFilterValue;
	
	private final Command filterCommand = new Command() {
		@Override
		public void menuSelected(final MenuItem selectedItem) {
			String filter = selectedItem.getParent().getDescription();
			String filterValue = selectedItem.getText();
			if(filter != null ){
				if (filter.equals("Position Filter")){
					setPositionFilterValue(filterValue);
					selectedItem.getParent().setText(filterValue);
				}
			}
			playersDataProvider.refreshAll();
		}};

	public CheatSheetView(){

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

		myRanks = playerService.getAllSbfRanks(sbfId);
		
		if(!viewBuilt) {
			buildView();
			viewBuilt=true;
		}
		

		playersDataProvider.refreshAll();
	}

	private void buildView(){
		setSizeFull();
		final VerticalLayout layout = new VerticalLayout();
		final HorizontalLayout upDownLayout = new HorizontalLayout();

		layout.setSizeFull();

		Grid<SbfRank> grid = new Grid<>();
		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setItems(myRanks);
		Column<SbfRank, ?> currentRankCol = grid.addColumn(SbfRank::getRank)
				.setCaption("My Rank");
		grid.addColumn(s->playerService.getPlayerById(s.getPlayerId()).getProRank()
				).setCaption("Pro Rank");
		grid.addColumn(s->playerService.getPlayerById(s.getPlayerId()).getDisplayName()
				).setCaption("Name").setId("PlayerNameColumn");
		grid.addColumn(s->playerService.getPlayerById(s.getPlayerId()).getTeam()
				).setCaption("Team");
		grid.addColumn(s->playerService.getPlayerById(s.getPlayerId()).getPosition()
				).setCaption("Position").setId("PositionColumn");


		Button up = new Button("Up");
		up.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			final List<SbfRank> rankList =  new ArrayList<SbfRank>(grid.getSelectedItems());
			if (rankList.size() == 0) return;
			Iterator<SbfRank> displayedPlayersIter = grid.getDataProvider().fetch(new Query<>()).sorted().iterator();
			int newRank = 0;
			int oldRank = rankList.get(0).getRank();
			int scrollToRow = -2;
			while (displayedPlayersIter.hasNext()){
				SbfRank curRank = (SbfRank) displayedPlayersIter.next();
				if(curRank.getRank() == oldRank){
					break;
				}
				newRank = curRank.getRank();
				scrollToRow++;
			}
			if (scrollToRow < 0) scrollToRow = 0;
			leagueMgr.setNewRankValue(rankList.get(0),newRank);	
			playersDataProvider.refreshAll();
		//grid.sort(currentRankCol, SortDirection.ASCENDING);		
			grid.scrollTo(scrollToRow);
		} });
		
		Button down = new Button("Down");
		down.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			final List<SbfRank> rankList =  new ArrayList<SbfRank>(grid.getSelectedItems());
			if (rankList.size() == 0) return;
			Iterator<SbfRank> displayedPlayersIter = grid.getDataProvider().fetch(new Query<>()).sorted().iterator();
			int newRank = 0;
			int scrollToRow = 1;
			int oldRank = rankList.get(0).getRank();
			boolean foundNewRank = false;
			while (displayedPlayersIter.hasNext()){
				SbfRank curRank = (SbfRank) displayedPlayersIter.next();
				if (foundNewRank){
					newRank = curRank.getRank();
					break;
				}
				if(curRank.getRank() == oldRank){
					//do one more iteration to get to the rank directly below the selected one
					foundNewRank = true;
				}		
				scrollToRow++;
			}
			leagueMgr.setNewRankValue(rankList.get(0),newRank);	
			playersDataProvider.refreshAll();
			//grid.sort(currentRankCol, SortDirection.ASCENDING);		
			grid.scrollTo(scrollToRow);
		} });
		

//		Button down = new Button("Down");
//		down.addClickListener(new Button.ClickListener()
//		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
//		{
//			final List<SbfRank> rankList =  new ArrayList<SbfRank>(grid.getSelectedItems());
//			if (rankList.size() == 0) return;
//			Player selectedPlayer = playerService.getPlayerById(rankList.get(0).getPlayerId());
//			leagueMgr.movePlayerDown(selectedPlayer);		
//			grid.sort(currentRankCol, SortDirection.ASCENDING);		
//			grid.scrollTo(playerService.getSbfRankById(selectedPlayer.getPlayerId(), leagueId).getRank()-1);
//		} });
//
		Button updateJdfRanks = new Button ("Update JDF Ranks");
		updateJdfRanks.addClickListener(new Button.ClickListener() {
			@Override public void buttonClick(Button.ClickEvent clickEvent){
				playerService.updateFlaggedRanks();
				Notification.show("Ranks updated successfully");
			}
		});

		HeaderRow filterRow = grid.appendHeaderRow();
		
		MenuBar positionFilter = getPositionFilter();
		
		TextField availPlayerNameFilter = getTextFilter();
		availPlayerNameFilter.addValueChangeListener(event -> {
			setPlayerNameFilterValue(event.getValue());
			playersDataProvider.refreshAll();
		});
		
		filterRow.getCell("PositionColumn").setComponent(positionFilter);

		grid.sort(currentRankCol, SortDirection.ASCENDING);		
		grid.setSizeFull();

		upDownLayout.addComponents(up,down,updateJdfRanks);
		layout.addComponents(grid,upDownLayout);
		layout.setExpandRatio(grid, 1f);
		addComponent(layout);
		playersDataProvider = (ListDataProvider<SbfRank>) grid.getDataProvider();
		playersDataProvider.setFilter(p->p, p -> gridFilter(p));
		filterRow.getCell("PlayerNameColumn").setComponent(availPlayerNameFilter);
	}

	public TextField getTextFilter(){
		TextField filter = new TextField();
		filter.setWidth("100%");
		filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
		filter.setPlaceholder("Filter");
		return filter;
	}
	
	public void setPlayerNameFilterValue(String name){
		this.playerNameFilterValue = name.toLowerCase();
	}
	
	public boolean gridFilter(SbfRank r){
		Player p = playerService.getPlayerById(r.getPlayerId());
		if(leagueService.getSbfKeeperByPlayerId(p.getPlayerId(), leagueId) != null){
			return false;
		}
		if(playerService.getPlayerById(r.getPlayerId()).getProRank() > 500){
			return false;
		}
		//Player Name
		if (playerNameFilterValue != null && !playerNameFilterValue.equals("")){
			String playerLower = p.getDisplayName().toLowerCase(Locale.ENGLISH);
			if(!playerLower.contains(playerNameFilterValue)) return false;
		}
		
		//player position
		if (positionFilterValue != null && !positionFilterValue.equals("") && !positionFilterValue.equalsIgnoreCase("All")){
			if (!p.getPosition().toLowerCase().equals(positionFilterValue)) return false;
		}
		return true;
	}
	
	public void setPositionFilterValue(String position){
		if (position == null) {
			this.positionFilterValue = position;
		}
		else {
			this.positionFilterValue = position.toLowerCase();
		}
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

}