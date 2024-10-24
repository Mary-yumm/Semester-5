package LibraryManagement;
import java.util.Scanner;

public class Library {
    PersistentHandler phandler;
    private String storage;

    Library() {
    }

    public void connect(PersistentHandler ph) {
        phandler = ph;
    }

    public void Set_Storage(String storage) {
        this.storage = storage;
    }

    public String get_Storage() {
        return storage;
    }

    public String[] input_user_info(Scanner scanner) {
        String[] userInfo = new String[5]; // Array to store user details
        int userid = 0;
        // User ID Validation
        do {
            System.out.print("Enter User ID: ");
            userInfo[0] = scanner.nextLine();
            userid = Integer.parseInt(userInfo[0]);
            if (userInfo[0].isEmpty()) {
                System.out.println("User ID cannot be empty. Please enter a valid User ID.");
            } else if (phandler.findUser(userid) != null) {
                System.out.println("User ID already exists. Please choose another user ID.");
            }
        } while (userInfo[0].isEmpty());

        // Name Validation
        do {
            System.out.print("Enter Name: ");
            userInfo[1] = scanner.nextLine();
            if (userInfo[1].isEmpty()) {
                System.out.println("Name cannot be empty. Please enter a valid name.");
            }
        } while (userInfo[1].isEmpty());

        // Email Validation
        do {
            System.out.print("Enter Email: ");
            userInfo[2] = scanner.nextLine();
            if (!userInfo[2].matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.out.println("Invalid email format. Please enter a valid email.");
            }
        } while (!userInfo[2].matches("^[A-Za-z0-9+_.-]+@(.+)$"));

        // Phone Number Validation
        do {
            System.out.print("Enter Phone Number: ");
            userInfo[3] = scanner.nextLine();
            if (!userInfo[3].matches("\\d{10}")) {
                System.out.println("Invalid phone number. Please enter a 10-digit number.");
            }
        } while (!userInfo[3].matches("\\d{10}"));

        // Address Validation
        do {
            System.out.print("Enter Address: ");
            userInfo[4] = scanner.nextLine();
            if (userInfo[4].isEmpty()) {
                System.out.println("Address cannot be empty. Please enter a valid address.");
            }
        } while (userInfo[4].isEmpty());

        return userInfo; // Return the array with user details
    }

    public String[] input_book_info(Scanner scanner) {
        String[] bookInfo = new String[6]; // Array to store book details
        int bookid;
        // Book ID Validation
        do {
            System.out.print("Enter Book ID: ");
            bookInfo[0] = scanner.nextLine();
            bookid = Integer.parseInt(bookInfo[0]);
            if (bookInfo[0].isEmpty()) {
                System.out.println("Book ID cannot be empty. Please enter a valid Book ID.");
            } else if (phandler.findBook(bookid) != null) {
                System.out.println("Book already exists. Please choose a different Book ID.");
            }

        } while (bookInfo[0].isEmpty());

        // Title Validation
        do {
            System.out.print("Enter Title: ");
            bookInfo[1] = scanner.nextLine();
            if (bookInfo[1].isEmpty()) {
                System.out.println("Title cannot be empty. Please enter a valid title.");
            }
        } while (bookInfo[1].isEmpty());

        // Author Validation
        do {
            System.out.print("Enter Author: ");
            bookInfo[2] = scanner.nextLine();
            if (bookInfo[2].isEmpty()) {
                System.out.println("Author cannot be empty. Please enter a valid author.");
            }
        } while (bookInfo[2].isEmpty());

        // ISBN Validation (e.g., checking for length)
        do {
            System.out.print("Enter ISBN: ");
            bookInfo[3] = scanner.nextLine();
            if (bookInfo[3].length() != 13) {
                System.out.println("Invalid ISBN format. Please enter a 13-digit ISBN.");
            }
        } while (bookInfo[3].length() != 13);

        // Year of Publication Validation
        int year = 0;
        do {
            try {
                System.out.print("Enter Year of Publication: ");
                year = Integer.parseInt(scanner.nextLine());
                if (year <= 0) {
                    System.out.println("Invalid year. Please enter a positive year.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid year.");
            }
        } while (year <= 0);
        bookInfo[4] = Integer.toString(year);

        // Genre Validation (e.g., not empty)
        do {
            System.out.print("Enter Genre: ");
            bookInfo[5] = scanner.nextLine();
            if (bookInfo[5].isEmpty()) {
                System.out.println("Genre cannot be empty. Please enter a valid genre.");
            }
        } while (bookInfo[5].isEmpty());

        return bookInfo;

    }

    public void addBook(Book book) {
        phandler.addBook(book);
    }

    public void displayAvailableBooks() {
        phandler.displayAvailableBooks();
    }

    public void removeBook(int book_id) {
        phandler.removeBook(book_id);
    }

    public void addUser(User user) {
        phandler.addUser(user);
    }

    public void removeUser(int user) {
        phandler.removeUser(user);
    }

    public void displayUsers() {
        phandler.displayUsers();
    }

    public void loanBook(int bookID, int UserID,int days) {
        phandler.loanBook(bookID, UserID, days);
    }

    public void returnBook(int bookID, int UserID) {
        phandler.returnBook(bookID, UserID);
    }

    public void extend_loan(int bookID, int UserID, int days) {
        phandler.extend_loan(bookID, UserID, days);
    }

    public void displayLoanDetails(int user_id) {
        phandler.displayLoanDetails(user_id);
    }

    public void calculateTotalLoanCost(int user_id) {
        phandler.calculateTotalLoanCost(user_id);
    }

    public void displayLoanStatus(int bookid){
        phandler.displayLoanStatus(bookid);
    }

    public void printUserInfo(int userid){
        phandler.printUserInfo(userid);
    }

    public void searchBook(int bookid){
        phandler.searchBook(bookid);
    }

    public void searchBook(String title){
        phandler.searchBook(title);
    }

    public void searchBookbyAuthor(String Author){
        phandler.searchBook(Author);
    }

    public void searchBookbyISBN(String isbn){
        phandler.searchBook(isbn);
    }

    public void searchUser(int userid){
        phandler.searchUser(userid);
    }

    public void searchUser(String username){
        phandler.searchUser(username);
    }

    public void sortBooksAlphabetically(){
        phandler.sortBooksAlphabetically();

    }
    public void sortUsersAlphabetically(){
        phandler.sortUsersAlphabetically();

    }

}
