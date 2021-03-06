package com.jdf.SbfPortal.backend;

import java.util.ArrayList;
import java.util.List;

import com.jdf.SbfPortal.backend.DAO.SbfPickTradesDAO;
import com.jdf.SbfPortal.backend.DAO.SbfDraftRecordDAO;
import com.jdf.SbfPortal.backend.data.SbfPickTrade;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;

public class SbfDraftService {
	protected List<SbfPickTrade> sbfDraftPicks; 
	protected List<SbfDraftRecord> draftRecords; 
	protected SbfPickTradesDAO sbfDraftPickDao;
	protected SbfDraftRecordDAO sbfDraftRecordDao;
	protected Integer			lastRetrievedLeagueIdPickTrades;
	protected Integer			lastRetrievedLeagueIdDraftRecords;

	protected SbfDraftService(SbfPickTradesDAO sbfDraftPickDao, SbfDraftRecordDAO sbfDraftRecordDao){
		this.sbfDraftPickDao = sbfDraftPickDao;
		this.sbfDraftRecordDao = sbfDraftRecordDao;
	}
	
	public synchronized void addSbfPickTrade(SbfPickTrade pick){
		getAllSbfPickTrades(pick.getLeagueId()).add(pick);
		sbfDraftPickDao.insertSbfPickTrade(pick);
	}
	
	public synchronized void updateSbfPickTrade(SbfPickTrade pick){
		sbfDraftPickDao.updateSbfPickTrade(pick);
	}

	public synchronized void deleteSbfPickTrade(SbfPickTrade pick){
		getAllSbfPickTrades(pick.getLeagueId()).remove(pick);
		sbfDraftPickDao.deleteSbfPickTrade(pick);
	}
	
	public synchronized void addSbfDraftRecord(SbfDraftRecord rec){
		this.getAllDraftRecords(rec.getLeagueId()).add(rec);
		sbfDraftRecordDao.insertDraftRecord(rec);
	}
	
	public synchronized void addSbfDraftRecordtoSession(SbfDraftRecord rec){
		this.getAllDraftRecords(rec.getLeagueId()).add(rec);
	}
	
	public void removeSbfDraftRecordFromSession(SbfDraftRecord r) {
		SbfDraftRecord removeMe = this.getAllDraftRecords(r.getLeagueId()).stream().
				filter(d->d.getPlayerId()==r.getPlayerId()).findFirst().orElse(null);
		if(removeMe != null) getAllDraftRecords(r.getLeagueId()).remove(removeMe);
	}
	
	public synchronized void deleteSbfDraftRecord(SbfDraftRecord rec){
		this.getAllDraftRecords(rec.getLeagueId()).remove(rec);
		sbfDraftRecordDao.deleteDraftRecord(rec);
	}

	public synchronized void updateSbfDraftRecord(SbfDraftRecord rec){
		sbfDraftRecordDao.updateDraftRecord(rec);
	}
	
	public synchronized List<SbfPickTrade> getAllSbfPickTrades(Integer leagueId) {
		if (sbfDraftPicks == null || lastRetrievedLeagueIdPickTrades != leagueId) {
			lastRetrievedLeagueIdPickTrades=leagueId;
			sbfDraftPicks = sbfDraftPickDao.getAllSbfPickTrades(leagueId);
		}
		return sbfDraftPicks;
	}
	
	public synchronized List<SbfDraftRecord> getAllDraftRecords(Integer leagueId) {
		if (draftRecords == null || lastRetrievedLeagueIdDraftRecords != leagueId) {
			lastRetrievedLeagueIdDraftRecords = leagueId;
			draftRecords = sbfDraftRecordDao.getAllDraftRecords(leagueId);
		}
		return draftRecords;
	}

	public synchronized Integer getPickOwnerId(int pick, int leagueId) {
		//teamList.sort((t1,t2)->Integer.compare(t1.getDraftSlot(), t2.getDraftSlot()));
		List<SbfPickTrade> trades = getAllSbfPickTrades(leagueId);
		List<SbfPickTrade> tradesSorted = new ArrayList<SbfPickTrade>();
		for(SbfPickTrade t : trades){
			tradesSorted.add(t);
		}
		tradesSorted.sort((p1,p2)->p2.getProcessedTs().compareTo(p1.getProcessedTs()));
		SbfPickTrade test= tradesSorted.stream().filter(
				p->p.getPick()==pick).findFirst().orElse(null);
		if(test==null) return null;
		return test.getToTeamId();
	}	

	public synchronized SbfPickTrade getPickBySbfIdPick(int pick, int leagueId) {
		SbfPickTrade test= getAllSbfPickTrades(leagueId).stream().filter(
				p->p.getPick() == pick).findFirst().orElse(null);
		if(test==null) return null;
		return test;
	}
	
	public synchronized SbfDraftRecord getSbfDraftRecordByPlayerId(int playerId, int leagueId){
		return getAllDraftRecords(leagueId).stream().
				filter(r->r.getPlayerId()==playerId).findFirst().orElse(null);
	}
	
	public synchronized SbfDraftRecord getSbfDraftRecordByPickNum(int pick, int leagueId){
		return getAllDraftRecords(leagueId).stream().
				filter(r->r.getSlotDrafted()==pick).findFirst().orElse(null);
	}


}
