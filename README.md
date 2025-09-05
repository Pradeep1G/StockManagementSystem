# StockManagementSystem
A web application that manages stocks

#Stock Manager Overview

Stock Manager is a robust Java-based application developed to efficiently manage stock inventories and customer data within a simplified interface. The system provides core functionalities such as adding, updating, and removing stocks, handling customer information, and facilitating stock transactions including purchases and sales. Designed with maintainability and scalability in mind, Stock Manager leverages Oracle database integration for persistent data storage, ensuring data integrity across sessions.

##Key Features
###Stock Management
Add New Stock: Users can add new stocks by specifying key details such as the stock name, symbol, price, and quantity.
*Remove Stock: Enables the deletion of stock entries based on their unique symbol.
*Update Stock: Allows modification of existing stock records, including changes to stock price and available quantity.
*Retrieve Stock: Retrieves existing stock records.
Customer Management
*Retrieve Customer: Retrieves existing customer records.
###Stock Transactions
*Purchase Stocks: Customers can buy stocks from the system. The purchase updates the stock quantity in the inventory and logs the transaction in the customer’s history.
*Sell Stocks: Allows customers to sell previously purchased stocks. The transaction adjusts the stock quantity accordingly and records the sale in the customer's history.
##Data Persistence
Database Integration: The application employs Oracle database tables for persistent storage of stock and customer information.
