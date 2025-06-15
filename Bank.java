package com.mycompany.blackjack;

import java.sql.*;

public class Bank {
    private int balance;
    private static final String DB_PATH = "derby-dbs/BlackjackDB";
    private static final String JDBC_URL = "jdbc:derby:" + DB_PATH + ";create=true";
    private static Connection sharedConnection;

    static {
        try {
            DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                shutdownDerby();
            }));
        } catch (SQLException e) {
            System.err.println("Failed to register Derby driver:");
            e.printStackTrace();
        }
    }

    public Bank() {
        initializeDatabase();
        this.balance = readBalance();
    }

    private static void shutdownDerby() {
        try {
            if (sharedConnection != null && !sharedConnection.isClosed()) {
                sharedConnection.close();
            }
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("XJ015")) {
                System.err.println("ERROR: Derby shutdown failed");
                e.printStackTrace();
            }
        }
    }

    private void initializeDatabase() {
        try {
            sharedConnection = DriverManager.getConnection(JDBC_URL);
            createBankTableIfNotExists(sharedConnection);
        } catch (SQLException e) {
            System.err.println("ERROR: Database initialization failed");
            e.printStackTrace();
        }
    }

    private void createBankTableIfNotExists(Connection conn) throws SQLException {
        DatabaseMetaData dbMeta = conn.getMetaData();
        try (ResultSet tables = dbMeta.getTables(null, null, "BANK_DATA", null)) {
            if (!tables.next()) {
                String createTableSQL = "CREATE TABLE BANK_DATA (" +
                        "ID INT PRIMARY KEY, " +
                        "BALANCE INT DEFAULT 100)";
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);
                    String insertSQL = "INSERT INTO BANK_DATA (ID, BALANCE) VALUES (1, 100)";
                    stmt.executeUpdate(insertSQL);
                }
            }
        }
    }

    private int readBalance() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT BALANCE FROM BANK_DATA WHERE ID=1")) {
            if (rs.next()) {
                return rs.getInt("BALANCE");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to read balance");
            e.printStackTrace();
        }
        return 100;
    }

    public int getBalance() {
        return readBalance();
    }

    public void deductBalance(int betSize) {
        int currentBalance = getBalance();
        if (currentBalance > 0) {
            updateBalance(currentBalance - betSize);
        }
    }

    public void increaseBalance(int betSize) {
        updateBalance(getBalance() + betSize);
    }

    private void updateBalance(int newBalance) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE BANK_DATA SET BALANCE=? WHERE ID=1")) {
            pstmt.setInt(1, newBalance);
            pstmt.executeUpdate();
            this.balance = newBalance;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to update balance");
            e.printStackTrace();
        }
    }
}
