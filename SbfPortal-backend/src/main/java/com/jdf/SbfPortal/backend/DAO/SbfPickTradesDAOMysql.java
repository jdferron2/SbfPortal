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

import com.jdf.SbfPortal.backend.data.SbfPickTrade;

public class SbfPickTradesDAOMysql implements SbfPickTradesDAO {

	InitialContext ctx;
	Context envContext;
	DataSource ds;
	public SbfPickTradesDAOMysql(){
		try {
			ctx = new InitialContext();
			envContext  = (Context)ctx.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/MyDB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized List<SbfPickTrade> getAllSbfPickTrades(Integer leagueId) {
		List<SbfPickTrade> sbfPickTrades = new ArrayList<SbfPickTrade>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			stmt = conn.createStatement();
			String sql = "select "
					+ "LEAGUE_ID, FROM_TEAM_ID, TO_TEAM_ID, PICK_NUM, PROCESSED_TS "
					+ "from SBF_PICK_TRADES "
					+ "where LEAGUE_ID = " + leagueId;

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfPickTrade pick = new SbfPickTrade(rs.getInt("LEAGUE_ID"),
						rs.getInt("FROM_TEAM_ID"),
						rs.getInt("TO_TEAM_ID"),
						rs.getInt("PICK_NUM"),
						rs.getTimestamp("PROCESSED_TS")
						);
				sbfPickTrades.add(pick);    				
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
		return sbfPickTrades;
	}

	public synchronized void insertSbfPickTrade(SbfPickTrade p) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "insert into sbf_pick_trades "
					+ "(FROM_TEAM_ID, TO_TEAM_ID, LEAGUE_ID, PICK_NUM, PROCESSED_TS) "
					+ "values (?,?,?,?,?)";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, p.getFromTeamId());
			prepStmt.setInt(2, p.getToTeamId());
			prepStmt.setInt(3, p.getLeagueId());
			prepStmt.setInt(4, p.getPick());
			Timestamp processedTs = p.getProcessedTs();
			if(processedTs == null){
				processedTs = new Timestamp(System.currentTimeMillis());
				p.setProcessedTs(processedTs);
			}
			prepStmt.setTimestamp(5, processedTs);
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

	public synchronized void updateSbfPickTrade(SbfPickTrade p) {
//		PreparedStatement prepStmt=null;
//		Connection conn = null;
//		try {
//			conn = ds.getConnection();
//
//			String sql = "update sbf_pick_trades set "
//					+ "FROM_TEAM_ID "
//					+ "=?, "
//					+ "TO_TEAM_ID = ?";
//			prepStmt = conn.prepareStatement(sql);
//
//			prepStmt.setInt(1, p.getFromTeamId());
//			prepStmt.setInt(2, p.getToTeamId());
//			prepStmt.execute();
//		} catch (Exception ex) {
//			System.out.println(ex.getMessage());
//			// handle the error
//		}
//		finally {
//			if (conn != null) {
//				try {
//					conn.close();
//				} catch (SQLException sqlEx) { } // ignore
//
//				conn = null;
//			}
//
//		}	
	}

	public void deleteSbfPickTrade(SbfPickTrade a) {
		//manual process for now...
//		PreparedStatement prepStmt=null;
//		Connection conn = null;
//		try {
////			conn = ds.getConnection();
////
////			String sql = "delete from sbf_pick_trades where "
////					+ "PICK_NUM = ? and "
////					+ "LEAGUE_ID = ?";
////			prepStmt = conn.prepareStatement(sql);
////
////			prepStmt.setInt(1,a.getTeamId());
////			prepStmt.setInt(2,a.getPick());
////			prepStmt.setInt(3,a.getLeagueId());
////			prepStmt.execute();
//		} catch (Exception ex) {
//			System.out.println(ex.getMessage());
//			// handle the error
//		}
//		finally {
//			if (conn != null) {
//				try {
//					conn.close();
//				} catch (SQLException sqlEx) { } // ignore
//
//				conn = null;
//			}
//
//		}	

	}
}
