package com.jdf.SbfPortal.backend;

import java.util.List;

import com.jdf.SbfPortal.backend.DAO.SbfDraftPickDAO;
import com.jdf.SbfPortal.backend.DAO.SbfDraftRecordDAO;
import com.jdf.SbfPortal.backend.data.SbfDraftPick;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;

public class SbfDraftService {
	protected List<SbfDraftPick> sbfDraftPicks; 
	protected List<SbfDraftRecord> draftRecords; 
	protected SbfDraftPickDAO sbfDraftPickDao;
	protected SbfDraftRecordDAO sbfDraftRecordDao;

	protected SbfDraftService(SbfDraftPickDAO sbfDraftPickDao, SbfDraftRecordDAO sbfDraftRecordDao){
		this.sbfDraftPickDao = sbfDraftPickDao;
		this.sbfDraftRecordDao = sbfDraftRecordDao;
	}
	
	public synchronized void addSbfDraftPick(SbfDraftPick pick){
		sbfDraftPickDao.insertSbfDraftPick(pick);
		sbfDraftPicks.add(pick);
	}
	
	public synchronized void updateSbfDraftPick(SbfDraftPick pick){
		sbfDraftPickDao.updateSbfDraftPick(pick);
	}

	public synchronized void deleteSbfDraftPick(SbfDraftPick pick){
		sbfDraftPickDao.deleteSbfDraftPick(pick);
		sbfDraftPicks.remove(pick);
	}
	
	public synchronized void addSbfDraftRecord(SbfDraftRecord rec){
		sbfDraftRecordDao.insertDraftRecord(rec);
		draftRecords.add(rec);
	}
	
	public synchronized void deleteSbfDraftRecord(SbfDraftRecord rec){
		sbfDraftRecordDao.deleteDraftRecord(rec);
		draftRecords.remove(rec);
	}

	public synchronized void updateSbfDraftRecord(SbfDraftRecord rec){
		sbfDraftRecordDao.updateDraftRecord(rec);
	}
	
	public synchronized List<SbfDraftPick> getAllSbfDraftPicks(Integer leagueId) {
		if (sbfDraftPicks == null) {
			sbfDraftPicks = sbfDraftPickDao.getAllSbfDraftPicks(leagueId);
		}
		return sbfDraftPicks;
	}
	
	public synchronized List<SbfDraftRecord> getAllDraftRecords(Integer leagueId) {
		if (draftRecords == null) {
			draftRecords = sbfDraftRecordDao.getAllDraftRecords(leagueId);
		}
		return draftRecords;
	}

	public synchronized Integer getPickOwnerId(int pick, int leagueId) {
		SbfDraftPick test= getAllSbfDraftPicks(leagueId).stream().filter(
				p->p.getPick()==pick).findFirst().orElse(null);
		if(test==null) return null;
		return test.getSbfId();
	}	

	public synchronized SbfDraftPick getPickBySbfIdPick(int pick, int leagueId) {
		SbfDraftPick test= getAllSbfDraftPicks(leagueId).stream().filter(
				p->p.getPick() == pick).findFirst().orElse(null);
		if(test==null) return null;
		return test;
	}
	
	public synchronized SbfDraftRecord getSbfDraftRecordByPlayerId(int playerId, int leagueId){
		return getAllDraftRecords(leagueId).stream().
				filter(r->r.getPlayerId()==playerId).findFirst().orElse(null);
	}
}
