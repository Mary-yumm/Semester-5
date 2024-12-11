#include <iostream>
#include <fstream>
#include "iomanip"
#include "AdminFeatures.h"
#include "utilities.h"
using namespace std;

void removeUser()
{
    cout << "Enter the Username of the user you want to remove: ";

    // Input Username
    char *username = new char[50];
    cin >> username;

    // Open files
    ifstream file("txtFiles/users.txt");
    ofstream tempFile("txtFiles/tempUsers.txt");

    if (!file || !tempFile)
    {
        cout << "Error opening the file!" << endl;
        delete[] username;
        return;
    }

    char charline;
    char *extractedUsername = new char[50];
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

        // Extract Username
        while ((file.get(charline)) && (charline != '|'))
        {
            extractedUsername[i] = charline;
            i++;
        }
        extractedUsername[i] = '\0';

        // If the username matches, skip the line
        if (isEqual(extractedUsername, username))
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
            tempFile << extractedUsername << "|";

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
        remove("txtFiles/users.txt");
        rename("txtFiles/tempUsers.txt", "txtFiles/users.txt");
        cout << "User with username " << username << " has been removed." << endl;
    }
    else
    {
        cout << "User with username " << username << " not found!" << endl;
    }

    delete[] username;
    delete[] extractedUsername;
    delete[] header;
}

void addUser()
{
    cout << "Enter the details of the new user:\n";

    char *username = new char[50];
    char *password = new char[50];
    char *role = new char[50];
    char *email = new char[50];
    char *name = new char[50];

    cout << "Username: ";
    cin >> username;
    cout << "Password: ";
    cin >> password;
    cout << "Role (e.g., Admin, Customer, Employee): ";
    cin >> role;
    cout << "Email: ";
    cin >> email;
    cout << "Full Name: ";
    cin.ignore(); // To clear the input buffer before getline
    cin.getline(name, 50);

    // Open Users.txt for appending
    ofstream file;
    file.open("txtFiles/users.txt", ios::app);
    if (!file)
    {
        cout << "Error opening the file for writing!" << endl;
        delete[] username;
        delete[] password;
        delete[] role;
        delete[] email;
        delete[] name;
        return;
    }

    // Append the new user details to the file
    file << endl
         << username << "|" << password << "|" << role << "|"
         << email << "|" << name << endl;

    cout << "The new user has been added.\n";

    file.close();

    delete[] username;
    delete[] password;
    delete[] role;
    delete[] email;
    delete[] name;
}

void viewActivityLogs()
{
    ifstream file;
    file.open("txtFiles/ActivityLogs.txt");
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return;
    }

    char *timestamp = new char[50];
    char *action = new char[50];
    char *performedBy = new char[50];

    // Print table headers
    cout << left << setw(20) << "Timestamp"
         << setw(20) << "Action"
         << setw(20) << "Performed By" << endl;
    char charline;
    while (!file.eof())
    {
        // skip first header line
        while (file.get(charline) && charline != '\n')
        {
        }

        int i = 0;
        // extract timestamp
        while ((file >> charline) && charline != '|')
        {
            timestamp[i] = charline;
            i++;
        }
        timestamp[i] = '\0';

        // extract action
        i = 0;
        while ((file.get(charline)) && charline != '|')
        {
            action[i] = charline;
            i++;
        }
        action[i] = '\0';

        // extract performedBy
        i = 0;
        while ((file.get(charline)) && charline != '\n')
        {
            performedBy[i] = charline;
            i++;
        }
        performedBy[i] = '\0';

        // Print the extracted data in a formatted table
        cout << left << setw(20) << timestamp
             << setw(20) << action
             << setw(20) << performedBy << endl;
    }

    delete[] timestamp;
    delete[] action;
    delete[] performedBy;

    file.close();
    return;
}

void createDiscount()
{
    char *productID = new char[50];
    char *discount = new char[50];
    cout << "Enter product id: ";
    cin >> productID;
    cout << "Enter discount: ";
    cin >> discount;
    bool found = ProductExists(productID);
    if (found)
    {
        ofstream file("txtFiles/Discount.txt", ios::app);
        file << endl
             << productID << "|" << discount;
    }
    else
    {
        cout << "Product doesn't exist" << endl;
    }
}

bool ProductExists(char *productID)
{
    // Open files
    ifstream file("txtFiles/Inventory.txt");

    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return false;
    }

    char charline;
    char *extractedID = new char[50];
    char *header = new char[500];
    bool found = false;

    int i = 0;
    while (file.get(charline) && charline != '\n')
    {
    }

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
        while (file.get(charline) && charline != '\n')
        {
            // Do nothing
        }

        // If the product ID matches, skip the line
        if (isEqual(extractedID, productID))
        {
            found = true;
            cout << found << endl;
            return true;
        }
    }

    file.close();
    delete[] productID;
    delete[] extractedID;
    delete[] header;
    return false;
}

void removeDiscount()
{
    cout << "Enter the Product ID of the discount you want to remove: ";

    // Input Product ID
    char *productID = new char[50];
    cin >> productID;

    // Open the Discounts.txt file for reading and a temporary file for writing
    ifstream file("txtFiles/Discount.txt");
    ofstream tempFile("txtFiles/tempDiscounts.txt");

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

    // Read and copy the header (First line)
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

        // Extract Product ID from the line
        while ((file.get(charline)) && (charline != '|'))
        {
            extractedID[i] = charline;
            i++;
        }
        extractedID[i] = '\0';

        // If the product ID matches, skip writing it to the temp file
        if (isEqual(extractedID, productID))
        {
            found = true;

            // Skip the rest of the line (the discount part)
            while (file.get(charline) && charline != '\n')
            {
                // Do nothing
            }
        }
        else
        {
            // If the product ID doesn't match, write the entire line to the temp file
            tempFile << extractedID << "|";

            // Copy remaining fields (the discount part)
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

    // Replace the original Discounts.txt file with the updated one
    if (found)
    {
        remove("txtFiles/Discount.txt");
        rename("txtFiles/tempDiscounts.txt", "txtFiles/Discount.txt");
        cout << "Discount for product with ID " << productID << " has been removed." << endl;
    }
    else
    {
        cout << "Discount for product with ID " << productID << " not found!" << endl;
    }

    delete[] productID;
    delete[] extractedID;
    delete[] header;
}

void CreateAuditTrail(char *username, int choice)
{
    // Get current time
    time_t now = std::time(0); // Get the current time
    char timestamp[20];

    // Format the current time into a string with the format: YYYY-MM-DD HH:MM:SS
    strftime(timestamp, sizeof(timestamp), "%Y-%m-%d %H:%M:%S", std::localtime(&now));

    // Set the action message based on the choice
    const char *action = "Unknown action";

    switch (choice)
    {
    case 3:
        action = "Product added successfully.";
        break;
    case 4:
        action = "Product removed successfully.";
        break;
    case 5:
        action = "Product updated successfully.";
        break;
    case 6:
        action = "Feedback Response given successfully.";
        break;
    case 7:
        action = "Resolved a query successfully.";
        break;
    case 10:
        action = "A User added successfully.";
        break;
    case 11:
        action = "A User removed successfully.";
        break;
    case 13:
        action = "Discount created successfully.";
        break;
    case 14:
        action = "Discount removed successfully.";
        break;
    default:
        action = "An unknown action was performed.";
        break;
    }

    // Open the audit trail file in append mode
    ofstream auditTrail("txtFiles/AuditTrail.txt", ios::app);

    if (!auditTrail)
    {
        cout << "Error opening the file!" << endl;
        return;
    }

    // Write the timestamp, action, and performed by information to the file
    auditTrail << timestamp << "|";
    auditTrail << action << "|";
    auditTrail << username << endl;

    // Close the file
    auditTrail.close();
}

void viewAuditTrail()
{
    ifstream file;
    file.open("txtFiles/AuditTrail.txt");
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return;
    }

    char *timestamp = new char[50];
    char *action = new char[100]; // Adjust size for action message
    char *performedBy = new char[50];

    // Print table headers
    cout << left << setw(20) << "Timestamp"
         << setw(50) << "Action"
         << setw(20) << "Performed By" << endl;

    char charline;
    // Skip first header line or empty lines
    while (file.get(charline) && charline != '\n')
    {
    }

    while (!file.eof())
    {

        int i = 0;
        // Extract timestamp
        while ((file >> charline) && charline != '|')
        {
            timestamp[i] = charline;
            i++;
        }
        timestamp[i] = '\0';

        // Extract action
        i = 0;
        while ((file.get(charline)) && charline != '|')
        {
            action[i] = charline;
            i++;
        }
        action[i] = '\0';

        // Extract performedBy
        i = 0;
        while ((file.get(charline)) && charline != '\n')
        {
            performedBy[i] = charline;
            i++;
        }
        performedBy[i] = '\0';

        // Print the extracted data in a formatted table
        cout << left << setw(20) << timestamp
             << setw(50) << action
             << setw(20) << performedBy << endl;
    }

    delete[] timestamp;
    delete[] action;
    delete[] performedBy;

    file.close();
    return;
}

void bulkImportProducts()
{
    cout<<"Enter filename for bulk import: ";
    char* filename = new char[50];
    cin >> filename;
    cout << "Starting bulk import of products from " << filename << "...\n";

    ifstream file;
    file.open(filename);
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return;
    }

    cin.ignore();  // Ignore any leftover newline character
    char charline;
    char *productID = new char[50];
    char *name = new char[50];
    char *category = new char[50];
    char *price = new char[50];
    char *quantity = new char[50];

    // Skip the first header line if it exists
    while (file.get(charline) && charline != '\n') {}

    int importedCount = 0;

    // Read each line from the file
    while (!file.eof())
    {
        // Extract product ID
        int i = 0;
        while ((file >> charline) && charline != '|')
        {
            productID[i] = charline;
            i++;
        }
        productID[i] = '\0';

        // Extract product name
        i = 0;
        while ((file >> charline) && charline != '|')
        {
            name[i] = charline;
            i++;
        }
        name[i] = '\0';

        // Extract category
        i = 0;
        while ((file >> charline) && charline != '|')
        {
            category[i] = charline;
            i++;
        }
        category[i] = '\0';

        // Extract price
        i = 0;
        while ((file >> charline) && charline != '|')
        {
            price[i] = charline;
            i++;
        }
        price[i] = '\0';

        // Extract quantity
        i = 0;
        while (file.get(charline) && charline != '\n')
        {
            quantity[i] = charline;
            i++;
        }
        quantity[i] = '\0';

        // Check if all fields are valid (you can add more validation as needed)
        if (productID[0] != '\0' && name[0] != '\0' && category[0] != '\0' && price[0] != '\0' && quantity[0] != '\0')
        {
            // Add the product to the inventory file
            ofstream outFile;
            outFile.open("txtFiles/Inventory.txt", ios::app);
            if (!outFile)
            {
                cout << "Error opening the inventory file for writing!" << endl;
                continue; // Skip this product if file can't be opened
            }

            // Append the new product details to the file
            outFile << productID << "|" << name << "|" << category << "|"
                    << price << "|" << quantity << endl;

            outFile.close();
            importedCount++;
        }
        else
        {
            cout << "Skipping invalid product data: " << productID << endl;
        }
    }

    delete[] productID;
    delete[] name;
    delete[] category;
    delete[] price;
    delete[] quantity;

    file.close();
    cout << "Bulk import completed. " << importedCount << " products were added to the inventory.\n";
}