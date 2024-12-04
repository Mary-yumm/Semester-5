#include <iostream>
#include <fstream>
#include "iomanip"
#include "AdminFeatures.h"
using namespace std;

bool isEqual3(char *str1, char *str2)
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

void removeUser()
{
    cout << "Enter the Username of the user you want to remove: ";

    char *username = new char[50];
    cin >> username;

    // Open Users.txt for reading and create a temporary file for writing
    ifstream file("txtFiles/users.txt");
    ofstream tempFile("txtFiles/tempUsers.txt");

    if (!file || !tempFile)
    {
        cout << "Error opening the file!" << endl;
        delete[] username;
        return;
    }

    string line;
    bool found = false;

    while (getline(file, line))
    {
        // Extract Username from the line
        int i = 0;
        char extractedUsername[50];
        while (line[i] != '|' && i < line.length())
        {
            extractedUsername[i] = line[i];
            i++;
        }
        extractedUsername[i] = '\0';

        // If the username matches, skip writing it to the temp file
        if (isEqual3(extractedUsername, username))
        {
            found = true; // Mark that we found the user to be removed
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
        cout << "User with username " << username << " not found!" << endl;
    }
    else
    {
        // Replace the original Users.txt with the updated one
        remove("txtFiles/users.txt");
        rename("txtFiles/tempUsers.txt", "txtFiles/users.txt");
        cout << "User with username " << username << " has been removed." << endl;
    }

    delete[] username;
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
    file << username << "|" << password << "|" << role << "|"
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
    char *details = new char[100];

    // Print table headers
    cout << left << setw(20) << "Timestamp"
         << setw(20) << "Action"
         << setw(20) << "Performed By"
         << setw(50) << "Details" << endl;

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
        while ((file >> charline) && charline != '|')
        {
            action[i] = charline;
            i++;
        }
        action[i] = '\0';

        // extract performedBy
        i = 0;
        while ((file >> charline) && charline != '|')
        {
            performedBy[i] = charline;
            i++;
        }
        performedBy[i] = '\0';

        // extract details
        i = 0;
        while (file.get(charline) && charline != '\n')
        {
            details[i] = charline;
            i++;
        }
        details[i] = '\0';

        // Print the extracted data in a formatted table
        cout << left << setw(20) << timestamp
             << setw(20) << action
             << setw(20) << performedBy
             << setw(50) << details << endl;
    }

    delete[] timestamp;
    delete[] action;
    delete[] performedBy;
    delete[] details;

    file.close();
    return;
}