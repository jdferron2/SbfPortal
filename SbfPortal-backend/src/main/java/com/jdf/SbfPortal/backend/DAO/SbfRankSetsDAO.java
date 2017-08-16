package com.jdf.SbfPortal.backend.DAO;

import java.util.List;

import com.jdf.SbfPortal.backend.data.SbfRankSet;

public interface SbfRankSetsDAO {
	List<SbfRankSet> getAllSbfRankSets(Integer userId);
	void insertSbfRankSet(SbfRankSet s);
	void updateSbfRankSet(SbfRankSet s);
	void deleteSbfRankSet(SbfRankSet s);
}
