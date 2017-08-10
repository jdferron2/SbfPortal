package com.jdf.SbfPortal.backend.DAO;

import java.util.List;

import com.jdf.SbfPortal.backend.data.SbfKeeper;

public interface SbfKeeperDAO {
	List<SbfKeeper> getAllSbfKeepers(int leagueId);
	void insertSbfKeeper(SbfKeeper k);
	void updateSbfKeeper(SbfKeeper k);
	void deleteSbfKeeper(SbfKeeper k);
}
