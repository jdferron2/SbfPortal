package com.jdf.SbfPortal.backend.DAO;

import java.util.List;

import com.jdf.SbfPortal.backend.data.SbfTeam;

public interface SbfTeamDAO {
	List<SbfTeam> getAllTeams();
	void insertTeam(SbfTeam r);
	void updateTeam(SbfTeam r);
	void deleteTeam(SbfTeam r);
}
