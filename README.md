**STILL A MAJOR WORK IN PROGRESS**
# Easy POS
A POS software that uses serialisation and a locally run 
server to communicate between devices, allowing
multiple devices to recieve, edit and manage orders in a 
hospitallity industry or similar. It was created using JavaFX 
and a model/controller/service java OOP approach. This is the
largest (non uni) java project that I have made so was a really
good test and learning expierence for my java skills especially 
in Object Oriented Programming

## Installation
TODO

## Guide
### Setup
- On launch, EasyPOS will open to the home screen. This is where orders are taken and is also the main gateway to everywhere else on the software
- Navigate to settings (The last button in the actions section)
- Click on "Manage Categories" and add your desired categories
- Click on "Done" when satisfied with your categories (You can always add more later) and now click on the "Manage Items" button
- Press the "Create Item" button and go through the fields to create an item. Be sure that an item's name is unique and that the price is two decimal points (0.00)
- You can now close the manage items window and on the settings screen press the home button
- You will now see your categories and each item in the selected category

### Operating
- A new order is created and selected when you first open EasyPOS
- To add items to the order, simply click on the corresponding button from the bottom section of the GUI
- "Void Item" in the actions section, will remove the selected item (Or the last item if none are selected)
- You can add a name to the order by pressing the "Change Name" button in the actions section, but this is not required. It's just for ease of identification in the future
- To select a different existing order, click "Select Orders" and select the desired order name or order number (Both are visible on the select order menu)
- To navigate to the payment menu, click on "Checkout Order" in the actions section. This will take you to the payment menu for the currently selected order

### Payment Menu
- See the next section for config of the payment menu
- At the top of the screen, you will see the order name/number and the total due
- You will also see 2 rows of buttons. Each of these represent a coin or a note (This can be configed in settings)
- You can click on these buttons for each note/coin that a customer gives you (You can click multiple as many times as neccesary)
- You will see two number values in the bottom section; Amount Payed, and Amount Remaining
- If Amount Remaining is a positive value (So no negative sign) that means the customer still has to pay that amount to meet the total due
- If it is negative, that's the change that the customer is owed
- You can also enter a custom input in the input marked "Other Value"
- When complete, press the "Mark Complete" button which will mark the order as completed
- This will remove the order from the order selection, but will still be available from past orders selection

### Config
- All the config is done through settings
- As seen in previous sections, you are in charge of configuration of items and their categories
- Other things that you can config is the note and coin buttons on the payment menu aswell as some additional behaviour of the payment menu
- There is also a "Reset EasyPOS" button on the settings page, this will delete all your items, categories, and config. So be careful with this!


## Code Documentation
Javadoc was used to document important parts of the code (Controllers, 
Services, important models etc). You can find this in /docs just simply open the index.html file in a web browser.

### Method structure
The following list is the standard structure of methods/functions in each class, with some exceptions of large classes where this format
was not optimal.
- INIT
- HANDLERS
- FXML HANDLERS
- HELPERS
- GETTERS
- SETTERS