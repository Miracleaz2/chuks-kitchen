package gui;

import controller.LibraryManager;
import model.LibraryItem;
import model.UserAccount;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AdminPanel extends JPanel {
    private LibraryManager manager;
    private MainWindow parent;
    private JTabbedPane tabs;
    private JTable itemTable;
    private DefaultTableModel itemTableModel;
    private JTable userTable;
    private DefaultTableModel userTableModel;

    private static final String[] ITEM_COLS = {"ID", "Type", "Title", "Author", "Year", "Available"};
    private static final String[] USER_COLS = {"ID", "Name", "Email", "Role", "Borrowed", "Overdue"};

    public AdminPanel(LibraryManager manager, MainWindow parent) {
        this.manager = manager;
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabs = new JTabbedPane();
        tabs.addTab("Manage Items", createItemsTab());
        tabs.addTab("Manage Users", createUsersTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createItemsTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addBookBtn = new JButton("Add Book");
        addBookBtn.setToolTipText("Add a new book to the library");
        addBookBtn.addActionListener(e -> showAddBookDialog());

        JButton addMagBtn = new JButton("Add Magazine");
        addMagBtn.setToolTipText("Add a new magazine");
        addMagBtn.addActionListener(e -> showAddMagazineDialog());

        JButton addJournalBtn = new JButton("Add Journal");
        addJournalBtn.setToolTipText("Add a new journal");
        addJournalBtn.addActionListener(e -> showAddJournalDialog());

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setToolTipText("Delete the selected item (can be undone)");
        deleteBtn.addActionListener(e -> deleteSelectedItem());

        JButton undoBtn = new JButton("Undo Delete");
        undoBtn.setToolTipText("Undo the last delete operation");
        undoBtn.addActionListener(e -> undoDelete());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshItemTable());

        toolbar.add(addBookBtn);
        toolbar.add(addMagBtn);
        toolbar.add(addJournalBtn);
        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        toolbar.add(deleteBtn);
        toolbar.add(undoBtn);
        toolbar.add(refreshBtn);
        panel.add(toolbar, BorderLayout.NORTH);

        itemTableModel = new DefaultTableModel(ITEM_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        itemTable = new JTable(itemTableModel);
        itemTable.setRowHeight(24);
        itemTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        itemTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(new JScrollPane(itemTable), BorderLayout.CENTER);

        refreshItemTable();
        return panel;
    }

    private JPanel createUsersTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addUserBtn = new JButton("Add User");
        addUserBtn.setToolTipText("Add a new user account");
        addUserBtn.addActionListener(e -> showAddUserDialog());

        JButton deleteUserBtn = new JButton("Delete Selected");
        deleteUserBtn.setToolTipText("Remove selected user");
        deleteUserBtn.addActionListener(e -> deleteSelectedUser());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshUserTable());

        toolbar.add(addUserBtn);
        toolbar.add(deleteUserBtn);
        toolbar.add(refreshBtn);
        panel.add(toolbar, BorderLayout.NORTH);

        userTableModel = new DefaultTableModel(USER_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = new JTable(userTableModel);
        userTable.setRowHeight(24);
        userTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        refreshUserTable();
        return panel;
    }

    public void refresh() {
        refreshItemTable();
        refreshUserTable();
    }

    private void refreshItemTable() {
        itemTableModel.setRowCount(0);
        for (LibraryItem item : manager.getDatabase().getItems()) {
            itemTableModel.addRow(new Object[]{
                item.getId(), item.getType(), item.getTitle(), item.getAuthor(),
                item.getYear(), item.isAvailable() ? "Yes" : "No"
            });
        }
    }

    private void refreshUserTable() {
        userTableModel.setRowCount(0);
        for (UserAccount user : manager.getDatabase().getUsers()) {
            userTableModel.addRow(new Object[]{
                user.getUserId(), user.getName(), user.getEmail(), user.getRole(),
                user.getCurrentlyBorrowed().size(), user.getOverdueCount()
            });
        }
    }

    private void showAddBookDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField yearField = new JTextField(6);
        JTextField isbnField = new JTextField(15);
        JTextField genreField = new JTextField(15);
        JTextField pagesField = new JTextField(6);

        String[] labels = {"Title:", "Author:", "Year:", "ISBN:", "Genre:", "Pages:"};
        JTextField[] flds = {titleField, authorField, yearField, isbnField, genreField, pagesField};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = (i % 2) * 2; gbc.gridy = i / 2;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx++;
            panel.add(flds[i], gbc);
        }

        int result = JOptionPane.showConfirmDialog(parent, panel, "Add New Book",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());
                String isbn = isbnField.getText().trim();
                String genre = genreField.getText().trim();
                int pages = Integer.parseInt(pagesField.getText().trim());
                if (title.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(parent, "Title and Author are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String id = manager.addBook(title, author, year, isbn, genre, pages);
                parent.refreshAllPanels();
                parent.setStatus("Book added: " + id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Invalid number format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddMagazineDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField yearField = new JTextField(6);
        JTextField issueField = new JTextField(6);
        JTextField publisherField = new JTextField(15);
        JTextField monthField = new JTextField(10);

        String[] labels = {"Title:", "Author:", "Year:", "Issue #:", "Publisher:", "Month:"};
        JTextField[] flds = {titleField, authorField, yearField, issueField, publisherField, monthField};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = (i % 2) * 2; gbc.gridy = i / 2;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx++;
            panel.add(flds[i], gbc);
        }

        int result = JOptionPane.showConfirmDialog(parent, panel, "Add New Magazine",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());
                int issue = Integer.parseInt(issueField.getText().trim());
                String publisher = publisherField.getText().trim();
                String month = monthField.getText().trim();
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(parent, "Title is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String id = manager.addMagazine(title, author, year, issue, publisher, month);
                parent.refreshAllPanels();
                parent.setStatus("Magazine added: " + id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Invalid number format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddJournalDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField yearField = new JTextField(6);
        JTextField volumeField = new JTextField(6);
        JTextField issueField = new JTextField(6);
        JTextField subjectField = new JTextField(15);

        String[] labels = {"Title:", "Author:", "Year:", "Volume:", "Issue #:", "Subject:"};
        JTextField[] flds = {titleField, authorField, yearField, volumeField, issueField, subjectField};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = (i % 2) * 2; gbc.gridy = i / 2;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx++;
            panel.add(flds[i], gbc);
        }

        int result = JOptionPane.showConfirmDialog(parent, panel, "Add New Journal",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());
                int volume = Integer.parseInt(volumeField.getText().trim());
                int issue = Integer.parseInt(issueField.getText().trim());
                String subject = subjectField.getText().trim();
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(parent, "Title is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String id = manager.addJournal(title, author, year, volume, issue, subject);
                parent.refreshAllPanels();
                parent.setStatus("Journal added: " + id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Invalid number format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddUserDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"student", "faculty", "admin"});

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; panel.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; panel.add(emailField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; panel.add(roleCombo, gbc);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Add New User",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Name and email are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String id = manager.addUser(name, email, role);
            parent.refreshAllPanels();
            parent.setStatus("User added: " + id);
        }
    }

    private void deleteSelectedItem() {
        int row = itemTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(parent, "Please select an item to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String itemId = (String) itemTableModel.getValueAt(row, 0);
        LibraryItem item = manager.getDatabase().findItemById(itemId);
        if (item == null) return;
        int confirm = JOptionPane.showConfirmDialog(parent,
            "Delete '" + item.getTitle() + "'? (Can be undone)", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            manager.deleteItem(item);
            parent.refreshAllPanels();
            parent.setStatus("Deleted: " + item.getTitle() + " (use Undo Delete to restore)");
        }
    }

    private void undoDelete() {
        if (manager.undoLastDelete()) {
            parent.refreshAllPanels();
            parent.setStatus("Undo: last deleted item restored.");
        } else {
            JOptionPane.showMessageDialog(parent, "Nothing to undo.", "Undo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(parent, "Please select a user.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String userId = (String) userTableModel.getValueAt(row, 0);
        UserAccount user = manager.getDatabase().findUserById(userId);
        if (user == null) return;
        int confirm = JOptionPane.showConfirmDialog(parent,
            "Delete user '" + user.getName() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            manager.getDatabase().removeUser(user);
            parent.refreshAllPanels();
            parent.setStatus("User removed: " + user.getName());
        }
    }
}
