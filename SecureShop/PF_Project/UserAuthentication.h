#ifndef USERAUTHENTICATION_H
#define USERAUTHENTICATION_H

#include <iostream>
#include <fstream>
using namespace std;

// Displays the main menu for user operations
void UserMenu(char UserType);

// Registers a new user by collecting details and storing them in a file
void registerUser(char userType);

// Logs in a user by verifying credentials from the user file
char* login();

// Main function for handling user authentication workflows
char* UserMain(char userType);

void createActivity(char usertype);

#endif
