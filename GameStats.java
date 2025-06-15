package com.mycompany.blackjack;

import java.sql.*;

public class GameStats {
    private int wins;
    private int losses;
    private int draws;
    private static Connection sharedConnection;

    private static final String DB_PATH = "derby-dbs/BlackjackDB";
    private static final String JDBC_URL = "jdbc:derby:" + DB_PATH + ";create=true";

    public GameStats() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            sharedConnection = DriverManager.getConnection(JDBC_URL);
            createTableIfNotExists(sharedConnection);
            loadStats(sharedConnection);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("ERROR: Database initialization failed.");
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists(Connection conn) throws SQLException {
        DatabaseMetaData dbMeta = conn.getMetaData();
        try (ResultSet tables = dbMeta.getTables(null, null, "GAME_STATS", null)) {
            if (!tables.next()) {
                String createTableSQL = "CREATE TABLE GAME_STATS (" +
                        "ID INT PRIMARY KEY, " +
                        "WINS INT DEFAULT 0, " +
                        "LOSSES INT DEFAULT 0, " +
                        "DRAWS INT DEFAULT 0)";
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);
                    System.out.println("GAME_STATS table created successfully.");
                }
            }
        }
    }

    private void loadStats(Connection conn) throws SQLException {
        String selectSQL = "SELECT WINS, LOSSES, DRAWS FROM GAME_STATS WHERE ID=1";
        try (PreparedStatement pstmt = conn.prepareStatement(selectSQL);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                wins = rs.getInt("WINS");
                losses = rs.getInt("LOSSES");
                draws = rs.getInt("DRAWS");
                System.out.println("Loaded existing stats: W=" + wins + " L=" + losses + " D=" + draws);
            } else {
                initializeStats(conn);
            }
        }
    }

    private void initializeStats(Connection conn) throws SQLException {
        String insertSQL = "INSERT INTO GAME_STATS (ID) VALUES (1)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.executeUpdate();
        }
        wins = 0;
        losses = 0;
        draws = 0;
        System.out.println("Initialized new stats record.");
    }

    private void saveStats() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            String updateSQL = "UPDATE GAME_STATS SET WINS=?, LOSSES=?, DRAWS=? WHERE ID=1";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setInt(1, wins);
                pstmt.setInt(2, losses);
                pstmt.setInt(3, draws);
                pstmt.executeUpdate();
                System.out.println("Stats saved: W=" + wins + " L=" + losses + " D=" + draws);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to save stats");
            e.printStackTrace();
        }
    }

    public void recordWin() {
        wins++;
        saveStats();
    }

    public void recordLoss() {
        losses++;
        saveStats();
    }

    public void recordDraw() {
        draws++;
        saveStats();
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getDraws() {
        return draws;
    }

    public String getStatsSummary() {
        return String.format("Wins: %d | Losses: %d | Draws: %d", wins, losses, draws);
    }
}
