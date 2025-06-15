package com.mycompany.blackjack;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// handles everything money / bank related for the players
public class Bank {
    // user balance and username
    private int balance;
    private String username;
    
    // location of database
    private static final String DB_PATH = "derby-dbs/BlackjackDB";
    private static final String JDBC_URL = "jdbc:derby:" + DB_PATH + ";create=true";
    private static Connection sharedConnection;
    
    // sets up database driver
    static {
        try {
            DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                shutdownDerby();
            }));
        } catch (SQLException e) {
            System.err.println("failed to register derby driver:");
            e.printStackTrace();
        }
    }

    // creates the bank for the player
    public Bank(String username) {
        this.username = username;
        initializeDatabase();
        this.balance = readBalance();
    }
    
    
    // shuts down derby afterwards to avoid multi intances error
    private static void shutdownDerby() {
        try {
            if (sharedConnection != null && !sharedConnection.isClosed()) {
                sharedConnection.close();
            }
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("XJ015")) {
                System.err.println("error: derby shutdown failed");
                e.printStackTrace();
            }
        }
    }
    
    private void initializeDatabase() {
        try {
            sharedConnection = DriverManager.getConnection(JDBC_URL);
            createBankTableIfNotExists(sharedConnection);
        } catch (SQLException e) {
            System.err.println("error: database initialization failed");
            e.printStackTrace();
        }
    }
    
    // creates the table where the data is stored
    private void createBankTableIfNotExists(Connection conn) throws SQLException {
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
            } else {
                addMissingColumns(conn);
            }
        }
    }
    
    // this is here to add the missing columns from the priovus version, since we did not have
    // amount waged and other stats before
    private void addMissingColumns(Connection conn) throws SQLException {
        String[] columns = {"TOTAL_WAGERED", "TOTAL_WON", "TOTAL_LOST", "NET_PROFIT"};
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = conn.getMetaData().getColumns(null, null, "PLAYER_BANKS", null)) {
            
            Set<String> existingColumns = new HashSet<>();
            while (rs.next()) {
                existingColumns.add(rs.getString("COLUMN_NAME").toUpperCase());
            }

            for (String column : columns) {
                if (!existingColumns.contains(column)) {
                    stmt.execute("ALTER TABLE PLAYER_BANKS ADD COLUMN " + column + " INT DEFAULT 0");
                }
            }
        }
    }
    
    // reads the users balance.
    private int readBalance() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT BALANCE FROM PLAYER_BANKS WHERE USERNAME = ?")) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("BALANCE");
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO PLAYER_BANKS (USERNAME) VALUES (?)")) {
                        insertStmt.setString(1, username);
                        insertStmt.executeUpdate();
                    }
                    return 100;
                }
            }
        } catch (SQLException e) {
            System.err.println("error: failed to read balance");
            e.printStackTrace();
        }
        return 100;
    }
    
    // gets a fresh balance everytime its called
    public int getBalance() {
        return readBalance();
    }
    
    // deducts from player balance
    public void deductBalance(int betSize) {
        int currentBalance = getBalance();
        // check balance is bigger than 0
        if (currentBalance > 0) {
            updateBalance(currentBalance - betSize);
        }
    }
    
    // increase the balance by the betsize
    public void increaseBalance(int betSize) {
        updateBalance(getBalance() + betSize);
    }

    private void updateBalance(int newBalance) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE PLAYER_BANKS SET BALANCE = ? WHERE USERNAME = ?")) {
            pstmt.setInt(1, newBalance);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            this.balance = newBalance;
        } catch (SQLException e) {
            System.err.println("error: failed to update balance");
            e.printStackTrace();
        }
    }
    
    
    // these 3 methods records  data into the DB
    public void recordWager(int amount) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE PLAYER_BANKS SET TOTAL_WAGERED = TOTAL_WAGERED + ? WHERE USERNAME = ?")) {
            pstmt.setInt(1, amount);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("error: failed to record wager");
            e.printStackTrace();
        }
    }

    public void recordWin(int amount) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE PLAYER_BANKS SET TOTAL_WON = TOTAL_WON + ?, NET_PROFIT = NET_PROFIT + ? WHERE USERNAME = ?")) {
            pstmt.setInt(1, amount);
            pstmt.setInt(2, amount);
            pstmt.setString(3, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("error: failed to record win");
            e.printStackTrace();
        }
    }

    public void recordLoss(int amount) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE PLAYER_BANKS SET TOTAL_LOST = TOTAL_LOST + ?, NET_PROFIT = NET_PROFIT - ? WHERE USERNAME = ?")) {
            pstmt.setInt(1, amount);
            pstmt.setInt(2, amount);
            pstmt.setString(3, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("error: failed to record loss");
            e.printStackTrace();
        }
    }
    
    // get players stats
    public Map<String, Integer> getFinancialStats() {
        Map<String, Integer> stats = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT TOTAL_WAGERED, TOTAL_WON, TOTAL_LOST, NET_PROFIT FROM PLAYER_BANKS WHERE USERNAME = ?")) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("wagered", rs.getInt("TOTAL_WAGERED"));
                    stats.put("won", rs.getInt("TOTAL_WON"));
                    stats.put("lost", rs.getInt("TOTAL_LOST"));
                    stats.put("net", rs.getInt("NET_PROFIT"));
                }
            }
        } catch (SQLException e) {
            System.err.println("error: failed to get financial stats");
            e.printStackTrace();
        }
        return stats;
    }
}
