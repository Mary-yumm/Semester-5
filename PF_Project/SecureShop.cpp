#include <iostream>
#include "UserAuthentication.h"
#include "Inventory.h"
#include "AdminFeatures.h"

using namespace std;

void CustomerMenu(char *username);
void AdminMenu();
void EmployeeMenu();

int main()
{

    char choice;
    while (true)
    {
        bool loggedIn = false;
        cout << "\n\nWelcome to SecureShop!" << endl;
        cout << "Press 'c' to Login as Customer" << endl;
        cout << "Press 'e' to Login as Employee" << endl;
        cout << "Press 'a' to login as an admin" << endl;
        cout << "Press 'q' to Exit" << endl;
        cin >> choice;

        char *username;
        char *failed = new char[1];
        failed[0] = '0';

        switch (choice)
        {
        case 'c':
            username = UserMain('c');
            if (username[0] != failed[0])
                CustomerMenu(username);

            break;
        case 'e':
            username = UserMain('e');
            if (username[0] != failed[0])
                EmployeeMenu();

            break;
        case 'a':
            username = UserMain('a');
            if (username[0] != failed[0])
                AdminMenu();

            break;
        case 'q':
            return 0;
        default:
            cout << "Invalid choice" << endl;
        }

        if (loggedIn)
        {
            break;
        }
    }

    return 0;
}

void CustomerMenu(char *username)
{
    while (true)
    {

        cout << "\n\nCustomer Menu" << endl;
        // cout << "Press 1 to Search for a product" << endl;
        cout << "Press 1 to View all products" << endl;
        cout << "Press 2 to View your order history" << endl;
        cout << "Press 3 to Place an order" << endl;
        cout << "Press 4 for Customer Support" << endl;
        // cout << "Press 6 to View Cart" << endl;
        cout << "Press 5 to View WishList" << endl;
        cout << "Press 6 to give Feedback" << endl;
        cout << "Press 7 to Logout" << endl
             << endl;
        // switch
        char choice;
        cin >> choice;
        switch (choice)
        {
        case '1':
            viewAllProducts();
            break;
        case '2':
            viewOrderHistory(username);
            break;
        case '3':
            PlaceOrder(username);
            break;
        case '4':
            supportRequests(username);
            break;
        case '5':
            viewWishList(username);
            break;
        case '6':
            giveFeedback(username);
            break;
        case '7':
            return;
        default:
            cout << "Invalid choice" << endl;
        }
    }
}

void EmployeeMenu()
{
    while (true)
    {
        cout << "\n\nEmployee Menu" << endl;
        cout << "Press 1 to View all products" << endl;
        cout << "Press 2 to View all orders" << endl;
        cout << "Press 3 to Add a product" << endl;
        cout << "Press 4 to Remove a product" << endl;
        // cout << "Press 5 to Update a product" << endl;
        // cout << "Press 6 to Respond to feedback" << endl;
        cout << "Press 5 to Logout" << endl;
        char choice;
        cin >> choice;
        switch (choice)
        {
        case '1':
            viewAllProducts();
            break;
        case '2':
            viewAllOrders();
            break;
        case '3':
            addProduct();
            break;
        case '4':
            removeProduct();
            break;
        case '5':
            return;
        default:
            cout << "Invalid choice" << endl;
        }
    }
}

void AdminMenu()
{
    // add user
    // add product
    // modify product
    // remove customer
    // remove product
    // create discount
    // two factor autorization
    // activity logs
    // sales and revenue analysis
    while (true)
    {
        cout << "\n\nAdmin Menu" << endl;
        cout << "Press 1 to View all products" << endl;
        cout << "Press 2 to View all orders" << endl;
        cout << "Press 3 to Add a product" << endl;
        cout << "Press 4 to Remove a product" << endl;
        cout << "Press 5 to Add a user" << endl;
        cout << "Press 6 to Remove a user" << endl;
        cout << "Press 7 to View Activity Logs" << endl;
        cout << "Press 8 to Logout" << endl;

        char choice;
        cin >> choice;
        switch (choice)
        {
        case '1':
            viewAllProducts();
            break;
        case '2':
            viewAllOrders();
            break;
        case '3':
            addProduct();
            break;
        case '4':
            removeProduct();
            break;
        case '5':
            addUser();
            break;
        case '6':
            removeUser();
            break;
        case '7':
            viewActivityLogs();
            break;
        case '8':
            return;

        default:
            cout << "Invalid choice" << endl;
        }
    }
}