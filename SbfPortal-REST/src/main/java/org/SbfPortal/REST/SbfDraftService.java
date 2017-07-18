package org.SbfPortal.REST;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.jdf.SbfPortal.backend.DAO.SbfDraftPickDAO;
import com.jdf.SbfPortal.backend.DAO.SbfDraftPickDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfDraftRecordDAO;
import com.jdf.SbfPortal.backend.DAO.SbfDraftRecordDAOMysql;
import com.jdf.SbfPortal.backend.DAO.SbfTeamDAO;
import com.jdf.SbfPortal.backend.DAO.SbfTeamDAOMysql;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfDraftPick;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.backend.data.SbfTeam;

@Path("/sbfdraftservice")

public class SbfDraftService {
	protected SbfDraftPickDAO sbfDraftPickDao;
	protected SbfDraftRecordDAO sbfDraftRecordDao;
	protected SbfTeamDAO sbfTeamDao;

	public SbfDraftService(){
		sbfDraftPickDao = new SbfDraftPickDAOMysql();
		sbfDraftRecordDao = new SbfDraftRecordDAOMysql();
		sbfTeamDao = new SbfTeamDAOMysql();
	}
	
//	public synchronized void addSbfDraftPick(SbfDraftPick pick){
//		sbfDraftPickDao.insertSbfDraftPick(pick);
//	}
//	
//	public synchronized void updateSbfDraftPick(SbfDraftPick pick){
//		sbfDraftPickDao.updateSbfDraftPick(pick);
//	}
//
//	public synchronized void deleteSbfDraftPick(SbfDraftPick pick){
//		sbfDraftPickDao.deleteSbfDraftPick(pick);
//	}
//	
//	public synchronized void addSbfDraftRecord(SbfDraftRecord rec){
//		sbfDraftRecordDao.insertDraftRecord(rec);
//	}
//	
//	public synchronized void deleteSbfDraftRecord(SbfDraftRecord rec){
//		sbfDraftRecordDao.deleteDraftRecord(rec);
//	}
//
//	public synchronized void updateSbfDraftRecord(SbfDraftRecord rec){
//		sbfDraftRecordDao.updateDraftRecord(rec);
//	}
	
//	public Response getAllSbfDraftPicks() {
//		//return sbfDraftPickDao.getAllSbfDraftPicks(l);
//		return Response.status(200).entity(sbfDraftPickDao.getAllSbfDraftPicks(l)).build();
//
//	}
	
	
	@GET
	@Path("/draftresults/{i}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SbfDraftRecord> getAllDraftRecords(@PathParam("i") Integer i) {
		return sbfDraftRecordDao.getAllDraftRecords(i);
		//return (ArrayList<SbfDraftRecord>) sbfDraftRecordDao.getAllDraftRecords(1);
	}
	
	@GET
	@Path("/sbfteams/{i}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SbfTeam> getAllSbfTeams(@PathParam("i") Integer i) {
		return sbfTeamDao.getAllTeams(i);
		//return (ArrayList<SbfDraftRecord>) sbfDraftRecordDao.getAllDraftRecords(1);
	}
}
