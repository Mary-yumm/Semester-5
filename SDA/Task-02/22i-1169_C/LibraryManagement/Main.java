package LibraryManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    static Scanner scanner = new Scanner(System.in);
    private static int getValidChoice(int lim) {
        int choice;
        while (true) {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (choice >= 1 && choice <= lim) {
                    break; // Valid choice
                } else {
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
            }
        }
        return choice;
    }

    private static int getValidId() {
        int bookId;
        while (true) {
            System.out.print("Enter ID: ");
            if (scanner.hasNextInt()) {
                bookId = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                return bookId; // Valid book ID
            } else {
                System.out.println("Invalid input. Please enter a valid ID (number).");
                scanner.next(); // Clear the invalid input
            }
        }
    }


    private static String getValidTitle() {
        String title;
        while (true) {
            System.out.print("Enter book title/name: ");
            title = scanner.nextLine().trim(); // Read and trim whitespace
            if (!title.isEmpty()) {
                return title; // Valid title
            } else {
                System.out.println("Title cannot be empty.");
            }
        }
    }


    public static void main(String[] args) throws ClassNotFoundException {

        Library library = new Library();

        // Adding initial users
        PersistentHandler handler;

        // library.loanBook("B1","S1");
        Scanner scanner = new Scanner(System.in);
        

        String storage;
        do {
            System.out.println("What method of storage do you want to use? 's' for sql , 'f' for csv");
            storage = scanner.nextLine();
            System.out.println("storage "+storage);
            if (storage.equals("s")) {
                library.Set_Storage("mysql");
                handler = new SQL_Handler("jdbc:mysql://localhost:3306/LMS", "root", "16033004");
                library.connect(handler);
            } else if (storage.equals("f")) {
                library.Set_Storage("csv");
                handler = new File_Handler("users.csv","books.csv","loans.csv");
                library.connect(handler);
            } else {
                System.out.println("Invalid choice. Enter a valid choice.");
            }
        } while (!storage.equals("s") && !storage.equals("f"));

        

        int choice;

        do {
            System.out.println("\n\nLibrary Management System Menu:");
            System.out.println("1. Add User");
            System.out.println("2. Add Book");
            System.out.println("3. Loan Book");
            System.out.println("4. Return Book");
            System.out.println("5. Remove Book");
            System.out.println("6. Remove User");
            System.out.println("7. Display Loan Status");
            System.out.println("8. Print Available Books");
            System.out.println("9. Print User Info");
            System.out.println("10. Print All Users Info");
            System.out.println("11. Extend Loan (Textbook only)");
            System.out.println("12. Display Loan Details");
            System.out.println("13. Display Total Loan Cost");
            System.out.println("14. Search book");
            System.out.println("15. Search user");
            System.out.println("16. Printed sorted books by title");
            System.out.println("17. Print sorted users by name");
            System.out.println("18. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            System.out.println();
            switch (choice) {
                case 1:
                    // Collect and validate user details
                    String[] userInfo = library.input_user_info(scanner);

                    // User type validation
                    String userType;
                    do {
                        System.out.print("Enter User Type (Student/Faculty/PublicMember): ");
                        userType = scanner.nextLine();

                        if (!(userType.equalsIgnoreCase("Student") ||
                                userType.equalsIgnoreCase("Faculty") ||
                                userType.equalsIgnoreCase("PublicMember"))) {
                            System.out.println(
                                    "Invalid user type! Please enter 'Student', 'Faculty', or 'PublicMember'.");
                        }
                    } while (!(userType.equalsIgnoreCase("Student") ||
                            userType.equalsIgnoreCase("Faculty") ||
                            userType.equalsIgnoreCase("PublicMember")));

                    // Add the user based on the type selected

                    if (userType.equalsIgnoreCase("Student")) {
                        library.addUser(new Student(Integer.parseInt(userInfo[0]), userInfo[1], userInfo[2], userInfo[3], userInfo[4]));
                    } else if (userType.equalsIgnoreCase("Faculty")) {
                        library.addUser(new Faculty(Integer.parseInt(userInfo[0]), userInfo[1], userInfo[2], userInfo[3], userInfo[4]));
                    } else if (userType.equalsIgnoreCase("PublicMember")) {
                        library.addUser(new PublicMember(Integer.parseInt(userInfo[0]), userInfo[1], userInfo[2], userInfo[3], userInfo[4]));
                    }
                    break;
                case 2:
                    // Collect and validate book details
                    String[] bookInfo = library.input_book_info(scanner);

                    // Book type validation
                    String bookType;
                    do {
                        System.out.print("Enter Book Type (Textbook/Novel/ReferenceBook): ");
                        bookType = scanner.nextLine();
                        if (!(bookType.equalsIgnoreCase("Textbook") ||
                                bookType.equalsIgnoreCase("Novel") ||
                                bookType.equalsIgnoreCase("ReferenceBook"))) {
                            System.out.println(
                                    "Invalid book type! Please enter 'Textbook', 'Novel', or 'ReferenceBook'.");
                        }
                    } while (!(bookType.equalsIgnoreCase("Textbook") ||
                            bookType.equalsIgnoreCase("Novel") ||
                            bookType.equalsIgnoreCase("ReferenceBook")));

                    // Add the book based on the type selected
                    if (bookType.equalsIgnoreCase("Textbook")) {
                        library.addBook(new Textbook(Integer.parseInt(bookInfo[0]), bookInfo[1], bookInfo[2],
                                bookInfo[3],
                                Integer.parseInt(bookInfo[4]), bookInfo[5]));
                    } 
                    else if (bookType.equalsIgnoreCase("Novel")) {
                        library.addBook(new Novel(Integer.parseInt(bookInfo[0]), bookInfo[1], bookInfo[2], bookInfo[3],
                                Integer.parseInt(bookInfo[4]), bookInfo[5]));
                    }
                    else  if (bookType.equalsIgnoreCase("ReferenceBook")) {
                        library.addBook(new ReferenceBook(Integer.parseInt(bookInfo[0]), bookInfo[1], bookInfo[2],
                                bookInfo[3],
                                Integer.parseInt(bookInfo[4]), bookInfo[5]));
                    }
                    break;
                case 3:
                    // Loan Book

                    System.out.print("Enter Book ID to Loan: ");
                    int loanBookID = scanner.nextInt();
                    System.out.print("Enter User ID: ");
                    int loanUserID = scanner.nextInt();
                    System.out.print("Enter number of days: ");
                    int days = scanner.nextInt();
                    library.loanBook(loanBookID, loanUserID, days);
                    break;
                case 4:
                    // Return Book
                    System.out.print("Enter Book ID to Return: ");
                    int returnBookID = scanner.nextInt();
                    System.out.print("Enter User ID: ");
                    int returnUserID = scanner.nextInt();
                    library.returnBook(returnBookID, returnUserID);
                    break;
                case 5:
                    // Remove Book
                    System.out.print("Enter Book ID to Remove: ");
                    int removeBookID = scanner.nextInt();
                    library.removeBook(removeBookID);
                    break;
                case 6:
                    //Remove User
                    System.out.println("Enter User ID to Remove");
                    int removeUserID = scanner.nextInt();
                    library.removeUser(removeUserID);
                    break;

                case 7:
                    // Display Loan Status
                    System.out.print("Enter Book ID to Check Loan Status: ");
                    int statusLoan = scanner.nextInt();
                    library.displayLoanStatus(statusLoan);
                    break;
                case 8:
                    // Print Available Books
                    library.displayAvailableBooks();

                    break;
                case 9:
                    // Print User Info
                    System.out.println("Enter the user id: ");
                    int userID = scanner.nextInt();
                    library.printUserInfo(userID);
                    break;
                case 10:
                    // Print all user's info
                    library.displayUsers();
                    break;
                case 11:
                    // Extend Loan (Textbook only)
                    System.out.print("Enter Book ID to Extend: ");
                    int extendBookID = scanner.nextInt();

                    System.out.print("Enter User ID: ");
                    int extendUserID = scanner.nextInt();

                    // Validate that daysToExtend is a positive integer
                    int daysToExtend = -1; // Initial invalid value
                    do {
                        System.out.print("Enter number of days to Extend: ");
                        while (!scanner.hasNextInt()) {
                            System.out.println("Invalid input. Please enter a positive integer.");
                            scanner.next(); // Clear the invalid input
                        }
                        daysToExtend = scanner.nextInt();
                        if (daysToExtend <= 0) {
                            System.out.println("Days to extend must be a positive number. Please try again.");
                        }
                    } while (daysToExtend <= 0);
                    scanner.nextLine(); // Clear newline

                    // Call the method to extend the loan
                    library.extend_loan(extendBookID, extendUserID, daysToExtend);
                    break;
                case 12:
                    // Display Loan Details
                    System.out.println("Enter the user id: ");
                    int user_ID = scanner.nextInt();
                    library.displayLoanDetails(user_ID);

                    break;
                case 13:
                    // Display Total Loan Cost
                    System.out.print("Enter User ID to Display Total Loan Cost: ");
                    int costUserID = scanner.nextInt();
                    library.calculateTotalLoanCost(costUserID);
                    break;
                case 14:
                    //Search book
                    int choice1;
                    do {
                        System.out.println("Book Search Menu:");
                        System.out.println("1. Search by book ID");
                        System.out.println("2. Search by book title");
                        System.out.println("3. Search by book author");
                        System.out.println("4. Search by book ISBN");
                        System.out.println("5. Exit");
                        System.out.print("Please enter your choice: ");

                        // Validate input for choice
                        choice1 = getValidChoice(5);

                        switch (choice1) {
                            case 1: // Search by book ID
                                int bookId = getValidId();
                                library.searchBook(bookId);
                                break;
                            case 2: // Search by book title
                                String title = getValidTitle();
                                library.searchBook(title);
                                break;
                            case 3: // Search by book author
                                String author = getValidTitle();
                                library.searchBookbyAuthor(author);
                                break;
                            case 4: // Search by book ISBN
                                String isbn = getValidTitle();
                                library.searchBookbyISBN(isbn);
                                break;
                            case 5: // Exit
                                System.out.println("Exiting the search menu.");
                                break;
                            default:
                                System.out.println("Invalid choice. Please try again.");
                                break;
                        }
                        System.out.println(); // Print an empty line for better readability
                    } while (choice1 != 5); // Repeat until the user chooses to exit
                    break;
                case 15:
                    //Search user
                    int choice2;
                    do {
                        System.out.println("1. Search by user id: ");
                        System.out.println("2. Search by user name: ");
                        System.out.println("3. Exit Search ");
                        choice2 = getValidChoice(3);
                        switch (choice2) {
                            case 1:
                                int user_id = getValidId();
                                library.searchUser(user_id);
                                break;
                            case 2:
                                String title = getValidTitle();
                                library.searchUser(title);
                                break;
                            default:
                                System.out.println("Invalid choice. Please try again.");
                                break;
                        }
                    }while(choice2 != 3);

                    break;
                case 16:
                    //Print sorted books by title
                    library.sortBooksAlphabetically();
                    break;
                case 17:
                    //Print sorted users by name
                    library.sortUsersAlphabetically();
                    break;
                case 18:
                    // Exit
                    System.out.println("Exiting the system...");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 18);

        scanner.close();

    }



}
