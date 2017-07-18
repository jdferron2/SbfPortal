package com.jdf.SbfPortal.backend.DAO;

import java.util.List;

import com.jdf.SbfPortal.backend.data.SbfDraftRecord;

public interface SbfDraftRecordDAO {
	List<SbfDraftRecord> getAllDraftRecords(Integer leagueId);
	void insertDraftRecord(SbfDraftRecord r);
	void updateDraftRecord(SbfDraftRecord r);
	void deleteDraftRecord(SbfDraftRecord r);
}
