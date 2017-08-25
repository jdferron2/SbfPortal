package com.jdf.SbfPortal.utility;

import com.jdf.SbfPortal.SbfUI;
import com.jdf.SbfPortal.UiComponents.DraftBoardPopupUI;
import com.jdf.SbfPortal.UiComponents.DraftDisplayPopupUI;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.backend.utility.BroadcastCommands;
import com.jdf.SbfPortal.views.DraftDayBoardView;
import com.jdf.SbfPortal.views.DraftDayView;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

public class MessageHandler  {
	private VaadinSession session;
	public MessageHandler(VaadinSession ses){
		session = ses;
	}
	public void processMessage(VaadinSession ses, String command, Object[] args){
		if(!session.equals(ses)){
			if(command.equals(BroadcastCommands.DRAFT_PLAYER)){
				playerDraftedCmd((SbfDraftRecord)args[0], (boolean)args[1]);
			}else if (command.equals(BroadcastCommands.UNDO_DRAFT_PICK)){
				undoDraftPickCmd((SbfDraftRecord)args[0]);
			}
		}
	}

	private void undoDraftPickCmd(SbfDraftRecord r) {
		UserSessionVars.getDraftService().removeSbfDraftRecordFromSession(r);
		View v = null;
		for(UI ui : session.getUIs()){
			if (ui.getClass().equals(SbfUI.class)){
				v = UserSessionVars.getCurrentView();
			}
			if (ui.getClass().equals(DraftBoardPopupUI.class)){
				((DraftBoardPopupUI)ui).removeDraftSelection(r);
			}
			if (ui.getClass().equals(DraftDisplayPopupUI.class)){
				((DraftDisplayPopupUI)ui).processPick(false, true);
			}
			if(v != null){
				if (v.getClass().equals(DraftDayView.class)){
					((DraftDayView) v).refreshPage();
				}
			}
		}

	}
	private void playerDraftedCmd(SbfDraftRecord r, boolean isAWinner){
		UserSessionVars.getDraftService().addSbfDraftRecordtoSession(r);
		View v = null;
		for(UI ui : session.getUIs()){
			if (ui.getClass().equals(SbfUI.class)){
				v = UserSessionVars.getCurrentView();
			}
			if (ui.getClass().equals(DraftBoardPopupUI.class)){
				((DraftBoardPopupUI)ui).addDraftSelection(r);
			}
			if (ui.getClass().equals(DraftDisplayPopupUI.class)){
				((DraftDisplayPopupUI)ui).processPick(isAWinner, false);
			}
			if(v != null){
				if (v.getClass().equals(DraftDayView.class)){
					((DraftDayView) v).refreshPage();
				}
			}
		}
		

	}

}
