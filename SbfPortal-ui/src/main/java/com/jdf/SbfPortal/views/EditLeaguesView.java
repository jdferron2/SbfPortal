package com.jdf.SbfPortal.views;

import java.util.List;

import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.data.SbfPickTrade;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.jdf.SbfPortal.backend.data.SbfUserTeam;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class EditLeaguesView extends VerticalLayout implements View {
	public static final String NAME = "Edit Leagues";

	private SbfLeagueService leagueService;
	SbfUserTeam currentUT;

	private SbfDraftService draftService;
	private ListDataProvider<SbfLeague> leaguesDataProvider;
	private ListDataProvider<SbfPickTrade> tradesDataProvider;

	private boolean viewBuilt = false;


	private Grid<SbfLeague> leaguesGrid;
	private Grid<SbfPickTrade> tradesGrid;

	List<SbfLeague> leagueList;
	List<SbfPickTrade> tradesList;

	private ComboBox<Integer> team1PickSelector;
	private ComboBox<Integer> team2PickSelector;
	private ComboBox<SbfTeam> team1Selector;
	private ComboBox<SbfTeam> team2Selector;
	private Button processTrade = new Button("Process Trade");
	private GridLayout tradeLayout = new GridLayout(3,3);
	
	@Override
	public void enter(ViewChangeEvent event) {
		leagueService = UserSessionVars.getLeagueService();
		draftService = UserSessionVars.getDraftService();
		currentUT = UserSessionVars.getLeagueService().getSbfUserTeamForLeagueAndUser(
				UserSessionVars.getCurrentUser(),UserSessionVars.getCurrentLeague());
		if (!viewBuilt) {
			buildView();
			viewBuilt=true;
		}

	}

	void buildView(){
		leagueList =  leagueService.getAllSbfLeaguesManagedByUser(UserSessionVars.getCurrentUser().getUserId());
		tradesList = draftService.getAllSbfPickTrades(UserSessionVars.getCurrentLeague().getLeagueId());
		leaguesGrid = configureLeagueGrid(leagueList);
		tradesGrid = configureTradesGrid(tradesList);

		team1PickSelector = createTradeBox(currentUT.getTeamId());
		team2PickSelector = createTradeBox(currentUT.getTeamId());
		team1Selector =  this.createTeamSelectorCB("Team 1", team1PickSelector);
		team2Selector =  this.createTeamSelectorCB("Team 2", team2PickSelector);
		
		processTrade.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				if(team2PickSelector.getValue()!= null ){
					UserSessionVars.getLeagueManager().addTradeRecord(team1Selector.getValue(), team2Selector.getValue(), UserSessionVars.getCurrentLeague().getLeagueId(), team2PickSelector.getValue());
				}
				if(team1PickSelector.getValue()!= null ){
					UserSessionVars.getLeagueManager().addTradeRecord(team2Selector.getValue(), team1Selector.getValue(), UserSessionVars.getCurrentLeague().getLeagueId(), team1PickSelector.getValue());
				}
				team1PickSelector.clear();
				team2PickSelector.clear();
				team1Selector.clear();
				team2Selector.clear();
				tradesDataProvider.refreshAll();
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
		
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		//layout.setSizeFull();
		layout.setSpacing(true);
		layout.addComponents(leaguesGrid,new Label("Add Trade"), tradeLayout, tradesGrid);
		//setSizeFull();
		addComponents(layout);
		setWidth("100%");
	}

	@SuppressWarnings("unchecked")
	public Grid<SbfLeague> configureLeagueGrid(List<SbfLeague> leagues){
		leaguesGrid = new Grid<>();
		leaguesGrid.setCaption("Leagues");
		leaguesGrid.setItems(leagues);

		leaguesGrid.setWidth("75%");
		leaguesGrid.setSelectionMode(SelectionMode.SINGLE);
		leaguesGrid.setHeightMode(HeightMode.UNDEFINED);
		leaguesGrid.addColumn(SbfLeague::getLeagueName).setCaption("League Name").setId("leagueNameCol");		
		leaguesGrid.addColumn(SbfLeague::getNumTeams).setCaption("Number of Teams").setId("numTeamsCol");
		leaguesGrid.addColumn(s->"Edit League", editButtonRenderer()).setId("editLeagueCol");
		leaguesGrid.addColumn(s->"Copy League", copyButtonRenderer()).setId("copyLeagueCol");
		leaguesGrid.addColumn(s->"Delete League", deleteButtonRenderer()).setId("deleteLeagueCol");
		leaguesDataProvider = (ListDataProvider<SbfLeague>) leaguesGrid.getDataProvider();

		leaguesDataProvider.setFilter(l->l, l -> leaguesGridFilter(l));
		//HeaderRow filterRow = teamsGrid.appendHeaderRow();

		leaguesGrid.sort("leagueNameCol");

		return leaguesGrid;

	}
	
	@SuppressWarnings("unchecked")
	public Grid<SbfPickTrade> configureTradesGrid(List<SbfPickTrade> trades){
		tradesGrid = new Grid<>();
		tradesGrid.setCaption("Trades for " + UserSessionVars.getCurrentLeague().getLeagueName());
		tradesGrid.setItems(trades);

		tradesGrid.setWidth("75%");
		//tradesGrid.setHeightMode(HeightMode.UNDEFINED);
		//tradesGrid.setHeightByRows(10);
		tradesGrid.setSelectionMode(SelectionMode.SINGLE);
		tradesGrid.addColumn(t->t.getPick() + ", Round " + LeagueInfoManager.getRound(t.getPick()) + " Pick " + LeagueInfoManager.getPickInRound(t.getPick())
		).setCaption("Pick");
			
		tradesGrid.addColumn(t->leagueService.getSbfTeamByTeamId(t.getFromTeamId(),t.getLeagueId()
				).getTeamName()).setCaption("From").setId("fromTeamCol");
		tradesGrid.addColumn(t->leagueService.getSbfTeamByTeamId(t.getToTeamId(),t.getLeagueId()
				).getTeamName()).setCaption("To").setId("toTeamCol");
		tradesGrid.addColumn(s->"Delete Trade", deleteTradeRenderer()).setId("deleteTradeCol");
		
		tradesDataProvider = (ListDataProvider<SbfPickTrade>) tradesGrid.getDataProvider();

		//leaguesDataProvider.setFilter(l->l, l -> leaguesGridFilter(l));
		//HeaderRow filterRow = teamsGrid.appendHeaderRow();

		//leaguesGrid.sort("leagueNameCol");

		return tradesGrid;

	}


	@SuppressWarnings({ "rawtypes" })
	private ButtonRenderer editButtonRenderer (){
		ButtonRenderer<Object> editButtonRenderer = new ButtonRenderer<Object>();
		editButtonRenderer.addClickListener(clickEvent -> {
			SbfLeague l = (SbfLeague)clickEvent.getItem();
			UI.getCurrent().addWindow(getEditLeagueWindow(l));
		});
		return editButtonRenderer;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private ButtonRenderer deleteButtonRenderer (){
		ButtonRenderer<Object> deleteButtonRenderer = new ButtonRenderer<Object>();
		deleteButtonRenderer.addClickListener(clickEvent -> {
			SbfLeague l = (SbfLeague)clickEvent.getItem();
			UI.getCurrent().addWindow(getDeleteLeagueConfirm(l));
		});
		return deleteButtonRenderer;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private ButtonRenderer deleteTradeRenderer (){
		ButtonRenderer<Object> deleteTradeRenderer = new ButtonRenderer<Object>();
		deleteTradeRenderer.addClickListener(clickEvent -> {
			SbfPickTrade t = (SbfPickTrade)clickEvent.getItem();
			UI.getCurrent().addWindow(getDeleteTradeConfirm(t));
		});
		return deleteTradeRenderer;
	}

	private boolean leaguesGridFilter(SbfLeague l){
		//Place holder
		return true;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private ButtonRenderer copyButtonRenderer (){
		ButtonRenderer<Object> copyButtonRenderer = new ButtonRenderer<Object>();
		copyButtonRenderer.addClickListener(clickEvent -> {
			SbfLeague l = (SbfLeague)clickEvent.getItem();
			UI.getCurrent().addWindow(getCopyLeagueWindow(l));
		});
		return copyButtonRenderer;
	}



	private Window getEditLeagueWindow(SbfLeague l){
		String leagueName = l.getLeagueName();
		if (leagueName == null) leagueName = "";
		Window subWindow = new Window(leagueName);
		VerticalLayout subContent = new VerticalLayout();
		TextField leagueNameTextField = new TextField();
		leagueNameTextField.setWidth("100%");
		leagueNameTextField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		leagueNameTextField.setValue(leagueName);

//		TextField numTeams = new TextField();
//		numTeams.setWidth("100%");
//		numTeams.addStyleName(ValoTheme.TEXTFIELD_TINY);
//		numTeams.setValue(String.valueOf(l.getNumTeams()));

		subContent.setMargin(true);



		Button submitButton = new Button("Submit");
		submitButton.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			leagueService.updateSbfLeague(l);
			leaguesDataProvider.refreshAll();
			subWindow.close();

		} });

		subContent.addComponents(leagueNameTextField, submitButton);
		subWindow.setContent(subContent);
		subWindow.center();

		//	subWindow.setClosable(false);
		return subWindow;

	}
	
	private Window getCopyLeagueWindow(SbfLeague l){
		String leagueName = l.getLeagueName();
		if (leagueName == null) leagueName = "";
		Window subWindow = new Window(leagueName);
		VerticalLayout subContent = new VerticalLayout();

		TextField leagueNameTextField = new TextField();
		leagueNameTextField.setWidth("100%");
		leagueNameTextField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		leagueNameTextField.setValue("Enter New Name");

//		TextField numTeams = new TextField();
//		numTeams.setWidth("100%");
//		numTeams.addStyleName(ValoTheme.TEXTFIELD_TINY);
//		numTeams.setValue(String.valueOf(l.getNumTeams()));

		subContent.setMargin(true);



		Button submitButton = new Button("Submit");
		submitButton.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			SbfLeague newLeauge = UserSessionVars.getLeagueManager().copyLeague(l, leagueNameTextField.getValue());
			leagueList.add(newLeauge);
			leaguesDataProvider.refreshAll();
			subWindow.close();

		} });

		subContent.addComponents(leagueNameTextField, submitButton);
		subWindow.setContent(subContent);
		subWindow.center();

		//	subWindow.setClosable(false);
		return subWindow;

	}
	
	private Window getDeleteLeagueConfirm(SbfLeague l){
		String leagueName = l.getLeagueName();
		if (leagueName == null) leagueName = "";
		Window subWindow = new Window("Delete " + leagueName + "?");
		VerticalLayout subContent = new VerticalLayout();

		subContent.setMargin(true);

		Button yesButton = new Button("Yes, Delete");
		yesButton.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			UserSessionVars.getLeagueManager().deleteLeague(l);
			leagueList.remove(l);
			leaguesDataProvider.refreshAll();
			subWindow.close();

		} });
		
		Button noButton = new Button("NO!!!");
		noButton.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			subWindow.close();

		} });

		subContent.addComponents(yesButton, noButton);
		subWindow.setContent(subContent);
		subWindow.center();

		//	subWindow.setClosable(false);
		return subWindow;

	}
	
	private Window getDeleteTradeConfirm(SbfPickTrade t){
		Window subWindow = new Window("Delete trade?");
		VerticalLayout subContent = new VerticalLayout();

		subContent.setMargin(true);

		Button yesButton = new Button("Yes, Delete");
		yesButton.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			UserSessionVars.getDraftService().deleteSbfPickTrade(t);
			tradesList.remove(t);
			tradesDataProvider.refreshAll();
			subWindow.close();

		} });
		
		Button noButton = new Button("NO!!!");
		noButton.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			subWindow.close();

		} });

		subContent.addComponents(yesButton, noButton);
		subWindow.setContent(subContent);
		subWindow.center();

		//	subWindow.setClosable(false);
		return subWindow;

	}
	
	public ComboBox<Integer> createTradeBox(int teamId){
		ComboBox<Integer> teamPicksCB = new ComboBox<Integer>("Picks");
		teamPicksCB.setItemCaptionGenerator(
				i->"Pick: " + Integer.toString(i) + " (r" + 
						Integer.toString(LeagueInfoManager.getRound(i))+
						" p" + LeagueInfoManager.getPickInRound(i) + ")");
		teamPicksCB.setItems(UserSessionVars.getLeagueManager().getPicksForTeam(teamId));
		teamPicksCB.addValueChangeListener(event-> {
			setSubmitVisible();
		});
		return teamPicksCB;
	}

	public ComboBox<SbfTeam> createTeamSelectorCB(String name, ComboBox<Integer> connectedBox){
		ComboBox<SbfTeam> teamCB = new ComboBox<SbfTeam>(name);
		teamCB.setItems(leagueService.getAllSbfTeamsForLeague(UserSessionVars.getCurrentLeague().getLeagueId()));
		teamCB.setItemCaptionGenerator(SbfTeam::getOwnerName);
		teamCB.addValueChangeListener(event-> {
			if(event.getValue()!= null){
				connectedBox.clear();
				connectedBox.setItems(UserSessionVars.getLeagueManager().getPicksForTeam(event.getValue().getTeamId()));
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
				(team1Selector.getValue() !=null && team2Selector.getValue() !=null) &&
				team1Selector.getValue() != team2Selector.getValue()){
			processTrade.setVisible(true);
		}else{
			processTrade.setVisible(false);
		}
	}

}
