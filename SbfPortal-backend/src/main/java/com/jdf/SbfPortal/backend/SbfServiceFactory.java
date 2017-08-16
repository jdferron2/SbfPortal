package com.jdf.SbfPortal.backend;

import com.jdf.SbfPortal.backend.DAO.PlayerDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfPickTradesDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfDraftRecordDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfKeeperDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfLeagueDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfRankDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfRankSetDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfTeamDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfUserDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfUserTeamsDAOMysql;

public class SbfServiceFactory {

	public static PlayerService createPlayerService(){
		return new PlayerService(new PlayerDAOMysql(), new SbfRankDAOMysql(), new SbfRankSetDAOMysql());
	}

	public static SbfLeagueService createLeagueService(){
		return new SbfLeagueService(new SbfLeagueDAOMysql(), new SbfTeamDAOMysql(), 
				new SbfKeeperDAOMysql(), new SbfUserDAOMysql(), new SbfUserTeamsDAOMysql());
	}

	public static SbfDraftService createDraftService(){
		return new SbfDraftService(new SbfPickTradesDAOMysql(), new SbfDraftRecordDAOMysql());
	}
}
