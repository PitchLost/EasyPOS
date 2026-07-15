# EasyPOS
A point-of-sale system built in Java/JavaFX for the hospitality industry or similar. Supports multiple terminals communicating over a locally-run HTTP server, with persistent storage via JSON serialisation.

Built as a personal project to deepen my understanding of Java OOP, REST architecture, and JavaFX. It is the largest non-university Java project I've undertaken.

## Installation

### Running from Release (Windows):
1. Go to the [Releases](https://github.com/PitchLost/EasyPOS/releases) page and download the latest `.zip`
2. Extract the zip to your desired location
3. Open the extracted folder and double click `EasyPOS.exe`
4. If Windows SmartScreen blocks it, click **More info** → **Run anyway**, this is normal for unsigned open source software

### Running from Source Code:
1. Download the source code by either:
   - Cloning the repository: `git clone https://github.com/PitchLost/EasyPOS.git`
   - Downloading the zip from GitHub and extracting at the desired location
2. Open a terminal in the newly created folder
3. Run `./gradlew run` to launch EasyPOS
4. To build a jar file run `./gradlew jar`. The output will be at `build/libs/`

### Requirements (Source only):
- Java 21 or later [Download here](https://adoptium.net)
- No additional dependencies needed. Gradle handles everything automatically

## Setup Guide
- On launch, EasyPOS opens to the home screen
- Navigate to **Settings** → **Manage Categories** to add your categories
- Navigate to **Settings** → **Manage Items** to add items
- Return home: your categories and items will now appear

## Tech Stack
- **Java**
- **JavaFX** for the UI
- **Gson** for JSON serialisation/deserialisation
- **Java HttpServer** (`com.sun.net.httpserver`) for the local REST API
- **Gradle** as the build tool

## Architecture Overview
EasyPOS follows a **Model / Service / Controller** pattern:
- **Models**: Pure data objects.
- **Services**: "Backend" logic and persistence. 
- **Controllers**: JavaFX FXML controllers, one per screen.
- **Server**: Local HTTP server with individual handlers.
- (FXML files can be found in main/resources/FXML)
### Networking
Devices operate in one of two modes configured at startup:
- **Host mode**: Runs a local HTTP server, acts as the single source of truth for all order data
- **Client mode**: Connects to the host and polls for order updates every 2 seconds via `GET /orders`

In this architecture, only the host can create or modify orders. Client devices (e.g. kitchen displays) are read-only and automatically reflect any changes made on the host.

> **Known limitation:** the current polling interval is 2 seconds, meaning clients may briefly display stale data. A future improvement would be to replace polling with Server-Sent Events (SSE) for real-time push updates from the host.

> **Planned Upgrade:** At the moment, you can only have one Host and (theoratically) as many order recievers (Clients) as you need. But in the future I will make it so there is still only one Host but each connected Client has the option to either be an order reciever or another POS. To do this some queue implementation will need to be put in place to handle requests from multiple Clients simultaniously. This is planned for release 2.0

### Persistence
All data is stored as JSON under `~/.easypos/`:
- **menu.json**: Item list
- **categories.json**: Category list 
- **orders.json**: Active orders 
- **old_orders.json**: Last 100 completed orders 
- **environment.json**: Server and app config 
It is reccomended that you **DO NOT** modify these files manually, instead use the manage items/categories available in settings. A corrupted or incorrectly formatted file can cause the app to crash. In this case, delete the problematic file.



## Guide
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
> In some cases this is not the order but these have been commented with their reasons

## Screenshots
### Home/Main Screen
<img width="1919" height="1030" alt="image" src="https://github.com/user-attachments/assets/cd0e40d4-d4f4-431f-99ad-f3801823b809" />

### Order selection screen
<img width="1906" height="1018" alt="image" src="https://github.com/user-attachments/assets/bdec6ec7-e48a-47a8-878e-6ff7e6391049" />

### Settings
<img width="1915" height="1027" alt="image" src="https://github.com/user-attachments/assets/42d04a31-0098-4f79-bee1-82b5931a46bb" />

### Manage Categories
<img width="1916" height="1029" alt="image" src="https://github.com/user-attachments/assets/b69e7ba9-6333-4a09-bdbd-310c50fd0f67" />

### Manage Items
<img width="1906" height="1030" alt="image" src="https://github.com/user-attachments/assets/3de65449-cfda-4996-bce8-dc655110ef32" />

### Connection to local server for an orders tablet
<img width="1914" height="1025" alt="image" src="https://github.com/user-attachments/assets/46c28a95-4e1c-46d0-821a-68d388296b0d" />






