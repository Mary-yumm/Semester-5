#include <iostream>
#include <fstream>
#include <iomanip>
#include "Inventory.h"
#include "utilities.h"

using namespace std;

void viewAllProducts()
{
    cout << "\nHere are all the products we have in stock:" << endl;
    cout << left << setw(15) << "Product ID"
         << setw(20) << "Name"
         << setw(20) << "Category"
         << setw(10) << "Price"
         << setw(10) << "Quantity" << endl;

    ifstream file;
    file.open("txtFiles/Inventory.txt");
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return;
    }
    cin.ignore();
    char charline;
    char *ProductID = new char[50];
    char *name = new char[50];
    char *category = new char[50];
    char *price = new char[50];
    char *quantity = new char[50];

    // skip first header line
    while (file.get(charline) && charline != '\n')
    {
    }
    while (!file.eof())
    {
        // extract product id
        int i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            ProductID[i] = charline;
            i++;
        }
        ProductID[i] = '\0';
        // extract name
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            name[i] = charline;
            i++;
        }
        name[i] = '\0';
        // extract category
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            category[i] = charline;
            i++;
        }
        category[i] = '\0';
        // extract price
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            price[i] = charline;
            i++;
        }
        price[i] = '\0';

        // extract quantity
        i = 0;
        while (file.get(charline) && charline != '\n')
        {
            quantity[i] = charline;
            i++;
        }
        quantity[i] = '\0';

        //  Print the extracted product details with formatting
        cout << left << setw(15) << ProductID
             << setw(20) << name
             << setw(20) << category
             << setw(10) << price
             << setw(10) << quantity << endl;
    }

    delete[] ProductID;
    delete[] name;
    delete[] category;
    delete[] price;
    delete[] quantity;

    file.close();
}

void viewOrderHistory(char *username)
{
    cout << "Username: " << username << endl;
    ifstream file;
    file.open("txtFiles/Orders.txt");
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return;
    }
    char *customer = new char[50];
    char *orderID = new char[50];
    char *date = new char[50];
    char *productIds = new char[50];
    char *quantities = new char[50];
    char *totalPrice = new char[50];

    // Print table headers
    cout << left << setw(15) << "Customer Name"
         << setw(15) << "Order ID"
         << setw(15) << "Date"
         << setw(20) << "Product IDs"
         << setw(15) << "Quantities"
         << setw(10) << "Total Price" << endl;

    char charline;
    while (!file.eof())
    {
        // skip first header line
        while (file.get(charline) && charline != '\n')
        {
        }
        int i = 0;

        // extract customer username
        while ((file >> charline) && charline != '|')
        {
            customer[i] = charline;
            i++;
        }
        customer[i] = '\0';

        if (isEqual(username, customer))
        {

            // extract order id
            i = 0;
            while ((file >> charline) && charline != '|')
            {
                orderID[i] = charline;
                i++;
            }
            orderID[i] = '\0';
            // extract date
            i = 0;
            while ((file >> charline) && charline != '|')
            {
                date[i] = charline;
                i++;
            }
            date[i] = '\0';
            // extract product ids
            i = 0;
            while ((file >> charline) && charline != '|')
            {
                productIds[i] = charline;
                i++;
            }
            productIds[i] = '\0';
            // extract quantities
            i = 0;
            while ((file >> charline) && charline != '|')
            {
                quantities[i] = charline;
                i++;
            }
            quantities[i] = '\0';
            // extract total price
            i = 0;
            while (file.get(charline) && charline != '\n')
            {
                totalPrice[i] = charline;
                i++;
            }
            totalPrice[i] = '\0';

            // Print the extracted data in a formatted table
            cout << left << setw(15) << customer
                 << setw(15) << orderID
                 << setw(15) << date
                 << setw(20) << productIds
                 << setw(15) << quantities
                 << setw(10) << totalPrice << endl;
        }
        else
        {
            // skip the whole line if username doesnt match
            while (file.get(charline) && charline != '\n')
            {
            }
        }
    }
    delete[] orderID;
    delete[] date;
    delete[] productIds;
    delete[] quantities;
    delete[] totalPrice;

    file.close();
    return;
}

void PlaceOrder(char *username)
{
    // View products and select one to order
    viewAllProducts();

    cout << "\nEnter the product ID of the product you want to order: ";
    char *productID = new char[50];
    cin.getline(productID, 50);

    cout << "Enter the quantity you want to order: ";
    int quantity;
    cin >> quantity;

    cin.ignore(); // To clear the newline left by cin

    cout << "Enter the date of the order (YYYY-MM-DD): ";
    char *orderDate = new char[50];
    cin.getline(orderDate, 50);

    cout << "Enter Order ID: ";
    char *orderID = new char[50];
    cin.getline(orderID, 50);

    // Calculate the price of the product
    double price = calculateOrderPrice(productID);
    double totalPrice = price * quantity;
    cout << "Total price: $" << totalPrice << endl;

    ofstream file;
    file.open("txtFiles/Orders.txt", ios::app); // Open in append mode

    if (file.is_open())
    {
        file << username << "|";
        file << orderID << "|";
        file << orderDate << "|";
        file << productID << "|";
        file << quantity << "|";
        file << totalPrice << endl;
        file.close();
        cout << "Order placed successfully!" << endl;
    }
    else
    {
        cout << "Error opening Orders.txt file!" << endl;
    }

    // updateSalesReport(productID, orderDate, quantity, totalPrice);

    delete[] productID;
    delete[] orderDate;
    delete[] orderID;
}

double calculateOrderPrice(char *ProdID)
{
    ifstream file;
    file.open("txtFiles/Inventory.txt");

    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return -1;
    }

    char charline;
    char *productID = new char[50];
    char *priceStr = new char[50];
    double price = 0.0;

    // Skip header line
    while (file.get(charline) && charline != '\n')
    {
    }

    while (!file.eof())
    {
        // Extract Product ID
        int i = 0;
        while (file >> charline && charline != '|')
        {
            productID[i] = charline;
            i++;
        }
        productID[i] = '\0';

        // If Product ID matches the given ProdID, extract the price
        if (isEqual(productID, ProdID))
        {
            // Skip to the price field
            for (int j = 0; j < 2; j++)
            {
                while (file.get(charline) && charline != '|')
                {
                }
            }

            // Extract price
            i = 0;
            while (file.get(charline) && charline != '|')
            {
                priceStr[i] = charline;
                i++;
            }
            priceStr[i] = '\0';

            price = stringToDouble(priceStr);
            cout << "String: " << priceStr << endl;
            cout << "double: " << price << endl;
            break;
        }
        else
        {
            // Skip the rest of the line if Product ID doesn't match
            while (file.get(charline) && charline != '\n')
            {
            }
        }
    }

    delete[] productID;
    delete[] priceStr;

    file.close();

    return price;
}

/*
void updateSalesReport(char *productID, char *orderDate, int quantity, double totalPrice)
{
    // Open SalesReport.txt for reading and writing
    ifstream file("txtFiles/SalesReport.txt");
    ofstream tempFile("txtFiles/tempSalesReport.txt");

    char* line = new char[256];
    bool found = false;

    // Read through the file and check for an existing entry
    while (file.getline(line, 256))
    {
        char date[50], prodID[50], prodName[50];
        char quantitySoldStr[50], totalRevenueStr[50];
        int quantitySold = 0;
        double totalRevenue = 0.0;

        int i = 0;
         // skip first header line
        while (file.get(line) && charline != '\n')
        {
        }
        // Parse the line manually using char[] arrays
        // Parse date
        while (line[i] != '|' && line[i] != '\0')
        {
            date[i] = line[i];
            i++;
        }
        date[i] = '\0';
        i++;

        // Parse product ID
        int j = 0;
        while (line[i] != '|' && line[i] != '\0')
        {
            prodID[j++] = line[i++];
        }
        prodID[j] = '\0';
        i++;

        // Parse product name
        j = 0;
        while (line[i] != '|' && line[i] != '\0')
        {
            prodName[j++] = line[i++];
        }
        prodName[j] = '\0';
        i++;

        // Parse quantity sold
        j = 0;
        while (line[i] != '|' && line[i] != '\0')
        {
            quantitySoldStr[j++] = line[i++];
        }
        quantitySoldStr[j] = '\0';
        quantitySold = stringToDouble(quantitySoldStr); // Convert to integer manually
        i++;

        // Parse total revenue
        j = 0;
        while (line[i] != '|' && line[i] != '\0')
        {
            totalRevenueStr[j++] = line[i++];
        }
        totalRevenueStr[j] = '\0';
        totalRevenue = stringToDouble(totalRevenueStr); // Convert to double manually

        // If the product ID and date match, update the sales record
        if (isEqual(prodID, productID) == 0 && isEqual(date, orderDate) == 0)
        {
            quantitySold += quantity;
            totalRevenue += totalPrice;
            found = true;
            // Write updated record to temp file
            tempFile << date << "|" << prodID << "|" << prodName << "|"
                     << quantitySold << "|" << totalRevenue << endl;
            cout<<"Updated SalesReport.txt"<<endl;
        }
        else
        {
            // Otherwise, write the line as it is
            tempFile << line << endl;
        }
    }

    file.close();
    tempFile.close();

    // Replace the original SalesReport.txt with the updated one
    remove("txtFiles/SalesReport.txt");
    rename("txtFiles/tempSalesReport.txt", "txtFiles/SalesReport.txt");
}
*/

void viewWishList(char *username)
{
    cout << "Username: " << username << endl;

    ifstream file;
    file.open("txtFiles/Wishlist.txt");
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return;
    }

    char *customer = new char[50];
    char *productIDs = new char[200];

    // Print table headers
    cout << left << setw(15) << "Username"
         << "Product IDs" << endl;

    char charline;
    while (!file.eof())
    {
        // Extract username
        int i = 0;
        while ((file >> charline) && charline != '|')
        {
            customer[i] = charline;
            i++;
        }
        customer[i] = '\0';

        if (isEqual(username, customer))
        {
            // Extract product IDs
            i = 0;
            while (file.get(charline) && charline != '\n')
            {
                productIDs[i] = charline;
                i++;
            }
            productIDs[i] = '\0';

            // Print the extracted data
            cout << left << setw(15) << customer
                 << productIDs << endl;
        }
        else
        {
            // Skip the line if username doesn't match
            while (file.get(charline) && charline != '\n')
            {
            }
        }
    }

    delete[] customer;
    delete[] productIDs;

    file.close();
}

void viewAllOrders()
{
    cout << "\nHere are all the orders:" << endl;
    cout << left << setw(20) << "Customer Name"
         << setw(15) << "Order ID"
         << setw(15) << "Date"
         << setw(30) << "Product IDs"
         << setw(20) << "Quantities"
         << setw(15) << "Total Price" << endl;

    ifstream file;
    file.open("txtFiles/Orders.txt");
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return;
    }
    cin.ignore();
    char charline;
    char *customerName = new char[50];
    char *orderID = new char[50];
    char *orderDate = new char[50];
    char *productIDs = new char[100];
    char *quantities = new char[100];
    char *totalPrice = new char[50];

    // Skip the first header line
    while (file.get(charline) && charline != '\n')
    {
    }

    while (!file.eof())
    {
        // Extract customer name
        int i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            customerName[i] = charline;
            i++;
        }
        customerName[i] = '\0';
        i++;

        // Extract order ID
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            orderID[i] = charline;
            i++;
        }
        orderID[i] = '\0';
        i++;

        // Extract order date
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            orderDate[i] = charline;
            i++;
        }
        orderDate[i] = '\0';
        i++;

        // Extract product IDs
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            productIDs[i] = charline;
            i++;
        }
        productIDs[i] = '\0';
        i++;

        // Extract quantities
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            quantities[i] = charline;
            i++;
        }
        quantities[i] = '\0';
        i++;

        // Extract total price
        i = 0;
        while (file.get(charline) && charline != '\n')
        {
            totalPrice[i] = charline;
            i++;
        }
        totalPrice[i] = '\0';

        // Print the extracted order details with formatting
        cout << left << setw(20) << customerName
             << setw(15) << orderID
             << setw(15) << orderDate
             << setw(30) << productIDs
             << setw(20) << quantities
             << setw(15) << totalPrice << endl;
    }

    delete[] customerName;
    delete[] orderID;
    delete[] orderDate;
    delete[] productIDs;
    delete[] quantities;
    delete[] totalPrice;

    file.close();
}

void addProduct()
{
    cout << "Enter the details of the new product:\n";

    char *productID = new char[50];
    char *name = new char[50];
    char *category = new char[50];
    char *price = new char[50];
    char *stockQuantity = new char[50];

    cout << "Product ID: ";
    cin >> productID;
    cout << "Name: ";
    cin.ignore(); // To clear the input buffer before getline
    cin.getline(name, 50);
    cout << "Category: ";
    cin.getline(category, 50);
    cout << "Price: ";
    cin >> price;
    cout << "Stock Quantity: ";
    cin >> stockQuantity;

    ofstream file;
    file.open("txtFiles/Inventory.txt", ios::app);
    if (!file)
    {
        cout << "Error opening the file for writing!" << endl;
        delete[] productID;
        delete[] name;
        delete[] category;
        delete[] price;
        delete[] stockQuantity;
        return;
    }

    // Append the new product to the file
    file << productID << "|" << name << "|" << category << "|"
         << price << "|" << stockQuantity << endl;

    cout << "The new product has been added to the inventory.\n";

    file.close();

    delete[] productID;
    delete[] name;
    delete[] category;
    delete[] price;
    delete[] stockQuantity;
}

void removeProduct()
{
    cout << "Enter the Product ID of the product you want to remove: ";

    // Input Product ID
    char *productID = new char[50];
    cin >> productID;

    // Open files
    ifstream file("txtFiles/Inventory.txt");
    ofstream tempFile("txtFiles/tempInventory.txt");

    if (!file || !tempFile)
    {
        cout << "Error opening the file!" << endl;
        delete[] productID;
        return;
    }

    char charline;
    char *extractedID = new char[50];
    char *header = new char[500];
    bool found = false;

    // Read and copy the header
    int i = 0;
    while (file.get(charline) && charline != '\n')
    {
        header[i] = charline;
        i++;
    }
    header[i] = '\0';
    tempFile << header << endl;

    // Read each line from the file
    while (!file.eof())
    {
        i = 0;

        // Extract product ID
        while ((file.get(charline)) && (charline != '|'))
        {
            extractedID[i] = charline;
            i++;
        }
        extractedID[i] = '\0';

        // If the product ID matches, skip the line
        if (isEqual(extractedID, productID))
        {
            found = true;

            // Skip the rest of the line
            while (file.get(charline) && charline != '\n')
            {
                // Do nothing
            }
        }
        else
        {
            // If not matched, write the entire line to the temp file
            tempFile << extractedID << "|";

            // Copy remaining fields
            while (file.get(charline))
            {
                tempFile.put(charline);
                if (charline == '\n')
                    break;
            }
        }
    }

    file.close();
    tempFile.close();

    // Replace the original file with the updated one
    if (found)
    {
        remove("txtFiles/Inventory.txt");
        rename("txtFiles/tempInventory.txt", "txtFiles/Inventory.txt");
        cout << "Product with ID " << productID << " has been removed." << endl;
    }
    else
    {
        cout << "Product with ID " << productID << " not found!" << endl;
    }

    delete[] productID;
    delete[] extractedID;
    delete[] header;
}

void viewSalesReport()
{
    cout << "\nHere is the sales report:" << endl;
    cout << left << setw(15) << "Date"
         << setw(15) << "Product ID"
         << setw(25) << "Product Name"
         << setw(15) << "Quantity Sold"
         << setw(15) << "Total Revenue" << endl;

    ifstream file;
    file.open("txtFiles/SalesReport.txt");
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return;
    }
    char charline;
    char *date = new char[20];
    char *productID = new char[50];
    char *productName = new char[50];
    char *quantitySold = new char[50];
    char *totalRevenue = new char[50];

    // Skip the first header line
    while (file.get(charline) && charline != '\n')
    {
    }

    while (!file.eof())
    {
        // Extract date
        int i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            date[i] = charline;
            i++;
        }
        date[i] = '\0';
        i++;

        // Extract product ID
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            productID[i] = charline;
            i++;
        }
        productID[i] = '\0';
        i++;

        // Extract product name
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            productName[i] = charline;
            i++;
        }
        productName[i] = '\0';
        i++;

        // Extract quantity sold
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            quantitySold[i] = charline;
            i++;
        }
        quantitySold[i] = '\0';
        i++;

        // Extract total revenue
        i = 0;
        while (file.get(charline) && charline != '\n')
        {
            totalRevenue[i] = charline;
            i++;
        }
        totalRevenue[i] = '\0';

        // Print the extracted sales details with formatting
        cout << left << setw(15) << date
             << setw(15) << productID
             << setw(25) << productName
             << setw(15) << quantitySold
             << setw(15) << totalRevenue << endl;
    }

    delete[] date;
    delete[] productID;
    delete[] productName;
    delete[] quantitySold;
    delete[] totalRevenue;

    file.close();
}

void viewLowStock()
{
    cout << "\nHere are the products with low stock (less than 3 units):" << endl;
    cout << left << setw(15) << "Product ID"
         << setw(25) << "Name"
         << setw(20) << "Category"
         << setw(10) << "Price"
         << setw(15) << "Stock Quantity" << endl;

    ifstream file;
    file.open("txtFiles/Inventory.txt"); // Assuming Products.txt contains the product data
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return;
    }

    char charline;
    char *productID = new char[20];
    char *productName = new char[50];
    char *category = new char[20];
    char *price = new char[20];
    char *stockQuantity = new char[20];

    // Skip the first header line
    while (file.get(charline) && charline != '\n')
    {
    }

    while (!file.eof())
    {
        // Extract product ID
        int i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            productID[i] = charline;
            i++;
        }
        productID[i] = '\0';
        i++;

        // Extract product name
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            productName[i] = charline;
            i++;
        }
        productName[i] = '\0';
        i++;

        // Extract category
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            category[i] = charline;
            i++;
        }
        category[i] = '\0';
        i++;

        // Extract price
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            price[i] = charline;
            i++;
        }
        price[i] = '\0';
        i++;

        // Extract stock quantity
        i = 0;
        while (file.get(charline) && charline != '\n')
        {
            stockQuantity[i] = charline;
            i++;
        }
        stockQuantity[i] = '\0';

        // Check if stock quantity is less than 3
        int stock = stringToDouble(stockQuantity); // Convert stock quantity to integer
        if (stock < 3)
        {
            // Print the extracted product details with formatting
            cout << left << setw(15) << productID
                 << setw(25) << productName
                 << setw(20) << category
                 << setw(10) << price
                 << setw(15) << stockQuantity << endl;
        }
    }

    file.close();

    delete[] productID;
    delete[] productName;
    delete[] category;
    delete[] price;
    delete[] stockQuantity;
}

void updateProduct()
{
    // Input Product ID
    cin.ignore();
    cout << "Enter the Product ID to update: ";
    char *productIDInput = new char[20];
    cin.getline(productIDInput, 20);

    // Open the file
    ifstream file;
    file.open("txtFiles/Inventory.txt");
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        delete[] productIDInput;
        return;
    }

    // Prepare for updating
    ofstream tempFile;
    tempFile.open("txtFiles/TempProducts.txt");
    if (!tempFile)
    {
        cout << "Error creating the temporary file!" << endl;
        file.close();
        delete[] productIDInput;
        return;
    }

    char charline;
    char *productID = new char[20];
    char *productName = new char[50];
    char *category = new char[20];
    char *price = new char[20];
    char *stockQuantity = new char[20];

    // Skip the first header line
    char *header = new char[500];
    int i = 0;
    while (file.get(charline) && charline != '\n')
    {
        header[i] = charline;
        i++;
    }
    header[i] = '\0';

    tempFile << header << endl;

    bool productFound = false;
    bool changed = false;

    while (file.peek() != EOF)
    {
        // Extract product ID
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            productID[i] = charline;
            i++;
        }
        productID[i] = '\0';
        i++;

        // Extract product name
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            productName[i] = charline;
            i++;
        }
        productName[i] = '\0';
        i++;

        // Extract category
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            category[i] = charline;
            i++;
        }
        category[i] = '\0';
        i++;

        // Extract price
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            price[i] = charline;
            i++;
        }
        price[i] = '\0';
        i++;

        // Extract stock quantity
        i = 0;
        while (file.get(charline) && charline != '\n')
        {
            stockQuantity[i] = charline;
            i++;
        }
        stockQuantity[i] = '\0';
        // Check if the product ID matches
        if (isEqual(productID, productIDInput) && changed == false)
        {
            productFound = true;

            // Display options for the user to select what to update
            cout << "\nWhat would you like to update?" << endl;
            cout << "1. Product Name" << endl;
            cout << "2. Category" << endl;
            cout << "3. Price" << endl;
            cout << "4. Stock Quantity" << endl;
            cout << "Enter your choice (1-4): ";

            int choice;
            cin >> choice;
            cin.ignore(); // To clear the input buffer

            switch (choice)
            {
            case 1:
            {
                char *newName = new char[50];
                cout << "Enter new Product Name: ";
                cin.getline(newName, 50);
                tempFile << productID << "|" << newName << "|" << category << "|" << price << "|" << stockQuantity << endl;
                delete[] newName;
                changed = true;
                break;
            }
            case 2:
            {
                char *newCategory = new char[20];
                cout << "Enter new Category: ";
                cin.getline(newCategory, 20);
                tempFile << productID << "|" << productName << "|" << newCategory << "|" << price << "|" << stockQuantity << endl;
                delete[] newCategory;
                changed = true;
                break;
            }
            case 3:
            {
                char *newPrice = new char[20];
                cout << "Enter new Price: ";
                cin.getline(newPrice, 20);
                tempFile << productID << "|" << productName << "|" << category << "|" << newPrice << "|" << stockQuantity << endl;
                delete[] newPrice;
                changed = true;
                break;
            }
            case 4:
            {
                char *newStock = new char[20];
                cout << "Enter new Stock Quantity: ";
                cin.getline(newStock, 20);
                tempFile << productID << "|" << productName << "|" << category << "|" << price << "|" << newStock << endl;
                delete[] newStock;
                changed = true;
                break;
            }
            default:
                cout << "Invalid choice!" << endl;
            }
        }
        else
        {
            // If not the product to update, copy the line to temp file
            tempFile << productID << "|" << productName << "|" << category << "|" << price << "|" << stockQuantity << endl;
        }
    }

    // Close the files
    file.close();
    tempFile.close();

    // Replace original file with updated file
    remove("txtFiles/Inventory.txt");
    rename("txtFiles/TempProducts.txt", "txtFiles/Inventory.txt");

    if (productFound)
    {
        cout << "Product updated successfully!" << endl;
    }
    else
    {
        cout << "Product ID not found!" << endl;
    }

    delete[] productIDInput;
    delete[] productID;
    delete[] productName;
    delete[] category;
    delete[] price;
    delete[] stockQuantity;
}
