package com.jdf.SbfPortal.backend.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
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

import org.apache.log4j.Logger;

import com.jdf.SbfPortal.backend.data.SbfPickTrade;

public class SbfPickTradesDAOMysql implements SbfPickTradesDAO {

	String jdbcUrl;
	private static Logger logger = Logger.getLogger(SbfPickTradesDAOMysql.class);
	public SbfPickTradesDAOMysql(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dbName = System.getProperty("RDS_DB_NAME");
			String userName = System.getProperty("RDS_USERNAME");
			String password = System.getProperty("RDS_PASSWORD");
			String hostname = System.getProperty("RDS_HOSTNAME");
			String port = System.getProperty("RDS_PORT");
			jdbcUrl = "jdbc:mysql://" + hostname + ":" +
					port + "/" + dbName + "?user=" + userName + "&password=" + password;
			logger.trace("Building connection string from environment variables.");
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFound in..." , e);
		} 
	}
	public synchronized List<SbfPickTrade> getAllSbfPickTrades(Integer leagueId) {
		List<SbfPickTrade> sbfPickTrades = new ArrayList<SbfPickTrade>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

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
			logger.error("Stack Trace: ",  ex);
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
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "insert into SBF_PICK_TRADES "
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
			logger.error("Stack Trace: ",  ex);
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
//			conn = DriverManager.getConnection(jdbcUrl);
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

	public void deleteSbfPickTrade(SbfPickTrade t) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "delete from SBF_PICK_TRADES where "
					+ "PICK_NUM = ? and "
					+ "LEAGUE_ID = ? and "
					+ "TO_TEAM_ID = ? and "
					+ "FROM_TEAM_ID = ?";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1,t.getPick());
			prepStmt.setInt(2,t.getLeagueId());
			prepStmt.setInt(3,t.getToTeamId());
			prepStmt.setInt(4,t.getFromTeamId());
			prepStmt.execute();
		} catch (Exception ex) {
			logger.error("Stack Trace: ",  ex);
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
