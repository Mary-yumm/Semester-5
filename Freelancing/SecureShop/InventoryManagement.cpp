
#include <iostream>
#include <fstream>
#include <string>
#include <iomanip>
using namespace std;

// Function to display the inventory menu
void displayInventoryMenu() {
    cout << "\n=== Inventory Management ===\n";
    cout << "1. Add Product\n";
    cout << "2. Edit Product\n";
    cout << "3. Delete Product\n";
    cout << "4. Display Products\n";
    cout << "5. Search Products\n";
    cout << "6. Back to Main Menu\n";
    cout << "Enter your choice: ";
}

// Function to add a new product
void addProduct() {
    string id, name, category;
    int quantity;
    double price;
    ofstream productFile("products.txt", ios::app); // Open file in append mode
    if (!productFile) {
        cout << "Error opening file!\n";
        return;
    }

    cout << "Enter Product ID: ";
    cin >> id;
    cout << "Enter Product Name: ";
    cin.ignore(); // Clear input buffer
    getline(cin, name);
    cout << "Enter Category: ";
    cin >> category;
    cout << "Enter Price: ";
    cin >> price;
    cout << "Enter Quantity: ";
    cin >> quantity;

    productFile << id << " " << name << " " << category << " " << price << " " << quantity << endl;
    productFile.close();

    cout << "Product added successfully!\n";
}

// Function to display all products
void displayProducts() {
    ifstream productFile("products.txt"); // Open file in read mode
    if (!productFile) {
        cout << "Error opening file!\n";
        return;
    }

    string id, name, category;
    int quantity;
    double price;

    cout << left << setw(10) << "ID" << setw(20) << "Name" << setw(15) << "Category"
         << setw(10) << "Price" << setw(10) << "Quantity" << endl;
    cout << "-------------------------------------------------------------\n";

    while (productFile >> id >> ws && getline(productFile, name, ' ') >> category >> price >> quantity) {
        cout << left << setw(10) << id << setw(20) << name << setw(15) << category
             << setw(10) << price << setw(10) << quantity << endl;
    }

    productFile.close();
}

// Function to search products by category
void searchProductsByCategory() {
    string category, id, name, fileCategory;
    int quantity;
    double price;

    cout << "Enter category to search: ";
    cin >> category;

    ifstream productFile("products.txt");
    if (!productFile) {
        cout << "Error opening file!\n";
        return;
    }

    bool found = false;
    cout << "\nProducts in category '" << category << "':\n";
    cout << left << setw(10) << "ID" << setw(20) << "Name" << setw(15) << "Category"
         << setw(10) << "Price" << setw(10) << "Quantity" << endl;
    cout << "-------------------------------------------------------------\n";

    while (productFile >> id >> ws && getline(productFile, name, ' ') >> fileCategory >> price >> quantity) {
        if (fileCategory == category) {
            found = true;
            cout << left << setw(10) << id << setw(20) << name << setw(15) << fileCategory
                 << setw(10) << price << setw(10) << quantity << endl;
        }
    }

    if (!found) {
        cout << "No products found in this category.\n";
    }

    productFile.close();
}

// Main function for inventory management
void inventoryManagement() {
    int choice;

    while (true) {
        displayInventoryMenu();
        cin >> choice;

        switch (choice) {
            case 1:
                addProduct();
                break;
            case 2:
                cout << "Edit Product functionality not yet implemented.\n";
                break;
            case 3:
                cout << "Delete Product functionality not yet implemented.\n";
                break;
            case 4:
                displayProducts();
                break;
            case 5:
                searchProductsByCategory();
                break;
            case 6:
                cout << "Returning to Main Menu...\n";
                return;
            default:
                cout << "Invalid choice!\n";
        }
    }
}
