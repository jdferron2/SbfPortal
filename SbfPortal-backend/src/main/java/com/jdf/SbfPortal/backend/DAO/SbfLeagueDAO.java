package com.jdf.SbfPortal.backend.DAO;

import java.util.List;

import com.jdf.SbfPortal.backend.data.SbfLeague;

public interface SbfLeagueDAO {
	List<SbfLeague> getAllSbfLeagues();
	void insertSbfLeague(SbfLeague r);
	void updateSbfLeague(SbfLeague r);
	void deleteSbfLeague(SbfLeague r);
}
