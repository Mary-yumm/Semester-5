#ifndef ADMINFEATURES_H
#define ADMINFEATURES_H


void removeUser();

void addUser();

void viewActivityLogs();

void createDiscount();

void removeDiscount();

bool ProductExists(char *productID);

void CreateAuditTrail(char* username,int choice);

void viewAuditTrail();

void bulkImportProducts();

#endif