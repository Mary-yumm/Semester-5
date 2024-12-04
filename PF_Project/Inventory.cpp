#include <iostream>
#include <fstream>
#include <iomanip>
#include "Inventory.h"
using namespace std;

bool isEqual2(char *str1, char *str2)
{
    int i = 0;

    while (str1[i] != '\0' && str2[i] != '\0')
    {
        if (str1[i] != str2[i])
        {
            return false;
        }
        i++;
    }
    if (str1[i] != '\0' || str2[i] != '\0')
    {
        return false;
    }
    else
    {
        return true;
    }
}

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

        if (isEqual2(username, customer))
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
        if (isEqual2(productID, ProdID))
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

double stringToDouble(char *str)
{
    double result = 0.0;
    int i = 0;
    // Process the integer part
    while (str[i] >= '0' && str[i] <= '9')
    {
        result = result * 10 + (str[i] - '0');
        i++;
    }

    // Process the decimal part if present
    if (str[i] == '.')
    {
        i++;
        double decimalPlace = 0.1;
        while (str[i] >= '0' && str[i] <= '9')
        {
            result += (str[i] - '0') * decimalPlace;
            decimalPlace *= 0.1;
            i++;
        }
    }

    return result;
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
        if (isEqual2(prodID, productID) == 0 && isEqual2(date, orderDate) == 0)
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

        if (isEqual2(username, customer))
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

void giveFeedback(char *username)
{
    ifstream file;
    file.open("txtFiles/Orders.txt");
    if (!file)
    {
        cout << "Unable to open Orders file!" << endl;
        return;
    }

    char *customer = new char[50];
    char *orderID = new char[50];
    char *date = new char[50];
    char *productIDs = new char[200];
    char *quantities = new char[50];
    char *totalPrice = new char[50];
    char productArray[50][50]; // Array to store product IDs
    int productCount = 0;

    cout << "Orders for username: " << username << endl;
    cout << left << setw(15) << "Order ID"
         << setw(15) << "Date"
         << "Product IDs" << endl;

    char charline;
    while (!file.eof())
    {
        // Extract customer name
        int i = 0;
        while ((file >> charline) && charline != '|')
        {
            customer[i++] = charline;
        }
        customer[i] = '\0';

        if (isEqual2(username, customer))
        {
            // Extract order details
            i = 0;
            while ((file >> charline) && charline != '|')
                orderID[i++] = charline;
            orderID[i] = '\0';

            i = 0;
            while ((file >> charline) && charline != '|')
                date[i++] = charline;
            date[i] = '\0';

            i = 0;
            while ((file >> charline) && charline != '|')
                productIDs[i++] = charline;
            productIDs[i] = '\0';

            i = 0;
            while ((file >> charline) && charline != '|')
                quantities[i++] = charline;
            quantities[i] = '\0';

            i = 0;
            while (file.get(charline) && charline != '\n')
                totalPrice[i++] = charline;
            totalPrice[i] = '\0';

            // Display the order
            cout << left << setw(15) << orderID
                 << setw(15) << date
                 << productIDs << endl;

            // Parse and store product IDs
            int j = 0, k = 0;
            while (productIDs[j] != '\0')
            {
                if (productIDs[j] == ',')
                {
                    productArray[productCount][k] = '\0';
                    productCount++;
                    k = 0;
                }
                else
                {
                    productArray[productCount][k] = productIDs[j];
                    k++;
                }
                j++;
            }
            productArray[productCount][k] = '\0';
            productCount++;
        }
        else
        {
            // Skip line if username doesn't match
            while (file.get(charline) && charline != '\n')
            {
            }
        }
    }
    file.close();

    // Check if any products were found
    if (productCount == 0)
    {
        cout << "No orders found for this user. Feedback cannot be provided." << endl;
        delete[] customer;
        delete[] orderID;
        delete[] date;
        delete[] productIDs;
        delete[] quantities;
        delete[] totalPrice;
        return;
    }

    // Ask for product ID feedback
    cout << "\nEnter the Product ID for which you want to give feedback: ";
    char selectedProduct[50];
    cin >> selectedProduct;

    // Check if the entered product ID exists
    bool found = false;
    for (int i = 0; i < productCount; i++)
    {
        if (isEqual2(selectedProduct, productArray[i]))
        {
            found = true;
            break;
        }
    }

    if (!found)
    {
        cout << "Invalid Product ID. Feedback cannot be provided." << endl;
    }
    else
    {
        cout << "Enter your feedback for product " << selectedProduct << ": ";
        cin.ignore(); // Clear input buffer
        char feedback[200];
        cin.getline(feedback, 200);

        // Save feedback to file
        ofstream feedbackFile;
        feedbackFile.open("txtFiles/Feedback.txt", ios::app);
        if (!feedbackFile)
        {
            cout << "Unable to open Feedback file!" << endl;
        }
        else
        {
            feedbackFile << username << "|" << selectedProduct << "|" << feedback << endl;
            cout << "Your feedback has been recorded successfully!" << endl;
            feedbackFile.close();
        }
    }

    delete[] customer;
    delete[] orderID;
    delete[] date;
    delete[] productIDs;
    delete[] quantities;
    delete[] totalPrice;
}

void supportRequests(char *username)
{
    cout << "Support Requests for " << username << ":\n";

    char *request = new char[200];
    cout << "Enter your request message: ";
    cin.ignore(); // To clear any previous input buffer before getline
    cin.getline(request, 200);

    ofstream file;
    file.open("txtFiles/Support.txt", ios::app);
    if (!file)
    {
        cout << "Error opening the file for writing!" << endl;
        delete[] request;
        return;
    }

    file << username << "|" << request << "|Pending" << endl;

    cout << "Your support request has been submitted with the status 'Pending'.\n";

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

    char *productID = new char[50];
    cin >> productID;

    ifstream file("txtFiles/Inventory.txt");
    ofstream tempFile("txtFiles/tempInventory.txt");

    if (!file || !tempFile)
    {
        cout << "Error opening the file!" << endl;
        delete[] productID;
        return;
    }

    string line;
    bool found = false;

    while (getline(file, line))
    {
        // Extract Product ID from the line
        int i = 0;
        char extractedID[50];
        while (line[i] != '|' && i < line.length())
        {
            extractedID[i] = line[i];
            i++;
        }
        extractedID[i] = '\0';

        // If the product ID matches, skip writing it to the temp file
        if (isEqual2(extractedID, productID))
        {
            found = true; // Mark that we found the product to be removed
        }
        else
        {
            tempFile << line << endl; // Otherwise, write the line to the temp file
        }
    }

    file.close();
    tempFile.close();

    if (!found)
    {
        cout << "Product with ID " << productID << " not found!" << endl;
    }
    else
    {
        // Replace the original Inventory.txt with the updated one
        remove("txtFiles/Inventory.txt");
        rename("txtFiles/tempInventory.txt", "txtFiles/Inventory.txt");
        cout << "Product with ID " << productID << " has been removed." << endl;
    }

    delete[] productID;
}

