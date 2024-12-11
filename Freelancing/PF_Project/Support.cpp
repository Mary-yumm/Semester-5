#include<iostream>
#include<fstream>
#include<iomanip>
#include "Support.h"
#include "utilities.h"

using namespace std;


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

        if (isEqual(username, customer))
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
        if (isEqual(selectedProduct, productArray[i]))
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
            feedbackFile <<"FXXX |"<< username << "|" << selectedProduct << "|" << feedback << endl;
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

void ResolveQuery()
{
    // Print all queries from txtFiles/Support.txt
    cout << "\nHere are the queries from Support.txt:" << endl;
    cout << left << setw(20) << "Username"
         << setw(50) << "Query"
         << setw(20) << "Status" << endl;

    ifstream file;
    file.open("txtFiles/Support.txt");
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return;
    }

    char charline;
    char *username = new char[50];
    char *query = new char[500];
    char *status = new char[20];

    // Skip the first header line
    while (file.get(charline) && charline != '\n')
    {
    }

    while (!file.eof())
    {
        // Extract username
        int i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            username[i] = charline;
            i++;
        }
        username[i] = '\0';
        i++;

        // Extract query
        i = 0;
        while ((file.get(charline)) && (charline != '|'))
        {
            query[i] = charline;
            i++;
        }
        query[i] = '\0';
        i++;

        // Extract status
        i = 0;
        while (file.get(charline) && charline != '\n')
        {
            status[i] = charline;
            i++;
        }
        status[i] = '\0';

        // Print the extracted query details with formatting
        cout << left << setw(20) << username
             << setw(50) << query
             << setw(20) << status << endl;
    }

    file.close();

    // Resolve a query
    cout << "\nEnter the username whose query you want to resolve: ";
    char *resolveUsername = new char[50];
    cin.ignore();
    cin.getline(resolveUsername, 50);

    cout << "Enter message to resolve the query: ";
    char *resolveMessage = new char[500];
    cin.getline(resolveMessage, 500);

    // Rewrite the file with the updated status for the resolved query
    ifstream readFile("txtFiles/Support.txt");
    ofstream tempFile("txtFiles/TempSupport.txt");

    if (!readFile || !tempFile)
    {
        cout << "Error processing the file!" << endl;
        delete[] username;
        delete[] query;
        delete[] status;
        delete[] resolveUsername;
        delete[] resolveMessage;
        return;
    }

    // Rewrite the header line
    char headerLine;
    // Skip the first header line
    char *header = new char[500];
    int i = 0;
    while (readFile.get(headerLine) && headerLine != '\n')
    {
        header[i] = headerLine;
        i++;
    }
    header[i] = '\0';
    tempFile << header << endl;

    bool queryResolved = false;
    while (!readFile.eof())
    {
        char *fileUsername = new char[50];
        char *fileQuery = new char[500];
        char *fileStatus = new char[50];

        // Extract username
        i = 0;
        while ((readFile >> headerLine) && (headerLine != '|'))
        {
            fileUsername[i] = headerLine;
            i++;
        }
        fileUsername[i] = '\0';
        i++;

        // Extract query
        i = 0;
        while ((readFile.get(headerLine)) && (headerLine != '|'))
        {
            fileQuery[i] = headerLine;
            i++;
        }
        fileQuery[i] = '\0';
        i++;

        // Extract status
        i = 0;
        while (readFile.get(headerLine) && headerLine != '\n')
        {
            fileStatus[i] = headerLine;
            i++;
        }
        fileStatus[i] = '\0';

        if (isEqual(fileUsername, resolveUsername) && !queryResolved)
        {
            tempFile << fileUsername << "|" << fileQuery << "|Resolved: " << resolveMessage << endl;
            queryResolved = true;
        }
        else
        {
            tempFile << fileUsername << "|" << fileQuery << "|" << fileStatus << endl;
        }
    }

    readFile.close();
    tempFile.close();

    // Replace original file with the updated one
    remove("txtFiles/Support.txt");
    rename("txtFiles/TempSupport.txt", "txtFiles/Support.txt");

    if (queryResolved)
    {
        cout << "The query for user " << resolveUsername << " has been resolved.\n";
    }
    else
    {
        cout << "No query found for user " << resolveUsername << ".\n";
    }

    delete[] username;
    delete[] query;
    delete[] status;
    delete[] resolveUsername;
    delete[] resolveMessage;
    delete[] header;
}

void FeedbackResponse()
{
    // Print all feedback from txtFiles/Feedback.txt
    cout << "\nHere is the feedback from Feedback.txt:" << endl;
    cout << left << setw(15) << "Feedback ID"
         << setw(15) << "Username"
         << setw(15) << "Product ID"
         << setw(50) << "Feedback" << endl;

    ifstream file;
    file.open("txtFiles/Feedback.txt");
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return;
    }

    char charline;
    char *feedbackID = new char[20];
    char *username = new char[50];
    char *productID = new char[20];
    char *feedback = new char[500];

    // Skip the first header line
    while (file.get(charline) && charline != '\n')
    {
    }

    while (!file.eof())
    {
        // Extract feedback ID
        int i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            feedbackID[i] = charline;
            i++;
        }
        feedbackID[i] = '\0';
        i++;

        // Extract username
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            username[i] = charline;
            i++;
        }
        username[i] = '\0';
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

        // Extract feedback
        i = 0;
        while (file.get(charline) && charline != '\n')
        {
            feedback[i] = charline;
            i++;
        }
        feedback[i] = '\0';

        // Print the extracted feedback details with formatting
        cout << left << setw(15) << feedbackID
             << setw(15) << username
             << setw(15) << productID
             << setw(50) << feedback << endl;
    }

    file.close();

    // Respond to feedback
    cout << "\nEnter the Feedback ID you want to respond to: ";
    char *resolveFeedbackID = new char[20];
    cin.ignore();
    cin.getline(resolveFeedbackID, 20);

    cout << "Enter your response: ";
    char *responseMessage = new char[500];
    cin.getline(responseMessage, 500);

    // Write the response to FeedbackResponse.txt
    ofstream responseFile("txtFiles/FeedbackResponse.txt", ios::app);
    if (!responseFile)
    {
        cout << "Error opening the response file!" << endl;
        delete[] feedbackID;
        delete[] username;
        delete[] productID;
        delete[] feedback;
        delete[] resolveFeedbackID;
        delete[] responseMessage;
        return;
    }

    // Append the response to the file
    responseFile
        << resolveFeedbackID << "|"
        << responseMessage << endl;

    responseFile.close();
    cout << "Your response has been recorded.\n";

    delete[] feedbackID;
    delete[] username;
    delete[] productID;
    delete[] feedback;
    delete[] resolveFeedbackID;
    delete[] responseMessage;
}
