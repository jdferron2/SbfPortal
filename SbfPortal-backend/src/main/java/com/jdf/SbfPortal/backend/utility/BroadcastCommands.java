package com.jdf.SbfPortal.backend.utility;

public class BroadcastCommands {
	/**
	 * Arguments: <br>
	 * 0 - SbfDraftRecord record to be added <br>
	 * 1 - boolean isAWinner ( for icing )
	 */
	public static final String DRAFT_PLAYER = "DraftPlayer";
	
	/**
	 * Arguments: <br>
	 * 0 - SbfDraftRecord record to be removed
	 */
	public static final String UNDO_DRAFT_PICK = "UndoDraftPick";
}
