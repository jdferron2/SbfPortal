package com.jdf.SbfPortal.backend.DAO;

import java.util.List;

import com.jdf.SbfPortal.backend.data.SbfPickTrade;

public interface SbfPickTradesDAO {
	List<SbfPickTrade> getAllSbfPickTrades(Integer leagueId);
	void insertSbfPickTrade(SbfPickTrade a);
	void updateSbfPickTrade(SbfPickTrade a);
	void deleteSbfPickTrade(SbfPickTrade a);
}
