package com.jdf.SbfPortal.backend.DAO;

import java.util.List;

import com.jdf.SbfPortal.backend.data.SbfDraftPick;

public interface SbfDraftPickDAO {
	List<SbfDraftPick> getAllSbfDraftPicks(Integer leagueId);
	void insertSbfDraftPick(SbfDraftPick a);
	void updateSbfDraftPick(SbfDraftPick a);
	void deleteSbfDraftPick(SbfDraftPick a);
}
