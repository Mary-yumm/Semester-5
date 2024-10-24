package LibraryManagement;

import java.io.*;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class File_Handler extends PersistentHandler {
    String bookFile;
    String userFile;
    String loansFile;
    protected List<Book> books;
    protected List<User> users;

    public File_Handler(String file1, String file2, String file3) {
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
        this.userFile = file1;
        this.bookFile = file2;
        this.loansFile = file3;

        // Check if files exist, create them if they don't
        createFileIfNotExists(userFile);
        createFileIfNotExists(bookFile);
        createFileIfNotExists(loansFile);

        // Load data from CSV after ensuring files exist
        loadDataFromCSV();
    }

    private void createFileIfNotExists(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile(); // Create the file if it doesn't exist
                System.out.println("File created: " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Error creating file: " + fileName);
            e.printStackTrace();
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////// Save and load to CSV files ////////////////////////////////////////////////////////////////////////////////////////////
    public void saveDataToCSV() {
        saveBooksToCSV();
        saveUsersToCSV();
        saveLoansToCSV();
    }

    private void saveBooksToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(bookFile))) {
            writer.write("id,title,author,isbn,publication_year,genre,IsLoanable,IsExtendable,book_type");
            writer.newLine();
            for (Book book : books) {
                writer.write(book.toCSVString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving books to CSV: " + e.getMessage());
        }
    }

    private void saveUsersToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
            writer.write("id,name,email,phone,address,user_type,fine");
            writer.newLine();
            for (User user : users) {
                writer.write(user.toCSVString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users to CSV: " + e.getMessage());
        }
    }

    private void saveLoansToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(loansFile))) {
            writer.write("user_id,book_id,loanDate,returnDate,loan_fee,isExtended");
            writer.newLine();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (User user : users) {
                ArrayList<Book> userLoans = (ArrayList<Book>) user.getLoanedBooks();
                // for loop of userLoans
                for (Book book : userLoans) {
                    String loanDateStr = book.loan.get_loanDate().format(formatter);
                    String returnDateStr = book.loan.get_returnDate().format(formatter);

                    writer.write(user.getUserId() + "," + book.getBookId() + "," + loanDateStr + ","
                            + returnDateStr + "," + book.loan.get_fee() + ","
                            + book.loan.get_isExtended());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving loans to CSV: " + e.getMessage());
        }
    }


    // Load users and books from CSV files
    private void loadDataFromCSV() {
        loadBooksFromCSV();
        loadUsersFromCSV();
        loadLoansFromCSV();
    }

    private void loadBooksFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(bookFile))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                //print data
                System.out.println(data[0]+" "+ data[1] + " "+ data[2] + " " + data[8]);
                Book book;
                switch (data[8]) { // Assuming book_type is at index 7
                    case "novel":
                        book = new Novel(Integer.parseInt(data[0]), data[1], data[2], data[3],
                                Integer.parseInt(data[4]),data[5]);
                        break;
                    case "referencebook":
                        book = new ReferenceBook(Integer.parseInt(data[0]), data[1], data[2], data[3],
                                Integer.parseInt(data[4]),data[5]);
                        break;
                    case "textbook":
                        book = new Textbook(Integer.parseInt(data[0]), data[1], data[2], data[3],
                                Integer.parseInt(data[4]),data[5]);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown book type: " + data[8]);
                }
                books.add(book);
            }
        } catch (IOException e) {
            System.out.println("Error loading books from CSV: " + e.getMessage());
        }
    }

    private void loadUsersFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                User user;
                switch (data[5]) { // user_type is at index 5
                    case "student":
                        user = new Student(Integer.parseInt(data[0]), data[1], data[2],
                                data[4], data[5]);
                        user.addFine(Double.parseDouble(data[6]));
                        break;
                    case "faculty":
                        user = new Faculty(Integer.parseInt(data[0]), data[1], data[2],
                                data[4], data[5]);
                        user.addFine(Double.parseDouble(data[6]));

                        break;
                    case "publicmember":
                        user = new PublicMember(Integer.parseInt(data[0]), data[1], data[2],
                                data[4], data[5]);
                        user.addFine(Double.parseDouble(data[6]));

                        break;
                    default:
                        throw new IllegalArgumentException("Unknown user type: " + data[5]);
                }
                users.add(user);
            }
        } catch (IOException e) {
            System.out.println("Error loading users from CSV: " + e.getMessage());
        }
    }

    private void loadLoansFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(loansFile))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                int userId = Integer.parseInt(data[0]);
                int bookId = Integer.parseInt(data[1]);

                String currentDateStr = data[2];
                String returnDateStr = data[3];

                // Parse the date strings to java.sql.Date objects
                // Parse the date strings to LocalDate objects
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate loanDate = LocalDate.parse(currentDateStr, dtf);
                LocalDate returnDate = LocalDate.parse(returnDateStr, dtf);

                double loan_fee = Double.parseDouble(data[4]);
                boolean isExtended = Boolean.parseBoolean(data[5]);

                Book book = findBook(bookId);
                book.loan.Set_Values(loanDate, returnDate, loan_fee, isExtended);
                if(loan_fee>0){
                    book.setLoaned(true);
                }
                User user = findUser(userId);

                if (user != null && book != null) {
                    user.add_loaned_book(book);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading loans from CSV: " + e.getMessage());
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////// Library Functions /////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void addUser(User user) {
        for (User check_users : users) {
            if (check_users.getUserId() == (user.getUserId())) {
                System.out.println("Book already exists");
                return;
            }
        }
        users.add(user);
        saveUsersToCSV(); // save to csv
        System.out.println(user.getName() + " added to the library.");

    }

    @Override
    public void addBook(Book book) {
        for (Book check_books : books) {
            if (check_books.getBookId() == (book.getBookId())) {
                System.out.println("Book already exists");
                return;
            }
        }
        books.add(book);
        saveBooksToCSV(); // save to csv
        System.out.println(book.getTitle() + " added to the library.");
    }

    @Override
    public void loanBook(int bookID, int userID, int days) {
        Book book = findBook(bookID);
        User user = findUser(userID);

        if (book == null) {
            System.out.println("Book not found!");
        } else if (user == null) {
            System.out.println("User not found!");
        } else if (!book.get_IsLoanable()) {
            System.out.println("Reference books cannot be loaned!");
        } else if (user.getLoanedBooks().size() == user.getMaxBooks()) {
            System.out.println("You cannot loan more than " + user.getMaxBooks() + " books.");
        } else if (!book.isLoaned()) {
            book.setLoaned(true);
            user.add_loaned_book(book);
            double fee = book.calculateLoanFee(days);

            // Get the current date
            LocalDate currentDate = LocalDate.now();

            // Add the specified number of days to the current date
            LocalDate returnDate = currentDate.plusDays(days);

            // Set the values for the loan function
            book.loan.Set_Values(currentDate, returnDate, fee, false);

            // Save to csv
            saveDataToCSV();

            System.out.println(book.getTitle() + " loaned.");
        } else {
            System.out.println("This Book has already been loaned.");
        }
    }

    @Override
    public void returnBook(int bookID, int userID) {
        Book book = findBook(bookID);
        User user = findUser(userID);

        if (book == null) {
            System.out.println("Book not found\n");
            return;
        }
        else if (user == null) {
            System.out.println("User not found.\n");
            return;
        }
        //check if book is loaned by that user
        ArrayList<Book> loaned = (ArrayList<Book>) user.getLoanedBooks();
        boolean check=false;
        for(Book b : loaned){
            if(b.getBookId() == bookID){
                check=true;
            }
        }

        if (!check) {
            System.out.println("Book was not loaned by this user\n");
        }
        else if(user.getUserId()!=userID || book.getBookId()!=bookID){
            System.out.println("Book and user ids don't match");
        }
        else {
            user.remove_loaned_book(book);
            book.setLoaned(false);
            book.loan.set_fee(0.0);
            book.loan.set_isExtended(false);

            LocalDate currentDate = LocalDate.now();
            // Check for late return
            if (currentDate.isAfter(book.loan.get_returnDate())) {
                double lateFee = book.getBaseFee(); // base fee is the fine for late returns
                System.out.println("Book has been returned late. Fine: " + lateFee);
                System.out.println("Total unpaid fines: " + user.getFine());
                user.addFine(lateFee); // Add fine to the user's account
            } else {
                System.out.println("Book has been returned.");
            }

            // update csv
            saveDataToCSV();
        }
    }

    @Override
    public void removeBook(int book_id) {
        for (Book book : books) {
            if (book.getBookId() == book_id) {
                if (!book.isLoaned()) {
                    books.remove(book);
                    System.out.println(book.getTitle() + " removed from the library.");
                    // update csv
                    saveDataToCSV();
                    break;
                } else {
                    System.out.println(book.getTitle() + " cannot be removed because it is already loaned.");

                }

            }

        }
    }

    @Override
    public void removeUser(int user) {
        // Check if user has already been removed or doesnt exist
        User user1 = findUser(user);
        boolean check = false;

        if(user1!=null){
            //if loaned books is null
            if(user1.getLoanedBooks().isEmpty()){
                users.remove(user1);
                System.out.println(user1.getName() + " removed as a user.");
                check = true;
                // update csv
                saveUsersToCSV();
                return;
            }
            else{
                System.out.println("user cannot be removed because it has loaned some books");
                return;
            }

        }
        System.out.println( "User cannot be removed because it is not in the library.");

    }

    @Override
    public void displayLoanStatus(int bookID) {
        Book book = findBook(bookID);
        if (book == null) {
            System.out.println("Book not found.");
        } else {
            if (book.isLoaned())
                System.out.println(book.getTitle() + " is currently loaned");
            else
                System.out.println(book.getTitle() + " is not loaned");
        }
    }

    @Override
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

    @Override
    public void printUserInfo(int userID) {
        User user = findUser(userID);
        if (user == null) {
            System.out.println("User not found.");
        } else {
            user.print_info();
        }
    }

    @Override
    public void displayUsers() {
        System.out.println("Library Users:");
        for (User user : users) {
            user.print_info();
            System.out.println();
        }
    }

    @Override
    public void extend_loan(int bookID, int userID, int days) {
        Book book = findBook(bookID);
        User user = findUser(userID);
        if (book == null) {
            System.out.println("Book not found\n");
        } else if (user == null) {
            System.out.println("User not found.\n");
        } else {
            if (book.isLoaned()) {

                if (book.IsExtendable() && !book.loan.get_isExtended()) {
                    // modify loan fee
                    double loan_fee = book.calculateLoanFee(days);
                    // modify return date
                    //modify return date by adding days

                    book.loan.set_returnDate(book.loan.get_returnDate().plusDays(days));
                    book.loan.set_isExtended(true);
                    book.loan.set_fee(book.loan.get_fee() + loan_fee);
                    System.out.println("Book loan has been extended.");
                    System.out.println("New loan fee: " + book.loan.get_fee());

                    // update csv
                    saveDataToCSV();
                } else {
                    System.out.println("Extension not available\n");
                }
            } else {
                System.out.println("Book was not loaned.\n");
            }
        }
    }

    @Override
    public void displayLoanDetails(int user_id) {
        boolean no_loan = true;
        User user = findUser(user_id);
        if (user == null) {
            System.out.println("User not found\n");
            return;
        }
        List<Book> books_loaned = user.getLoanedBooks();

        if (!books_loaned.isEmpty()) {
            no_loan = false;
            System.out.println(user.getUserId() + " " + user.getName());
            System.out.println("Number of books Loaned: " + books_loaned.size());

            int count = 1;
            double total_fee = 0.0;
            System.out.println("\nLoaned Books:");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Book book : books_loaned) {
                String loanDateStr = book.loan.get_loanDate().format(formatter);
                String returnDateStr = book.loan.get_returnDate().format(formatter);

                System.out.println(count + ") " + book.getBookId() + ") " + book.getTitle() +
                        " , Loan: " + book.loan.get_fee() + " , Loan Date: " + loanDateStr +
                        " , Return Date: " + returnDateStr + " , Extended: " + book.loan.get_isExtended());

                total_fee += book.loan.get_fee();
                count++;
                System.out.println("---------------------------------------------------------" + "\n");
            }

            System.out.println("\nTotal Loan Fees: " + total_fee);
            System.out.println();
        }

        if (no_loan) {
            System.out.println("No books loaned\n");
        }
    }

    @Override
    public void calculateTotalLoanCost(int user_id) {
        User user = findUser(user_id);
        if (user == null) {
            System.out.println("User not found.\n");
        } else {
            ArrayList<Book> Loaned_books = (ArrayList<Book>) user.getLoanedBooks();
            int count = 1;
            double total_fee = 0.0;
            for (Book book : Loaned_books) {
                total_fee += book.loan.get_fee();
                count++;
            }
            System.out.println("\nTotal Loan Fees: " + total_fee);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////// Helper Functions /////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Book findBook(int bookID) {
        for (Book book : books) {
            if (book.getBookId() == bookID) {
                return book;
            }
        }
        return null;
    }

    @Override
    public User findUser(int user_id) {
        for (User user : users) {
            if (user.getUserId() == user_id) {
                return user;
            }
        }
        return null;
    }

    /////////////////////////////////////////// SEARCH FUNCTIONS //////////////////////////////
    @Override
    public void searchBook(int bookid) {
        Book book = findBook(bookid);
        if (book != null) {
            book.print_info();
        } else {
            System.out.println("Book not found.");
        }

    }

    @Override
    public void searchBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                book.print_info();
                return;
            }
        }
        System.out.println("Book not found");

    }

    @Override
    public void searchBookbyAuthor(String Author) {
        for (Book book : books) {
            if (book.getAuthor().equals(Author)) {
                book.print_info();
                return;
            }
        }
        System.out.println("Book not found");

    }

    @Override
    public void searchBookbyISBN(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                book.print_info();
                return;
            }
        }
        System.out.println("Book not found");
    }

    @Override
    public void searchUser(int userid) {
        User user = findUser(userid);
        if (user != null) {
            user.print_info();
        } else {
            System.out.println("User not found.");
        }
    }

    @Override
    public void searchUser(String username){
        for(User user : users){
            if(user.getName().equals(username)){
                user.print_info();
                return;
            }
        }
        System.out.println("USer not found.");
    }

    ////////////////////////////////////////Sorting Functionality //////////////////////
    @Override
    public void sortBooksAlphabetically() {
        // Create a copy of the books list
        List<Book> sortedBooks = new ArrayList<>(books);

        // Sort the copy alphabetically by title
        sortedBooks.sort(new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return b1.getTitle().compareToIgnoreCase(b2.getTitle()); // Case-insensitive sorting
            }
        });

        // Print the sorted books
        System.out.println("Books sorted alphabetically by title:");
        for (Book book : sortedBooks) {
            book.print_info();
            System.out.println();
        }

    }

    @Override
    public void sortUsersAlphabetically() {
        // Create a copy of the users list
        List<User> sortedUsers = new ArrayList<>(users);

        // Sort the copy alphabetically by name
        sortedUsers.sort(new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u1.getName().compareToIgnoreCase(u2.getName()); // Case-insensitive sorting
            }
        });

        // Print the sorted users
        System.out.println("Users sorted alphabetically by name:");
        for (User user : sortedUsers) {
            user.print_info();
            System.out.println();
        }

    }


}
