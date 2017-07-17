package com.jdf.SbfPortal.backend.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.jdf.SbfPortal.backend.data.SbfDraftPick;

public class SbfDraftPickDAOMysql implements SbfDraftPickDAO {

	InitialContext ctx;
	Context envContext;
	DataSource ds;
	public SbfDraftPickDAOMysql(){
		try {
			ctx = new InitialContext();
			envContext  = (Context)ctx.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/MyDB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized List<SbfDraftPick> getAllSbfDraftPicks(Integer leagueId) {
		List<SbfDraftPick> sbfDraftPicks = new ArrayList<SbfDraftPick>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			stmt = conn.createStatement();
			String sql = "select "
					+ "LEAGUE_ID, SBF_ID, PICK_NUM "
					+ "from SBF_DRAFT_PICKS "
					+ "where LEAGUE_ID = " + leagueId;

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfDraftPick pick = new SbfDraftPick(rs.getInt("LEAGUE_ID"),
						rs.getInt("SBF_ID"),
						rs.getInt("PICK_NUM")
						);
				sbfDraftPicks.add(pick);    				
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
		return sbfDraftPicks;
	}

	public synchronized void insertSbfDraftPick(SbfDraftPick p) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "insert into sbf_draft_picks "
					+ "(SBF_ID, LEAGUE_ID, PICK_NUM) "
					+ "values (?,?,?)";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, p.getSbfId());
			prepStmt.setInt(2, p.getLeagueId());
			prepStmt.setInt(3, p.getPick());
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

	public synchronized void updateSbfDraftPick(SbfDraftPick p) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "update sbf_draft_picks set "
					+ "SBF_ID "
					+ "=?";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, p.getSbfId());
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

	public void deleteSbfDraftPick(SbfDraftPick a) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "delete from sbf_draft_picks where "
					+ "SBF_ID = ? and "
					+ "PICK_NUM = ? and "
					+ "LEAGUE_ID = ?";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1,a.getSbfId());
			prepStmt.setInt(2,a.getPick());
			prepStmt.setInt(3,a.getLeagueId());
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
}
