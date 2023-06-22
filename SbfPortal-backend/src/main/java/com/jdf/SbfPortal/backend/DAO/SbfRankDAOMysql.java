package com.jdf.SbfPortal.backend.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
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

import org.apache.log4j.Logger;

import com.jdf.SbfPortal.backend.data.SbfRank;

public class SbfRankDAOMysql implements SbfRankDAO {

	String jdbcUrl;
	private static Logger logger = Logger.getLogger(SbfRankDAOMysql.class);
	public SbfRankDAOMysql(){
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
	public List<SbfRank> getAllSbfRanks(Integer rankSetId) {
		List<SbfRank> sbfRanks = new ArrayList<SbfRank>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			stmt = conn.createStatement();
			String sql = "select "
					+ "RANK_SET_ID, PLAYER_ID, PLAYER_RANK, TIER, AUCTION_VALUE "
					+ "from SBF_RANKS "
					+ "where RANK_SET_ID = " + rankSetId
					+ " order by PLAYER_RANK asc";

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfRank rank = new SbfRank(rs.getInt("RANK_SET_ID"),
						rs.getInt("PLAYER_ID"),
						rs.getInt("PLAYER_RANK"),
						rs.getInt("TIER"),
						rs.getInt("AUCTION_VALUE"));			
				sbfRanks.add(rank);    				
			}
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
		return sbfRanks;
	}

	public synchronized void insertSbfRank(SbfRank s) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "insert into SBF_RANKS "
					+ "(player_id, rank_set_id, PLAYER_RANK, tier, AUCTION_VALUE) "
					+ "values (?,?,?,?, ?)";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, s.getPlayerId());
			prepStmt.setInt(2, s.getRankSetId());
			prepStmt.setInt(3, s.getRank());	
			prepStmt.setInt(4, s.getTier());
			prepStmt.setInt(5, s.getAuctionValue());
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

	public synchronized void deleteSbfRank(SbfRank s) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "delete from SBF_RANKS "
					+ "where rank_set_id = ? and player_id = ?";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, s.getRankSetId());
			prepStmt.setInt(2, s.getPlayerId());
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

	public synchronized void updateSbfRank(SbfRank rank) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "update SBF_RANKS set "
					+ "PLAYER_RANK = ?, "
					+ "tier = ? ,"
					+ "auction_value = ? "
					+ "where rank_set_id = ? and player_id = ?";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, rank.getRank());
			prepStmt.setInt(2, rank.getTier());
			prepStmt.setInt(3, rank.getAuctionValue());
			prepStmt.setInt(4, rank.getRankSetId());
			prepStmt.setInt(5, rank.getPlayerId());

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

	public synchronized void deleteAllSbfRanks(int userId) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "delete from SBF_RANKS "
					+ "where USER_ID = ?";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, userId);
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
