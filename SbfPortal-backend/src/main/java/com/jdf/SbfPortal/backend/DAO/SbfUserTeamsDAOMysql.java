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

import org.apache.log4j.Logger;

import com.jdf.SbfPortal.backend.data.SbfUserTeam;

public class SbfUserTeamsDAOMysql implements SbfUserTeamsDAO{
	InitialContext ctx;
	Context envContext;
	DataSource ds;
	private static Logger logger = Logger.getLogger(SbfUserTeamsDAOMysql.class);
	public SbfUserTeamsDAOMysql(){
		try {
			ctx = new InitialContext();
			envContext  = (Context)ctx.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/MyDB");
		} catch (NamingException e) {
			logger.error("Stack Trace: ", e);
		}
	}

	@Override
	public synchronized List<SbfUserTeam> getAllSBfUserTeams() {

		List<SbfUserTeam> sbfUserTeams = new ArrayList<SbfUserTeam>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			stmt = conn.createStatement();
			String sql = "select "
					+ "USER_ID, LEAGUE_ID, TEAM_ID, DEFAULT_RANK_SET_ID "
					+ "from SBF_USER_TEAMS ";

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfUserTeam team = new SbfUserTeam(rs.getInt("LEAGUE_ID"),
						rs.getInt("TEAM_ID"),
						rs.getInt("USER_ID"),
						rs.getInt("DEFAULT_RANK_SET_ID")
						
						);
				sbfUserTeams.add(team);    				
			}
		} catch (Exception ex) {
			logger.error("Stack Trace: ", ex);
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqlEx) { } // ignore

				conn = null;
			}

		}	
		return sbfUserTeams;
	}

	@Override
	public void insertSbfUserTeam(SbfUserTeam t) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "insert into sbf_user_teams "
					+ "(USER_ID, TEAM_ID, LEAGUE_ID, DEFAULT_RANK_SET_ID) "
					+ "values (?,?,?,?)";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, t.getUserId());
			prepStmt.setInt(2, t.getTeamId());
			prepStmt.setInt(3, t.getLeagueId());
			prepStmt.setInt(4, t.getDefaultRankSetId());
			prepStmt.execute();
		} catch (Exception ex) {
			logger.error("Stack Trace: ", ex);
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

	@Override
	public void updateSbfUserTeam(SbfUserTeam t) {
				PreparedStatement prepStmt=null;
				Connection conn = null;
				try {
					conn = ds.getConnection();
		
					String sql = "update sbf_user_teams "
							+ "set "
							+ "TEAM_ID=?, "
							+ "DEFAULT_RANK_SET_ID=? "
							+ "where USER_ID = ? and league_id = ?";
					prepStmt = conn.prepareStatement(sql);
					prepStmt.setInt(1, t.getTeamId());
					prepStmt.setInt(2, t.getDefaultRankSetId());
					prepStmt.setInt(3, t.getUserId());
					prepStmt.setInt(4, t.getLeagueId());
					prepStmt.execute();
				} catch (Exception ex) {
					logger.error("Stack Trace: ", ex);
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

	@Override
	public void deleteSbfUserTeam(SbfUserTeam t) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "delete from sbf_user_teams "
					+ "where USER_ID = ? and team_id = ? and league_id = ? ";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, t.getUserId());
			prepStmt.setInt(2, t.getTeamId());
			prepStmt.setInt(3, t.getLeagueId());
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