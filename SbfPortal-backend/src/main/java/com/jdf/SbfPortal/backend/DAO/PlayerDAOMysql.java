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

import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.utility.PropertyReader;

public class PlayerDAOMysql implements PlayerDAO {
	String jdbcUrl;
	private static Logger logger = Logger.getLogger(PlayerDAOMysql.class);
	
	String INSERT_SQL = "insert into PLAYERS "
			+ "(player_id, pro_rank, full_name, first_name, last_name, height, jersey_num, position, nfl_team, weight, DOB) "
			+ "values (?,?,?,?,?,?,?,?,?,?,?)";
	
	String DELETE_SQL = "delete from PLAYERS "
			+ "where player_id = ?";
	
	public PlayerDAOMysql(){
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
	public List<Player> getAllPlayers() {
		List<Player> players = new ArrayList<Player>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);
			stmt = conn.createStatement();
			String sql = "select "
					+ "player_id, pro_rank, full_name, first_name, last_name, height, jersey_num, position, nfl_team, weight, DOB "
					+ "from PLAYERS";

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				Player player = new Player(rs.getInt("player_id"),
						rs.getInt("jersey_num"),
						rs.getString("last_name"),
						rs.getString("first_name"),
						rs.getString("full_name"),
						rs.getString("nfl_team"),
						rs.getString("position"),
						rs.getString("height"),
						rs.getInt("weight"),
						rs.getDate("DOB"),
						rs.getInt("pro_rank")
						);
				players.add(player);    				
			}
		} catch (Exception ex) {
			logger.error("Error in getAllPlayers() call: " + ex.getMessage());
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

		return players;
	}

	public void insertPlayer(Player p) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);
			
			prepStmt = conn.prepareStatement(INSERT_SQL);

			prepStmt.setInt(1, p.getPlayerId());
			prepStmt.setInt(2, p.getProRank());
			prepStmt.setString(3, p.getDisplayName());
			prepStmt.setString(4, p.getFname());
			prepStmt.setString(5, p.getLname());
			prepStmt.setString(6, p.getHeight());
			prepStmt.setInt(7, p.getJersey());	
			prepStmt.setString(8, p.getPosition());
			prepStmt.setString(9, p.getTeam());
			prepStmt.setInt(10, p.getWeight());	
			prepStmt.setDate(11, p.getDob());
			prepStmt.execute();
			//			getAllPlayers().add(p);
		} catch (Exception ex) {
			logger.error("Error in insertPlayer() call: " + ex.getMessage());
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

	public void insertPlayersBatch(List<Player> playerList) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);
			
			prepStmt = conn.prepareStatement(INSERT_SQL);
			for(Player p : playerList) {
				prepStmt.setInt(1, p.getPlayerId());
				prepStmt.setInt(2, p.getProRank());
				prepStmt.setString(3, p.getDisplayName());
				prepStmt.setString(4, p.getFname());
				prepStmt.setString(5, p.getLname());
				prepStmt.setString(6, p.getHeight());
				prepStmt.setInt(7, p.getJersey());	
				prepStmt.setString(8, p.getPosition());
				prepStmt.setString(9, p.getTeam());
				prepStmt.setInt(10, p.getWeight());	
				prepStmt.setDate(11, p.getDob());
				prepStmt.addBatch();
			}
			prepStmt.executeBatch();
		} catch (Exception ex) {
			logger.error("Error in insertPlayer() call: " + ex.getMessage());
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
	public void deletePlayer(Player p) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);
			//stmt = conn.createStatement();

			
			prepStmt = conn.prepareStatement(DELETE_SQL);

			prepStmt.setInt(1, p.getPlayerId());
			prepStmt.execute();
			//getAllPlayers().remove(p);
		} catch (Exception ex) {
			logger.error("Error in deletePlayer() call: " + ex.getMessage());
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
	
	public void deletePlayersBatch(List<Player> players) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);
			//stmt = conn.createStatement();

			
			prepStmt = conn.prepareStatement(DELETE_SQL);
			
			for(Player p : players) {
				prepStmt.setInt(1, p.getPlayerId());
				prepStmt.addBatch();
			}
			prepStmt.executeBatch();
			//getAllPlayers().remove(p);
		} catch (Exception ex) {
			logger.error("Error in deletePlayer() call: " + ex.getMessage());
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

	public void deleteAllPlayers() {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "delete from PLAYERS";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.execute();
			//players.clear();
		} catch (Exception ex) {
			logger.error("Error in deleteAllPlayers() call: " + ex.getMessage());
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
	public void updatePlayer(Player p) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "update PLAYERS "
					+ "set "
					+ "pro_rank=?, "
					+ "full_name=?, "
					+ "first_name=?, "
					+ "last_name=?, "
					+ "height=?, "
					+ "jersey_num=?, "
					+ "position=?, "
					+ "nfl_team=?, "
					+ "weight=?,"
					+ "DOB=? "
					+ "where player_id = ? ";
			prepStmt = conn.prepareStatement(sql);


			prepStmt.setInt(1, p.getProRank());
			prepStmt.setString(2, p.getDisplayName());
			prepStmt.setString(3, p.getFname());
			prepStmt.setString(4, p.getLname());
			prepStmt.setString(5, p.getHeight());
			prepStmt.setInt(6, p.getJersey());	
			prepStmt.setString(7, p.getPosition());
			prepStmt.setString(8, p.getTeam());
			prepStmt.setInt(9, p.getWeight());	
			prepStmt.setDate(10, p.getDob());
			prepStmt.setInt(11, p.getPlayerId());
			prepStmt.executeUpdate();
		} catch (Exception ex) {
			logger.error("Error in updatePlayer() call: " + ex.getMessage());
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
}
