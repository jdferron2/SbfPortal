package com.jdf.SbfPortal.UiComponents;

import java.io.File;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Audio;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("sbftheme") 
@Push
public class DraftDisplayPopupUI extends UI {
	PlayerService playerService;
	SbfDraftService draftService;
	SbfLeagueService leagueService;
	LeagueInfoManager leagueMgr;
	Integer leagueId;
	boolean viewBuilt = false;
	boolean pickIsInDisplayed = false;
	//private Audio FIREWORKSSOUND = new Audio(null, new ThemeResource("audio/fireworks2.mp3"));
	private Audio TADASOUND = new Audio(null, new ThemeResource("audio/tada.mp3"));
	private Audio SHOTGUNSONG = new Audio(null, new ThemeResource("audio/shotgunsong.mp3"));
	//private Audio CHEERINGSOUND = new Audio(null, new ThemeResource("audio/cheering.mp3"));
	private Audio TEAMTHEMESONG = new Audio(null, new ThemeResource("audio/Lennon.mp3"));

	Resource iceResource = new ThemeResource("img/ice.jpg");
	Resource shotResource = new ThemeResource("img/shot.jpg");
	Resource shotGunResource = new ThemeResource("img/shotgun.png");
	Image winningImage = new Image(null, iceResource);
	Image shotgunImage = new Image(null, shotGunResource);
	
	private boolean themeSongsEnabled = true;
	
	VerticalLayout mainContent;
	VerticalLayout pickIsInContent;
	VerticalLayout winnerContent;
	VerticalLayout shotgunContent;

	Label latestPickLabel = new Label();
	Label selectedPlayerLabel = new Label();
	Label onTheClockLabel = new Label();


	@Override
	protected void init(VaadinRequest request) {
		draftService = UserSessionVars.getDraftService();
		leagueService = UserSessionVars.getLeagueService();
		playerService = UserSessionVars.getPlayerService();
		leagueId = UserSessionVars.getCurrentLeague().getLeagueId();
		leagueMgr = UserSessionVars.getLeagueManager();

		if (!viewBuilt){
			buildLayout();
			viewBuilt=true;
		}
	}

	protected void buildLayout(){
		buildPickIsIn();
		buildWinnerDisplay();
		this.buildShotgunDisplay();
		addStyleName("bgMain");
		mainContent = new VerticalLayout();
		HorizontalLayout topBannerLayout = new HorizontalLayout();
		HorizontalLayout onTheClockLayout = new HorizontalLayout();
		VerticalLayout lastPickLayout = new VerticalLayout();
		VerticalLayout selectedPlayerLayout = new VerticalLayout();
		selectedPlayerLayout.setSizeFull();
		topBannerLayout.setSizeFull();
		lastPickLayout.setSizeFull();
		onTheClockLayout.setWidth("100%");
		lastPickLayout.setMargin(new MarginInfo(false,false,true,false));
		lastPickLayout.addStyleName("bgRed");
		onTheClockLayout.setSpacing(true);
		selectedPlayerLayout.addStyleName("bgNfl");
		mainContent.setSizeFull();
		setSizeFull();


		setLatestPickValue();
		setSelectedPlayerLabel();
		setOnTheClockLabel();

		latestPickLabel.setContentMode(ContentMode.HTML);
		selectedPlayerLabel.setContentMode(ContentMode.HTML);
		onTheClockLabel.setContentMode(ContentMode.HTML);
		selectedPlayerLabel.setWidth("100%");

		onTheClockLabel.setSizeFull();
		onTheClockLayout.addComponent(onTheClockLabel);

		lastPickLayout.addComponent(latestPickLabel);
		lastPickLayout.setComponentAlignment(latestPickLabel, Alignment.MIDDLE_CENTER);

		selectedPlayerLayout.addComponent(selectedPlayerLabel);
		selectedPlayerLayout.setComponentAlignment(selectedPlayerLabel, Alignment.MIDDLE_CENTER);

		topBannerLayout.addComponents(selectedPlayerLayout, getSbfLogo(), lastPickLayout);
		mainContent.addComponents(topBannerLayout, onTheClockLayout,TEAMTHEMESONG);
		TEAMTHEMESONG.setShowControls(false); TEAMTHEMESONG.setSizeUndefined();
		mainContent.setExpandRatio(topBannerLayout, 1);
		mainContent.setExpandRatio(TEAMTHEMESONG, 0);
		setContent(mainContent);
	}

	public synchronized void setLatestPickValue(){
		int lastPick = leagueMgr.getCurrentPick()-1;
		if (lastPick == 0) return;
		int round = LeagueInfoManager.getRound(lastPick);
		int pickInRound = LeagueInfoManager.getPickInRound(lastPick);
		latestPickLabel.setValue("<h1 class=\"otc\">Round <br />" + round 
				+ "<br />Pick<br />" + pickInRound + "</h1>");		
	}	

	public synchronized void setSelectedPlayerLabel(){		
		SbfDraftRecord latestPick = draftService.getSbfDraftRecordByPickNum(leagueMgr.getCurrentPick()-1, leagueId);
		if (latestPick == null) return;
		SbfTeam teamOnClock = leagueService.getSbfTeamByTeamId(latestPick.getTeamId(), leagueId);
		String teamName = teamOnClock.getOwnerName();
		Player selectedPlayer = playerService.getPlayerById(latestPick.getPlayerId()).orElseThrow(()->new RuntimeException("Couldnt find player: " + latestPick.getPlayerId()));		
		selectedPlayerLabel.setValue("<div class=\"selectedPlayerInfo\">" + teamName + " selects<br />"
				+ "<div class=\"selectedPlayer\">" + selectedPlayer.getDisplayName() + "<br /> </div>"
				+ selectedPlayer.getPosition() + " | " + selectedPlayer.getTeam() + "<br />"
				+ "Pro Rank " + selectedPlayer.getProRank() + "</div>");
	}

	public synchronized void setOnTheClockLabel(){
		SbfTeam teamOnClock = leagueMgr.getTeamOnTheClock();	
		onTheClockLabel.setValue("<div class=\"onTheClock\">On the clock: " + teamOnClock.getOwnerName() + "</div>");
	}
	Image getSbfLogo(){
		Resource res = new ThemeResource("img/2020_SBF_Draft.png");
		Image image = new Image(null, res);
		image.setSizeFull();
		return image;
	}

	public void pickIsIn(){
		setContent(pickIsInContent);
		pickIsInDisplayed = true;
	}

	//	void removePickIsIn(){
	//		if (pickIsInDisplayed){
	//			setContent(mainContent);
	//			pickIsInDisplayed = false;
	//		}
	//	}

	void buildPickIsIn(){
		pickIsInContent = new VerticalLayout();
		pickIsInContent.setSizeFull();
		Label pickIsInLabel = new Label();
		pickIsInLabel.setValue("Pick Is In!");
		pickIsInLabel.addStyleName("pickIsIn");
		pickIsInLabel.setWidth("95%");
		pickIsInContent.addComponent(pickIsInLabel);
		pickIsInContent.setComponentAlignment(pickIsInLabel, Alignment.MIDDLE_CENTER);
	}

	void buildWinnerDisplay(){
		//FIREWORKSSOUND.setShowControls(false); FIREWORKSSOUND.setSizeUndefined();
		TADASOUND.setShowControls(false); TADASOUND.setSizeUndefined();
		
		VerticalLayout soundLayout = new VerticalLayout();
		soundLayout.addComponents(TADASOUND);

		winnerContent = new VerticalLayout();
		winnerContent.setSizeFull();
		HorizontalLayout picLayout = new HorizontalLayout();
		Resource res = new ThemeResource("img/fireworks.gif");
		Image fireWorksImg = new Image(null, res);
		fireWorksImg.setSizeFull();

		Image fireWorksImg2 = new Image(null, res);
		fireWorksImg2.setSizeFull();

		//iceImg.setSizeFull();
		picLayout.setSizeFull();

		picLayout.addComponents(fireWorksImg,winningImage, fireWorksImg2);
		picLayout.setComponentAlignment(winningImage, Alignment.MIDDLE_CENTER);

		Label winnerLabel = new Label();
		winnerLabel.setValue("YOU WIN!!");
		winnerLabel.addStyleName("pickIsIn");
		winnerLabel.setWidth("100%");

		Label congratsLabel = new Label();
		congratsLabel.setValue("Congratulations!");
		congratsLabel.addStyleName("pickIsIn");
		congratsLabel.setWidth("100%");

		winnerContent.addComponents(congratsLabel, picLayout, winnerLabel, soundLayout);
		winnerContent.setExpandRatio(picLayout, 1);
		winnerContent.setExpandRatio(soundLayout, 0);
	}
	
	void buildShotgunDisplay(){
		//FIREWORKSSOUND.setShowControls(false); FIREWORKSSOUND.setSizeUndefined();
		SHOTGUNSONG.setShowControls(false); SHOTGUNSONG.setSizeUndefined();
		
		VerticalLayout soundLayout = new VerticalLayout();
		soundLayout.addComponents(SHOTGUNSONG);

		shotgunContent= new VerticalLayout();
		shotgunContent.setSizeFull();
		HorizontalLayout picLayout = new HorizontalLayout();
		Resource res = new ThemeResource("img/fireworks.gif");
		Image fireWorksImg = new Image(null, res);
		fireWorksImg.setSizeFull();
		//iceImg.setSizeFull();
		picLayout.setSizeFull();

		picLayout.addComponents(fireWorksImg,shotgunImage);
		picLayout.setComponentAlignment(shotgunImage, Alignment.MIDDLE_CENTER);

		Label winnerLabel = new Label();
		winnerLabel.setValue("SHOTGUN!!");
		winnerLabel.addStyleName("pickIsIn");
		winnerLabel.setWidth("100%");

		Label congratsLabel = new Label();
		congratsLabel.setValue("LEAGUE!");
		congratsLabel.addStyleName("pickIsIn");
		congratsLabel.setWidth("100%");

		shotgunContent.addComponents(congratsLabel, picLayout, winnerLabel, soundLayout);
		shotgunContent.setExpandRatio(picLayout, 1);
		shotgunContent.setExpandRatio(soundLayout, 0);
	}

	public synchronized void processPick(String prize, boolean isUndo){
		access(new Runnable() {
			@Override
			public void run() { 
				if(UI.getCurrent().getSession().getAttribute(SessionAttributes.THEME_SONGS_ENABLED) == null){
					UI.getCurrent().getSession().setAttribute(SessionAttributes.THEME_SONGS_ENABLED, true);
					themeSongsEnabled = true;
				}else{
					themeSongsEnabled = (boolean) UI.getCurrent().getSession().getAttribute(SessionAttributes.THEME_SONGS_ENABLED);
				}
				if (prize.equals("ice")){
					winningImage.setSource(iceResource);
					setContent(winnerContent);
					TADASOUND.play();
				}else if (prize.equals("shot")){
					winningImage.setSource(shotResource);
					setContent(winnerContent);
					TADASOUND.play();
				}else if (prize.equals("shotgun")) {
					setContent(shotgunContent);
					SHOTGUNSONG.play();
					
				}else{
					setOnTheClockLabel();
					setLatestPickValue();
					setSelectedPlayerLabel();
					setContent(mainContent);
					if(!isUndo && themeSongsEnabled) playThemeSong();
				}

			}
		});
	}
	
	private void playThemeSong(){
		
		SbfDraftRecord latestPick = draftService.getSbfDraftRecordByPickNum(leagueMgr.getCurrentPick()-1, leagueId);
		SbfTeam team = leagueService.getSbfTeamByTeamId(latestPick.getTeamId(), leagueId);
		String teamName = team.getOwnerName();
		String basepath = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath();
		File soundFile = new File(
				basepath + "/WEB-INF/music/" + teamName + ".mp3");
		if (soundFile.exists() || (team.getThemeSongUrl() != null && !team.getThemeSongUrl().equals("")) ){
			//FileResource resource = new FileResource(soundFile);
			if (team.getThemeSongUrl() != null && !team.getThemeSongUrl().equals("")){
				TEAMTHEMESONG.setSource(new ExternalResource(team.getThemeSongUrl()));
			}else{
				TEAMTHEMESONG.setSource(new FileResource(soundFile));
			}
			
			mainContent.removeComponent(TEAMTHEMESONG);
			//TEAMTHEMESONG = new Audio(null, resource);
			mainContent.addComponent(TEAMTHEMESONG);
			TEAMTHEMESONG.setShowControls(false); TEAMTHEMESONG.setSizeUndefined();
			mainContent.setExpandRatio(TEAMTHEMESONG, 0);
			
			TEAMTHEMESONG.play();
		}else{
			//TEAMTHEMESONG = new Audio(null, new ThemeResource("audio/tada.mp3"));
		}
		

	}
}

