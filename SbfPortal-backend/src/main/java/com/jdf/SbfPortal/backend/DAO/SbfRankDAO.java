package com.jdf.SbfPortal.backend.DAO;

import java.util.List;

import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfRank;

public interface SbfRankDAO {
	List<SbfRank> getAllSbfRanks(Integer sbfId);
	void insertSbfRank(SbfRank r);
	void updateSbfRank(SbfRank r);
	void deleteSbfRank(SbfRank r);
}
