package LibraryManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {

        Library library = new Library();

        // Adding initial users
        PersistentHandler handler;
        List<Book> books = new ArrayList<>();
        List<User> users = new ArrayList<>();

        // library.loanBook("B1","S1");
        Scanner scanner = new Scanner(System.in);
        

        String storage;
        do {
            System.out.println("What method of storage do you want to use? 's' for sql , 'f' for csv");
            storage = scanner.nextLine();
            if (storage == "s") {
                library.Set_Storage("mysql");
                handler = new SQL_Handler("jdbc:mysql://localhost:3306/LMS", "root", "16033004");
                library.connect(handler);
            } else if (storage == "f") {
                library.Set_Storage("csv");
                handler = new File_Handler("users.csv","books.csv","loans.csv",books,users);
            } else {
                System.out.println("Invalid choice. Enter a valid choice.");
            }
        } while (storage != "s" && storage != "f");

        

        int choice;

        do {
            System.out.println("\n\nLibrary Management System Menu:");
            System.out.println("1. Add User");
            System.out.println("2. Add Book");
            System.out.println("3. Loan Book");
            System.out.println("4. Return Book");
            System.out.println("5. Remove Book");
            System.out.println("6. Display Loan Status");
            System.out.println("7. Print Available Books");
            System.out.println("8. Print User Info");
            System.out.println("9. Print All Users Info");
            System.out.println("10. Extend Loan (Textbook only)");
            System.out.println("11. Display Loan Details");
            System.out.println("12. Display Total Loan Cost");
            System.out.println("13. Exit");
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
                        library.addUser(new Student(Integer.parseInt(userInfo[0]), userInfo[1], userInfo[2],
                                new ArrayList<>(), 0.0, userInfo[3], userInfo[4], "student"));
                    } else if (userType.equalsIgnoreCase("Faculty")) {
                        library.addUser(new Faculty(Integer.parseInt(userInfo[0]), userInfo[1], userInfo[2],
                                new ArrayList<>(), 0.0, userInfo[3], userInfo[4], "faculty"));
                    } else if (userType.equalsIgnoreCase("PublicMember")) {
                        library.addUser(new PublicMember(Integer.parseInt(userInfo[0]), userInfo[1], userInfo[2],
                                new ArrayList<>(), 0.0, userInfo[3], userInfo[4], "public"));
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
                                Integer.parseInt(bookInfo[4]), bookInfo[5],
                                Boolean.parseBoolean(bookInfo[6]), Double.parseDouble(bookInfo[7]), "textbook"));
                    } else if (bookType.equalsIgnoreCase("Novel")) {
                        library.addBook(new Novel(Integer.parseInt(bookInfo[0]), bookInfo[1], bookInfo[2], bookInfo[3],
                                Integer.parseInt(bookInfo[4]), bookInfo[5],
                                Boolean.parseBoolean(bookInfo[6]), Double.parseDouble(bookInfo[7]), "novel"));
                    } else if (bookType.equalsIgnoreCase("ReferenceBook")) {
                        library.addBook(new ReferenceBook(Integer.parseInt(bookInfo[0]), bookInfo[1], bookInfo[2],
                                bookInfo[3],
                                Integer.parseInt(bookInfo[4]), bookInfo[5],
                                Boolean.parseBoolean(bookInfo[6]), Double.parseDouble(bookInfo[7]), "referencebook"));
                    }
                    break;
                case 3:
                    // Loan Book
                    System.out.print("Enter Book ID to Loan: ");
                    int loanBookID = scanner.nextInt();
                    System.out.print("Enter User ID: ");
                    int loanUserID = scanner.nextInt();
                    library.loanBook(loanBookID, loanUserID);
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
                    // Display Loan Status
                    System.out.print("Enter Book ID to Check Loan Status: ");
                    int statusLoan = scanner.nextInt();
                    library.displayLoanStatus(statusLoan);
                    break;
                case 7:
                    // Print Available Books
                    library.displayAvailableBooks();

                    break;
                case 8:
                    // Print User Info
                    System.out.println("Enter the user id: ");
                    int userID = scanner.nextInt();
                    library.printUserInfo(userID);
                    break;
                case 9:
                    // Print all user's info
                    library.displayUsers();
                    break;
                case 10:
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
                case 11:
                    // Display Loan Details
                    System.out.println("Enter the user id: ");
                    int user_ID = scanner.nextInt();
                    library.displayLoanDetails(user_ID);

                    break;
                case 12:
                    // Display Total Loan Cost
                    System.out.print("Enter User ID to Display Total Loan Cost: ");
                    int costUserID = scanner.nextInt();
                    library.calculateTotalLoanCost(costUserID);
                    break;
                case 13:
                    // Exit
                    System.out.println("Exiting the system...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 13);

        scanner.close();

    }
}