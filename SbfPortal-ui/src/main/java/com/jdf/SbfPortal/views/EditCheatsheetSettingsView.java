package com.jdf.SbfPortal.views;

import java.util.List;

import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.SbfRankSet;
import com.jdf.SbfPortal.backend.data.SbfUserTeam;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class EditCheatsheetSettingsView extends VerticalLayout implements View {
	public static final String NAME = "My Cheatsheets";

	private SbfLeagueService leagueService;

	private ListDataProvider<SbfRankSet> cheatsheetProvider;

//	private Integer leagueId;
	private boolean viewBuilt = false;


	private Grid<SbfRankSet> cheatsheetGrid;

	List<SbfRankSet> cheatsheetList;

	@Override
	public void enter(ViewChangeEvent event) {
	//	leagueId = UserSessionVars.getCurrentLeague().getLeagueId();
		leagueService = UserSessionVars.getLeagueService();
		cheatsheetList =  UserSessionVars.getPlayerService().getAllSbfRankSets(UserSessionVars.getCurrentUser().getUserId());

		if (!viewBuilt) {
			buildView();
			viewBuilt=true;
		}

	}

	void buildView(){

		cheatsheetGrid = configureCheatsheetGrid(cheatsheetList);

		final HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(false);
		layout.setSizeFull();
		layout.setSpacing(false);
		layout.addComponents(cheatsheetGrid);
		setSizeFull();

		addComponents(layout);
		setExpandRatio(layout, 1);
	}

	@SuppressWarnings("unchecked")
	public Grid<SbfRankSet> configureCheatsheetGrid(List<SbfRankSet> cheatsheets){
		cheatsheetGrid = new Grid<>();
		cheatsheetGrid.setCaption("My Cheatsheets");
		cheatsheetGrid.setItems(cheatsheets);

		SbfRankSet defRankSet = UserSessionVars.getPlayerService().getSbfRankSetByIdAndUser(UserSessionVars.getLeagueService().getDefaultRankSetForLeagueAndUser(
				UserSessionVars.getCurrentUser(), UserSessionVars.getCurrentLeague()), UserSessionVars.getCurrentUser().getUserId());
	//	cheatsheetGrid.setSizeFull();
		cheatsheetGrid.setSelectionMode(SelectionMode.SINGLE);
		cheatsheetGrid.addColumn(SbfRankSet::getRankSetName).setCaption("Name").setId("nameColumn");
		cheatsheetGrid.addColumn(rs->rs==defRankSet ? "Yes" : "No"
				).setCaption("Default").setId("defaultColumn");
		cheatsheetGrid.addColumn(s->"Edit", editButtonRenderer()).setId("EditColumn");
		cheatsheetGrid.addColumn(s->"Delete", deleteButtonRenderer()).setId("DeleteColumn");
		cheatsheetProvider = (ListDataProvider<SbfRankSet>) cheatsheetGrid.getDataProvider();

		return cheatsheetGrid;

	}

	@SuppressWarnings({ "rawtypes" })
	private ButtonRenderer editButtonRenderer (){
		ButtonRenderer<Object> editButtonRenderer = new ButtonRenderer<Object>();
		editButtonRenderer.addClickListener(clickEvent -> {
			SbfRankSet s = (SbfRankSet)clickEvent.getItem();
			UI.getCurrent().addWindow(getEditWindow(s));
		});
		return editButtonRenderer;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private ButtonRenderer deleteButtonRenderer (){
		ButtonRenderer<Object> deleteButtonRenderer = new ButtonRenderer<Object>();
		deleteButtonRenderer.addClickListener(clickEvent -> {
			SbfRankSet s = (SbfRankSet)clickEvent.getItem();
			UI.getCurrent().addWindow(getConfirmWindow(s));
		});
		return deleteButtonRenderer;
	}

	private Window getConfirmWindow(SbfRankSet s){
		Window subWindow = new Window("Delete " + s.getRankSetName() + "?");
		subWindow.setModal(true);
		subWindow.setClosable(false);
		subWindow.setResizable(false);
		Button yes = new Button("Yes, delete");
		Button no = new Button("NO! DON'T DO IT!");
		
		yes.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			//if using, use default, delete it & refresh dataset & close window
			if (UserSessionVars.getRankSet() == s){
				UserSessionVars.setRankSet(UserSessionVars.getPlayerService().getGlobalDefaultRankSet());
			}
			UserSessionVars.getLeagueService().removeRankSetAsDefault(s.getRankSetId());
			UserSessionVars.getPlayerService().deleteRankSet(s);
			cheatsheetProvider.refreshAll();
			subWindow.close();

		} });
		
		no.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			subWindow.close();

		} });
		HorizontalLayout yesNoLayout = new HorizontalLayout();
		yesNoLayout.setMargin(true);
		yesNoLayout.addComponents(yes, no);
		subWindow.setContent(yesNoLayout);
		return subWindow;
		
	}
	


	private Window getEditWindow(SbfRankSet s){
		boolean isDefault = UserSessionVars.getPlayerService().getSbfRankSetByIdAndUser(UserSessionVars.getLeagueService().getDefaultRankSetForLeagueAndUser(
				UserSessionVars.getCurrentUser(), UserSessionVars.getCurrentLeague()), UserSessionVars.getCurrentUser().getUserId()) == s;
		String cheatsheetName = s.getRankSetName();
		if (cheatsheetName == null) cheatsheetName = "";
		Window subWindow = new Window(cheatsheetName);
		VerticalLayout subContent = new VerticalLayout();

		TextField nameTextField = new TextField();
		nameTextField.setWidth("100%");
		nameTextField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nameTextField.setValue(cheatsheetName);
		CheckBox isDefaultCB = new CheckBox();
		if(UserSessionVars.getCurrentLeague() != null){
			isDefaultCB.setCaption("Use for " + UserSessionVars.getCurrentLeague().getLeagueName());
			isDefaultCB.setValue(isDefault);
		}else{
			isDefaultCB.setVisible(false);
			isDefaultCB.setValue(false);
		}

		subContent.setMargin(true);

		Button submitButton = new Button("Submit");
		submitButton.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			if(nameTextField.isEmpty()) return;
			s.setRankSetName(nameTextField.getValue());
			UserSessionVars.getPlayerService().updateSbfRankSet(s);
			if(isDefaultCB.getValue() != isDefault){
				SbfUserTeam ut = leagueService.getSbfUserTeamForLeagueAndUser(UserSessionVars.getCurrentUser(), 
						UserSessionVars.getCurrentLeague());
				if(isDefaultCB.getValue()){
					ut.setDefaultRankSetId(s.getRankSetId());
					UserSessionVars.setRankSet(s);
				}else{
					UserSessionVars.setRankSet(
			        		UserSessionVars.getPlayerService().getGlobalDefaultRankSet());
					ut.setDefaultRankSetId(0);
				}
				leagueService.updateSbfUserTeam(ut);
			}
			cheatsheetProvider.refreshAll();
			subWindow.close();
			Page.getCurrent().reload();

		} });

		subContent.addComponents(nameTextField, isDefaultCB, submitButton);
		subWindow.setContent(subContent);
		subWindow.center();

		//	subWindow.setClosable(false);
		return subWindow;

	}

}
