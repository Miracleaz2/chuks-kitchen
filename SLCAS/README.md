# Smart Library Circulation & Automation System (SLCAS)

A full-featured Java Swing library management system demonstrating OOP, data structures, algorithms, and GUI design.

## Features

- **Item Management**: Books, Magazines, Journals with full CRUD operations
- **Borrow/Return System**: 2-week loan periods with reservation queue (LinkedList)
- **Overdue Tracking**: Recursive fine calculation ($0.50/day per overdue item)
- **Undo Delete**: Stack-based undo for item deletions
- **Access Cache**: Fixed-size array tracking top-5 most borrowed items
- **Search Algorithms**: Linear, Binary, Recursive search by title/author/type
- **Sort Algorithms**: Selection Sort, Insertion Sort, Merge Sort, Quick Sort
- **Reports**: Most borrowed items, overdue users, category distribution
- **Data Persistence**: CSV-based save/load for items and users

## Project Structure

```
SLCAS/
  src/
    model/         - Domain classes (LibraryItem, Book, Magazine, Journal, UserAccount, LibraryDatabase)
    controller/    - Business logic (LibraryManager, SearchEngine, BorrowController)
    gui/           - Swing UI (MainWindow, ViewItemsPanel, BorrowPanel, AdminPanel, SearchSortPanel)
    utils/         - Utilities (IDGenerator, FileHandler)
  data/            - CSV persistence files
  bin/             - Compiled class files (generated)
  Makefile
```

## Build & Run

```bash
cd SLCAS
make compile   # Compile all sources to bin/
make run       # Compile and launch the GUI
make clean     # Remove compiled classes
```

**Requirements**: Java 8+

## Data Structures Used

| Structure | Usage |
|-----------|-------|
| `ArrayList` | Main item and user collections |
| `Queue` (LinkedList) | Reservation queue for unavailable items |
| `Stack` | Undo stack for deleted items |
| `Array` | Fixed-size top-5 access cache |

## OOP Concepts

- **Interface**: `Borrowable` defines borrow/return contract
- **Abstract Class**: `LibraryItem` implements `Borrowable`, provides common fields
- **Inheritance**: `Book`, `Magazine`, `Journal` extend `LibraryItem`
- **Polymorphism**: `processItem(LibraryItem)` works with any subtype
- **Encapsulation**: Private fields with public accessors
