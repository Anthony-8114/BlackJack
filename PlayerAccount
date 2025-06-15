package com.mycompany.blackjack;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

// handles player accounts and authentication
public class PlayerAccount {
    private String username;
    private String password;
    private Bank bank;
    private static final String DB_PATH = "derby-dbs/BlackjackDB";
    private static final String JDBC_URL = "jdbc:derby:" + DB_PATH + ";create=true";

    // creates new player account
    public PlayerAccount(String username, String password) {
        this.username = username;
        this.password = password;
        this.bank = new Bank(username);
        initializeDatabase();
    }

    // sets up database tables if needed
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            createPlayerTableIfNotExists(conn);
            createPlayerBankTableIfNotExists(conn);
            createPlayerStatsTableIfNotExists(conn);
        } catch (SQLException e) {
            System.err.println("error: database initialization failed");
            e.printStackTrace();
        }
    }

    // creates accounts table if missing
    private void createPlayerTableIfNotExists(Connection conn) throws SQLException {
        DatabaseMetaData dbMeta = conn.getMetaData();
        try (ResultSet tables = dbMeta.getTables(null, null, "PLAYER_ACCOUNTS", null)) {
            if (!tables.next()) {
                String createTableSQL = "CREATE TABLE PLAYER_ACCOUNTS (" +
                    "USERNAME VARCHAR(50) PRIMARY KEY, " +
                    "PASSWORD VARCHAR(50) NOT NULL)";
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);
                }
            }
        }
    }

    // creates bank table if missing
    private void createPlayerBankTableIfNotExists(Connection conn) throws SQLException {
        DatabaseMetaData dbMeta = conn.getMetaData();
        try (ResultSet tables = dbMeta.getTables(null, null, "PLAYER_BANKS", null)) {
            if (!tables.next()) {
                String createTableSQL = "CREATE TABLE PLAYER_BANKS (" +
                    "USERNAME VARCHAR(50) PRIMARY KEY, " +
                    "BALANCE INT DEFAULT 100, " +
                    "TOTAL_WAGERED INT DEFAULT 0, " +
                    "TOTAL_WON INT DEFAULT 0, " +
                    "TOTAL_LOST INT DEFAULT 0, " +
                    "NET_PROFIT INT DEFAULT 0)";
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);
                }
            }
        }
    }

    // creates stats table if missing
    private void createPlayerStatsTableIfNotExists(Connection conn) throws SQLException {
        DatabaseMetaData dbMeta = conn.getMetaData();
        try (ResultSet tables = dbMeta.getTables(null, null, "PLAYER_STATS", null)) {
            if (!tables.next()) {
                String createTableSQL = "CREATE TABLE PLAYER_STATS (" +
                    "USERNAME VARCHAR(50) PRIMARY KEY, " +
                    "WINS INT DEFAULT 0, " +
                    "LOSSES INT DEFAULT 0, " +
                    "DRAWS INT DEFAULT 0)";
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);
                }
            }
        }
    }

    // registers new player account
    public boolean register() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            // check if username taken
            if (usernameExists(conn)) {
                System.err.println("error: username already exists");
                return false;
            }

            conn.setAutoCommit(false);

            try {
                // add to accounts table
                try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO PLAYER_ACCOUNTS (USERNAME, PASSWORD) VALUES (?, ?)")) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    pstmt.executeUpdate();
                }

                // add bank account with default balance
                try {
                    try (PreparedStatement bankStmt = conn.prepareStatement(
                        "INSERT INTO PLAYER_BANKS (USERNAME, BALANCE) VALUES (?, ?)")) {
                        bankStmt.setString(1, username);
                        bankStmt.setInt(2, 100);
                        bankStmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    // update if exists
                    try (PreparedStatement bankStmt = conn.prepareStatement(
                        "UPDATE PLAYER_BANKS SET BALANCE = ? WHERE USERNAME = ?")) {
                        bankStmt.setInt(1, 100);
                        bankStmt.setString(2, username);
                        bankStmt.executeUpdate();
                    }
                }

                // add stats record
                try {
                    try (PreparedStatement statsStmt = conn.prepareStatement(
                        "INSERT INTO PLAYER_STATS (USERNAME) VALUES (?)")) {
                        statsStmt.setString(1, username);
                        statsStmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    // reset stats if exists
                    try (PreparedStatement statsStmt = conn.prepareStatement(
                        "UPDATE PLAYER_STATS SET WINS = 0, LOSSES = 0, DRAWS = 0 WHERE USERNAME = ?")) {
                        statsStmt.setString(1, username);
                        statsStmt.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("error: failed to register player");
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("error: database connection failed");
            e.printStackTrace();
            return false;
        }
    }
    
    // checks if username exists
    private boolean usernameExists(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
            "SELECT USERNAME FROM PLAYER_ACCOUNTS WHERE USERNAME = ?")) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // verifies login credentials
    public boolean login() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT PASSWORD FROM PLAYER_ACCOUNTS WHERE USERNAME = ?")) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("PASSWORD").equals(password);
                }
            }
        } catch (SQLException e) {
            System.err.println("error: failed to login");
            e.printStackTrace();
        }
        return false;
    }

    // gets player's bank account
    public Bank getBank() {
        return bank;
    }

    // gets player's game stats
    public GameStats getGameStats() {
        return new GameStats(username);
    }

    // gets username
    public String getUsername() {
        return username;
    }

    // gets stats for all players
    public static Map<String, Map<String, Integer>> getAllPlayerStats() {
        Map<String, Map<String, Integer>> stats = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT USERNAME, WINS, LOSSES, DRAWS FROM PLAYER_STATS")) {
            while (rs.next()) {
                Map<String, Integer> playerStats = new HashMap<>();
                playerStats.put("wins", rs.getInt("WINS"));
                playerStats.put("losses", rs.getInt("LOSSES"));
                playerStats.put("draws", rs.getInt("DRAWS"));
                stats.put(rs.getString("USERNAME"), playerStats);
            }
        } catch (SQLException e) {
            System.err.println("error: failed to get player stats");
            e.printStackTrace();
        }
        return stats;
    }
}
