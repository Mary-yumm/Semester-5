package LibraryManagement;

import java.sql.*;

public class SQL_Handler extends PersistentHandler {
    private Connection conn;

    public SQL_Handler(String dbUrl, String user, String password) throws ClassNotFoundException {
        // replace with your password
        System.out.println("helloo");
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
        String checkSql = "SELECT COUNT(*) FROM Users WHERE user_id = ?";
        String insertSql = "INSERT INTO Users (user_id, name, email, user_type, phone, address) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            // Check if the user already exists
            checkStmt.setLong(1, user.getUserId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("User with ID " + user.getUserId() + " already exists. Skipping insert.");
                return;
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setLong(1, user.getUserId());
                insertStmt.setString(2, user.getName());
                insertStmt.setString(3, user.getEmail());
                insertStmt.setString(4, user.getType());
                insertStmt.setString(5, user.getPhoneNumber());
                insertStmt.setString(6, user.getAddress());
                insertStmt.executeUpdate();
                System.out.println("User added successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addBook(Book book) {
        String checkSql = "SELECT COUNT(*) FROM Books WHERE book_id = ?";
        String insertSql = "INSERT INTO Books (book_id, title, author, isbn, publication_year, genre, IsLoaned, loan_cost, book_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            // Check if the book already exists
            checkStmt.setLong(1, book.getBookId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Book with ID " + book.getBookId() + " already exists. Skipping insert.");
                return;
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setLong(1, book.getBookId());
                insertStmt.setString(2, book.getTitle());
                insertStmt.setString(3, book.getAuthor());
                insertStmt.setString(4, book.getIsbn());
                insertStmt.setInt(5, book.getYear());
                insertStmt.setString(6, book.getGenre());
                insertStmt.setBoolean(7, book.isLoaned());
                insertStmt.setDouble(8, book.getCurrent_loan());
                insertStmt.setString(9, book.getType());
                insertStmt.executeUpdate();
                System.out.println("Book added successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loanBook(int bookID, int userID) {
        PreparedStatement bookStmt = null;
        PreparedStatement userStmt = null;
        ResultSet rsBook = null;
        ResultSet rsUser = null;

        try {

            // Check if the book exists and get its details
            String bookQuery = "SELECT IsLoaned, loan_cost, title FROM Books WHERE book_id = ?";
            bookStmt = conn.prepareStatement(bookQuery);
            bookStmt.setLong(1, bookID);
            rsBook = bookStmt.executeQuery();

            if (!rsBook.next()) {
                System.out.println("Book with ID " + bookID + " not found!");
                return;
            }

            boolean isLoaned = rsBook.getBoolean("IsLoaned");
            double loanCost = rsBook.getDouble("loan_cost");
            String bookTitle = rsBook.getString("title");

            if (isLoaned) {
                System.out.println("This book has already been loaned.");
                return;
            }

            // Check if the user exists and their loan status
            String userQuery = "SELECT COUNT(*) AS loanedBooksCount, max_books FROM Users " +
                    "LEFT JOIN Loans ON Users.user_id = Loans.user_id " +
                    "WHERE Users.user_id = ?";
            userStmt = conn.prepareStatement(userQuery);
            userStmt.setLong(1, userID);
            rsUser = userStmt.executeQuery();

            if (!rsUser.next()) {
                System.out.println("User with ID " + userID + " not found!");
                return;
            }

            int loanedBooksCount = rsUser.getInt("loanedBooksCount");
            int maxBooks = rsUser.getInt("max_books");

            if (loanedBooksCount >= maxBooks) {
                System.out.println("User has reached the maximum limit of loaned books: " + maxBooks);
                return;
            }

            // Loan the book
            String loanBookQuery = "INSERT INTO Loans (user_id, book_id, loan_date, return_date) VALUES (?, ?, ?, ?)";
            PreparedStatement loanStmt = conn.prepareStatement(loanBookQuery);
            loanStmt.setLong(1, userID);
            loanStmt.setLong(2, bookID);
            loanStmt.setDate(3, new Date(System.currentTimeMillis()));
            loanStmt.setDate(4, null);
            loanStmt.executeUpdate();

            // Update book's loan status
            String updateBookStatusQuery = "UPDATE Books SET IsLoaned = 1 WHERE book_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateBookStatusQuery);
            updateStmt.setLong(1, bookID);
            updateStmt.executeUpdate();

            System.out.println(bookTitle + " has been loaned successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void returnBook(int bookID, int userID) {
        PreparedStatement stmt = null;
        ResultSet rsBook = null;
        ResultSet rsLoan = null;

        try {

            // Check if the book exists
            String bookQuery = "SELECT IsLoaned, loan_cost, title FROM Books WHERE book_id = ?";
            stmt = conn.prepareStatement(bookQuery);
            stmt.setLong(1, bookID);
            rsBook = stmt.executeQuery();

            if (!rsBook.next()) {
                System.out.println("Book with ID " + bookID + " not found!");
                return;
            }

            boolean isLoaned = rsBook.getBoolean("IsLoaned");
            String bookTitle = rsBook.getString("title");

            if (!isLoaned) {
                System.out.println("This book is not currently loaned.");
                return;
            }

            // Check if the user exists and has loaned the book
            String loanQuery = "SELECT loan_cost FROM Loans WHERE user_id = ? AND book_id = ?";
            stmt = conn.prepareStatement(loanQuery);
            stmt.setLong(1, userID);
            stmt.setLong(2, bookID);
            rsLoan = stmt.executeQuery();

            if (!rsLoan.next()) {
                System.out.println("User with ID " + userID + " has not loaned this book.");
                return;
            }

            double loanCost = rsLoan.getDouble("loan_cost");

            // Delete loan entry from the Loans table (the user is returning the book)
            String deleteLoanQuery = "DELETE FROM Loans WHERE user_id = ? AND book_id = ?";
            stmt = conn.prepareStatement(deleteLoanQuery);
            stmt.setLong(1, userID);
            stmt.setLong(2, bookID);
            stmt.executeUpdate();

            // Update the book's loan status
            String updateBookQuery = "UPDATE Books SET IsLoaned = 0, loan_cost = 0 WHERE book_id = ?";
            stmt = conn.prepareStatement(updateBookQuery);
            stmt.setLong(1, bookID);
            stmt.executeUpdate();

            // Optionally update the user's total loan fees, if you're tracking that
            // separately in the database
            String updateUserQuery = "UPDATE Users SET total_loan_fees = total_loan_fees - ? WHERE user_id = ?";
            stmt = conn.prepareStatement(updateUserQuery);
            stmt.setDouble(1, loanCost);
            stmt.setLong(2, userID);
            stmt.executeUpdate();

            System.out.println("Book '" + bookTitle + "' has been successfully returned.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeBook(int bookId) {
        String checkLoanedSql = "SELECT IsLoaned, title FROM Books WHERE book_id = ?";
        String deleteSql = "DELETE FROM Books WHERE book_id = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkLoanedSql);
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

            // Check if the book is loaned
            checkStmt.setLong(1, bookId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                boolean isLoaned = rs.getBoolean("IsLoaned");
                String title = rs.getString("title");

                if (!isLoaned) {
                    // If the book is not loaned, delete it
                    deleteStmt.setLong(1, bookId);
                    deleteStmt.executeUpdate();
                    System.out.println(title + " removed from the library.");
                } else {
                    System.out.println(title + " cannot be removed because it is already loaned.");
                }
            } else {
                System.out.println("Book with ID " + bookId + " not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeUser(User user) {
        String checkUserSql = "SELECT user_id FROM Users WHERE user_id = ?";
        String checkLoansSql = "SELECT COUNT(*) AS loanCount FROM Loans WHERE user_id = ?";
        String deleteUserSql = "DELETE FROM Users WHERE user_id = ?";

        try (PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSql);
                PreparedStatement checkLoansStmt = conn.prepareStatement(checkLoansSql);
                PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSql)) {

            // Check if the user exists
            checkUserStmt.setLong(1, user.getUserId());
            ResultSet rsUser = checkUserStmt.executeQuery();

            if (rsUser.next()) {
                // User exists, check if they have any loans
                long userId = rsUser.getLong("user_id");

                checkLoansStmt.setLong(1, userId);
                ResultSet rsLoans = checkLoansStmt.executeQuery();
                rsLoans.next();
                int loanCount = rsLoans.getInt("loanCount");

                if (loanCount > 0) {
                    System.out.println(user.getName() + " cannot be removed because they have loaned books.");
                    return;
                }

                // If the user has no loans, delete them
                deleteUserStmt.setLong(1, userId);
                deleteUserStmt.executeUpdate();
                System.out.println(user.getName() + " removed as a user.");
            } else {
                System.out.println(user.getName() + " cannot be removed because they do not exist in the library.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }
    }

    @Override
    public void displayLoanStatus(int bookId) {
        String sql = "SELECT IsLoaned FROM Books WHERE book_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean isLoaned = rs.getBoolean("IsLoaned");
                if (isLoaned) {
                    System.out.println("Book is loaned.");
                } else {
                    System.out.println("Book is not loaned.");
                }
            } else {
                System.out.println("Book does not exist.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void displayAvailableBooks() {
        String sql = "SELECT book_id, title, genre FROM Books WHERE IsLoaned = 0";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Available books:");
            boolean hasAvailableBooks = false;

            while (rs.next()) {
                long bookId = rs.getLong("book_id");
                String title = rs.getString("title");
                String genre = rs.getString("genre");
                System.out.println("- " + title + " (" + genre + ")");
                hasAvailableBooks = true; // Set flag if there are available books
            }

            if (!hasAvailableBooks) {
                System.out.println("No available books found.");
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void printUserInfo(int userID) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Retrieve user information from the result set
                String name = rs.getString("name");
                String email = rs.getString("email");
                String userType = rs.getString("user_type");
                String phone = rs.getString("phone");
                String address = rs.getString("address");

                // Print user information
                System.out.println("User Information:");
                System.out.println("ID: " + userID);
                System.out.println("Name: " + name);
                System.out.println("Email: " + email);
                System.out.println("User Type: " + userType);
                System.out.println("Phone: " + phone);
                System.out.println("Address: " + address);
            } else {
                System.out.println("User with ID " + userID + " does not exist.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }
    }

    @Override
    public void displayUsers() {
        String sql = "SELECT * FROM Users";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            System.out.println("User Information:");

            if (!rs.isBeforeFirst()) {
                System.out.println("No users found.");
                return;
            }

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String userType = rs.getString("user_type");
                String phone = rs.getString("phone");
                String address = rs.getString("address");

                System.out.println("ID: " + userId);
                System.out.println("Name: " + name);
                System.out.println("Email: " + email);
                System.out.println("User Type: " + userType);
                System.out.println("Phone: " + phone);
                System.out.println("Address: " + address);
                System.out.println("-----------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void extend_loan(int bookID, int userID, int days) {
        PreparedStatement stmt = null;
        ResultSet rsBook = null;
        ResultSet rsUser = null;

        try {
            // Check if the book exists and is loaned
            String bookQuery = "SELECT IsLoaned, loan_cost, extension_available, loan_days, current_loan, book_type FROM Books WHERE book_id = ?";
            stmt = conn.prepareStatement(bookQuery);
            stmt.setInt(1, bookID);
            rsBook = stmt.executeQuery();

            if (!rsBook.next()) {
                System.out.println("Book with ID " + bookID + " not found.");
                return;
            }

            boolean isLoaned = rsBook.getBoolean("IsLoaned");
            boolean extensionAvailable = rsBook.getBoolean("extension_available");
            int currentLoanDays = rsBook.getInt("loan_days");
            double currentLoanCost = rsBook.getDouble("current_loan");
            String bookType = rsBook.getString("book_type"); // Retrieve book type

            if (!isLoaned) {
                System.out.println("Book was not loaned.");
                return;
            }

            // Check if the user exists and get current loan fees
            String userQuery = "SELECT total_loan_fees FROM Users WHERE user_id = ?";
            stmt = conn.prepareStatement(userQuery);
            stmt.setInt(1, userID);
            rsUser = stmt.executeQuery();

            if (!rsUser.next()) {
                System.out.println("User with ID " + userID + " not found.");
                return;
            }

            double totalLoanFees = rsUser.getDouble("total_loan_fees");

            // Check if extension is available for the book
            if (extensionAvailable) {
                // Create the appropriate book instance based on the book type
                Book book = null;
                switch (bookType) {
                    case "TextBook":
                        book = new Textbook(bookID, "", "", "", 0, "", false, 0.0, "textbook"); // Replace with actual
                                                                                                // parameters
                        break;
                    case "Novel":
                        book = new Novel(bookID, "", "", "", 0, "", false, 0.0, "novel"); // Replace with actual
                                                                                          // parameters
                        break;
                    case "ReferenceBook":
                        book = new ReferenceBook(bookID, "", "", "", 0, "", false, 0.0, "referencebook"); // Replace
                                                                                                          // with actual
                                                                                                          // parameters
                        break;
                    default:
                        System.out.println("Unknown book type.");
                        return;
                }

                // Calculate the loan fee using the specific book class's method
                double additionalFee = book.calculateLoanFee(days);

                // Update book's loan days and loan cost
                String updateBookQuery = "UPDATE Books SET loan_days = loan_days + ?, current_loan = current_loan + ? WHERE book_id = ?";
                stmt = conn.prepareStatement(updateBookQuery);
                stmt.setInt(1, days);
                stmt.setDouble(2, additionalFee);
                stmt.setInt(3, bookID);
                stmt.executeUpdate();

                // Update user's total loan fees
                String updateUserQuery = "UPDATE Users SET total_loan_fees = total_loan_fees + ? WHERE user_id = ?";
                stmt = conn.prepareStatement(updateUserQuery);
                stmt.setDouble(1, additionalFee);
                stmt.setInt(2, userID);
                stmt.executeUpdate();

                System.out.println("Book loan has been extended.");
                System.out.println("New loan fee for the book: " + (currentLoanCost + additionalFee));
                System.out.println("Total loan fees for the user: " + (totalLoanFees + additionalFee));
            } else {
                System.out.println("Extension not available for this book.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rsBook != null)
                    rsBook.close();
                if (rsUser != null)
                    rsUser.close();
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void displayLoanDetails(int user_id) {
        String sql = "SELECT b.book_id, b.title, l.loan_date, l.return_date, l.days_extended, b.loan_cost " +
                "FROM Loans l " +
                "JOIN Books b ON l.book_id = b.book_id " +
                "WHERE l.user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user_id); // Set user_id parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No loan records found for user ID " + user_id + "\n");
                    return;
                }

                System.out.println("Loan details for User ID " + user_id + ":");

                double totalLoanFees = 0.0; // Variable to hold total loan fees
                int count = 1;

                // Print loan details for each book
                while (rs.next()) {
                    int bookId = rs.getInt("book_id");
                    String title = rs.getString("title");
                    Date loanDate = rs.getDate("loan_date");
                    Date returnDate = rs.getDate("return_date");
                    int daysExtended = rs.getInt("days_extended");
                    double loanCost = rs.getDouble("loan_cost");

                    System.out.println(count + ") Book ID: " + bookId);
                    System.out.println("   Title: " + title);
                    System.out.println("   Loan Date: " + loanDate);
                    System.out.println("   Return Date: " + (returnDate != null ? returnDate : "Not returned yet"));
                    System.out.println("   Days Extended: " + daysExtended);
                    System.out.println("   Loan Fee: $" + loanCost);
                    totalLoanFees += loanCost; // Accumulate total loan fees
                    count++;
                }

                System.out.println("Total Loan Fees: $" + totalLoanFees + "\n");

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }
    }

    @Override
    public void calculateTotalLoanCost(int userID) {

    }

    @Override
    public Book findBook(int bookID) {
        
        return null;
    }

    @Override
    public User findUser(int user_id) {
        
        return null;
    }
}
