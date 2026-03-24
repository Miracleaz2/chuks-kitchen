package gui;

import controller.LibraryManager;
import model.LibraryItem;
import model.UserAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BorrowPanel extends JPanel {
    private LibraryManager manager;
    private MainWindow parent;
    private JComboBox<String> userCombo;
    private JComboBox<String> itemCombo;
    private JTextArea resultArea;
    private JList<String> queueList;
    private DefaultListModel<String> queueModel;

    public BorrowPanel(LibraryManager manager, MainWindow parent) {
        this.manager = manager;
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Borrow / Return"));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.add(new JLabel("User:"));
        userCombo = new JComboBox<>();
        userCombo.setPreferredSize(new Dimension(250, 30));
        userCombo.setToolTipText("Select the user");
        userPanel.add(userCombo);
        leftPanel.add(userPanel);

        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPanel.add(new JLabel("Item:"));
        itemCombo = new JComboBox<>();
        itemCombo.setPreferredSize(new Dimension(250, 30));
        itemCombo.setToolTipText("Select the item to borrow or return");
        itemPanel.add(itemCombo);
        leftPanel.add(itemPanel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton borrowBtn = new JButton("Borrow");
        borrowBtn.setToolTipText("Borrow the selected item");
        borrowBtn.addActionListener(e -> performBorrow());

        JButton returnBtn = new JButton("Return");
        returnBtn.setToolTipText("Return the selected item");
        returnBtn.addActionListener(e -> performReturn());

        JButton overdueBtn = new JButton("Check Overdue");
        overdueBtn.setToolTipText("Check overdue status for selected user");
        overdueBtn.addActionListener(e -> checkOverdue());

        btnPanel.add(borrowBtn);
        btnPanel.add(returnBtn);
        btnPanel.add(overdueBtn);
        leftPanel.add(btnPanel);

        resultArea = new JTextArea(8, 35);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(BorderFactory.createTitledBorder("Result"));
        leftPanel.add(resultScroll);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Reservation Queue"));
        queueModel = new DefaultListModel<>();
        queueList = new JList<>(queueModel);
        queueList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        rightPanel.add(new JScrollPane(queueList), BorderLayout.CENTER);

        JButton refreshQueueBtn = new JButton("Refresh Queue");
        refreshQueueBtn.addActionListener(e -> refreshQueue());
        rightPanel.add(refreshQueueBtn, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(480);
        add(splitPane, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        refreshCombos();
        refreshQueue();
    }

    private void refreshCombos() {
        userCombo.removeAllItems();
        for (UserAccount u : manager.getDatabase().getUsers()) {
            userCombo.addItem(u.getUserId() + " - " + u.getName());
        }

        itemCombo.removeAllItems();
        for (LibraryItem item : manager.getDatabase().getItems()) {
            String status = item.isAvailable() ? "[Available]" : "[Borrowed]";
            itemCombo.addItem(item.getId() + " - " + item.getTitle() + " " + status);
        }
    }

    private void refreshQueue() {
        queueModel.clear();
        for (String r : manager.getDatabase().getReservationQueue()) {
            String[] parts = r.split(":");
            String itemId = parts[0];
            String userId = parts.length > 1 ? parts[1] : "";
            LibraryItem item = manager.getDatabase().findItemById(itemId);
            UserAccount user = manager.getDatabase().findUserById(userId);
            String itemName = item != null ? item.getTitle() : itemId;
            String userName = user != null ? user.getName() : userId;
            queueModel.addElement("Item: " + itemName + " | User: " + userName);
        }
    }

    private String getSelectedUserId() {
        String selected = (String) userCombo.getSelectedItem();
        if (selected == null) return null;
        return selected.split(" - ")[0];
    }

    private String getSelectedItemId() {
        String selected = (String) itemCombo.getSelectedItem();
        if (selected == null) return null;
        return selected.split(" - ")[0];
    }

    private void performBorrow() {
        String userId = getSelectedUserId();
        String itemId = getSelectedItemId();
        if (userId == null || itemId == null) {
            JOptionPane.showMessageDialog(parent, "Please select a user and item.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String result = manager.getBorrowController().borrowItem(itemId, userId);
        resultArea.setText(result);
        parent.refreshAllPanels();
    }

    private void performReturn() {
        String userId = getSelectedUserId();
        String itemId = getSelectedItemId();
        if (userId == null || itemId == null) {
            JOptionPane.showMessageDialog(parent, "Please select a user and item.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String result = manager.getBorrowController().returnItem(itemId, userId);
        resultArea.setText(result);
        parent.refreshAllPanels();
    }

    private void checkOverdue() {
        String userId = getSelectedUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(parent, "Please select a user.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String result = manager.getBorrowController().getOverdueInfo(userId);
        resultArea.setText(result);
    }
}
