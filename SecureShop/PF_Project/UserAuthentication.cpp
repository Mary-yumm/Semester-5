#include <iostream>
#include <fstream>

using namespace std;

// Main Menu
void mainMenu()
{
    cout << "Welcome to the SecureShop!" << endl;
    cout << "1. Create Account" << endl;
    cout << "2. Login" << endl;
    cout << "3. Exit" << endl;
    cout << "Enter your choice: ";
}



int main()
{
    int choice = 0;
    while (true)
    {
        mainMenu();
        cin >> choice;
        switch (choice)
        {
        case 1:
            createAccount();
            break;
        case 2:
            login();
            break;
        case 3:
            cout << "Exiting the program." << endl;
            return 0;
        default:
            cout << "Invalid choice. Please try again." << endl;
        }
    }

    return 0;
}