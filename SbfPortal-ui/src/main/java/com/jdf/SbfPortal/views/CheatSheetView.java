package com.jdf.SbfPortal.views;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.vaadin.teemusa.gridextensions.SelectGrid;
import org.vaadin.teemusa.gridextensions.client.tableselection.TableSelectionState.TableSelectionMode;
import org.vaadin.teemusa.gridextensions.tableselection.TableSelectionModel;

import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfRank;
import com.jdf.SbfPortal.backend.data.SbfRankSet;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.data.provider.DataCommunicator;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.dnd.criteria.ComparisonOperator;
import com.vaadin.shared.ui.dnd.criteria.Criterion.Match;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.GridDragSource;
import com.vaadin.ui.components.grid.GridDropTarget;
import com.vaadin.ui.components.grid.GridRowDragger;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class CheatSheetView extends HorizontalLayout implements View {
	public final static String NAME = "Cheat Sheet";
	private ListDataProvider<SbfRank> playersDataProvider;
	boolean viewBuilt = false;
	private PlayerService playerService;
	private LeagueInfoManager leagueMgr;
	private List<SbfRank> myRanks;
	private String positionFilterValue="";
	GridRowDragger<SbfRank> gridRowDragger;

	VerticalLayout mainLayout = new VerticalLayout();
	private SelectGrid<SbfRank> grid;
	List<SbfRank> rankDragSource;
	final HorizontalLayout upDownLayout = new HorizontalLayout();
	private HorizontalLayout createNewListLayout = new HorizontalLayout();
	ComboBox<SbfRankSet> switchCheatsheetCB = new ComboBox<SbfRankSet>();
	private Integer[] tiers = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};

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
			//	leagueId = UserSessionVars.getCurrentLeague().getLeagueId();
			leagueMgr = UserSessionVars.getLeagueManager();
			playerService = UserSessionVars.getPlayerService();
			myRanks = playerService.getAllSbfRanks(UserSessionVars.getRankSet().getRankSetId());


			if(!viewBuilt) {
				buildView();
				viewBuilt=true;
			}else{
				switchCheatsheetCB.setItems(UserSessionVars.getPlayerService().getAllSbfRankSets(UserSessionVars.getCurrentUser().getUserId()));
				switchCheatsheetCB.setValue(UserSessionVars.getRankSet());
			}

			//grid.setCaption("Cheatsheet: " + UserSessionVars.getRankSet().getRankSetName());

			if(UserSessionVars.getRankSet().getRankSetId() == 0){//default set, dont allow changes
				upDownLayout.setVisible(false);
				//	createNewListLayout.setVisible(true);
			}else{
				upDownLayout.setVisible(true);
				//	createNewListLayout.setVisible(false);
			}


			playersDataProvider.refreshAll();
		}

		public void handleDragData(SbfRank target, DropLocation l) {
			if(l.equals(DropLocation.ABOVE)) {
				UserSessionVars.getLeagueManager().moveRanksAbove(rankDragSource, target);
			}else if(l.equals(DropLocation.BELOW)) {
				UserSessionVars.getLeagueManager().moveRanksBelow(rankDragSource, target);
			}
			playersDataProvider.refreshAll();
		}

		@SuppressWarnings("unchecked")
		private void buildView(){
			setSizeFull();

			mainLayout.setSizeFull();

			grid = new SelectGrid<SbfRank>();
			GridDragSource<SbfRank> dragSource;
			grid.setSizeFull();
			
			gridRowDragger = new GridRowDragger<>(grid);
					//GridDropTarget<SbfRank> dropTarget = new GridDropTarget<>(grid, DropMode.BETWEEN);
					//dropTarget.addDropListener(dropEvent->{
					//	dropEvent.getDragData().ifPresent(r->handleDragData((SbfRank)r));
					//}
					//);
			GridDropTarget<SbfRank> dropTarget = gridRowDragger.getGridDropTarget();
			dragSource = gridRowDragger.getGridDragSource();
			
			dragSource.addGridDragStartListener(startEvent->{
				rankDragSource = startEvent.getDraggedItems();	
			});

			dropTarget.addGridDropListener(event->{
				event.getDropTargetRow().ifPresent(r->handleDragData(
						(SbfRank)r, 
						event.getDropLocation())
						);
			});
			//gridRowDragger.setTargetDataProviderUpdater(targetDataProviderUpdater);
					
			TableSelectionModel<SbfRank> selectionModel = new TableSelectionModel<SbfRank>();
			selectionModel.setMode(TableSelectionMode.SHIFT);
			grid.setSelectionModel(selectionModel);
			//grid.setSelectionMode(SelectionMode.MULTI);
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
			grid.addComponentColumn(rank->{
				ComboBox<Integer> tierSelector = new ComboBox<Integer>();
				tierSelector.setPageLength(16);
				tierSelector.setItems(tiers);
				tierSelector.addStyleName(ValoTheme.COMBOBOX_BORDERLESS);
				tierSelector.addStyleName(ValoTheme.COMBOBOX_TINY);
				tierSelector.setValue(rank.getTier());
				tierSelector.setEmptySelectionAllowed(false);
				tierSelector.setWidth("60px");
				tierSelector.setTextInputAllowed(false);
				if(UserSessionVars.getRankSet().getRankSetId() == 0) {
					tierSelector.setReadOnly(true);
				}else {
					tierSelector.setReadOnly(false);
				}
				tierSelector.addValueChangeListener(value->{
					if(!grid.getSelectedItems().contains(rank)) {
						rank.setTier(value.getValue());
						UserSessionVars.getPlayerService().updateSbfRank(rank);
						playersDataProvider.refreshItem(rank);
					}else {
						final List<SbfRank> rankList =  new ArrayList<SbfRank>(grid.getSelectedItems());
						for (SbfRank r : rankList) {
							r.setTier(value.getValue());
							UserSessionVars.getPlayerService().updateSbfRank(r);
							playersDataProvider.refreshItem(r);
						}	
					}
				});
				return tierSelector;
			}).setCaption("Tier").setId("tierColumn");

			grid.setStyleGenerator(rank -> {
				if (rank.getTier()>0) {
					return "T"+rank.getTier();
				} else {
					return null;
				}
			});

//			Button up = new Button("Up");
//			up.addClickListener(new Button.ClickListener()
//			{ @Override public void buttonClick(Button.ClickEvent clickEvent)
//			{
//				final List<SbfRank> rankList =  new ArrayList<SbfRank>(grid.getSelectedItems());
//				if (rankList.size() == 0 || rankList.size() > 1) return;
//				Iterator<SbfRank> displayedPlayersIter = grid.getDataProvider().fetch(new Query<>()).sorted().iterator();
//				int newRank = 0;
//				int oldRank = rankList.get(0).getRank();
//				int scrollToRow = -2;
//				while (displayedPlayersIter.hasNext()){
//					SbfRank curRank = (SbfRank) displayedPlayersIter.next();
//					if(curRank.getRank() == oldRank){
//						break;
//					}
//					newRank = curRank.getRank();
//					scrollToRow++;
//				}
//				if (scrollToRow < 0) scrollToRow = 0;
//				leagueMgr.setNewRankValue(rankList.get(0),newRank);	
//				playersDataProvider.refreshAll();
//				grid.sort(currentRankCol, SortDirection.ASCENDING);		
//				grid.scrollTo(scrollToRow);
//			} });

//			Button down = new Button("Down");
//			down.addClickListener(new Button.ClickListener()
//			{ @Override public void buttonClick(Button.ClickEvent clickEvent)
//			{
//				final List<SbfRank> rankList =  new ArrayList<SbfRank>(grid.getSelectedItems());
//				if (rankList.size() == 0 || rankList.size() > 1) return;
//				Iterator<SbfRank> displayedPlayersIter = grid.getDataProvider().fetch(new Query<>()).sorted().iterator();
//				int newRank = 0;
//				int scrollToRow = 1;
//				int oldRank = rankList.get(0).getRank();
//				boolean foundNewRank = false;
//				while (displayedPlayersIter.hasNext()){
//					SbfRank curRank = (SbfRank) displayedPlayersIter.next();
//					if (foundNewRank){
//						newRank = curRank.getRank();
//						break;
//					}
//					if(curRank.getRank() == oldRank){
//						//do one more iteration to get to the rank directly below the selected one
//						foundNewRank = true;
//					}		
//					scrollToRow++;
//				}
//				leagueMgr.setNewRankValue(rankList.get(0),newRank);	
//				playersDataProvider.refreshAll();
//				//grid.sort(currentRankCol, SortDirection.ASCENDING);		
//				grid.scrollTo(scrollToRow);
//			} });
//
//			Button saveChangesButton = new Button ("Save Changes");
//			saveChangesButton.addClickListener(new Button.ClickListener() {
//				@Override public void buttonClick(Button.ClickEvent clickEvent){
//					playerService.updateFlaggedRanks();
//					Notification.show("Ranks updated successfully");
//				}
//			});
			
//			Button down = new Button("Down");
//			down.addClickListener(new Button.ClickListener()
//			{ @Override public void buttonClick(Button.ClickEvent clickEvent)
//			{
//				DataCommunicator<SbfRank> dataComm = grid.getDataCommunicator();
//				final List<SbfRank> rankList =  new ArrayList<SbfRank>(grid.getSelectedItems());
//				if (rankList.size() == 0 || rankList.size() > 1) return;
//				SbfRank oldRank = rankList.get(0);
//				SbfRank newRank=null;
////				int scrollTo =0;
////				SbfRank newRank = grid.getDataProvider().fetch(new Query<>()).filter(r->{
////					if(gridFilter(r)) {
////						return r.getRank()>rankList.get(0).getRank();
////					}else {return false;}
////				}).sorted().findFirst().orElse(null);
//				List<SbfRank> ranks = dataComm.fetchItemsWithRange(0, 500);
//				for(SbfRank r : ranks) {
//					if(r.getRank() > oldRank.getRank()) {
//						newRank = r;
//						//dataComm.fetchItemsWithRange(oldRank.getRank(), 2).sort();
//						break;
//					}
//				}
//				System.out.println("New rank: " +newRank.getRank());
////				int newRank = 0;
////				int scrollToRow = 1;
////				int oldRank = rankList.get(0).getRank();
////				boolean foundNewRank = false;
////				while (displayedPlayersIter.hasNext()){
////					SbfRank curRank = (SbfRank) displayedPlayersIter.next();
////					if (foundNewRank){
////						newRank = curRank.getRank();
////						break;
////					}
////					if(curRank.getRank() == oldRank){
////						//do one more iteration to get to the rank directly below the selected one
////						foundNewRank = true;
////					}		
////					scrollToRow++;
////				}
//				leagueMgr.swapRanks(oldRank, newRank);	
//				playersDataProvider.refreshItem(oldRank);
//				playersDataProvider.refreshItem(newRank);
//				//grid.sort(currentRankCol, SortDirection.ASCENDING);		
//				//grid.scrollTo(scrollToRow);
//			} });


			HeaderRow filterRow = grid.appendHeaderRow();

			MenuBar positionFilter = getPositionFilter();

			TextField availPlayerNameFilter = getTextFilter();
			availPlayerNameFilter.addValueChangeListener(event -> {
				setPlayerNameFilterValue(event.getValue());
				playersDataProvider.refreshAll();
			});

			filterRow.getCell("PositionColumn").setComponent(positionFilter);

			//grid.sort(currentRankCol, SortDirection.ASCENDING);		
			grid.setSizeFull();

			//upDownLayout.addComponents(saveChangesButton);
			createNewListLayout=getCreateNewListLayout();
			mainLayout.addComponents(createNewListLayout,grid);
			mainLayout.setExpandRatio(grid, 1f);
			addComponent(mainLayout);
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
			//below will hide keepers...dont think i like this
			//		if(UserSessionVars.getCurrentLeague() != null){
			//			if(leagueService.getSbfKeeperByPlayerId(p.getPlayerId(), UserSessionVars.getCurrentLeague().getLeagueId()) != null){
			//				return false;
			//			}
			//		}

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

		private HorizontalLayout getCreateNewListLayout(){
			switchCheatsheetCB.setItems(UserSessionVars.getPlayerService().getAllSbfRankSets(UserSessionVars.getCurrentUser().getUserId()));
			switchCheatsheetCB.setItemCaptionGenerator(SbfRankSet::getRankSetName);
			switchCheatsheetCB.addStyleName(ValoTheme.COMBOBOX_BORDERLESS);
			switchCheatsheetCB.addStyleName(ValoTheme.COMBOBOX_SMALL);
			switchCheatsheetCB.addValueChangeListener(event-> {
				if(event.getValue()!= null){
					if(event.getValue() != UserSessionVars.getRankSet()){
						UserSessionVars.setRankSet(event.getValue());
						Page.getCurrent().reload();
					}
				}
			});		
			switchCheatsheetCB.setEmptySelectionAllowed(false);
			switchCheatsheetCB.setValue(UserSessionVars.getRankSet());

			HorizontalLayout l = new HorizontalLayout();
			l.addStyleName("cheatsheetGridTitle");
			l.setWidth("100%");
			//Label gridTitle = new Label(UserSessionVars.getRankSet().getRankSetName());
			Button createCustom = new Button("+ New Cheatsheet");
			createCustom.addStyleName(ValoTheme.BUTTON_FRIENDLY);
			//createCustom.addStyleName("customLinkButton");
			createCustom.addClickListener(new Button.ClickListener()
			{ @Override public void buttonClick(Button.ClickEvent clickEvent)
			{
				UI.getCurrent().addWindow(getCreateNewListWindow());
			} });
			Label blankLabel = new Label();
			l.addComponents(switchCheatsheetCB,blankLabel, createCustom);
			//createCustom.setSizeFull();
			//gridTitle.setSizeFull();

			//l.setComponentAlignment(gridTitle, Alignment.BOTTOM_LEFT);
			l.setExpandRatio(blankLabel, 1);
			return l;
		}

		private Window getCreateNewListWindow(){
			UserSessionVars.getCurrentLeague();
			Window subWindow = new Window("Create Custom Cheatsheet");
			subWindow.setModal(true);
			VerticalLayout subContent = new VerticalLayout();
			TextField listName = new TextField("Cheatsheet Name");
			CheckBox makeDefault = new CheckBox();
			makeDefault.setValue(false);
			makeDefault.setVisible(false);
			if(UserSessionVars.getCurrentLeague() != null){
				makeDefault.setCaption("Make this your default for " + UserSessionVars.getCurrentLeague().getLeagueName());
				makeDefault.setValue(true);
				makeDefault.setVisible(true);
			}

			subContent.setMargin(true);
			Button createButton = new Button("Create");
			createButton.addClickListener(new Button.ClickListener()
			{ @Override public void buttonClick(Button.ClickEvent clickEvent)
			{
				if (listName.getValue() != null && !listName.getValue().equals("")){
					SbfRankSet newRankSet = new SbfRankSet();
					newRankSet.setRankSetName(listName.getValue());
					newRankSet.setUserId(UserSessionVars.getCurrentUser().getUserId());
					playerService.insertSbfRankSet(newRankSet);
					UserSessionVars.setRankSet(newRankSet);
					if(makeDefault.getValue()){
						leagueMgr.setCheatsheetAsDefaultForLeagueAndUser(newRankSet, 
								UserSessionVars.getCurrentLeague(), UserSessionVars.getCurrentUser());
					}
					leagueMgr.resetCheatsheetToDefaultRanks(newRankSet);
					subWindow.close();
					Page.getCurrent().reload();
				}
			} });

			subContent.addComponents(listName, makeDefault, createButton);
			subWindow.setContent(subContent);
			subWindow.center();
			//	subWindow.setClosable(false);
			return subWindow;
		}

}