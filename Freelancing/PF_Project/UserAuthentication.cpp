#include <iostream>
#include <fstream>
#include "UserAuthentication.h"
#include "utilities.h"
#include <ctime>

using namespace std;

void UserMenu()
{

    cout << "\nWelcome to the SecureShop!" << endl;
    cout << "1. Create Account" << endl;
    cout << "2. Login" << endl;
    cout << "3. Exit" << endl;
    cout << "Enter your choice: ";
}

void registerUser(char usertype)
{

    char *username = new char[50];
    char *password = new char[50];
    char *email = new char[50];
    char *name = new char[50];

    cin.ignore();
    cout << "Enter username: ";
    cin.getline(username, 50);

    cout << "Enter password: ";
    cin.getline(password, 50);

    cout << "Enter email: ";
    cin.getline(email, 50);

    cout << "Enter name: ";
    cin.getline(name, 50);

    ofstream file;
    file.open("txtFiles/users.txt", ios::app); // open in append mode
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return;
    }

    char *role = new char[50];
    if (usertype == 'c')
        role = "Customer";
    else if (usertype == 'a')
        role = "Admin";
    else if (usertype == 'e')
        role = "Employee";

    // append to file
    file << username << "|" << password << "|" << role << "|" << email << "|" << name << endl;

    cout << "You have been registered successfully!" << endl;
    // cout << username << "|" << password << "|" << role << "|" << email << "|" << name << endl;

    delete[] username;
    delete[] password;
    delete[] email;
    delete[] name;
    file.close();
}

char *login()
{
    char *username = new char[50];
    char *password = new char[50];
    char *returnStr = new char[1];
    returnStr[0] = '0';

    ifstream file;
    file.open("txtFiles/users.txt");
    if (!file)
    {
        cout << "Error opening the file!" << endl;
        return returnStr;
    }

    cin.ignore();
    cout << "Enter username: ";
    cin.getline(username, 50);

    cout << "Enter password: ";
    cin.getline(password, 50);

    char charline;
    char *fileUserName = new char[50];
    char *filePassword = new char[50];
    while (!file.eof())
    {
        fileUserName = new char[50];
        filePassword = new char[50];
        // skip first header line
        while (file.get(charline) && charline != '\n')
        {
        }

        int i = 0;

        // extract username
        while ((file >> charline) && (charline != '|'))
        {
            fileUserName[i] = charline;
            i++;
        }
        fileUserName[i] = '\0';
        // extract password
        i = 0;
        while ((file >> charline) && (charline != '|'))
        {
            filePassword[i] = charline;
            i++;
        }
        filePassword[i] = '\0';
        if (isEqual(username, fileUserName) && isEqual(password, filePassword))
        {
            cout << "\nLogin Successful! Welcome to SecureShope :)" << endl;
            return username;
        }
        delete[] fileUserName;
        delete[] filePassword;
    }
    cout << "\nLogin Unsuccessful!" << endl;

    delete[] username;
    delete[] password;
    file.close();
    return returnStr;
}

char *UserMain(char usertype)
{
    int choice = 0;
    char* returnStr = new char[1];
    returnStr[0] = '0';
    while (true)
    {
        char *username;
        UserMenu();
        cin >> choice;
        switch (choice)
        {
        case 1:
            registerUser(usertype);
            break;
        case 2:
            username = login();
            if(username[0]=='0'){
                createActivity(usertype);
            }

            break;
        case 3:
            cout << "Exiting the user program." << endl;
            return returnStr;
        default:
            cout << "Invalid choice. Please try again." << endl;
        }
        if (username) // logged in
            return username;
    }
}

void createActivity(char usertype) {
    // Get current time
    time_t now = std::time(0);  // Get the current time
    char timestamp[20];
    
    // Format the current time into a string with the format: YYYY-MM-DD HH:MM:SS
    strftime(timestamp, sizeof(timestamp), "%Y-%m-%d %H:%M:%S", std::localtime(&now));
    
    const char* action = "Login Attempt";
    const char* usertypeStr = "";

    // Determine usertype
    if (usertype == 'A' || usertype == 'a') {
        usertypeStr = "Admin interface";
    } else if (usertype == 'E' || usertype == 'e') {
        usertypeStr = "Employee interface";
    } else if (usertype == 'C' || usertype == 'c') {
        usertypeStr = "Customer interface";
    }

    // Open file in append mode
    ofstream file("txtFiles/ActivityLogs.txt", ios::app);

    if (!file) {
        cout << "Error opening the file!" << endl;
        return;
    }

    // Write the log entry to the file
    file << timestamp << "|";
    file << action << "|" << usertypeStr <<endl;

    // Close the file
    file.close();
}