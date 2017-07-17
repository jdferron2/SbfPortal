package com.jdf.SbfPortal.views;


import java.util.ArrayList;
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
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.HorizontalLayout;
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

	private Integer leagueId;
	private String playerNameFilterValue;

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

		myRanks = playerService.getAllSbfRanks(1);
		
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
				).setCaption("Position");

		Button up = new Button("Up");
		up.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			final List<SbfRank> rankList =  new ArrayList<SbfRank>(grid.getSelectedItems());
			if (rankList.size() == 0) return;
			Player selectedPlayer = playerService.getPlayerById(rankList.get(0).getPlayerId());
			leagueMgr.movePlayerUp(selectedPlayer);		
			grid.sort(currentRankCol, SortDirection.ASCENDING);		
			grid.scrollTo(playerService.getSbfRankById(selectedPlayer.getPlayerId(), leagueId).getRank()-1);
		} });

		Button down = new Button("Down");
		down.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			final List<SbfRank> rankList =  new ArrayList<SbfRank>(grid.getSelectedItems());
			if (rankList.size() == 0) return;
			Player selectedPlayer = playerService.getPlayerById(rankList.get(0).getPlayerId());
			leagueMgr.movePlayerDown(selectedPlayer);		
			grid.sort(currentRankCol, SortDirection.ASCENDING);		
			grid.scrollTo(playerService.getSbfRankById(selectedPlayer.getPlayerId(), leagueId).getRank()-1);
		} });

		Button updateJdfRanks = new Button ("Update JDF Ranks");
		updateJdfRanks.addClickListener(new Button.ClickListener() {
			@Override public void buttonClick(Button.ClickEvent clickEvent){
				playerService.updateFlaggedRanks();
				Notification.show("Ranks updated successfully");
			}
		});

		HeaderRow filterRow = grid.appendHeaderRow();
		
		TextField availPlayerNameFilter = getTextFilter();
		availPlayerNameFilter.addValueChangeListener(event -> {
			setPlayerNameFilterValue(event.getValue());
			playersDataProvider.refreshAll();
		});

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
		//Player Name
		if (playerNameFilterValue != null && !playerNameFilterValue.equals("")){
			String playerLower = playerService.getPlayerById(r.getPlayerId()).getDisplayName().toLowerCase(Locale.ENGLISH);
			if(!playerLower.contains(playerNameFilterValue)) return false;
		}
		return true;
	}
}