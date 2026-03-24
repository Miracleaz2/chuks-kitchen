package gui;

import controller.LibraryManager;
import model.LibraryItem;
import model.UserAccount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainWindow extends JFrame {
    private LibraryManager manager;
    private String dataDir;
    private JTabbedPane tabbedPane;
    private ViewItemsPanel viewItemsPanel;
    private BorrowPanel borrowPanel;
    private AdminPanel adminPanel;
    private SearchSortPanel searchSortPanel;
    private JLabel statusBar;
    private Timer overdueTimer;

    public MainWindow(LibraryManager manager, String dataDir) {
        super("Smart Library Circulation & Automation System (SLCAS)");
        this.manager = manager;
        this.dataDir = dataDir;
        initUI();
        startOverdueTimer();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setJMenuBar(createMenuBar());

        tabbedPane = new JTabbedPane();
        viewItemsPanel = new ViewItemsPanel(manager, this);
        borrowPanel = new BorrowPanel(manager, this);
        adminPanel = new AdminPanel(manager, this);
        searchSortPanel = new SearchSortPanel(manager, this);

        tabbedPane.addTab("\uD83D\uDCDA View Items", viewItemsPanel);
        tabbedPane.addTab("\uD83D\uDD04 Borrow/Return", borrowPanel);
        tabbedPane.addTab("\u2699 Admin", adminPanel);
        tabbedPane.addTab("\uD83D\uDD0D Search & Sort", searchSortPanel);

        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

        add(tabbedPane, BorderLayout.CENTER);

        statusBar = new JLabel(" Ready | Items: 0 | Users: 0");
        statusBar.setBorder(new EmptyBorder(3, 5, 3, 5));
        statusBar.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(statusBar, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainWindow.this,
                    "Save data before exit?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    manager.saveData(dataDir);
                    System.exit(0);
                } else if (confirm == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }
        });

        updateStatus();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem saveItem = new JMenuItem("Save Data", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> {
            manager.saveData(dataDir);
            setStatus("Data saved successfully.");
        });

        JMenuItem loadItem = new JMenuItem("Load Data", KeyEvent.VK_L);
        loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        loadItem.addActionListener(e -> {
            manager.loadData(dataDir);
            refreshAllPanels();
            setStatus("Data loaded successfully.");
        });

        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu reportsMenu = new JMenu("Reports");
        reportsMenu.setMnemonic(KeyEvent.VK_R);

        JMenuItem mostBorrowedItem = new JMenuItem("Most Borrowed Items");
        mostBorrowedItem.addActionListener(e -> showMostBorrowedReport());

        JMenuItem overdueItem = new JMenuItem("Overdue Users");
        overdueItem.addActionListener(e -> showOverdueReport());

        JMenuItem categoryItem = new JMenuItem("Category Distribution");
        categoryItem.addActionListener(e -> showCategoryReport());

        reportsMenu.add(mostBorrowedItem);
        reportsMenu.add(overdueItem);
        reportsMenu.add(categoryItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Smart Library Circulation & Automation System (SLCAS)\n" +
            "Version 1.0\n\nFeatures:\n" +
            "- Book, Magazine, and Journal management\n" +
            "- Borrow/Return with reservation queue\n" +
            "- Search (Linear, Binary, Recursive)\n" +
            "- Sort (Selection, Insertion, Merge, Quick)\n" +
            "- Undo delete operations\n" +
            "- Overdue reminders & fine calculation\n" +
            "- Data persistence (CSV)",
            "About SLCAS", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(reportsMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void startOverdueTimer() {
        overdueTimer = new Timer(30000, e -> {
            List<UserAccount> overdue = manager.getOverdueUsers();
            if (!overdue.isEmpty()) {
                setStatus("WARNING: " + overdue.size() + " user(s) have overdue items!");
            }
        });
        overdueTimer.start();
    }

    public void refreshAllPanels() {
        viewItemsPanel.refresh();
        borrowPanel.refresh();
        adminPanel.refresh();
        searchSortPanel.refresh();
        updateStatus();
    }

    public void updateStatus() {
        int items = manager.getDatabase().getItems().size();
        int users = manager.getDatabase().getUsers().size();
        int overdue = manager.getOverdueUsers().size();
        statusBar.setText(String.format(" Ready | Items: %d | Users: %d | Overdue: %d", items, users, overdue));
    }

    public void setStatus(String message) {
        statusBar.setText(" " + message);
    }

    private void showMostBorrowedReport() {
        List<LibraryItem> items = manager.getMostBorrowedItems(10);
        StringBuilder sb = new StringBuilder("Most Borrowed Items (Top 10):\n\n");
        if (items.isEmpty()) {
            sb.append("No items borrowed yet.");
        } else {
            for (int i = 0; i < items.size(); i++) {
                sb.append(String.format("%d. %s - %d borrow(s)\n",
                    i + 1, items.get(i).getTitle(), items.get(i).getBorrowCount()));
            }
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Most Borrowed Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showOverdueReport() {
        List<UserAccount> users = manager.getOverdueUsers();
        StringBuilder sb = new StringBuilder("Users with Overdue Items:\n\n");
        if (users.isEmpty()) {
            sb.append("No overdue items.");
        } else {
            for (UserAccount u : users) {
                sb.append(String.format("- %s (%s) - %d overdue, Fine: $%.2f\n",
                    u.getName(), u.getUserId(), u.getOverdueCount(), u.computeOverdueFine()));
            }
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Overdue Report", JOptionPane.WARNING_MESSAGE);
    }

    private void showCategoryReport() {
        String report = manager.getCategoryDistribution();
        JOptionPane.showMessageDialog(this, report, "Category Distribution", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
        LibraryManager manager = new LibraryManager();
        String dataDir = args.length > 0 ? args[0] : "data";
        manager.loadData(dataDir);
        if (manager.getDatabase().getItems().isEmpty()) {
            addSampleData(manager);
        }
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(manager, dataDir);
            window.setVisible(true);
        });
    }

    private static void addSampleData(LibraryManager manager) {
        manager.addBook("Introduction to Algorithms", "Thomas H. Cormen", 2009, "978-0262033848", "Computer Science", 1312);
        manager.addBook("Clean Code", "Robert C. Martin", 2008, "978-0132350884", "Software Engineering", 464);
        manager.addBook("The Great Gatsby", "F. Scott Fitzgerald", 1925, "978-0743273565", "Fiction", 180);
        manager.addBook("Design Patterns", "Gang of Four", 1994, "978-0201633610", "Computer Science", 395);
        manager.addBook("Operating System Concepts", "Abraham Silberschatz", 2018, "978-1119456339", "Computer Science", 944);
        manager.addMagazine("National Geographic", "Various", 2023, 205, "National Geographic Society", "January");
        manager.addMagazine("Scientific American", "Various", 2023, 328, "Springer Nature", "March");
        manager.addJournal("Nature", "Various Authors", 2023, 615, 3, "Natural Sciences");
        manager.addJournal("IEEE Transactions", "IEEE Authors", 2023, 45, 2, "Computer Engineering");
        manager.addUser("Alice Johnson", "alice@university.edu", "student");
        manager.addUser("Bob Smith", "bob@university.edu", "faculty");
        manager.addUser("Carol White", "carol@university.edu", "student");
        manager.addUser("Admin User", "admin@university.edu", "admin");
    }
}
