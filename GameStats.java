package com.mycompany.blackjack;

import java.sql.*;

// keeps track of player's game statistics (wins, losses, draws)
public class GameStats {
    // tracking variables
    private int wins;
    private int losses;
    private int draws;
    private String username;
    
    // database connection info
    private static final String DB_PATH = "derby-dbs/BlackjackDB";
    private static final String JDBC_URL = "jdbc:derby:" + DB_PATH + ";create=true";

    // creates new stats tracker for a player
    public GameStats(String username) {
        this.username = username;
        initializeDatabase();  // make sure database is ready
        loadStats();  // load existing stats for this player
    }

    // sets up the database connection
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            createTableIfNotExists(conn);  // create table if needed
        } catch (SQLException e) {
            System.err.println("error: database initialization failed");
            e.printStackTrace();
        }
    }

    // creates the stats table if it doesn't exist
    private void createTableIfNotExists(Connection conn) throws SQLException {
        // check if table already exists
        DatabaseMetaData dbMeta = conn.getMetaData();
        try (ResultSet tables = dbMeta.getTables(null, null, "PLAYER_STATS", null)) {
            if (!tables.next()) {  // if table doesn't exist
                String createTableSQL = "CREATE TABLE PLAYER_STATS (" +
                    "USERNAME VARCHAR(50) PRIMARY KEY, " +  // player name as key
                    "WINS INT DEFAULT 0, " +  // total wins
                    "LOSSES INT DEFAULT 0, " +  // total losses
                    "DRAWS INT DEFAULT 0)";  // total draws
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);  // create the table
                }
            }
        }
    }

    // loads existing stats from database
    private void loadStats() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT WINS, LOSSES, DRAWS FROM PLAYER_STATS WHERE USERNAME = ?")) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // if player exists, load their stats
                    wins = rs.getInt("WINS");
                    losses = rs.getInt("LOSSES");
                    draws = rs.getInt("DRAWS");
                } else {
                    // if new player, create empty stats record
                    initializeStats();
                }
            }
        } catch (SQLException e) {
            System.err.println("error: failed to load stats");
            e.printStackTrace();
        }
    }

    // creates new stats record for a player
    private void initializeStats() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO PLAYER_STATS (USERNAME) VALUES (?)")) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();  // create record with default zeros
        } catch (SQLException e) {
            System.err.println("error: failed to initialize stats");
            e.printStackTrace();
        }
    }

    // saves current stats to database
    private void saveStats() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE PLAYER_STATS SET WINS=?, LOSSES=?, DRAWS=? WHERE USERNAME=?")) {
            // update all stats for this player
            pstmt.setInt(1, wins);
            pstmt.setInt(2, losses);
            pstmt.setInt(3, draws);
            pstmt.setString(4, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("error: failed to save stats");
            e.printStackTrace();
        }
    }

    // records a win and saves to database
    public void recordWin() {
        wins++;
        saveStats();
    }

    // records a loss and saves to database
    public void recordLoss() {
        losses++;
        saveStats();
    }

    // records a draw and saves to database
    public void recordDraw() {
        draws++;
        saveStats();
    }

    // returns total wins
    public int getWins() {
        return wins;
    }

    // returns total losses
    public int getLosses() {
        return losses;
    }

    // returns total draws
    public int getDraws() {
        return draws;
    }

    // returns formatted summary of stats
    public String getStatsSummary() {
        return String.format("Wins: %d | Losses: %d | Draws: %d", wins, losses, draws);
    }
}
