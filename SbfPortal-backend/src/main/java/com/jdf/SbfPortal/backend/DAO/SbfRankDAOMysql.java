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
import com.jdf.SbfPortal.backend.data.SbfRank;

public class SbfRankDAOMysql implements SbfRankDAO {

	InitialContext ctx;
	Context envContext;
	DataSource ds;
	public SbfRankDAOMysql(){
		try {
			ctx = new InitialContext();
			envContext  = (Context)ctx.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/MyDB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<SbfRank> getAllSbfRanks(Integer rankSetId) {
		List<SbfRank> sbfRanks = new ArrayList<SbfRank>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			stmt = conn.createStatement();
			String sql = "select "
					+ "RANK_SET_ID, PLAYER_ID, RANK "
					+ "from SBF_RANKS "
					+ "where RANK_SET_ID = " + rankSetId;

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfRank rank = new SbfRank(rs.getInt("RANK_SET_ID"),
						rs.getInt("PLAYER_ID"),
						rs.getInt("RANK"));			
				sbfRanks.add(rank);    				
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
		return sbfRanks;
	}

	public synchronized void insertSbfRank(SbfRank s) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "insert into sbf_ranks "
					+ "(player_id, rank_set_id, rank) "
					+ "values (?,?,?)";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, s.getPlayerId());
			prepStmt.setInt(2, s.getRankSetId());
			prepStmt.setInt(3, s.getRank());	
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

	public synchronized void deleteSbfRank(SbfRank s) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "delete from sbf_ranks "
					+ "where rank_set_id = ? and player_id = ?";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, s.getRankSetId());
			prepStmt.setInt(2, s.getPlayerId());
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

	public synchronized void updateSbfRank(SbfRank rank) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "update sbf_ranks set "
					+ "rank = ? "
					+ "where rank_set_id = ? and player_id = ?";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, rank.getRank());
			prepStmt.setInt(2, rank.getRankSetId());
			prepStmt.setInt(3, rank.getPlayerId());
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

	public synchronized void deleteAllSbfRanks(int userId) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "delete from sbf_ranks "
					+ "where USER_ID = ?";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, userId);
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
