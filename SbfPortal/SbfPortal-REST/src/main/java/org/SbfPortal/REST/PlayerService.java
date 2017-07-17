package org.SbfPortal.REST;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.jdf.SbfPortal.backend.DAO.PlayerDAO;
import com.jdf.SbfPortal.backend.DAO.PlayerDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfRankDAO;
import com.jdf.SbfPortal.backend.DAO.SbfRankDAOMysql;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfRank;

@Path("/playerservice") 

public class PlayerService {
	private PlayerDAO playerDao;
	private SbfRankDAO sbfRankDao;

	public PlayerService(){
		playerDao = new PlayerDAOMysql();
		sbfRankDao = new SbfRankDAOMysql();
	}

	@GET 
	@Path("/players") 
	@Produces(MediaType.APPLICATION_JSON) 
	public List<Player> getAllPlayers() {
		//GenericEntity<List<Player>> entity = new GenericEntity<List<Player>>(playerDao.getAllPlayers()) {};
		//return Response.ok().entity(entity).build();
		return playerDao.getAllPlayers();
	}

	
	@GET 
	@Path("/ranks/sbfid/{i}") 
	@Produces({MediaType.APPLICATION_JSON} ) 
	public List<SbfRank> getAllSbfRanks(@PathParam("i") Integer i) {
		return sbfRankDao.getAllSbfRanks(i);
	}

}
