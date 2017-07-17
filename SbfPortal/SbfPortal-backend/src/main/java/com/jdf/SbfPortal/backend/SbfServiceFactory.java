package com.jdf.SbfPortal.backend;

import com.jdf.SbfPortal.backend.DAO.PlayerDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfDraftPickDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfDraftRecordDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfLeagueDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfRankDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfTeamDAOMysql;

public class SbfServiceFactory {

	public static PlayerService createPlayerService(){
		return new PlayerService(new PlayerDAOMysql(), new SbfRankDAOMysql());
	}
	
	public static SbfLeagueService createLeagueService(){
		return new SbfLeagueService(new SbfLeagueDAOMysql(), new SbfTeamDAOMysql());
	}
	
	public static SbfDraftService createDraftService(){
		return new SbfDraftService(new SbfDraftPickDAOMysql(), new SbfDraftRecordDAOMysql());
	}
}
