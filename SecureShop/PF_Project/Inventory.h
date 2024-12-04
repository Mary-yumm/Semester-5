#ifndef INVENTORY_H
#define INVENTORY_H

#include <iostream>
#include <fstream>
using namespace std;

void viewAllProducts();

void viewOrderHistory(char *username);

void viewWishList(char *username);

void giveFeedback(char *username);

void supportRequests(char *username);

void PlaceOrder(char *username);

double calculateOrderPrice(char *ProdID);

double stringToDouble(char *str);

//void updateSalesReport(char *productID, char *orderDate, int quantity, double totalPrice);

void viewAllOrders();

void addProduct();

void removeProduct();

void ResolveQuery();

void FeedbackResponse();

void viewLowStock();

void updateProduct();

void viewSalesReport();

#endif