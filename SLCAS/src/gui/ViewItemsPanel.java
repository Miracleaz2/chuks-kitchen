package gui;

import controller.LibraryManager;
import model.LibraryItem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ViewItemsPanel extends JPanel {
    private LibraryManager manager;
    private MainWindow parent;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField filterField;
    private JComboBox<String> typeFilter;

    private static final String[] COLUMNS = {"ID", "Type", "Title", "Author", "Year", "Status", "Borrows"};

    public ViewItemsPanel(LibraryManager manager, MainWindow parent) {
        this.manager = manager;
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        filterField = new JTextField(20);
        filterField.setToolTipText("Type to filter items by title or author");
        filterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { applyFilter(); }
        });
        filterPanel.add(filterField);

        filterPanel.add(new JLabel("Type:"));
        typeFilter = new JComboBox<>(new String[]{"All", "Book", "Magazine", "Journal"});
        typeFilter.setToolTipText("Filter by item type");
        typeFilter.addActionListener(e -> applyFilter());
        filterPanel.add(typeFilter);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setToolTipText("Refresh the items list");
        refreshBtn.addActionListener(e -> refresh());
        filterPanel.add(refreshBtn);

        JButton detailsBtn = new JButton("Details");
        detailsBtn.setToolTipText("Show details of selected item");
        detailsBtn.addActionListener(e -> showDetails());
        filterPanel.add(detailsBtn);

        add(filterPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        table.setToolTipText("Double-click a row to see details");
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) showDetails();
            }
        });

        int[] widths = {80, 80, 250, 180, 60, 90, 70};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel cacheLabel = new JLabel("Most Frequently Accessed: (loading...)");
        cacheLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        cacheLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        add(cacheLabel, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        applyFilter();
    }

    private void applyFilter() {
        tableModel.setRowCount(0);
        String filter = filterField.getText().toLowerCase();
        String type = (String) typeFilter.getSelectedItem();
        List<LibraryItem> items = manager.getDatabase().getItems();
        for (LibraryItem item : items) {
            if (!"All".equals(type) && !item.getType().equals(type)) continue;
            if (!filter.isEmpty() &&
                !item.getTitle().toLowerCase().contains(filter) &&
                !item.getAuthor().toLowerCase().contains(filter)) continue;
            tableModel.addRow(new Object[]{
                item.getId(), item.getType(), item.getTitle(), item.getAuthor(),
                item.getYear(), item.isAvailable() ? "Available" : "Borrowed",
                item.getBorrowCount()
            });
        }
        parent.updateStatus();
    }

    private void showDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(parent, "Please select an item.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String itemId = (String) tableModel.getValueAt(row, 0);
        LibraryItem item = manager.getDatabase().findItemById(itemId);
        if (item != null) {
            JOptionPane.showMessageDialog(parent, item.getDetails(), "Item Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public LibraryItem getSelectedItem() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String itemId = (String) tableModel.getValueAt(row, 0);
        return manager.getDatabase().findItemById(itemId);
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if ("Available".equals(value)) {
                c.setForeground(new Color(0, 128, 0));
            } else {
                c.setForeground(new Color(180, 0, 0));
            }
            if (isSelected) c.setForeground(Color.WHITE);
            return c;
        }
    }
}
