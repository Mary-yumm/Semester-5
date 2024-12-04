#ifndef INVENTORY_H
#define INVENTORY_H

#include <iostream>
#include <fstream>
using namespace std;

void viewAllProducts();

void viewOrderHistory(char *username);

void viewWishList(char *username);

void PlaceOrder(char *username);

double calculateOrderPrice(char *ProdID);

//void updateSalesReport(char *productID, char *orderDate, int quantity, double totalPrice);

void viewAllOrders();

void addProduct();

void removeProduct();

void viewLowStock();

void updateProduct();

void viewSalesReport();

#endif