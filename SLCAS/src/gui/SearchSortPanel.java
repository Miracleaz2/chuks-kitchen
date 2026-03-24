package gui;

import controller.LibraryManager;
import model.LibraryItem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SearchSortPanel extends JPanel {
    private LibraryManager manager;
    private MainWindow parent;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JComboBox<String> searchAlgoCombo;
    private JComboBox<String> sortFieldCombo;
    private JComboBox<String> sortAlgoCombo;
    private JLabel resultCountLabel;

    private static final String[] COLUMNS = {"ID", "Type", "Title", "Author", "Year", "Status"};

    public SearchSortPanel(LibraryManager manager, MainWindow parent) {
        this.manager = manager;
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        // Search row
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Search Query:"), gbc);
        gbc.gridx = 1;
        searchField = new JTextField(20);
        searchField.setToolTipText("Enter search term");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performSearch();
            }
        });
        topPanel.add(searchField, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Search By:"), gbc);
        gbc.gridx = 3;
        searchTypeCombo = new JComboBox<>(new String[]{"Title", "Author", "Type"});
        searchTypeCombo.setToolTipText("Choose what field to search by");
        topPanel.add(searchTypeCombo, gbc);

        gbc.gridx = 4;
        topPanel.add(new JLabel("Algorithm:"), gbc);
        gbc.gridx = 5;
        searchAlgoCombo = new JComboBox<>(new String[]{"Linear", "Binary", "Recursive"});
        searchAlgoCombo.setToolTipText("Choose search algorithm");
        topPanel.add(searchAlgoCombo, gbc);

        gbc.gridx = 6;
        JButton searchBtn = new JButton("Search");
        searchBtn.setToolTipText("Search the library catalogue");
        searchBtn.addActionListener(e -> performSearch());
        topPanel.add(searchBtn, gbc);

        // Sort row
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Sort By:"), gbc);
        gbc.gridx = 1;
        sortFieldCombo = new JComboBox<>(new String[]{"Title", "Author", "Year"});
        sortFieldCombo.setToolTipText("Choose sort field");
        topPanel.add(sortFieldCombo, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Sort Algorithm:"), gbc);
        gbc.gridx = 3;
        sortAlgoCombo = new JComboBox<>(new String[]{"Selection Sort", "Insertion Sort", "Merge Sort", "Quick Sort"});
        sortAlgoCombo.setToolTipText("Choose sorting algorithm");
        topPanel.add(sortAlgoCombo, gbc);

        gbc.gridx = 4;
        JButton sortBtn = new JButton("Sort & Display");
        sortBtn.setToolTipText("Sort and display all items");
        sortBtn.addActionListener(e -> performSort());
        topPanel.add(sortBtn, gbc);

        gbc.gridx = 5;
        JButton showAllBtn = new JButton("Show All");
        showAllBtn.setToolTipText("Show all items without sorting");
        showAllBtn.addActionListener(e -> showAll());
        topPanel.add(showAllBtn, gbc);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        resultTable = new JTable(tableModel);
        resultTable.setRowHeight(24);
        resultTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        resultTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        int[] widths = {80, 80, 280, 180, 60, 90};
        for (int i = 0; i < widths.length; i++) {
            resultTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        resultCountLabel = new JLabel("Results: 0");
        resultCountLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        add(resultCountLabel, BorderLayout.SOUTH);

        showAll();
    }

    public void refresh() { showAll(); }

    private void populateTable(List<LibraryItem> items) {
        tableModel.setRowCount(0);
        for (LibraryItem item : items) {
            tableModel.addRow(new Object[]{
                item.getId(), item.getType(), item.getTitle(), item.getAuthor(),
                item.getYear(), item.isAvailable() ? "Available" : "Borrowed"
            });
        }
        resultCountLabel.setText("Results: " + items.size());
    }

    private void showAll() {
        populateTable(manager.getDatabase().getItems());
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) { showAll(); return; }

        String searchType = (String) searchTypeCombo.getSelectedItem();
        String algo = (String) searchAlgoCombo.getSelectedItem();
        List<LibraryItem> items = manager.getDatabase().getItems();
        List<LibraryItem> results = new ArrayList<>();

        try {
            switch (searchType) {
                case "Title":
                    if ("Binary".equals(algo)) {
                        List<LibraryItem> sorted = new ArrayList<>(items);
                        manager.selectionSortByTitle(sorted);
                        int idx = manager.getSearchEngine().binarySearchByTitle(sorted, query);
                        if (idx >= 0) results.add(sorted.get(idx));
                        parent.setStatus("Binary search completed. Found: " + results.size() + " item(s).");
                    } else if ("Recursive".equals(algo)) {
                        LibraryItem found = manager.getSearchEngine().recursiveSearchByTitle(items, query, 0);
                        if (found != null) results.add(found);
                        parent.setStatus("Recursive search completed. Found: " + results.size() + " item(s).");
                    } else {
                        results = manager.getSearchEngine().linearSearchByTitle(items, query);
                        parent.setStatus("Linear search completed. Found: " + results.size() + " item(s).");
                    }
                    break;
                case "Author":
                    if ("Recursive".equals(algo)) {
                        results = manager.getSearchEngine().recursiveSearchByAuthor(items, query, 0, new ArrayList<>());
                        parent.setStatus("Recursive search completed. Found: " + results.size() + " item(s).");
                    } else {
                        results = manager.getSearchEngine().linearSearchByAuthor(items, query);
                        parent.setStatus("Linear search completed. Found: " + results.size() + " item(s).");
                    }
                    break;
                case "Type":
                    results = manager.getSearchEngine().linearSearchByType(items, query);
                    parent.setStatus("Linear search by type. Found: " + results.size() + " item(s).");
                    break;
                default:
                    results = manager.getSearchEngine().linearSearchByTitle(items, query);
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        populateTable(results);
    }

    private void performSort() {
        String field = (String) sortFieldCombo.getSelectedItem();
        String algo = (String) sortAlgoCombo.getSelectedItem();
        List<LibraryItem> items = new ArrayList<>(manager.getDatabase().getItems());

        try {
            switch (algo) {
                case "Selection Sort":
                    manager.selectionSortByTitle(items);
                    break;
                case "Insertion Sort":
                    if ("Author".equals(field)) manager.insertionSortByAuthor(items);
                    else if ("Year".equals(field)) manager.insertionSortByYear(items);
                    else manager.selectionSortByTitle(items);
                    break;
                case "Merge Sort":
                    if ("Author".equals(field)) items = manager.mergeSortByAuthor(items);
                    else if ("Year".equals(field)) items = manager.mergeSortByYear(items);
                    else items = manager.mergeSortByTitle(items);
                    break;
                case "Quick Sort":
                    if (!items.isEmpty()) manager.quickSortByTitle(items, 0, items.size() - 1);
                    break;
                default:
                    manager.selectionSortByTitle(items);
                    break;
            }
            populateTable(items);
            parent.setStatus("Sorted by " + field + " using " + algo + ". " + items.size() + " item(s).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Sort error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
