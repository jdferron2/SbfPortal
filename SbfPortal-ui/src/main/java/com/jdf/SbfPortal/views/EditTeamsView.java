package com.jdf.SbfPortal.views;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.backend.data.SbfKeeper;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class EditTeamsView extends VerticalLayout implements View {
	public static final String NAME = "Edit Teams";
	private SbfDraftService draftService;
	private PlayerService playerService;
	private SbfLeagueService leagueService;
	private LeagueInfoManager leagueMgr;
	private ListDataProvider<SbfTeam> teamsDataProvider;
	
	private Integer leagueId;
	private Integer sbfId;	
	private boolean viewBuilt = false;
	private String playerNameFilterValue="";
	
	private Grid<SbfTeam> teamsGrid;
	
	List<SbfTeam> teamList;
	
	@Override
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
		if (!viewBuilt) {
			buildView();
			viewBuilt=true;
		}
		
	}
	
	void buildView(){
		teamList =  leagueService.getAllSbfTeams(leagueId);
		
		teamsGrid = configureTeamGrid(teamList);

		final HorizontalLayout layout = new HorizontalLayout();
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

		teamsGrid.setSizeFull();
		teamsGrid.setSelectionMode(SelectionMode.SINGLE);
		teamsGrid.addColumn(SbfTeam::getOwnerName).setCaption("Owner").setId("OwnerColumn");
		teamsGrid.addColumn(SbfTeam::getTeamName).setCaption("Team Name").setId("TeamColumn");
		teamsGrid.addColumn(SbfTeam::getDraftSlot).setCaption("Draft Slot").setId("DraftslotColumn").setMaximumWidth(80);
		teamsGrid.addColumn(s->"Edit Team", editButtonRenderer()).setId("EditColumn");

		teamsDataProvider = (ListDataProvider<SbfTeam>) teamsGrid.getDataProvider();

		teamsDataProvider.setFilter(t->t, t -> teamsGridFilter(t));
		HeaderRow filterRow = teamsGrid.appendHeaderRow();

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
		
		subContent.setMargin(true);
		ComboBox<Integer> draftSlotCB =
			    new ComboBox<>("Draft Slot");
		draftSlotCB.setItems(draftSlots);
		draftSlotCB.setValue(t.getDraftSlot());
		

		
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
			
			leagueService.updateSbfTeam(t);
			if (t2!=null) leagueService.updateSbfTeam(t2);
			teamsDataProvider.refreshAll();
			subWindow.close();
					
		} });
		
		subContent.addComponents(teamNameTextField, ownerName, draftSlotCB, submitButton);
		subWindow.setContent(subContent);
		subWindow.center();
		
	//	subWindow.setClosable(false);
		return subWindow;
		
	}
	
}
