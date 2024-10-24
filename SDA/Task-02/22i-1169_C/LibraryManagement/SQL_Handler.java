package LibraryManagement;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SQL_Handler extends PersistentHandler {
    private Connection conn;

    public SQL_Handler(String dbUrl, String user, String password) throws ClassNotFoundException {
        // replace with your password
        try {
            // Optional: Load the SQL Server JDBC driver
            // Class.forName("org.mariadb.jdbc.Driver");

            // Establish the connection
            conn = DriverManager.getConnection(dbUrl, user, password);
            if (conn != null) {
                System.out.println("Connection established successfully.");
            } else {
                System.out.println("Failed to establish connection.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addUser(User user) {
        // Check if user already exists in the database
        try (PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM User WHERE id = ?")) {

            checkStmt.setInt(1, user.getUserId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("User already exists");
                return;
            }

            // Insert user into the MySQL database
            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO User (id, name, email, phone, address, user_type, fine) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

                insertStmt.setInt(1, user.getUserId());
                insertStmt.setString(2, user.getName());
                insertStmt.setString(3, user.getEmail());
                insertStmt.setString(4, user.getPhoneNumber());
                insertStmt.setString(5, user.getAddress());
                insertStmt.setString(6, user.getType());
                insertStmt.setDouble(7, user.getFine());

                insertStmt.executeUpdate();
                System.out.println(user.getName() + " added to the library.");

            }

        } catch (SQLException e) {
            System.out.println("Error adding user to MySQL: " + e.getMessage());
        }
    }


    @Override
    public void addBook(Book book) {
        // Check if the book already exists in the database
        try (PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM Book WHERE id = ?")) {

            checkStmt.setInt(1, book.getBookId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Book already exists");
                return;
            }

            // Insert book into the MySQL database
            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO Book (id, title, author, isbn, publication_year, genre, IsLoanable, IsExtendable, book_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

                insertStmt.setInt(1, book.getBookId());
                insertStmt.setString(2, book.getTitle());
                insertStmt.setString(3, book.getAuthor());
                insertStmt.setString(4, book.getIsbn());
                insertStmt.setInt(5, book.getpublication_year());
                insertStmt.setString(6, book.getGenre());
                insertStmt.setBoolean(7, book.get_IsLoanable());
                insertStmt.setBoolean(8, book.IsExtendable());
                insertStmt.setString(9, book.getType());

                insertStmt.executeUpdate();
                System.out.println(book.getTitle() + " added to the library.");

            }

        } catch (SQLException e) {
            System.out.println("Error adding book to MySQL: " + e.getMessage());
        }
    }

    public boolean isBookLoaned(int bookID) {
        String query = "SELECT COUNT(*) FROM Loan WHERE book_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if there's at least one loan record without a return date and loan_fee > 0
            }
        } catch (SQLException e) {
            System.out.println("Error checking if book is loaned: " + e.getMessage());
        }
        return false; // Return false if there's no loan record found
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
        } else if (getLoanedBooksCount(userID) >= user.getMaxBooks()) {
            System.out.println("You cannot loan more than " + user.getMaxBooks() + " books.");
        } else if (!isBookLoaned(bookID)) {
            book.setLoaned(true);

            // Calculate loan fee based on the book type and number of days
            double fee = book.calculateLoanFee(days);

            // Get the current date and calculate return date
            LocalDate loanDate = LocalDate.now();
            LocalDate returnDate = loanDate.plusDays(days);

            // Set loan data for the book
            book.loan.Set_Values(loanDate, returnDate, fee, false);

            try {
                // Insert loan record into the Loan table in the database
                String sql = "INSERT INTO Loan (user_id, book_id, loanDate, returnDate, loan_fee, isExtended) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userID);
                stmt.setInt(2, bookID);
                stmt.setDate(3, java.sql.Date.valueOf(loanDate));
                stmt.setDate(4, java.sql.Date.valueOf(returnDate));
                stmt.setDouble(5, fee);
                stmt.setBoolean(6, false);
                // Execute the statement
                stmt.executeUpdate();

                System.out.println(book.getTitle() + " loaned to " + user.getName() + ".");
            } catch (SQLException e) {

                System.out.println("Error processing loan: " + e.getMessage());
            }
        } else {
            System.out.println("This book has already been loaned.");
        }
    }

    public int getLoanedBooksCount(int userID) {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) FROM Loan WHERE user_id = ? AND returnDate > CURRENT_DATE";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting loaned books count: " + e.getMessage());
        }
        return count;
    }

    @Override
    public void returnBook(int bookID, int userID) {
        Book book = findBook(bookID); // SQL function to find the book from the database
        User user = findUser(userID); // SQL function to find the user from the database

        if (book == null) {
            System.out.println("Book not found\n");
            return;
        }
        if (user == null) {
            System.out.println("User not found.\n");
            return;
        }

        try {
            // Check if the book is loaned by that user
            String checkLoanQuery = "SELECT * FROM Loan WHERE user_id = ? AND book_id = ?";
            PreparedStatement checkLoanStmt = conn.prepareStatement(checkLoanQuery);
            checkLoanStmt.setInt(1, userID);
            checkLoanStmt.setInt(2, bookID);
            ResultSet rs = checkLoanStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Book and user id don't match \n");
                return;
            }

            // Perform return operations
            LocalDate returnDate = rs.getDate("returnDate").toLocalDate();
            double loanFee = rs.getDouble("loan_fee");

            // Check if the book is returned late
            LocalDate currentDate = LocalDate.now();
            if (currentDate.isAfter(returnDate)) {
                double lateFee = book.getBaseFee(); // base fee is the fine for late returns
                System.out.println("Book has been returned late. Fine: " + lateFee);
                System.out.println("Total unpaid fines: " + user.getFine());
                updateUserFine(userID, lateFee); // Add fine to user's account
            } else {
                System.out.println("Book has been returned on time.");
            }

            // Mark the book as not loaned
            String returnBookQuery = "DELETE FROM Loan WHERE user_id = ? AND book_id = ?";
            PreparedStatement returnBookStmt = conn.prepareStatement(returnBookQuery);
            returnBookStmt.setInt(1, userID);
            returnBookStmt.setInt(2, bookID);
            returnBookStmt.executeUpdate();

            System.out.println("Book returned successfully.");

        } catch (SQLException e) {
            System.out.println("Error processing return: " + e.getMessage());
        }
    }
    public void updateUserFine(int userId, double fineAmount) {
        String updateFineQuery = "UPDATE User SET fine = fine + ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateFineQuery)) {
            pstmt.setDouble(1, fineAmount);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating fine: " + e.getMessage());
        }
    }


    @Override
    public void removeBook(int book_id) {
        try {
            // Check if the book exists and is loaned
            String checkBookQuery = "SELECT * FROM Book WHERE id = ?";
            PreparedStatement checkBookStmt = conn.prepareStatement(checkBookQuery);
            checkBookStmt.setInt(1, book_id);
            ResultSet rs = checkBookStmt.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                boolean isLoaned = isBookLoaned(book_id);  // Check if the book is loaned using a helper function

                if (!isLoaned) {
                    // Remove the book if it is not loaned
                    String deleteBookQuery = "DELETE FROM Book WHERE id = ?";
                    PreparedStatement deleteBookStmt = conn.prepareStatement(deleteBookQuery);
                    deleteBookStmt.setInt(1, book_id);
                    int rowsAffected = deleteBookStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println(title + " removed from the library.");
                    } else {
                        System.out.println("Failed to remove the book.");
                    }
                } else {
                    System.out.println(title + " cannot be removed because it is already loaned.");
                }
            } else {
                System.out.println("Book not found in the library.");
            }
        } catch (SQLException e) {
            System.out.println("Error while removing the book: " + e.getMessage());
        }
    }

    @Override
    public void removeUser(int userID) {
        try {
            // Check if the user exists
            String checkUserQuery = "SELECT * FROM User WHERE id = ?";
            PreparedStatement checkUserStmt = conn.prepareStatement(checkUserQuery);
            checkUserStmt.setInt(1, userID);
            ResultSet userResult = checkUserStmt.executeQuery();

            if (userResult.next()) {
                String userName = userResult.getString("name");

                // Check if the user has any loaned books
                if (!hasLoanedBooks(userID)) {
                    // Remove user from the User table
                    String deleteUserQuery = "DELETE FROM User WHERE id = ?";
                    PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery);
                    deleteUserStmt.setInt(1, userID);
                    int rowsAffected = deleteUserStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println(userName + " removed as a user.");
                    } else {
                        System.out.println("Failed to remove the user.");
                    }
                } else {
                    System.out.println(userName + " cannot be removed because they have loaned some books.");
                }
            } else {
                System.out.println("User cannot be removed because they are not in the library.");
            }
        } catch (SQLException e) {
            System.out.println("Error while removing the user: " + e.getMessage());
        }
    }
    public boolean hasLoanedBooks(int userID) {
        String checkLoanQuery = "SELECT * FROM Loan WHERE user_id = ?";
        try (PreparedStatement checkLoanStmt = conn.prepareStatement(checkLoanQuery)) {
            checkLoanStmt.setInt(1, userID);
            ResultSet loanResult = checkLoanStmt.executeQuery();

            // If the user has loaned books, return true
            return loanResult.next();
        } catch (SQLException e) {
            System.out.println("Error checking loan status: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void displayLoanStatus(int bookID) {
        try {
            // Check if the book exists
            String findBookQuery = "SELECT title FROM Book WHERE id = ?";
            PreparedStatement findBookStmt = conn.prepareStatement(findBookQuery);
            findBookStmt.setInt(1, bookID);
            ResultSet bookResult = findBookStmt.executeQuery();

            if (bookResult.next()) {
                String bookTitle = bookResult.getString("title");

                // Check loan status of the book
                String checkLoanQuery = "SELECT * FROM Loan WHERE book_id = ?";
                PreparedStatement checkLoanStmt = conn.prepareStatement(checkLoanQuery);
                checkLoanStmt.setInt(1, bookID);
                ResultSet loanResult = checkLoanStmt.executeQuery();

                if (loanResult.next()) {
                    // Book is currently loaned
                    System.out.println(bookTitle + " is currently loaned.");
                } else {
                    // Book is not loaned
                    System.out.println(bookTitle + " is not loaned.");
                }
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error while checking loan status: " + e.getMessage());
        }
    }

    @Override
    public void displayAvailableBooks() {
        try {
            // Query to select all books that are not loaned
            String query = "SELECT b.id, b.title, b.genre, b.author, b.publication_year " +
                    "FROM Book b LEFT JOIN Loan l ON b.id = l.book_id " +
                    "WHERE l.loan_fee IS NULL OR l.loan_fee = 0";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Available books:");
            while (rs.next()) {
                int bookID = rs.getInt("id");
                String title = rs.getString("title");
                String genre = rs.getString("genre");
                String author = rs.getString("author");
                int publicationYear = rs.getInt("publication_year");

                // Print book details
                System.out.println("- " + title + " (" + genre + ")");
                System.out.println("  Author: " + author + ", Year: " + publicationYear);
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error displaying available books: " + e.getMessage());
        }
    }

    @Override
    public void printUserInfo(int userID) {
        try {
            // Query to select user details by userID
            String userQuery = "SELECT id, name, email, phone, address, user_type, fine FROM User WHERE id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userQuery);
            userStmt.setInt(1, userID);

            ResultSet userRs = userStmt.executeQuery();

            // Check if user is found
            if (userRs.next()) {
                // Fetch user details from the result set
                int id = userRs.getInt("id");
                String name = userRs.getString("name");
                String email = userRs.getString("email");
                String phone = userRs.getString("phone");
                String address = userRs.getString("address");
                String userType = userRs.getString("user_type");
                double fine = userRs.getDouble("fine");

                // Print user information
                System.out.println("User Info:");
                System.out.println("ID: " + id);
                System.out.println("Name: " + name);
                System.out.println("Email: " + email);
                System.out.println("Phone: " + phone);
                System.out.println("Address: " + address);
                System.out.println("User Type: " + userType);
                System.out.println("Fine: $" + fine);
                System.out.println();

                // Query to select loans for the user
                String loanQuery = "SELECT book_id, loanDate, returnDate, loan_fee, isExtended FROM Loan WHERE user_id = ?";
                PreparedStatement loanStmt = conn.prepareStatement(loanQuery);
                loanStmt.setInt(1, userID);

                ResultSet loanRs = loanStmt.executeQuery();

                // Print loans associated with the user
                System.out.println("Loans for " + name + ":");
                boolean hasLoans = false;
                while (loanRs.next()) {
                    hasLoans = true;
                    int bookId = loanRs.getInt("book_id");
                    Date loanDate = loanRs.getDate("loanDate");
                    Date returnDate = loanRs.getDate("returnDate");
                    double loanFee = loanRs.getDouble("loan_fee");
                    boolean isExtended = loanRs.getBoolean("isExtended");

                    // Print loan information
                    System.out.println("Book ID: " + bookId + ", Loan Date: " + loanDate +
                            ", Return Date: " + returnDate + ", Loan Fee: $" + loanFee +
                            ", Extended: " + (isExtended ? "Yes" : "No"));
                }

                if (!hasLoans) {
                    System.out.println("No loans found for this user.");
                }
                System.out.println();
            } else {
                // If no user found
                System.out.println("User not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving user info: " + e.getMessage());
        }
    }

    @Override
    public void displayUsers() {
        try {
            // Query to select all users from the User table
            String query = "SELECT id, name, email, phone, address, user_type, fine FROM User";
            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();

            System.out.println("All Users Info:");
            boolean hasUsers = false;

            // Iterate through the result set
            while (rs.next()) {
                hasUsers = true;
                int userId = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String userType = rs.getString("user_type");
                double fine = rs.getDouble("fine");

                // Print user information
                System.out.println("ID: " + userId);
                System.out.println("Name: " + name);
                System.out.println("Email: " + email);
                System.out.println("Phone: " + phone);
                System.out.println("Address: " + address);
                System.out.println("User Type: " + userType);
                System.out.println("Fine: $" + fine);
                System.out.println("Loans:");

                // Query to get loans for the current user
                String loanQuery = "SELECT book_id, loanDate, returnDate, loan_fee, isExtended FROM Loan WHERE user_id = ?";
                PreparedStatement loanStmt = conn.prepareStatement(loanQuery);
                loanStmt.setInt(1, userId);
                ResultSet loanRs = loanStmt.executeQuery();

                boolean hasLoans = false;

                // Iterate through the loans for the current user
                while (loanRs.next()) {
                    hasLoans = true;
                    int bookId = loanRs.getInt("book_id");
                    Date loanDate = loanRs.getDate("loanDate");
                    Date returnDate = loanRs.getDate("returnDate");
                    double loanFee = loanRs.getDouble("loan_fee");
                    boolean isExtended = loanRs.getBoolean("isExtended");

                    // Print loan information
                    System.out.println("  Book ID: " + bookId);
                    System.out.println("  Loan Date: " + loanDate);
                    System.out.println("  Return Date: " + returnDate);
                    System.out.println("  Loan Fee: $" + loanFee);
                    System.out.println("  Is Extended: " + (isExtended ? "Yes" : "No"));
                    System.out.println("  --------------------------------------------------");
                }

                if (!hasLoans) {
                    System.out.println("  No loans found for this user.");
                }

                System.out.println("--------------------------------------------------");
            }

            if (!hasUsers) {
                System.out.println("No users found.");
            }

            System.out.println();
        } catch (SQLException e) {
            System.out.println("Error retrieving users info: " + e.getMessage());
        }
    }

    @Override
    public void extend_loan(int bookID, int userID, int days) {
        // Check if the book and user exist
        Book book = findBook(bookID);
        User user = findUser(userID);

        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        // Query to check if the book is loaned by the user
        String checkLoanQuery = "SELECT * FROM Loan WHERE book_id = ? AND user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(checkLoanQuery)) {
            pstmt.setInt(1, bookID);
            pstmt.setInt(2, userID);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // The book is loaned by the user; we can extend the loan
                boolean isExtendable = book.IsExtendable(); // Check if the book is extendable
                boolean isExtended = rs.getBoolean("isExtended"); // Get the current extension status

                if (isExtendable && !isExtended) {
                    // Modify loan fee and return date
                    double loan_fee = book.calculateLoanFee(days); // Calculate new loan fee
                    LocalDate newReturnDate = LocalDate.parse(rs.getString("returnDate")).plusDays(days); // Get and extend the return date

                    // Update the Loan table with the new return date and fee
                    String updateLoanQuery = "UPDATE Loan SET returnDate = ?, loan_fee = ?, isExtended = ? WHERE book_id = ? AND user_id = ?";
                    try (PreparedStatement updatePstmt = conn.prepareStatement(updateLoanQuery)) {
                        updatePstmt.setDate(1, java.sql.Date.valueOf(newReturnDate));
                        updatePstmt.setDouble(2, rs.getDouble("loan_fee") + loan_fee);
                        updatePstmt.setBoolean(3, true);
                        updatePstmt.setInt(4, bookID);
                        updatePstmt.setInt(5, userID);
                        updatePstmt.executeUpdate();

                        System.out.println("Book loan has been extended.");
                        System.out.println("New loan fee: " + (rs.getDouble("loan_fee") + loan_fee));
                    }
                } else {
                    System.out.println("Extension not available.");
                }
            } else {
                System.out.println("Book was not loaned by this user.");
            }
        } catch (SQLException e) {
            System.out.println("Error extending loan: " + e.getMessage());
        }
    }

    @Override
    public void displayLoanDetails(int user_id) {
        User user = findUser(user_id); // Find user by ID

        if (user == null) {
            System.out.println("User not found\n");
            return;
        }

        // Query to fetch loan details for the user
        String loanDetailsQuery = "SELECT b.id AS book_id, b.title, l.loan_fee, l.loanDate, l.returnDate, l.isExtended " +
                "FROM Loan l " +
                "JOIN Book b ON l.book_id = b.id " +
                "WHERE l.user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(loanDetailsQuery)) {
            pstmt.setInt(1, user_id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) { // Check if there are no results
                System.out.println("No books loaned\n");
                return;
            }

            System.out.println(user.getUserId() + " " + user.getName());
            System.out.println("Loaned Books:");

            int count = 1;
            double total_fee = 0.0;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                double loanFee = rs.getDouble("loan_fee");
                LocalDate loanDate = rs.getDate("loanDate").toLocalDate();
                LocalDate returnDate = rs.getDate("returnDate").toLocalDate();
                boolean isExtended = rs.getBoolean("isExtended");

                System.out.println(count + ") " + bookId + "- " + title +
                        " , Loan: " + loanFee +
                        " , Loan Date: " + loanDate.format(formatter) +
                        " , Return Date: " + returnDate.format(formatter) +
                        " , Extended: " + isExtended);

                total_fee += loanFee;
                count++;
                System.out.println("---------------------------------------------------------\n");
            }

            System.out.println("Total Loan Fees: " + total_fee);
            System.out.println();

        } catch (SQLException e) {
            System.out.println("Error retrieving loan details: " + e.getMessage());
        }
    }

    @Override
    public void calculateTotalLoanCost(int user_id) {
        User user = findUser(user_id); // Find user by ID
        if (user == null) {
            System.out.println("User not found.\n");
            return;
        }

        // Query to fetch total loan fees for the user
        String totalLoanCostQuery = "SELECT SUM(l.loan_fee) AS total_fee " +
                "FROM Loan l " +
                "WHERE l.user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(totalLoanCostQuery)) {
            pstmt.setInt(1, user_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double total_fee = rs.getDouble("total_fee");
                if (total_fee == 0) {
                    System.out.println("No loans found for user " + user.getName() + ".");
                } else {
                    System.out.println("\nTotal Loan Fees for " + user.getName() + ": " + total_fee);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error calculating total loan cost: " + e.getMessage());
        }
    }


    @Override
    public Book findBook(int bookID) {
        String query = "SELECT * FROM Book WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookID);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                // Retrieve book data from the ResultSet
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String isbn = resultSet.getString("isbn");
                int publicationYear = resultSet.getInt("publication_year");
                String genre = resultSet.getString("genre");
                boolean isLoanable = resultSet.getBoolean("IsLoanable");
                boolean isExtendable = resultSet.getBoolean("IsExtendable");
                String bookType = resultSet.getString("book_type");

                // Create and return the appropriate Book subclass based on bookType
                switch (bookType.toLowerCase()) {
                    case "textbook":
                        return new Textbook(id, title, author, isbn, publicationYear, genre);
                    case "novel":
                        return new Novel(id, title, author, isbn, publicationYear, genre);
                    case "referencebook":
                        return new ReferenceBook(id, title, author, isbn, publicationYear, genre);
                    default:
                        throw new IllegalArgumentException("Unknown book type: " + bookType);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding book: " + e.getMessage());
        }
        return null; // Return null if book not found
    }


    @Override
    public User findUser(int userID) {
        String query = "SELECT * FROM User WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userID);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                // Retrieve user data from the ResultSet
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                String userType = resultSet.getString("user_type");
                double fine = resultSet.getDouble("fine");

                // Create and return the appropriate User subclass based on userType
                switch (userType.toLowerCase()) {
                    case "student":
                        return new Student(id, name, email, phone, address);
                    case "faculty":
                        return new Faculty(id, name, email, phone, address);
                    case "publicmember":
                        return new PublicMember(id, name, email, phone, address);
                    default:
                        throw new IllegalArgumentException("Unknown user type: " + userType);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding user: " + e.getMessage());
        }
        return null; // Return null if user not found
    }


    @Override
    public void searchBook(int bookId) {
        String query = "SELECT b.id, b.title, b.author, b.isbn, l.loan_fee, l.loanDate, l.returnDate, l.isExtended " +
                "FROM Book b LEFT JOIN Loan l ON b.id = l.book_id " +
                "WHERE b.id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, bookId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Print the content of the book and loan info
                System.out.println("Book ID: " + resultSet.getInt("id"));
                System.out.println("Title: " + resultSet.getString("title"));
                System.out.println("Author: " + resultSet.getString("author"));
                System.out.println("ISBN: " + resultSet.getString("isbn"));

                double loanFee = resultSet.getDouble("loan_fee");
                if (loanFee != 0) {
                    System.out.println("Loan Fee: $" + loanFee);
                    System.out.println("Loan Date: " + resultSet.getDate("loanDate"));
                    System.out.println("Return Date: " + resultSet.getDate("returnDate"));
                    System.out.println("Is Extended: " + (resultSet.getBoolean("isExtended") ? "Yes" : "No"));
                } else {
                    System.out.println("This book is available.");
                }
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving book information: " + e.getMessage());
        }
    }

    @Override
    public void searchBook(String title) {
        String query = "SELECT b.id, b.title, b.author, b.isbn, l.loan_fee, l.loanDate, l.returnDate, l.isExtended " +
                "FROM Book b LEFT JOIN Loan l ON b.id = l.book_id " +
                "WHERE b.title = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();
            boolean found = false;

            while (resultSet.next()) {
                found = true;
                // Print the content of the book and loan info
                System.out.println("Book ID: " + resultSet.getInt("id"));
                System.out.println("Title: " + resultSet.getString("title"));
                System.out.println("Author: " + resultSet.getString("author"));
                System.out.println("ISBN: " + resultSet.getString("isbn"));

                double loanFee = resultSet.getDouble("loan_fee");
                if (loanFee != 0) {
                    System.out.println("Loan Fee: $" + loanFee);
                    System.out.println("Loan Date: " + resultSet.getDate("loanDate"));
                    System.out.println("Return Date: " + resultSet.getDate("returnDate"));
                    System.out.println("Is Extended: " + (resultSet.getBoolean("isExtended") ? "Yes" : "No"));
                } else {
                    System.out.println("This book is available.");
                }
                System.out.println("-------------------------------------------------");
            }
            if (!found) {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving book information: " + e.getMessage());
        }
    }

    @Override
    public void searchBookbyAuthor(String author) {
        String query = "SELECT b.id, b.title, b.author, b.isbn, l.loan_fee, l.loanDate, l.returnDate, l.isExtended " +
                "FROM Book b LEFT JOIN Loan l ON b.id = l.book_id " +
                "WHERE b.author = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, author);
            ResultSet resultSet = statement.executeQuery();
            boolean found = false;

            while (resultSet.next()) {
                found = true;
                // Print the content of the book and loan info
                System.out.println("Book ID: " + resultSet.getInt("id"));
                System.out.println("Title: " + resultSet.getString("title"));
                System.out.println("Author: " + resultSet.getString("author"));
                System.out.println("ISBN: " + resultSet.getString("isbn"));

                double loanFee = resultSet.getDouble("loan_fee");
                if (loanFee != 0) {
                    System.out.println("Loan Fee: $" + loanFee);
                    System.out.println("Loan Date: " + resultSet.getDate("loanDate"));
                    System.out.println("Return Date: " + resultSet.getDate("returnDate"));
                    System.out.println("Is Extended: " + (resultSet.getBoolean("isExtended") ? "Yes" : "No"));
                } else {
                    System.out.println("This book is available.");
                }
                System.out.println("-------------------------------------------------");
            }
            if (!found) {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving book information: " + e.getMessage());
        }
    }

    @Override
    public void searchBookbyISBN(String isbn) {
        String query = "SELECT b.id, b.title, b.author, b.isbn, l.loan_fee, l.loanDate, l.returnDate, l.isExtended " +
                "FROM Book b LEFT JOIN Loan l ON b.id = l.book_id " +
                "WHERE b.isbn = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Print the content of the book and loan info
                System.out.println("Book ID: " + resultSet.getInt("id"));
                System.out.println("Title: " + resultSet.getString("title"));
                System.out.println("Author: " + resultSet.getString("author"));
                System.out.println("ISBN: " + resultSet.getString("isbn"));

                double loanFee = resultSet.getDouble("loan_fee");
                if (loanFee != 0) {
                    System.out.println("Loan Fee: $" + loanFee);
                    System.out.println("Loan Date: " + resultSet.getDate("loanDate"));
                    System.out.println("Return Date: " + resultSet.getDate("returnDate"));
                    System.out.println("Is Extended: " + (resultSet.getBoolean("isExtended") ? "Yes" : "No"));
                } else {
                    System.out.println("This book is available.");
                }
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving book information: " + e.getMessage());
        }
    }

    @Override
    public void searchUser(int userId) {
        String query = "SELECT id, name, email, phone, address, user_type, fine FROM User WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Print the content of the user
                System.out.println("User ID: " + resultSet.getInt("id"));
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Email: " + resultSet.getString("email"));
                System.out.println("Phone: " + resultSet.getString("phone"));
                System.out.println("Address: " + resultSet.getString("address"));
                System.out.println("User Type: " + resultSet.getString("user_type"));
                System.out.println("Fine: $" + resultSet.getDouble("fine"));

                // Query to select loans for the user
                String loanQuery = "SELECT book_id, loanDate, returnDate, loan_fee, isExtended FROM Loan WHERE user_id = ?";
                try (PreparedStatement loanStmt = conn.prepareStatement(loanQuery)) {
                    loanStmt.setInt(1, userId);
                    ResultSet loanRs = loanStmt.executeQuery();

                    // Print loans associated with the user
                    System.out.println("Loans for " + resultSet.getString("name") + ":");
                    boolean hasLoans = false;
                    while (loanRs.next()) {
                        hasLoans = true;
                        int bookId = loanRs.getInt("book_id");
                        Date loanDate = loanRs.getDate("loanDate");
                        Date returnDate = loanRs.getDate("returnDate");
                        double loanFee = loanRs.getDouble("loan_fee");
                        boolean isExtended = loanRs.getBoolean("isExtended");

                        // Print loan information
                        System.out.println("Book ID: " + bookId + ", Loan Date: " + loanDate +
                                ", Return Date: " + returnDate + ", Loan Fee: $" + loanFee +
                                ", Extended: " + (isExtended ? "Yes" : "No"));
                    }

                    if (!hasLoans) {
                        System.out.println("No loans found for this user.");
                    }
                }
            } else {
                // If no user found
                System.out.println("User not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving user information: " + e.getMessage());
        }
    }

    @Override
    public void searchUser(String username) {
        String query = "SELECT id, name, email, phone, address, user_type, fine FROM User WHERE name = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            boolean found = false;

            while (resultSet.next()) {
                found = true;
                // Print the content of the user
                System.out.println("User ID: " + resultSet.getInt("id"));
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Email: " + resultSet.getString("email"));
                System.out.println("Phone: " + resultSet.getString("phone"));
                System.out.println("Address: " + resultSet.getString("address"));
                System.out.println("User Type: " + resultSet.getString("user_type"));
                System.out.println("Fine: $" + resultSet.getDouble("fine"));

                // Query to select loans for the user
                String loanQuery = "SELECT book_id, loanDate, returnDate, loan_fee, isExtended FROM Loan WHERE user_id = ?";
                try (PreparedStatement loanStmt = conn.prepareStatement(loanQuery)) {
                    loanStmt.setInt(1, resultSet.getInt("id"));
                    ResultSet loanRs = loanStmt.executeQuery();

                    // Print loans associated with the user
                    System.out.println("Loans for " + resultSet.getString("name") + ":");
                    boolean hasLoans = false;
                    while (loanRs.next()) {
                        hasLoans = true;
                        int bookId = loanRs.getInt("book_id");
                        Date loanDate = loanRs.getDate("loanDate");
                        Date returnDate = loanRs.getDate("returnDate");
                        double loanFee = loanRs.getDouble("loan_fee");
                        boolean isExtended = loanRs.getBoolean("isExtended");

                        // Print loan information
                        System.out.println("Book ID: " + bookId + ", Loan Date: " + loanDate +
                                ", Return Date: " + returnDate + ", Loan Fee: $" + loanFee +
                                ", Extended: " + (isExtended ? "Yes" : "No"));
                    }

                    if (!hasLoans) {
                        System.out.println("No loans found for this user.");
                    }
                }
            }

            if (!found) {
                System.out.println("User not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving user information: " + e.getMessage());
        }
    }



    @Override
    public void sortUsersAlphabetically() {
        String query = "SELECT u.id, u.name, u.email, u.phone, u.address, u.user_type, u.fine, " +
                "l.book_id, l.loanDate, l.returnDate, l.loan_fee, l.isExtended " +
                "FROM User u LEFT JOIN Loan l ON u.id = l.user_id " +
                "ORDER BY u.name ASC";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Users sorted alphabetically by name:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String userType = rs.getString("user_type");
                double fine = rs.getDouble("fine");

                // Print user information
                System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email +
                        ", Phone: " + phone + ", Address: " + address +
                        ", User Type: " + userType + ", Fine: $" + fine);

                // Print loan information
                int bookId = rs.getInt("book_id");
                Date loanDate = rs.getDate("loanDate");
                Date returnDate = rs.getDate("returnDate");
                double loanFee = rs.getDouble("loan_fee");
                boolean isExtended = rs.getBoolean("isExtended");

                if (loanDate != null) { // Check if the user has loaned any books
                    System.out.println("  Book ID: " + bookId + ", Loan Date: " + loanDate +
                            ", Return Date: " + returnDate +
                            ", Loan Fee: $" + loanFee +
                            ", Extended: " + (isExtended ? "Yes" : "No"));
                } else {
                    System.out.println("  No loans found for this user.");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving sorted users: " + e.getMessage());
        }
    }



    @Override
    public void sortBooksAlphabetically() {
        String query = "SELECT b.id, b.title, b.genre, b.author, b.publication_year, " +
                "l.loanDate, l.returnDate, l.loan_fee, l.isExtended " +
                "FROM Book b LEFT JOIN Loan l ON b.id = l.book_id " +
                "ORDER BY b.title ASC";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Books sorted alphabetically by title:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String genre = rs.getString("genre");
                String author = rs.getString("author");
                int publicationYear = rs.getInt("publication_year");

                // Print book information
                System.out.println("ID: " + id + ", Title: " + title + ", Genre: " + genre +
                        ", Author: " + author + ", Year: " + publicationYear);

                // Print loan information
                Date loanDate = rs.getDate("loanDate");
                Date returnDate = rs.getDate("returnDate");
                double loanFee = rs.getDouble("loan_fee");
                boolean isExtended = rs.getBoolean("isExtended");

                if (loanDate != null) { // Check if the book is loaned
                    System.out.println("  Loan Date: " + loanDate +
                            ", Return Date: " + returnDate +
                            ", Loan Fee: $" + loanFee +
                            ", Extended: " + (isExtended ? "Yes" : "No"));
                } else {
                    System.out.println("  This book is currently available.");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving sorted books: " + e.getMessage());
        }
    }


}
