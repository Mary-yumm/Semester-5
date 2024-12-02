
#include <iostream>
#include <fstream>
#include <string>
#include <cstring>
#include <cstdlib>
using namespace std;

// Function to display the main menu
void displayMainMenu() {
    cout << "\n=== SecureShop ===\n";
    cout << "1. Register\n";
    cout << "2. Login\n";
    cout << "3. Exit\n";
    cout << "Enter your choice: ";
}

// Function to register a new user
void registerUser() {
    string username, password, role;
    ofstream userFile("users.txt", ios::app); // Open file in append mode
    if (!userFile) {
        cout << "Error opening file!\n";
        return;
    }

    cout << "Enter username: ";
    cin >> username;
    cout << "Enter password: ";
    cin >> password;
    cout << "Enter role (Customer/Employee/Admin): ";
    cin >> role;

    userFile << username << " " << password << " " << role << endl;
    userFile.close();

    cout << "Registration successful!\n";
}

// Function to login a user
bool loginUser(string& role) {
    string username, password, fileUsername, filePassword, fileRole;
    ifstream userFile("users.txt"); // Open file in read mode
    if (!userFile) {
        cout << "Error opening file!\n";
        return false;
    }

    cout << "Enter username: ";
    cin >> username;
    cout << "Enter password: ";
    cin >> password;

    while (userFile >> fileUsername >> filePassword >> fileRole) {
        if (username == fileUsername && password == filePassword) {
            role = fileRole;
            userFile.close();
            cout << "Login successful!\n";
            return true;
        }
    }

    userFile.close();
    cout << "Invalid username or password!\n";
    return false;
}

// Main function to drive the program
int main() {
    int choice;
    string role;

    while (true) {
        displayMainMenu();
        cin >> choice;

        switch (choice) {
            case 1:
                registerUser();
                break;
            case 2:
                if (loginUser(role)) {
                    cout << "Logged in as " << role << ".\n";
                    // Future actions based on role can be added here
                }
                break;
            case 3:
                cout << "Exiting...\n";
                return 0;
            default:
                cout << "Invalid choice!\n";
        }
    }
}
