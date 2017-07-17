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

import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.utility.PropertyReader;

public class PlayerDAOMysql implements PlayerDAO {
	InitialContext ctx;
	Context envContext;
	DataSource ds;
	public PlayerDAOMysql(){
		try {
			ctx = new InitialContext();
			envContext  = (Context)ctx.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/MyDB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<Player> getAllPlayers() {
		List<Player> players = new ArrayList<Player>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			String sql = "select "
					+ "player_id, pro_rank, full_name, first_name, last_name, height, jersey_num, position, nfl_team, weight, DOB "
					+ "from players";

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

		return players;
	}

	public void insertPlayer(Player p) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			conn = DriverManager.getConnection("jdbc:mysql://localhost/SBF","root",PropertyReader.getProperty("adminPass"));
			//stmt = conn.createStatement();

			String sql = "insert into players "
					+ "(player_id, pro_rank, full_name, first_name, last_name, height, jersey_num, position, nfl_team, weight, DOB) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?)";
			prepStmt = conn.prepareStatement(sql);

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

	public void deletePlayer(Player p) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			//stmt = conn.createStatement();

			String sql = "delete from players "
					+ "where player_id = ?";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, p.getPlayerId());
			prepStmt.execute();
			//getAllPlayers().remove(p);
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

	public void deleteAllPlayers() {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "delete from players";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.execute();
			//players.clear();
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
	public void updatePlayer(Player p) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			
			String sql = "update players "
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
