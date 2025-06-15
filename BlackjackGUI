package com.mycompany.blackjack;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.Map;

// the main game window that handles all the visual parts of blackjack
public class BlackjackGUI {
    private JFrame frame;
    private JPanel panel, cardPanel, houseCardPanel;
    private JButton hitButton, standButton, doubleButton, newGameButton, statsButton;
    private JLabel playerScoreLabel, houseScoreLabel, resultLabel, balanceLabel, usernameLabel;
    
    private GameLogic game;
    private GameStats gameStats;
    private Bank bank;
    private int currentBet;  // how much is being bet in current round
    private ImageIcon cardBackImage;  // image for face-down cards
    private PlayerAccount playerAccount;
    
    // runs when class is loaded - sets up database shutdown hook
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch (SQLException e) {
                if (!e.getSQLState().equals("XJ015")) {
                    e.printStackTrace();
                }
            }
        }));
    }

    // creates the game window for a player
    public BlackjackGUI(PlayerAccount account) {
        this.playerAccount = account;
        this.bank = account.getBank();
        this.gameStats = account.getGameStats();

        // try to load the card back image
        try {
            String backImagePath = "src/main/java/com/mycompany/blackjack/cards/back.png";
            File backImageFile = new File(backImagePath);
            Image backImage = ImageIO.read(backImageFile);
            backImage = backImage.getScaledInstance(80, 120, Image.SCALE_SMOOTH);
            cardBackImage = new ImageIcon(backImage);
        } catch (Exception e) {
            System.out.println("Error loading card back image: " + e.getMessage());
            cardBackImage = createDefaultCardImage();  // use default if image fails
        }

        // set up the main window
        frame = new JFrame("Blackjack - " + account.getUsername());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);

        // main panel with green background
        panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 100, 0));  // dark green
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // top panel with player info and scores
        JPanel topPanel = new JPanel(new GridLayout(1, 4));
        topPanel.setOpaque(false);
        
        // labels showing player info
        usernameLabel = new JLabel(account.getUsername());
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        balanceLabel = new JLabel("Balance: $" + bank.getBalance());
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        playerScoreLabel = new JLabel("Your score: 0");
        playerScoreLabel.setForeground(Color.WHITE);
        playerScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));

        houseScoreLabel = new JLabel("House score: 0");
        houseScoreLabel.setForeground(Color.WHITE);
        houseScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // add all labels to top panel
        topPanel.add(usernameLabel);
        topPanel.add(houseScoreLabel);
        topPanel.add(balanceLabel);
        topPanel.add(playerScoreLabel);

        // panel for dealer's cards
        houseCardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, -20, 5));
        houseCardPanel.setOpaque(false);
        houseCardPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "House Hand"));
        ((javax.swing.border.TitledBorder)houseCardPanel.getBorder()).setTitleColor(Color.WHITE);
        houseCardPanel.setPreferredSize(new Dimension(800, 180));

        // panel for player's cards
        cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, -20, 5));
        cardPanel.setOpaque(false);
        cardPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Your Hand"));
        ((javax.swing.border.TitledBorder)cardPanel.getBorder()).setTitleColor(Color.WHITE);
        cardPanel.setPreferredSize(new Dimension(800, 180));

        // label for showing win/lose messages
        resultLabel = new JLabel("");
        resultLabel.setForeground(Color.YELLOW);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 36));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // panel to center the result message
        JPanel resultPanel = new JPanel(new GridBagLayout());
        resultPanel.setOpaque(false);
        resultPanel.add(resultLabel);

        // panel for all game buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");
        doubleButton = new JButton("Double Down");
        newGameButton = new JButton("New Game");
        statsButton = new JButton("Stats");

        // make buttons look nice
        styleButton(hitButton);
        styleButton(standButton);
        styleButton(doubleButton);
        styleButton(newGameButton);
        styleButton(statsButton);

        // add buttons to panel
        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(doubleButton);
        buttonPanel.add(newGameButton);
        buttonPanel.add(statsButton);

        // layered pane to show cards under result message
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 400));
        
        // wrapper for house cards
        JPanel housePanelWrapper = new JPanel(new BorderLayout());
        housePanelWrapper.setOpaque(false);
        housePanelWrapper.add(houseCardPanel, BorderLayout.CENTER);
        housePanelWrapper.setBounds(0, 0, 800, 200);
        
        // add components in layers
        layeredPane.add(housePanelWrapper, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(resultPanel, JLayeredPane.PALETTE_LAYER);

        // assemble all parts of the window
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(layeredPane, BorderLayout.CENTER);
        
        // bottom area with player cards and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(cardPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // set up button actions and start game
        setupButtonListeners();
        initializeGame();
        frame.add(panel);
        frame.setVisible(true);
    }

    // makes buttons look consistent
    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 70, 0));  // darker green
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setPreferredSize(new Dimension(120, 40));
    }
    
    // creates a simple card image when real images can't load
    private ImageIcon createDefaultCardImage() {
        BufferedImage image = new BufferedImage(80, 120, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 80, 120);  // white card
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 79, 119);  // black border
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("?", 35, 60);  // question mark
        return new ImageIcon(image);
    }
    
    // loads the image for a specific card
    private ImageIcon getCardImage(String cardName) {
        try {
            // parse card name like "A of Spades"
            String[] parts = cardName.split(" ");
            String rank = parts[0].toLowerCase();
            String suit = parts[2].toLowerCase();

            if (rank.equals("a")) rank = "ace";  // fix ace naming

            // build path to card image file
            String imagePath = "src/main/java/com/mycompany/blackjack/cards/" + rank + "_of_" + suit + ".png";
            
            // load and resize image
            Image image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image.getScaledInstance(80, 120, Image.SCALE_SMOOTH));
        }
        catch (Exception e) {
            System.out.println("Error loading " + cardName + ": " + e.getMessage());
            return createDefaultCardImage();  // use default if card image fails
        }
    }
    
    // sets up what happens when buttons are clicked
    private void setupButtonListeners() {   
        // show player stats
        statsButton.addActionListener(e -> showStats());
        
        // hit - take another card
        hitButton.addActionListener(e -> {
            game.hit("player");
            updateGameState();
            if (game.getPlayerScore() >= 21) {
                endPlayerTurn();  // automatic stand at 21 or over
            }
        });

        // stand - stop taking cards
        standButton.addActionListener(e -> endPlayerTurn());

        // double down - double bet and take one more card
        doubleButton.addActionListener(e -> {
            if (bank.getBalance() >= currentBet) {
                currentBet *= 2;
                game.hit("player");
                updateGameState();
                endPlayerTurn();  // stand after doubling
            } else {
                JOptionPane.showMessageDialog(frame, "Not enough balance to double!");
            }
        });

        // start a new game
        newGameButton.addActionListener(e -> startNewGame());
    }
    
    // starts the game or asks for deposit if broke
    private void initializeGame() {
        if (bank.getBalance() <= 0) {
            handleInitialDeposit();
        }

        if (bank.getBalance() > 0) {
            startNewGame();
        } else {
            JOptionPane.showMessageDialog(frame, "No initial deposit. Game exiting.");
            frame.dispose();  // close if no money
        }
    }
    
    // asks player to add money when balance is zero
    private void handleInitialDeposit() {
        String depositInput = JOptionPane.showInputDialog(frame, 
            "Your balance is $0. Enter deposit amount:", 
            "Initial Deposit", 
            JOptionPane.PLAIN_MESSAGE);

        try {
            int depositAmount = Integer.parseInt(depositInput);
            if (depositAmount <= 0) {
                JOptionPane.showMessageDialog(frame, "Deposit must be positive.");
            } else {
                bank.increaseBalance(depositAmount);
                balanceLabel.setText("Balance: $" + bank.getBalance());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid deposit amount.");
        }
    }
    
    // starts a fresh game round
    private boolean startNewGame() {
        // clear old cards and messages
        houseCardPanel.removeAll();
        cardPanel.removeAll();
        resultLabel.setText("");
        
        // create new deck and game logic
        HashMap<String, Integer> deck = SetDeck.createDeck();
        game = new GameLogic(deck);
        
        // keep asking until we get a valid bet
        while (true) {
            String betInput = JOptionPane.showInputDialog(frame, 
                "Balance: $" + bank.getBalance() + "\nEnter your bet:", 
                "New Game", 
                JOptionPane.PLAIN_MESSAGE);

            if (betInput == null) {
                System.exit(0);  // exit if cancel pressed
                return false;
            }
            
            try {
                currentBet = Integer.parseInt(betInput);
                if (currentBet <= 0) {
                    JOptionPane.showMessageDialog(frame, "Bet must be positive!");
                    continue;
                }
                if (currentBet > bank.getBalance()) {
                    JOptionPane.showMessageDialog(frame, "You don't have enough money!");
                    continue;
                }
                break;  // got valid bet
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number!");
            }
        }
        
        // record the bet and deal initial cards
        bank.recordWager(currentBet);
        game.hit("player");
        game.hit("house");
        game.hit("player");
        game.hit("house");

        updateGameState();
        return true;
    }

    // updates the display with current game state
    private void updateGameState() {
        // update dealer's cards (first card face up, others face down)
        houseCardPanel.removeAll();
        List<String> houseHand = game.getHouseHand();
        if (!houseHand.isEmpty()) {
            houseCardPanel.add(new JLabel(getCardImage(houseHand.get(0))) {
                @Override public Dimension getPreferredSize() { return new Dimension(80, 120); }
            });
            
            for (int i = 1; i < houseHand.size(); i++) {
                houseCardPanel.add(new JLabel(cardBackImage) {
                    @Override public Dimension getPreferredSize() { return new Dimension(80, 120); }
                });
            }
        }
        
        // update player's cards (all face up)
        cardPanel.removeAll();
        for (String card : game.getPlayerHand()) {
            cardPanel.add(new JLabel(getCardImage(card)) {
                @Override public Dimension getPreferredSize() { return new Dimension(80, 120); }
            });
        }
        
        // update scores (hide dealer score until end)
        playerScoreLabel.setText("Your score: " + game.getPlayerScore());
        houseScoreLabel.setText("House score: " + (houseHand.size() > 1 ? "?" : String.valueOf(game.getHouseScore())));
        
        // refresh the display
        houseCardPanel.revalidate();
        houseCardPanel.repaint();
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // finishes the player's turn and handles dealer's moves
    private void endPlayerTurn() {
        // dealer hits until 16 or higher
        while (game.getHouseScore() <= 16 && game.getPlayerScore() <= 21) {
            game.hit("house");
        }

        // show all dealer cards
        houseCardPanel.removeAll();
        for (String card : game.getHouseHand()) {
            houseCardPanel.add(new JLabel(getCardImage(card)) {
                @Override public Dimension getPreferredSize() { return new Dimension(80, 120); }
            });
        }

        // show actual dealer score
        houseScoreLabel.setText("House score: " + game.getHouseScore());

        // check who won
        Boolean result = game.bustCheck();
        if (result == null) {
            resultLabel.setText("It's a draw!");
            gameStats.recordDraw();
        } else if (result) {
            resultLabel.setText("You win!");
            bank.increaseBalance(currentBet);
            bank.recordWin(currentBet);
            gameStats.recordWin();
        } else {
            resultLabel.setText("You lose!");
            bank.deductBalance(currentBet);
            bank.recordLoss(currentBet);
            gameStats.recordLoss();
        }

        // update balance display
        balanceLabel.setText("Balance: $" + bank.getBalance());
        houseCardPanel.revalidate();
        houseCardPanel.repaint();

        // handle next steps based on balance
        if (bank.getBalance() > 0) {
            // start new game after 1 second delay
            Timer timer = new Timer(1000, e -> startNewGame());
            timer.setRepeats(false);
            timer.start();
        } else {
            // ask if player wants to deposit more
            int choice = JOptionPane.showConfirmDialog(frame, 
                "Game over! Deposit more money?", 
                "Out of Money", 
                JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                handleInitialDeposit();
                if (bank.getBalance() > 0) {
                    startNewGame();
                } else {
                    frame.dispose();  // close if still no money
                }
            } else {
                frame.dispose();  // close if player says no
            }
        }
    }
    
    // shows a popup with player statistics
    private void showStats() {
        JDialog statsDialog = new JDialog(frame, "Game Statistics - " + playerAccount.getUsername(), true);
        statsDialog.setSize(400, 500);
        statsDialog.setLayout(new BorderLayout());
        statsDialog.setLocationRelativeTo(frame);

        // panel to hold all stats
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statsPanel.setBackground(new Color(0, 100, 0));

        // calculate win percentage
        int wins = gameStats.getWins();
        int losses = gameStats.getLosses();
        int draws = gameStats.getDraws();
        int totalGames = wins + losses + draws;
        double winPercentage = totalGames > 0 ? (double) wins / totalGames * 100 : 0;
        
        // get financial stats from bank
        Map<String, Integer> financialStats = bank.getFinancialStats();
        int wagered = financialStats.get("wagered");
        int won = financialStats.get("won");
        int lost = financialStats.get("lost");
        int net = financialStats.get("net");

        // title
        JLabel titleLabel = new JLabel("Game Statistics", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPanel.add(titleLabel);
        
        statsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // game results section
        JLabel gameStatsLabel = new JLabel("Game Results:", SwingConstants.CENTER);
        gameStatsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gameStatsLabel.setForeground(Color.WHITE);
        statsPanel.add(gameStatsLabel);
        
        statsPanel.add(createStatLabel("Wins: " + wins));
        statsPanel.add(createStatLabel("Losses: " + losses));
        statsPanel.add(createStatLabel("Draws: " + draws));
        
        // win percentage in yellow
        JLabel percentageLabel = new JLabel(
            String.format("Win Percentage: %.1f%%", winPercentage), 
            SwingConstants.CENTER);
        percentageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        percentageLabel.setForeground(Color.YELLOW);
        percentageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPanel.add(percentageLabel);
        
        statsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // financial stats section
        JLabel financialStatsLabel = new JLabel("Financial Stats:", SwingConstants.CENTER);
        financialStatsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        financialStatsLabel.setForeground(Color.WHITE);
        statsPanel.add(financialStatsLabel);
        
        statsPanel.add(createStatLabel("Total Wagered: $" + wagered));
        statsPanel.add(createStatLabel("Total Won: $" + won));
        statsPanel.add(createStatLabel("Total Lost: $" + lost));
        
        // net profit in green or red
        JLabel netLabel = new JLabel("Net Profit: $" + net, SwingConstants.CENTER);
        netLabel.setFont(new Font("Arial", Font.BOLD, 16));
        netLabel.setForeground(net >= 0 ? Color.GREEN : Color.RED);
        netLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPanel.add(netLabel);

        // close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> statsDialog.dispose());
        styleButton(closeButton);
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);

        // assemble dialog
        statsDialog.add(statsPanel, BorderLayout.CENTER);
        statsDialog.add(buttonPanel, BorderLayout.SOUTH);

        statsDialog.setVisible(true);
    }

    // helper to create consistent stat labels
    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    // starts the program by showing login screen
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI());
    }
}
