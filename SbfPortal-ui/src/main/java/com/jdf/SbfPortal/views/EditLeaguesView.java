package com.jdf.SbfPortal.views;

import java.util.List;

import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class EditLeaguesView extends VerticalLayout implements View {
	public static final String NAME = "Edit Leagues";

	private SbfLeagueService leagueService;

	private ListDataProvider<SbfLeague> leaguesDataProvider;

	private boolean viewBuilt = false;


	private Grid<SbfLeague> leaguesGrid;

	List<SbfLeague> leagueList;

	@Override
	public void enter(ViewChangeEvent event) {
		leagueService = UserSessionVars.getLeagueService();

		if (!viewBuilt) {
			buildView();
			viewBuilt=true;
		}

	}

	void buildView(){
		leagueList =  leagueService.getAllSbfLeaguesManagedByUser(UserSessionVars.getCurrentUser().getUserId());

		leaguesGrid = configureLeagueGrid(leagueList);

		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSizeFull();
		layout.setSpacing(false);
		layout.addComponents(leaguesGrid);
		setSizeFull();
		addComponents(layout);
		setExpandRatio(layout, 1);
	}

	@SuppressWarnings("unchecked")
	public Grid<SbfLeague> configureLeagueGrid(List<SbfLeague> leagues){
		leaguesGrid = new Grid<>();
		leaguesGrid.setCaption("Leagues");
		leaguesGrid.setItems(leagues);

		leaguesGrid.setSizeFull();
		leaguesGrid.setSelectionMode(SelectionMode.SINGLE);
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
		yesButton.addClickListener(new Button.ClickListener()
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

}
