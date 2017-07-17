package com.jdf.SbfPortal.backend.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.jdf.SbfPortal.backend.data.SbfDraftRecord;


public class SbfDraftRecordDAOMysql implements SbfDraftRecordDAO {

	InitialContext ctx;
	Context envContext;
	DataSource ds;
	public SbfDraftRecordDAOMysql(){
		try {
			ctx = new InitialContext();
			envContext  = (Context)ctx.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/MyDB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public synchronized List<SbfDraftRecord> getAllDraftRecords(Integer leagueId) {
		ArrayList<SbfDraftRecord> sbfDraftRecords = new ArrayList<SbfDraftRecord>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			stmt = conn.createStatement();
			String sql = "select "
					+ "SBF_ID, PLAYER_ID, SLOT_DRAFTED, DRAFTED_TS "
					+ "from SBF_DRAFT where LEAGUE_ID = " + leagueId;

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfDraftRecord drafRecord = new SbfDraftRecord(leagueId, 
						rs.getInt("SBF_ID"),
						rs.getInt("PLAYER_ID"),
						rs.getInt("SLOT_DRAFTED"),
						rs.getTimestamp("DRAFTED_TS")
						);
				sbfDraftRecords.add(drafRecord);    				
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			// handle the error
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqlEx) { } // ignore

				conn = null;
			}

		}	

		return sbfDraftRecords;
	}

	public synchronized void insertDraftRecord(SbfDraftRecord record) {
		Connection conn = null;
		PreparedStatement prepStmt;
		try {
			conn = ds.getConnection();

			String sql = "insert into SBF_DRAFT (LEAGUE_ID, SBF_ID, PLAYER_ID, SLOT_DRAFTED, DRAFTED_TS) "
					+ "values (?,?,?,?,?)";

			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, record.getLeagueId());
			prepStmt.setInt(2, record.getSbfId());
			prepStmt.setInt(3, record.getPlayerId());
			prepStmt.setInt(4, record.getSlotDrafted());
			prepStmt.setTimestamp(5, new Timestamp(record.getTimeDrafted().getTime()));
			prepStmt.execute();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			// handle the error
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqlEx) { } // ignore

				conn = null;
			}

		}	
	}

	public synchronized void deleteDraftRecord(SbfDraftRecord record) {
		//Integer leagueId = (Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_ID);
		Connection conn = null;
		PreparedStatement prepStmt;
		try {
			conn = ds.getConnection();

			String sql = "DELETE FROM SBF_DRAFT "
					+ "WHERE SBF_ID = ? and PLAYER_ID = ? and LEAGUE_ID = ?";

			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, record.getSbfId());
			prepStmt.setInt(2, record.getPlayerId());
			prepStmt.setInt(3, record.getLeagueId());
			prepStmt.execute();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			// handle the error
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqlEx) { } // ignore

				conn = null;
			}

		}	
	}

	public synchronized void updateDraftRecord(SbfDraftRecord record) {
		//I'm not sure it makes sense to do any updats on draft record
	}
}
