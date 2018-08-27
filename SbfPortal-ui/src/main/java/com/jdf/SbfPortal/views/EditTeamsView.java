package com.jdf.SbfPortal.views;

import java.util.List;

import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.jdf.SbfPortal.backend.data.SbfUser;
import com.jdf.SbfPortal.backend.data.SbfUserTeam;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class EditTeamsView extends VerticalLayout implements View {
	public static final String NAME = "Edit Teams";

	private SbfLeagueService leagueService;

	private ListDataProvider<SbfTeam> teamsDataProvider;

	private Integer leagueId;
	private boolean viewBuilt = false;


	private Grid<SbfTeam> teamsGrid;

	List<SbfTeam> teamList;

	@Override
	public void enter(ViewChangeEvent event) {
		leagueId = UserSessionVars.getCurrentLeague().getLeagueId();
		leagueService = UserSessionVars.getLeagueService();

		if (!viewBuilt) {
			buildView();
			viewBuilt=true;
		}

	}

	void buildView(){
		teamList =  leagueService.getAllSbfTeamsForLeague(leagueId);

		teamsGrid = configureTeamGrid(teamList);

		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSizeFull();
		layout.setSpacing(false);
		layout.addComponents(teamsGrid);
		setSizeFull();

		addComponents(layout);
		setExpandRatio(layout, 1);
	}

	@SuppressWarnings("unchecked")
	public Grid<SbfTeam> configureTeamGrid(List<SbfTeam> teams){
		teamsGrid = new Grid<>();
		teamsGrid.setCaption("Teams");
		teamsGrid.setItems(teams);
		//SbfUserTeam ut;
		teamsGrid.setSizeFull();
		teamsGrid.setSelectionMode(SelectionMode.SINGLE);
		teamsGrid.addColumn(SbfTeam::getOwnerName).setCaption("Owner").setId("OwnerColumn");
		teamsGrid.addColumn(SbfTeam::getTeamName).setCaption("Team Name").setId("TeamColumn");
		teamsGrid.addColumn(SbfTeam::getDraftSlot).setCaption("Draft Slot").setId("DraftslotColumn").setMaximumWidth(80);
		teamsGrid.addColumn(SbfTeam::getThemeSongUrl).setCaption("Theme Song").setId("ThemeSongColumn");
		teamsGrid.addColumn(t->{SbfUserTeam ut = UserSessionVars.getLeagueService().getSbfUserTeamForTeam(t);
								return ut == null ? null : UserSessionVars.getLeagueService().getSbfUserById(ut.getUserId()).getUserName();}).
					setCaption("User").setId("userColumn");
		
		teamsGrid.addColumn(s->"Edit Team", editButtonRenderer()).setId("EditColumn");

		teamsGrid.getColumn("EditColumn").setStyleGenerator(t -> {
			SbfUserTeam ut = UserSessionVars.getLeagueService().getSbfUserTeamForTeam(t);
			if (UserSessionVars.getAccessControl().isUserLeagueManager()) return null;
			if (ut == null) { return "hidden";}
			if (ut.getUserId() != UserSessionVars.getCurrentUser().getUserId()) {
				return "hidden" ;
			}else{
				return null;
			}
		});
		
		teamsDataProvider = (ListDataProvider<SbfTeam>) teamsGrid.getDataProvider();

		teamsDataProvider.setFilter(t->t, t -> teamsGridFilter(t));
		//HeaderRow filterRow = teamsGrid.appendHeaderRow();

		teamsGrid.sort("DraftslotColumn");

		return teamsGrid;

	}

	@SuppressWarnings({ "rawtypes" })
	private ButtonRenderer editButtonRenderer (){
		ButtonRenderer<Object> editButtonRenderer = new ButtonRenderer<Object>();
		editButtonRenderer.addClickListener(clickEvent -> {
			SbfTeam t = (SbfTeam)clickEvent.getItem();
			UI.getCurrent().addWindow(getEditTeamWindow(t));
		});
		return editButtonRenderer;
	}

	private boolean teamsGridFilter(SbfTeam t){
		//Place holder
		return true;
	}



	private Window getEditTeamWindow(SbfTeam t){
		String teamName = t.getTeamName();
		String themeSong = t.getThemeSongUrl() == null ? "" : t.getThemeSongUrl();
		ComboBox<SbfUser> usersCB =
				new ComboBox<>("Available Users");
		
		if (teamName == null) teamName = "";
		Window subWindow = new Window(t.getOwnerName() + " - " + t.getTeamName());
		VerticalLayout subContent = new VerticalLayout();
		Integer[] draftSlots = new Integer[LeagueInfoManager.NUMBER_OF_TEAMS];
		for (int i = 0; i <= draftSlots.length-1; i++){
			draftSlots[i] = i+1;
		}
		TextField teamNameTextField = new TextField();
		teamNameTextField.setWidth("100%");
		teamNameTextField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		teamNameTextField.setValue(teamName);

		TextField ownerName = new TextField();
		ownerName.setWidth("100%");
		ownerName.addStyleName(ValoTheme.TEXTFIELD_TINY);
		ownerName.setValue(t.getOwnerName());
		
		SbfUserTeam ut = UserSessionVars.getLeagueService().getSbfUserTeamForTeam(t);
		SbfUser user=null;
		if (ut != null ) { user=UserSessionVars.getLeagueService().getSbfUserById(ut.getUserId());}
		HorizontalLayout userLayout = new HorizontalLayout();
		if(user != null) {
			Label userName = new Label("User: " + user.getUserName());
			Button removeButton = new Button("Remove");
			removeButton.setStyleName(ValoTheme.BUTTON_LINK);
			removeButton.addClickListener(new Button.ClickListener()
			{ @Override public void buttonClick(Button.ClickEvent clickEvent)
			{
				UserSessionVars.getLeagueService().deleteSbfUserTeam(
						UserSessionVars.getLeagueService().getSbfUserTeamForTeam(t));
				removeButton.setCaption("(User Removed)");
				removeButton.setEnabled(false);
			}
			});
			userLayout.addComponents(userName, removeButton);
		}else {
			usersCB.setItems(UserSessionVars.getLeagueService().getAllSbfUsers());
			usersCB.setItemCaptionGenerator(u->u.getUserName());
			userLayout.addComponents(usersCB);
		}
		
		
		subContent.setMargin(true);
		ComboBox<Integer> draftSlotCB =
				new ComboBox<>("Draft Slot");
		draftSlotCB.setItems(draftSlots);
		draftSlotCB.setValue(t.getDraftSlot());

		TextField themeSongUrl = new TextField();
		themeSongUrl.setCaption("Theme Song Url");
		themeSongUrl.setWidth("100%");
		themeSongUrl.addStyleName(ValoTheme.TEXTFIELD_LARGE);
		themeSongUrl.setValue(themeSong);

		Button submitButton = new Button("Submit");
		submitButton.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			SbfTeam t2= leagueService.getSbfTeamByDraftSlot(draftSlotCB.getValue(), leagueId);
			if (draftSlotCB.getValue() > 0 && draftSlotCB.getValue() <= LeagueInfoManager.NUMBER_OF_TEAMS){
				if (t2 != null){
					t2.setDraftSlot(t.getDraftSlot());
				}
				t.setDraftSlot(draftSlotCB.getValue());
			}
			if(teamNameTextField.getValue() != null && !teamNameTextField.getValue().equals("")){
				t.setTeamName(teamNameTextField.getValue());
			}
			if(ownerName.getValue() != null && !ownerName.getValue().equals("")){
				t.setOwnerName(ownerName.getValue());
			}
			t.setThemeSongUrl(themeSongUrl.getValue());
			
			if(usersCB.getValue() !=null) {
				t.setUserId(usersCB.getValue().getUserId());
				SbfUserTeam ut = new SbfUserTeam();
				ut.setDefaultRankSetId(0);
				ut.setLeagueId(leagueId);
				ut.setTeamId(t.getTeamId());
				ut.setUserId(usersCB.getValue().getUserId());
				leagueService.insertSbfUserTeam(ut);
			}
			leagueService.updateSbfTeam(t);
			if (t2!=null) leagueService.updateSbfTeam(t2);
			teamsDataProvider.refreshAll();
			subWindow.close();

		} });

		if (UserSessionVars.getAccessControl().isUserLeagueManager()){
			subContent.addComponents(teamNameTextField, ownerName, userLayout, draftSlotCB, submitButton, themeSongUrl);
		}else {
			subContent.addComponents(teamNameTextField, ownerName, submitButton, themeSongUrl);
		}
		subWindow.setContent(subContent);
		subWindow.center();

		//	subWindow.setClosable(false);
		return subWindow;

	}

}
