**STILL A MAJOR WORK IN PROGRESS**

# EasyPOS
A point-of-sale system built in Java/JavaFX for the hospitality industry or similar. Supports multiple terminals communicating over a locally-run HTTP server, with persistent storage via JSON serialisation.

Built as a personal project to deepen my understanding of Java OOP, REST architecture, and JavaFX. It is the largest non-university Java project I've undertaken.

## Tech Stack
- **Java 21** with **JavaFX 21** for the UI
- **Gson** for JSON serialisation/deserialisation
- **Java HttpServer** (`com.sun.net.httpserver`) for the local REST API
- **Gradle** as the build tool

## Architecture Overview
EasyPOS follows a **Model / Service / Controller** pattern:
- **Models** (`models/`): Pure data objects.
- **Services** (`services/`): "Backend" logic and persistence. 
- **Controllers** (`controllers/`): JavaFX FXML controllers, one per screen.
- **Server** (`server/`): Local HTTP server with individual handlers.
- (FXML files can be found in main/resources/FXML)

### Networking
Devices operate in one of two modes configured at startup:
- **Host mode** — runs a local HTTP server, acts as the source of truth for orders
- **Client mode** — POSTs order changes to the host after every mutation

> Known limitation: simultaneous writes from two clients can cause a race condition where one client's changes overwrite the other's. This is a known issue, the fix is to move from full-list syncing to individual operation syncing (ADD/MODIFY/REMOVE per order by UUID). This is documented in the code and planned for a future update.

### Persistence
All data is stored as JSON under `~/.easypos/`:
- `menu.json` | Item list
- `categories.json` | Category list 
- `orders.json` | Active orders 
- `old_orders.json` | Last 100 completed orders 
- `environment.json` | Server and app config 
It is reccomended that you **DO NOT** modify these files manually, instead use the manage items/categories available in settings. A corrupted or incorrectly formatted file can cause the app to crash. In this case, delete the problematic file.


## Installation
TODO

## Setup Guide
- On launch, EasyPOS opens to the home screen
- Navigate to **Settings** → **Manage Categories** to add your categories
- Navigate to **Settings** → **Manage Items** to add items
- Return home: your categories and items will now appear

## Operating
- A new order is created automatically on first launch or if there are 0 existing active orders
- Click item buttons to add them to the active order
- Use the **Actions** panel on the right to void items, rename orders, checkout, or switch orders
- **Select Orders** shows all active orders as cards: click to select, then choose an action

## Payment Menu
- Shows order name, total due, and coin/note buttons (configurable in Settings)
- Click coin/note buttons as the customer pays the running total updates automatically
- Negative "Amount Remaining" means change is owed to the customer
- Press **Mark Complete** to mark the order complete
- Press **Pay** to finish the order

## Settings & Config
- **Manage Categories / Items**: add, rename, delete
- **Manage Coins / Notes**: configure payment buttons
- **Reset EasyPOS**: wipes all data including data on the disk

## Code Documentation
Javadoc covers all service classes and important models. Generate or view it in `/docs` by opening `index.html` in a browser.

### Method order convention
Within each class, methods follow this order:
1. Init
2. Handlers
3. FXML Handlers
4. Helpers
5. Getters
6. Setters