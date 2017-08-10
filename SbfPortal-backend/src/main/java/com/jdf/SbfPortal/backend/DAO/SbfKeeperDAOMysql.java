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

import com.jdf.SbfPortal.backend.data.SbfKeeper;

public class SbfKeeperDAOMysql implements SbfKeeperDAO {

	InitialContext ctx;
	Context envContext;
	DataSource ds;
	public SbfKeeperDAOMysql(){
		try {
			ctx = new InitialContext();
			envContext  = (Context)ctx.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/MyDB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized List<SbfKeeper> getAllSbfKeepers(int leagueId) {
		List<SbfKeeper> SbfKeepers = new ArrayList<SbfKeeper>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			String sql = "select "
					+ "LEAGUE_ID, SBF_ID, PLAYER_ID, ROUND "
					+ "from SBF_KEEPERS where league_id = " + leagueId;

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfKeeper keeper = new SbfKeeper(rs.getInt("LEAGUE_ID"),
						rs.getInt("SBF_ID"),
						rs.getInt("PLAYER_ID"),
						rs.getInt("ROUND")
						);
				SbfKeepers.add(keeper);    				
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


		return SbfKeepers;
	}

	public void insertSbfKeeper(SbfKeeper r) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "insert into sbf_keepers "
					+ "(SBF_ID, LEAGUE_ID, PLAYER_ID, ROUND) "
					+ "values (?,?,?,?)";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, r.getSbfId());
			prepStmt.setInt(2, r.getLeagueId());
			prepStmt.setInt(3, r.getPlayerId());
			prepStmt.setInt(4, r.getRound());
			
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

	public void updateSbfKeeper(SbfKeeper r) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			
			String sql = "update sbf_keepers "
					+ "set "
					+ "SBF_ID=?, "
					+ "ROUND=? "
					+ "where league_id = ? and player_id = ?";
			prepStmt = conn.prepareStatement(sql);


			prepStmt.setInt(1, r.getSbfId());
			prepStmt.setInt(2, r.getRound());
			prepStmt.setInt(3, r.getLeagueId());
			prepStmt.setInt(4, r.getPlayerId());
			prepStmt.executeUpdate();
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

	public void deleteSbfKeeper(SbfKeeper r) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "delete from sbf_keepers where "
					+ "LEAGUE_ID = ? and "
					+ "PLAYER_ID = ? ";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1,r.getLeagueId());
			prepStmt.setInt(2,r.getPlayerId());
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
