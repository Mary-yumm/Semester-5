
#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <iomanip>
using namespace std;

// Structure to hold cart item details
struct CartItem {
    string id;
    string name;
    int quantity;
    double price;
};

vector<CartItem> cart; // Vector to hold items in the cart

// Function to display the cart menu
void displayCartMenu() {
    cout << "\n=== Cart Management ===\n";
    cout << "1. Add Item to Cart\n";
    cout << "2. Remove Item from Cart\n";
    cout << "3. View Cart\n";
    cout << "4. Checkout\n";
    cout << "5. Back to Main Menu\n";
    cout << "Enter your choice: ";
}

// Function to add an item to the cart
void addItemToCart() {
    CartItem item;
    cout << "Enter Product ID: ";
    cin >> item.id;
    cout << "Enter Product Name: ";
    cin.ignore(); // Clear input buffer
    getline(cin, item.name);
    cout << "Enter Quantity: ";
    cin >> item.quantity;
    cout << "Enter Price: ";
    cin >> item.price;

    cart.push_back(item);
    cout << "Item added to cart!\n";
}

// Function to remove an item from the cart
void removeItemFromCart() {
    string id;
    cout << "Enter Product ID to remove: ";
    cin >> id;

    for (size_t i = 0; i < cart.size(); ++i) {
        if (cart[i].id == id) {
            cart.erase(cart.begin() + i);
            cout << "Item removed from cart!\n";
            return;
        }
    }
    cout << "Item not found in cart!\n";
}

// Function to view the cart
void viewCart() {
    if (cart.empty()) {
        cout << "Cart is empty!\n";
        return;
    }

    double total = 0;
    cout << "\nCart Contents:\n";
    cout << left << setw(10) << "ID" << setw(20) << "Name" << setw(10) << "Quantity" << setw(10) << "Price" << endl;
    cout << "---------------------------------------------------\n";

    for (const auto& item : cart) {
        cout << left << setw(10) << item.id << setw(20) << item.name << setw(10) << item.quantity
             << setw(10) << item.price << endl;
        total += item.quantity * item.price;
    }

    cout << "---------------------------------------------------\n";
    cout << "Total: $" << total << "\n";
}

// Function to checkout and log the order
void checkout() {
    if (cart.empty()) {
        cout << "Cart is empty!\n";
        return;
    }

    ofstream orderFile("Orders.txt", ios::app); // Open file in append mode
    if (!orderFile) {
        cout << "Error opening file for order logging!\n";
        return;
    }

    double total = 0;
    orderFile << "New Order:\n";
    for (const auto& item : cart) {
        orderFile << item.id << " " << item.name << " " << item.quantity << " " << item.price << "\n";
        total += item.quantity * item.price;
    }
    orderFile << "Total: $" << total << "\n";
    orderFile << "-----------------------------------\n";

    cart.clear(); // Clear the cart after checkout
    orderFile.close();

    cout << "Checkout complete! Order logged successfully.\n";
}

// Main function for shopping features
void shoppingFeatures() {
    int choice;

    while (true) {
        displayCartMenu();
        cin >> choice;

        switch (choice) {
            case 1:
                addItemToCart();
                break;
            case 2:
                removeItemFromCart();
                break;
            case 3:
                viewCart();
                break;
            case 4:
                checkout();
                break;
            case 5:
                cout << "Returning to Main Menu...\n";
                return;
            default:
                cout << "Invalid choice!\n";
        }
    }
}
