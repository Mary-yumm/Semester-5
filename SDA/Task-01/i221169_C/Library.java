package LibraryManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Library {
    protected List<Book> books;
    protected List<User> users;
    Library(){
        books = new ArrayList<>();
        users = new ArrayList<>();
    }
    public String[] input_user_info(Scanner scanner) {
        String[] userInfo = new String[5];  // Array to store user details

        // User ID Validation
        do {
            System.out.print("Enter User ID: ");
            userInfo[0] = scanner.nextLine();
            if (userInfo[0].isEmpty()) {
                System.out.println("User ID cannot be empty. Please enter a valid User ID.");
            }
            else if(findUser(userInfo[0]) != null){
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

        return userInfo;  // Return the array with user details
    }

    public String[] input_book_info(Scanner scanner){
        String[] bookInfo = new String[8];  // Array to store book details

        // Book ID Validation
        do {
            System.out.print("Enter Book ID: ");
            bookInfo[0] = scanner.nextLine();
            if (bookInfo[0].isEmpty()) {
                System.out.println("Book ID cannot be empty. Please enter a valid Book ID.");
            }
            else if(findBook(bookInfo[0]) != null){
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

        // Loan Status Validation
        boolean loanStatus;
        do {
            System.out.print("Is the book loaned out (true/false): ");
            String loanStatusStr = scanner.nextLine();
            if (loanStatusStr.equalsIgnoreCase("true") || loanStatusStr.equalsIgnoreCase("false")) {
                loanStatus = Boolean.parseBoolean(loanStatusStr);
                break;
            } else {
                System.out.println("Please enter 'true' or 'false'.");
            }
        } while (true);
        bookInfo[6] = Boolean.toString(loanStatus);

        // Base Loan Fee Validation
        double baseLoanFee = 0.0;
        do {
            try {
                System.out.print("Enter Base Loan Fee: ");
                baseLoanFee = Double.parseDouble(scanner.nextLine());
                if (baseLoanFee < 0) {
                    System.out.println("Loan fee cannot be negative. Please enter a valid amount.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid loan fee.");
            }
        } while (baseLoanFee < 0);
        bookInfo[7] = Double.toString(baseLoanFee);

        return bookInfo;

    }


    public void addBook(Book book){
        //Check if book already exists
        for(Book check_books:books){
            if(check_books.getBookId().equals(book.getBookId())){
                System.out.println("Book already exists");
                return;
            }
        }
        books.add(book);
        System.out.println(book.getTitle() + " added to the library.");
    }
    public void displayAvailableBooks() {
        System.out.println("Available books:");
        for (Book book : books) {
            if (!book.isLoaned()) {
                System.out.println("- " + book.getTitle() + " (" + book.getGenre() + ")");
            }
        }
        System.out.println();
        for (Book book : books) {
            if (!book.isLoaned()) {
                book.print_info();
            }
            System.out.println();
        }
    }
    public void removeBook(String book_id){
        for (Book book : books) {
            if (book.getTitle().equals(book_id)) {
                if(!book.isLoaned()) {
                    books.remove(book);
                    System.out.println(book.getTitle() + " removed from the library.");
                    break;
                }
                else {
                    System.out.println(book.getTitle() + " cannot be removed because it is already loaned.");

                }

            }

        }

    }
    public void addUser(User user){
        //Check if user with that id already exists
        for(User check_user : users){
            if(check_user.getUserId().equals(user.getUserId())){
                System.out.println(check_user.getUserId() + " is already in the library.");
                return;
            }
        }
        users.add(user);
        System.out.println(user.getName() + " registered as a user.");

    }
    public void removeUser(User user){
        //Check if user has already been removed or doesnt exist
        boolean check=false;
        for(User check_user : users){
            if(check_user.getUserId().equals(user.getUserId())){
                users.remove(user);
                System.out.println(user.getName() + " removed as a user.");
                check=true;
                return;
            }
        }
        System.out.println(user.getName() + " cannot be removed because it is not in the library.");

    }
    public void displayUsers() {
        System.out.println("Library Users:");
        for (User user : users) {
            user.print_info();
            System.out.println();
        }
    }

    public void loanBook(String bookID, String UserID){
        System.out.println("in loanbook ftn");
        Book book = findBook(bookID);
        User user = findUser(UserID);

        if(book==null){
            System.out.println("Book not found!");
        }
        else if(user==null){
            System.out.println("User not found!");
        }
        else if(!book.get_loanable()){
            System.out.println("Reference books cannot be loaned!");
        }
        else if(user.getLoanedBooks().size()==user.getMaxBooks()){
            System.out.println("You cannot loan more than " + user.getMaxBooks() + " books.");
        }
        else if(!book.isLoaned()) {
            //System.out.println("in loanbook ftn, total loan fee " + user.getTotalLoanFees());
            book.setLoaned(true);
            user.add_loaned_book(book);
            double fee = book.calculateLoanFee(book.getDays_loan());
            //System.out.println("in loanbook ftn, double fee: "+ fee);
            book.setCurrent_loan(fee);
            user.addLoanFee(fee);

            System.out.println(book.getTitle() + " loaned.");
        }
        else{
            System.out.println("This Book has already been loaned.");
        }

    }

    public void returnBook(String bookID, String UserID){
        Book book = findBook(bookID);
        User user = findUser(UserID);

        if(book==null){
            System.out.println("Book not found\n");
        }
        else if(user==null){
            System.out.println("User not found.\n");
        }
        else{
            user.remove_loaned_book(book);
            user.subtract_loanFee(book.getCurrent_loan());
            book.setLoaned(false);
            book.setCurrent_loan(0.0);
            book.set_days_loan(0);
            System.out.println("Book has been returned.");
        }
    }
    public Book findBook(String bookID){
        for (Book book : books) {
            if(book.getBookId().equals(bookID)){
                return book;
            }
        }
        return null;
    }

    public User findUser(String user_id){
        for (User user : users) {
            if(user.getUserId().equals(user_id)){
                return user;
            }
        }
        return null;
    }
    public void extend_loan(String bookID, String UserID,int days){

        boolean book_found=false;
        Book book = findBook(bookID);
        User user = findUser(UserID);
        if(book==null){
            System.out.println("Book not found\n");
        }
        else if(user==null){
            System.out.println("User not found.\n");
        }
        else{
            if(book.isLoaned()) {
                book.extend_Days_loan(days);
                if (book.get_extension()) {
                    double loan_fee = book.calculateLoanFee(days);
                    //modify user total loan fee
                    user.addLoanFee(loan_fee);
                    double curr = book.getCurrent_loan();
                    book.setCurrent_loan(curr+loan_fee);
                    System.out.println("Book loan has been extended.");
                    System.out.println("Book loan fee: "+ book.getCurrent_loan());
                    System.out.println("Total Loan fee of user : " + user.getTotalLoanFees());
                } else {
                    System.out.println("Extension not available\n");
                }
            }
            else{
                System.out.println("Book was not loaned.\n");
            }
        }
    }
    public void displayLoanDetails(String user_id){

        boolean no_loan=true;
        User user = findUser(user_id);
        if(user==null){
            System.out.println("User not found\n");
            return;
        }
            List<Book> books_loaned = user.getLoanedBooks();

            if(!books_loaned.isEmpty()) {
                no_loan=false;
                System.out.println(user.getUserId() + " " + user.getName());
                System.out.println("Number of books Loaned: " + books_loaned.size());
                System.out.println("Total Loan Fees: " + user.getTotalLoanFees());
                System.out.println("Loaned Books: ");

                int count = 1;
                for (Book book : books_loaned) {
                    System.out.println(count + ") " + book.getBookId() + " " + book.getTitle());
                    System.out.println("Loan days: " + book.getDays_loan());
                    System.out.println("Loan fee: " + book.getCurrent_loan());
                    count++;
                }
                System.out.println();
            }

        if(no_loan){
            System.out.println("No books loaned\n");
        }
    }
    public void calculateTotalLoanCost(String user_id){
        User user = findUser(user_id);
        if(user==null){
            System.out.println("User not found.\n");
        }
        else{
            double loan_fee = user.getTotalLoanFees();
            System.out.println("Total Loan fee: " + loan_fee);
        }
    }
//    public User getUser(String userID){
//        for (User user : users) {
//
//        }
//    }
//    public Book getBook(String bookID){
//        for(Book book : books){
//            if(book.id.equals(bookID)){
//                return book;
//            }
//        }
//        return null;
//    }

}
