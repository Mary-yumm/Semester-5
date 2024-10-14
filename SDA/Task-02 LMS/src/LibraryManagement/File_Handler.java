package LibraryManagement;

import java.io.*;
import java.util.*;

public class File_Handler extends PersistentHandler {
    String bookfile;
    String userfile;
    String loansfile;
    protected List<Book> books;
    protected List<User> users;

    public File_Handler(String file1,String file2,String file3, List<Book> books, List<User> users) {
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
        this.userfile = file1;
        this.bookfile = file2;
        this.loansfile = file3;
        
        loadDataFromCSV();
    }

    // Save users and books to CSV files
    public void saveDataToCSV() {
        saveBooksToCSV();
        saveUsersToCSV();
    }

    private void saveBooksToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("books.csv"))) {
            for (Book book : books) {
                writer.write(book.toCSVString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving books to CSV: " + e.getMessage());
        }
    }

    private void saveUsersToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.csv"))) {
            for (User user : users) {
                writer.write(user.toCSVString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users to CSV: " + e.getMessage());
        }
    }

    // Load users and books from CSV files
    private void loadDataFromCSV() {
        loadBooksFromCSV();
        loadUsersFromCSV();
    }

    private void loadBooksFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader("books.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                Book book = new Book(Integer.parseInt(data[0]), data[1], data[2], data[3],
                        Boolean.parseBoolean(data[4]),
                        Boolean.parseBoolean(data[5]),
                        Double.parseDouble(data[6]));
                books.add(book);
            }
        } catch (IOException e) {
            System.out.println("Error loading books from CSV: " + e.getMessage());
        }
    }

    private void loadUsersFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                User user = new User(Integer.parseInt(data[0]), data[1], data[2],
                        Integer.parseInt(data[3]),
                        Double.parseDouble(data[4]));
                users.add(user);
            }
        } catch (IOException e) {
            System.out.println("Error loading users from CSV: " + e.getMessage());
        }
    }

    // other functions

    @Override
    public void addUser(User user) {
        for (User check_users : users) {
            if (check_users.getUserId() == (user.getUserId())) {
                System.out.println("Book already exists");
                return;
            }
        }
        users.add(user);
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
        System.out.println(book.getTitle() + " added to the library.");
    }

    @Override
    public void loanBook(int bookID, int userID) {
        System.out.println("in loanbook ftn");
        Book book = findBook(bookID);
        User user = findUser(userID);

        if (book == null) {
            System.out.println("Book not found!");
        } else if (user == null) {
            System.out.println("User not found!");
        } else if (!book.get_loanable()) {
            System.out.println("Reference books cannot be loaned!");
        } else if (user.getLoanedBooks().size() == user.getMaxBooks()) {
            System.out.println("You cannot loan more than " + user.getMaxBooks() + " books.");
        } else if (!book.isLoaned()) {
            // System.out.println("in loanbook ftn, total loan fee " +
            // user.getTotalLoanFees());
            book.setLoaned(true);
            user.add_loaned_book(book);
            double fee = book.calculateLoanFee(book.getDays_loan());
            // System.out.println("in loanbook ftn, double fee: "+ fee);
            book.setCurrent_loan(fee);
            user.addLoanFee(fee);

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
        } else if (user == null) {
            System.out.println("User not found.\n");
        } else {
            user.remove_loaned_book(book);
            user.subtract_loanFee(book.getCurrent_loan());
            book.setLoaned(false);
            book.setCurrent_loan(0.0);
            book.set_days_loan(0);
            System.out.println("Book has been returned.");
        }
    }

    @Override
    public void removeBook(int book_id) {
        for (Book book : books) {
            if (book.getBookId() == book_id) {
                if (!book.isLoaned()) {
                    books.remove(book);
                    System.out.println(book.getTitle() + " removed from the library.");
                    break;
                } else {
                    System.out.println(book.getTitle() + " cannot be removed because it is already loaned.");

                }

            }

        }
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
                book.extend_Days_loan(days);
                if (book.get_extension()) {
                    double loan_fee = book.calculateLoanFee(days);
                    // modify user total loan fee
                    user.addLoanFee(loan_fee);
                    double curr = book.getCurrent_loan();
                    book.setCurrent_loan(curr + loan_fee);
                    System.out.println("Book loan has been extended.");
                    System.out.println("Book loan fee: " + book.getCurrent_loan());
                    System.out.println("Total Loan fee of user : " + user.getTotalLoanFees());
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
            double loan_fee = user.getTotalLoanFees();
            System.out.println("Total Loan fee: " + loan_fee);
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
    public void removeUser(User user) {
        // Check if user has already been removed or doesnt exist
        boolean check = false;
        for (User check_user : users) {
            if (check_user.getUserId() == (user.getUserId())) {
                users.remove(user);
                System.out.println(user.getName() + " removed as a user.");
                check = true;
                return;
            }
        }
        System.out.println(user.getName() + " cannot be removed because it is not in the library.");

    }

    // helper functions
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

}
