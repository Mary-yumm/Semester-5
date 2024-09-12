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
        library.addBook(new Textbook("B1", "Java Programming", "John Doe", "123-456", 2020, "Programming", false, 10.0));
        library.addBook(new Novel("B2", "The Great Novel", "Jane Smith", "789-012", 2018, "Fiction", false, 5.0));
        library.addBook(new ReferenceBook("B3", "Encyclopedia", "Various Authors", "345-678", 2000, "Reference", false, 0.0));

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
            System.out.println("8. Print Users");
            System.out.println("9. Extend Loan (Textbook only)");
            System.out.println("10. Display Loan Details");
            System.out.println("11. Display Total Loan Cost");
            System.out.println("12. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            System.out.println();
            switch (choice) {
                case 1:
                    // Add User
                    System.out.print("Enter User ID: ");
                    String userID = scanner.nextLine();
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine();
                    List<Book> books = new ArrayList<>();
                    System.out.print("Enter Phone Number: ");
                    String phoneNumber = scanner.nextLine();
                    System.out.print("Enter Address: ");
                    String address = scanner.nextLine();
                    // Add user type here based on some input or a default type
                    System.out.print("Enter User Type (Student/Faculty/PublicMember): ");
                    String userType = scanner.nextLine();
                    if (userType.equalsIgnoreCase("Student")) {
                        library.addUser(new Student(userID, name, email, books, 0.0, phoneNumber, address));
                    } else if (userType.equalsIgnoreCase("Faculty")) {
                        library.addUser(new Faculty(userID, name, email, books, 0.0, phoneNumber, address));
                    } else if (userType.equalsIgnoreCase("PublicMember")) {
                        library.addUser(new PublicMember(userID, name, email, books, 0.0, phoneNumber, address));
                    } else {
                        System.out.println("Invalid user type!");
                    }
                    break;
                case 2:
                    // Add Book
                    System.out.print("Enter Book ID: ");
                    String bookID = scanner.nextLine();
                    System.out.print("Enter Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter Author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter ISBN: ");
                    String ISBN = scanner.nextLine();
                    System.out.print("Enter Year of Publication: ");
                    int year = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter Genre: ");
                    String genre = scanner.nextLine();
                    System.out.print("Is the book loaned out (true/false): ");
                    boolean loanStatus = scanner.nextBoolean();
                    System.out.print("Enter Base Loan Fee: ");
                    double baseLoanFee = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter Book Type (Textbook/Novel/ReferenceBook): ");
                    String bookType = scanner.nextLine();
                    if (bookType.equalsIgnoreCase("Textbook")) {
                        library.addBook(new Textbook(bookID, title, author, ISBN, year, genre, loanStatus, baseLoanFee));
                    } else if (bookType.equalsIgnoreCase("Novel")) {
                        library.addBook(new Novel(bookID, title, author, ISBN, year, genre, loanStatus, baseLoanFee));
                    } else if (bookType.equalsIgnoreCase("ReferenceBook")) {
                        library.addBook(new ReferenceBook(bookID, title, author, ISBN, year, genre, loanStatus, baseLoanFee));
                    } else {
                        System.out.println("Invalid book type!");
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
                    Book find_book = library.findBook("B1");
                    if(find_book.isLoaned()){
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
                    // Print Users
                    library.displayUsers();
                    break;
                case 9:
                    // Extend Loan (Textbook only)
                    System.out.print("Enter Book ID to Extend: ");
                    String extendBookID = scanner.nextLine();
                    System.out.print("Enter User ID: ");
                    String extendUserID = scanner.nextLine();
                    while(!scanner.hasNextInt()) {
                        System.out.print("Enter number of days to Extend: ");
                        scanner.next();
                    }
                    int daysToExtend = scanner.nextInt();
                    scanner.nextLine();
                    library.extend_loan(extendBookID, extendUserID,daysToExtend);
                    break;
                case 10:
                    // Display Loan Details
                    library.displayLoanDetails(); // needs to be modified

                    break;
                case 11:
                    // Display Total Loan Cost
                    System.out.print("Enter User ID to Display Total Loan Cost: ");
                    String costUserID = scanner.nextLine();
                    library.calculateTotalLoanCost(costUserID);
                    break;
                case 12:
                    // Exit
                    System.out.println("Exiting the system...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 12);

        scanner.close();

    }
}