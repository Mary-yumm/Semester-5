#include <iostream>
#include <Windows.h>
#include <fstream>  // For file operations
#include <stack>
#include <string>
using namespace std;




// Node structure for the doubly linked list
struct Node {
    char data;
    Node* prev;
    Node* next;
    Node(char ch) : data(ch), prev(nullptr), next(nullptr) {}
};

// Doubly linked list class to manage text
class DoublyLinkedList {
private:
    Node* head;
    Node* tail;
    int size;

public:
    DoublyLinkedList() : head(nullptr), tail(nullptr), size(0) {}

    // Add character to the end of the list
    void append(char ch) {
        Node* newNode = new Node(ch);
        if (!head) {
            head = tail = newNode;
        }
        else {
            tail->next = newNode;
            newNode->prev = tail;
            tail = newNode;
        }
        size++;

       
    }

    // Remove the last character from the list
    void removeLastChar() {
        if (!tail) return;

        if (tail == head) {
            delete tail;
            head = tail = nullptr;
        }
        else {
            Node* temp = tail;
            tail = tail->prev;
            tail->next = nullptr;
            delete temp;
        }
        size--;
    }


    // Print the entire text stored in the linked list
    void printText() const {
        Node* current = head;
        while (current) {
            cout << current->data;
            current = current->next;
        }
    }

    // Get the size of the list
    int getSize() const {
        return size;
    }

    // Save the entire text to a file
    void saveToFile(const string& filename) const {
        ofstream outFile(filename);
        if (outFile.is_open()) {
            Node* current = head;
            while (current) {
                outFile << current->data;
                current = current->next;
            }
            outFile.close();
            cout << "\nText saved to " << filename << " successfully.\n";
        }
        else {
            cerr << "\nError: Unable to open file for writing.\n";
        }
    }

    // Function to get the node at a specific position (index)
    Node* getNodeAt(int index) {
        if (index < 0 || index >= size) return nullptr;

        Node* current = head;
        int count = 0;

        while (current && count < index) {
            current = current->next;
            count++;
        }

        return current;
    }

    // Function to insert a character at a specific index in the list
    void insertAt(int index, char ch) {
        if (index < 0 || index > size) return; // Index out of bounds

        Node* newNode = new Node(ch);

        // If inserting at the start (head)
        if (index == 0) {
            newNode->next = head;
            if (head) head->prev = newNode;
            head = newNode;
            if (!tail) tail = head;  // In case the list was empty
        }
        // If inserting at the end (tail)
        else if (index == size) {
            append(ch); // Reuse the append method
        }
        // Insert at a specific middle position
        else {
            Node* temp = head;
            for (int i = 0; i < index; ++i) {
                temp = temp->next; // Navigate to the node at the index
            }

            // Insert before the found node
            newNode->next = temp;
            newNode->prev = temp->prev;
            if (temp->prev) temp->prev->next = newNode;
            temp->prev = newNode;
        }

        size++;
    }


};

// NotePad class to handle the console and text operations
class NotePad {
private:
    int columns;
    int rows;
    int x, y;   // Cursor positions
    int minX, minY, maxX, maxY;
    DoublyLinkedList textList;
    HANDLE rhnd;
    stack<char> undoStack;  // Stack to store characters for undo
    stack<char> redoStack;  // Stack to store characters for redo

public:
    NotePad(int cols, int rws) : columns(cols), rows(rws), x(1), y(1), minX(1), minY(1), maxX(1), maxY(1) {
        rhnd = GetStdHandle(STD_INPUT_HANDLE); // Handle to read console
    }

    // Function to set console cursor position
    void gotoxy(int x, int y) {
        COORD c = { x, y };
        SetConsoleCursorPosition(GetStdHandle(STD_OUTPUT_HANDLE), c);
    }

    // Function to set console window size and buffer
    void setConsoleSize(int width, int height) {
        SMALL_RECT windowSize = { 0, 0, (short)(width - 1), (short)(height - 1) };
        SetConsoleWindowInfo(GetStdHandle(STD_OUTPUT_HANDLE), TRUE, &windowSize);

        COORD bufferSize = { (short)width, (short)height };
        SetConsoleScreenBufferSize(GetStdHandle(STD_OUTPUT_HANDLE), bufferSize);

        HWND console = GetConsoleWindow();
        LONG style = GetWindowLong(console, GWL_STYLE);
        SetWindowLong(console, GWL_STYLE, style & ~WS_SIZEBOX & ~WS_MAXIMIZEBOX);
    }

    // Function to draw the frame with correct dimensions
    void drawFrame(int windowWidth, int windowHeight) {
        int mainAreaWidth = windowWidth * 0.8;
        int mainAreaHeight = windowHeight * 0.8;

        int suggestionsAreaHeight = windowHeight * 0.2;
        int suggestionsStartY = windowHeight - suggestionsAreaHeight;

        // Draw the vertical lines for the main area
        for (int i = 0; i <= suggestionsStartY; ++i) {
            gotoxy(0, i);
            cout << "|";
            gotoxy(mainAreaWidth, i);
            cout << "|";
        }

        // Draw the horizontal border between the main area and suggestions area
        for (int i = 1; i < mainAreaWidth; ++i) {
            gotoxy(i, suggestionsStartY);
            cout << "_";
        }

        // Draw the frame for the suggestions area
        for (int i = suggestionsStartY + 1; i <= mainAreaHeight; ++i) {
            gotoxy(0, i);
            cout << "|";
            gotoxy(mainAreaWidth, i);
            cout << "|";
        }

        

        // Label the "Search" area
        gotoxy(mainAreaWidth + 1, 0);
        cout << "Search";

        // Label the "Suggestions" area
        gotoxy(1, suggestionsStartY + 1);
        cout << "Word Suggestions";
    }


    // Main function to run the NotePad
    void run() {
        setConsoleSize(columns, rows);
        system("cls");

        DWORD Events = 0;
        DWORD EventsRead = 0;
        bool Running = true;

        drawFrame(columns, rows);
        gotoxy(x, y);

        while (Running) {
            GetNumberOfConsoleInputEvents(rhnd, &Events);

            if (Events != 0) {
                INPUT_RECORD eventBuffer[200];
                ReadConsoleInput(rhnd, eventBuffer, Events, &EventsRead);

                for (DWORD i = 0; i < EventsRead; ++i) {
                    if (eventBuffer[i].EventType == KEY_EVENT && eventBuffer[i].Event.KeyEvent.bKeyDown) {
                        switch (eventBuffer[i].Event.KeyEvent.wVirtualKeyCode) {
                        case '2':  // Let's say '2' is for undo
                            if (!undoStack.empty()) {
                                char ch;
                                do {
                                    ch = undoStack.top();
                                    undoStack.pop();
                                    redoStack.push(ch);  // Push to redo stack

                                    // Remove character from the text list
                                    textList.removeLastChar();

                                    // Update the cursor and the screen (you can add this logic)
                                    if (x > minX) {
                                        x--;
                                        gotoxy(x, y);
                                        cout << " ";  // Clear the character on screen
                                        gotoxy(x, y);
                                    }

                                } while (!undoStack.empty() && ch != ' ');  // Stop at space or empty
                            }
                            break;

                        case '3':  // Let's say '3' is for redo
                            if (!redoStack.empty()) {
                                char ch;
                                do {
                                    ch = redoStack.top();
                                    redoStack.pop();
                                    undoStack.push(ch);  // Push back to undo stack

                                    // Re-insert the character into the text list
                                    textList.append(ch);

                                    // Print the character on the screen and update the cursor
                                    gotoxy(x, y);
                                    cout << ch;
                                    x++;

                                    // Handle wrapping if necessary
                                    if (x >= columns * 0.8 - 1) {
                                        x = 1;
                                        y++;
                                    }

                                } while (!redoStack.empty() && ch != ' ');  // Stop at space or empty
                            }
                            break;

                        case VK_UP:
                            if (y > minY) y--;
                            break;
                        case VK_DOWN:
                            if (y < maxY) y++;
                            break;
                        case VK_RIGHT:
                            if (x < maxX) x++;
                            break;
                        case VK_LEFT:
                            if (x > minX) x--;
                            break;
                        case VK_BACK:
                            if (x > minX) {
                                x--;
                                gotoxy(x, y);
                                cout << " ";
                                gotoxy(x, y);
                                textList.removeLastChar();
                                if (textList.getSize() == 0) {
                                    maxX = minX;
                                    maxY = minY;
                                }
                            }
                            break;
                        case VK_RETURN:  // Handle Enter key
                        {
                            // Calculate the current position (index) in the text
                            int index = (y - 1) * columns + (x - 1);

                            // Insert a newline character at the current cursor position
                            textList.insertAt(index, '\n');

                            // Clear only the screen (without storing spaces in the linked list)
                            Node* temp = textList.getNodeAt(index + 1);  // Get the node after the inserted newline
                            int tempX = x;

                            // Move to the right to clear characters visually, but not in the list
                            while (temp) {
                                gotoxy(tempX, y);  // Position cursor on the screen
                                cout << " ";       // Clear the character on the screen
                                tempX++;

                                // Handle line wrapping if needed
                                if (tempX >= columns * 0.8 - 1) {
                                    tempX = 1;
                                    y++;
                                    if (y >= rows * 0.8 - 1) break;  // Prevent overflow beyond the frame
                                }

                                temp = temp->next;  // Move to the next node in the linked list
                            }

                            // Reset x and y to their positions after inserting the newline
                            y++;
                            x = minX;  // Reset x to the start of the new line

                            // Now print the rest of the text that comes after the newline on the new line
                            temp = textList.getNodeAt(index + 1);  // Get the node after the inserted newline
                            while (temp) {
                                gotoxy(x, y);  // Move cursor to the new position
                                cout << temp->data;  // Print the shifted character
                                x++;

                                // Handle line wrapping if needed
                                if (x >= columns * 0.8 - 1) {
                                    x = 1;
                                    y++;
                                    if (y >= rows * 0.8 - 1) break;  // Prevent overflow beyond the frame
                                }

                                temp = temp->next;
                            }

                            // Update maxX and maxY as needed
                            if (x > maxX) maxX = x;
                            if (y > maxY) maxY = y;
                            x = minX;  // Reset x to the start of the new line
                        }
                        break;

                        case '1':  // If '1' is pressed, save text to file
                            textList.saveToFile("output.txt");
                            break;
                        default:
                            if (x < columns * 0.8 - 1 && y < rows * 0.8 - 1) {
                                char ch = eventBuffer[i].Event.KeyEvent.uChar.AsciiChar;

                                if (ch != '\0') {  // Ensure that it's a valid character
                                    // Push the character to the undo stack
                                    undoStack.push(ch);
                                    redoStack = stack<char>();  // Clear the redo stack after a new action

                                    // Calculate the linear position (index) in the text based on (x, y)
                                    int index = (y - 1) * columns + (x - 1);

                                    // Insert character at the current position (handle case where cursor is in-between text)
                                    if (index < textList.getSize()) {
                                        // Insert the character at the current index in the list
                                        textList.insertAt(index, ch);

                                        // Print the newly inserted character at the current position
                                        gotoxy(x, y);
                                        cout << ch;
                                        x++;

                                        // Save the current position after the insert
                                        int current_pos = index + 2;

                                        // Print the rest of the characters that are shifted
                                        Node* temp = textList.getNodeAt(index + 1);  // Get the next node
                                        while (temp) {
                                            gotoxy(x, y);  // Move the cursor to the new position
                                            cout << temp->data;  // Print the shifted character
                                            x++;

                                            // Handle line wrapping if needed
                                            if (x >= columns * 0.8 - 1) {
                                                x = 1;
                                                y++;
                                                if (y >= rows * 0.8 - 1) break;  // Prevents overflow beyond the frame
                                            }

                                            temp = temp->next;  // Move to the next node
                                        }

                                        // Set the cursor back to the current position
                                        gotoxy(current_pos, y);
                                        x = current_pos;
                                    }
                                    else {
                                        // If the cursor is at the end of the text, just append the character
                                        textList.append(ch);
                                        cout << ch;
                                        x++;
                                    }

                                    // Update the maximum x and y positions if necessary
                                    if (x > maxX) maxX = x;
                                    if (y > maxY) maxY = y;
                                }
                            }
                            break;




                        }

                        if (x < minX) x = minX;
                        if (x > maxX) x = maxX;
                        if (y < minY) y = minY;
                        if (y > maxY) y = maxY;

                        gotoxy(x, y);
                    }
                }
            }
        }
    }
};




int main() {
    NotePad notepad(100, 30); // Create an instance of NotePad with 100 columns and 30 rows
    notepad.run(); // Run the notepad application
    return 0;
}
