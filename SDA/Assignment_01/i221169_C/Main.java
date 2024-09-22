package LibraryManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Library library = new Library();

        // Creating empty book lists for users
        List<Book> emptyBookList = new ArrayList<>();

        // Adding initial users
        library.addUser(new Student("S1", "Alice", "alice@example.com", emptyBookList, 0.0, "1234567890", "123 Elm Street"));
        library.addUser(new Faculty("F1", "Bob", "bob@example.com", emptyBookList, 0.0, "0987654321", "456 Oak Avenue"));
        library.addUser(new PublicMember("P1", "Charlie", "charlie@example.com", emptyBookList, 0.0, "1122334455", "789 Pine Road"));

        // Adding initial books
        library.addBook(new Textbook("T1", "Java Programming", "John Doe", "123-456", 2020, "Programming", false, 10.0));
        library.addBook(new Novel("N1", "The Great Novel", "Jane Smith", "789-012", 2018, "Fiction", false, 5.0));
        library.addBook(new ReferenceBook("R1", "Encyclopedia", "Various Authors", "345-678", 2000, "Reference", false, 0.0));
        library.addBook(new Textbook("T2", "Data Structures", "Paul Allen", "456-789", 2019, "Computer Science", false, 12.0));
        library.addBook(new Novel("N2", "Adventure Tales", "Tom Thomson", "654-321", 2017, "Adventure", false, 6.0));
        library.addBook(new ReferenceBook("R2", "Physics Handbook", "Richard Feynman", "987-654", 2015, "Science", false, 0.0));
        library.addBook(new Textbook("T3", "Operating Systems", "Andrew Tanenbaum", "321-654", 2021, "Computer Science", false, 15.0));
        library.addBook(new Novel("N3", "Mystery Nights", "Arthur Conan Doyle", "852-963", 2016, "Mystery", false, 8.0));
        library.addBook(new ReferenceBook("R3", "Chemistry Handbook", "Marie Curie", "741-852", 2010, "Science", false, 0.0));
        library.addBook(new Textbook("T4", "Algorithms", "Robert Sedgewick", "963-741", 2022, "Programming", false, 14.0));

        library.loanBook("T4","S1");


        //library.loanBook("B1","S1");
        Scanner scanner = new Scanner(System.in);
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
                            System.out.println("Invalid user type! Please enter 'Student', 'Faculty', or 'PublicMember'.");
                        }
                    } while (!(userType.equalsIgnoreCase("Student") ||
                            userType.equalsIgnoreCase("Faculty") ||
                            userType.equalsIgnoreCase("PublicMember")));

                    // Add the user based on the type selected
                    if (userType.equalsIgnoreCase("Student")) {
                        library.addUser(new Student(userInfo[0], userInfo[1], userInfo[2], new ArrayList<>(), 0.0, userInfo[3], userInfo[4]));
                    } else if (userType.equalsIgnoreCase("Faculty")) {
                        library.addUser(new Faculty(userInfo[0], userInfo[1], userInfo[2], new ArrayList<>(), 0.0, userInfo[3], userInfo[4]));
                    } else if (userType.equalsIgnoreCase("PublicMember")) {
                        library.addUser(new PublicMember(userInfo[0], userInfo[1], userInfo[2], new ArrayList<>(), 0.0, userInfo[3], userInfo[4]));
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
                            System.out.println("Invalid book type! Please enter 'Textbook', 'Novel', or 'ReferenceBook'.");
                        }
                    } while (!(bookType.equalsIgnoreCase("Textbook") ||
                            bookType.equalsIgnoreCase("Novel") ||
                            bookType.equalsIgnoreCase("ReferenceBook")));

                    // Add the book based on the type selected
                    if (bookType.equalsIgnoreCase("Textbook")) {
                        library.addBook(new Textbook(bookInfo[0], bookInfo[1], bookInfo[2], bookInfo[3],
                                Integer.parseInt(bookInfo[4]), bookInfo[5],
                                Boolean.parseBoolean(bookInfo[6]), Double.parseDouble(bookInfo[7])));
                    } else if (bookType.equalsIgnoreCase("Novel")) {
                        library.addBook(new Novel(bookInfo[0], bookInfo[1], bookInfo[2], bookInfo[3],
                                Integer.parseInt(bookInfo[4]), bookInfo[5],
                                Boolean.parseBoolean(bookInfo[6]), Double.parseDouble(bookInfo[7])));
                    } else if (bookType.equalsIgnoreCase("ReferenceBook")) {
                        library.addBook(new ReferenceBook(bookInfo[0], bookInfo[1], bookInfo[2], bookInfo[3],
                                Integer.parseInt(bookInfo[4]), bookInfo[5],
                                Boolean.parseBoolean(bookInfo[6]), Double.parseDouble(bookInfo[7])));
                    }
                    break;
                case 3:
                    // Loan Book
                    System.out.print("Enter Book ID to Loan: ");
                    String loanBookID = scanner.nextLine();
                    System.out.print("Enter User ID: ");
                    String loanUserID = scanner.nextLine();
                    library.loanBook(loanBookID, loanUserID);
                    break;
                case 4:
                    // Return Book
                    System.out.print("Enter Book ID to Return: ");
                    String returnBookID = scanner.nextLine();
                    System.out.print("Enter User ID: ");
                    String returnUserID = scanner.nextLine();
                    library.returnBook(returnBookID, returnUserID);
                    break;
                case 5:
                    // Remove Book
                    System.out.print("Enter Book ID to Remove: ");
                    String removeBookID = scanner.nextLine();
                    library.removeBook(removeBookID);
                    break;
                case 6:
                    // Display Loan Status
                    System.out.print("Enter Book ID to Check Loan Status: ");
                    String statusLoan = scanner.nextLine();
                    Book find_book = library.findBook(statusLoan);
                    if(find_book==null){
                        System.out.println("Book does not exist");
                    }
                    else if(find_book.isLoaned()){
                        System.out.print("Book is loaned ");
                    }
                    else{
                        System.out.print("Book is not loaned ");
                    }
                    break;
                case 7:
                    // Print Available Books
                    library.displayAvailableBooks();

                    break;
                case 8:
                    // Print User Info
                    System.out.println("Enter the user id: ");
                    String userID = scanner.nextLine();
                    User user = library.findUser(userID);
                    if(user==null){
                        System.out.println("User does not exist");
                    }
                    else{
                        user.print_info();
                    }
                    break;
                case 9:
                    //Print all user's info
                    library.displayUsers();
                    break;
                case 10:
                    // Extend Loan (Textbook only)
                    System.out.print("Enter Book ID to Extend: ");
                    String extendBookID = scanner.nextLine();

                    System.out.print("Enter User ID: ");
                    String extendUserID = scanner.nextLine();

                    // Validate that daysToExtend is a positive integer
                    int daysToExtend = -1;  // Initial invalid value
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
                    String user_ID = scanner.nextLine();
                    User user_loan = library.findUser(user_ID);
                    if(user_loan==null){
                        System.out.println("User does not exist");
                    }
                    else {
                        library.displayLoanDetails(user_ID); // needs to be modified
                    }

                    break;
                case 12:
                    // Display Total Loan Cost
                    System.out.print("Enter User ID to Display Total Loan Cost: ");
                    String costUserID = scanner.nextLine();
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