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

## Running in VS Code (Recommended)

### Prerequisites
1. Install **Java 8 or later** – download from [https://adoptium.net](https://adoptium.net)
2. Install **VS Code** – download from [https://code.visualstudio.com](https://code.visualstudio.com)
3. Install the **Extension Pack for Java** in VS Code:
   - Open VS Code → press `Ctrl+Shift+X` (Extensions)
   - Search for **"Extension Pack for Java"** (by Microsoft)
   - Click **Install**

### Opening the Project
1. In VS Code, choose **File → Open Folder…**
2. Navigate to and select the **`SLCAS`** folder (not the root of the repo)
3. VS Code will automatically detect the Java source files

### Running the App — Option A: Play Button (Easiest)
1. Open **`src/gui/MainWindow.java`** in the editor
2. Click the **▷ Run** button that appears above the `main` method
   *(or press `F5` to run with debugging)*

### Running the App — Option B: Run & Debug Panel
1. Press `Ctrl+Shift+D` to open the **Run and Debug** panel
2. Select **"Run SLCAS"** from the dropdown at the top
3. Click the green **▷** button (or press `F5`)

### Running the App — Option C: Terminal Task
1. Press `Ctrl+Shift+B` to run the default **build task** (compiles the project)
2. Then open a terminal (`Ctrl+\``) and run:
   ```
   java -cp bin gui.MainWindow data
   ```

### Keyboard Shortcuts Summary
| Action | Shortcut |
|--------|----------|
| Run without debugging | `Ctrl+F5` |
| Run with debugging | `F5` |
| Build (compile) | `Ctrl+Shift+B` |
| Open terminal | `` Ctrl+` `` |

---

## Build & Run (Command Line / Terminal)

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
