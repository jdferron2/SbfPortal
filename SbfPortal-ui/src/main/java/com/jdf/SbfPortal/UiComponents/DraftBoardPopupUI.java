package com.jdf.SbfPortal.UiComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@Theme("sbftheme") 
@Push
public class DraftBoardPopupUI extends UI {
	PlayerService playerService;
	SbfDraftService draftService;
	SbfLeagueService leagueService;
	LeagueInfoManager leagueMgr;
	Integer leagueId;
	boolean viewBuilt = false;
	int currentCol = 1;
	int currentRow = 1;
	GridLayout draftGrid;
	HashMap<Integer, Label> draftLabelMap = new HashMap<Integer, Label>();

	@Override
	protected void init(VaadinRequest request) {
		leagueId = (Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_ID);
		playerService = (PlayerService) UI.getCurrent().getSession().getAttribute(SessionAttributes.PLAYER_SERVICE);
		draftService = (SbfDraftService) UI.getCurrent().getSession().getAttribute(SessionAttributes.DRAFT_SERVICE);
		leagueService = (SbfLeagueService) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_SERVICE);
		if(leagueMgr==null){
			leagueMgr =
					(LeagueInfoManager) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_MANAGER);
		}
		if (!viewBuilt){
			buildLayout();
			viewBuilt=true;
		}

	}

	private void buildLayout(){
		VerticalLayout labelContainer;
		draftGrid = new GridLayout(LeagueInfoManager.NUMBER_OF_TEAMS+1,LeagueInfoManager.NUMBER_OF_ROUNDS+1);
		draftGrid.setSpacing(true);
		List<SbfTeam> teamList = leagueService.getAllSbfTeams(leagueId);
		teamList.sort((t1,t2)->Integer.compare(t1.getDraftSlot(), t2.getDraftSlot()));
		for(SbfTeam t : teamList){
			Label teamLabel = new Label(t.getOwnerName());
			draftGrid.addComponent(teamLabel, t.getDraftSlot(),0);
			draftGrid.setComponentAlignment(teamLabel, Alignment.MIDDLE_CENTER);
			teamLabel.addStyleName(ValoTheme.LABEL_BOLD);
		}

		for (int i=1; i<= LeagueInfoManager.NUMBER_OF_ROUNDS; i++){
			Label roundLabel = new Label(String.valueOf(i));
			roundLabel.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
			draftGrid.addComponent(roundLabel,0,i );
			draftGrid.setComponentAlignment(roundLabel, Alignment.MIDDLE_CENTER);
			roundLabel.addStyleName(ValoTheme.LABEL_BOLD);
		}
		List<SbfDraftRecord> draftList = draftService.getAllDraftRecords(leagueId);
		draftList.sort((s1,s2)->Integer.compare(s1.getSlotDrafted(), s2.getSlotDrafted()));
		for (int i = 0; i< LeagueInfoManager.NUMBER_OF_ROUNDS *LeagueInfoManager.NUMBER_OF_TEAMS; i++) {
			labelContainer = new VerticalLayout();
			Label l = new Label(Integer.toString(i+1));
			draftLabelMap.put(i+1, l);
			labelContainer.addStyleName("draftGridLabel");
			l.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
			labelContainer.setMargin(false);
			labelContainer.setSpacing(false);
			labelContainer.setHeight("45px");
			labelContainer.setWidth("143px");
			l.setWidth("133px");
			l.setVisible(false);
			labelContainer.addComponent(l);
			labelContainer.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
			draftGrid.addComponent(labelContainer,currentCol ,currentRow);
			if ((currentRow) % 2 == 0) {//even rounds
				currentCol--;
			}else{ //odd round
				currentCol++;
			}
			if(currentCol > LeagueInfoManager.NUMBER_OF_TEAMS){
				currentRow++;
				currentCol=LeagueInfoManager.NUMBER_OF_TEAMS;
			}else if(currentCol < 1){
				currentRow++;
				currentCol=1;
			}
		}

		for(SbfDraftRecord r : draftService.getAllDraftRecords(leagueId)){
			addDraftSelection(r);
		}
		setContent(draftGrid);
	}

	public synchronized void addDraftSelection(SbfDraftRecord r){
		access(new Runnable() {
			@Override
			public void run() {      
				Label l = draftLabelMap.get(r.getSlotDrafted());
				Player p = playerService.getPlayerById(r.getPlayerId());
				l.setValue(p.getDisplayName());
				l.getParent().addStyleName(getPositionStyle(p.getPosition()));
				//l.addStyleName(getPositionStyle(p.getPosition()));
				l.setVisible(true);
			}
		});	
	}

	private String getPositionStyle(String pos){
		String style = "";
		switch (pos.toUpperCase()){
		case 	"QB": style="bgQB";
				break;
		case 	"RB": style="bgRB";
				break;
		case 	"WR": style="bgWR";
				break;
		case 	"TE": style="bgTE";
				break;
		case 	"DEF": style="bgDEF";
				break;
		case 	"K": style="bgK";
				break;
		}
		return style;
	}

}
