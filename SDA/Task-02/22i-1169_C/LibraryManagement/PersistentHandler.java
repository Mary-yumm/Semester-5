package LibraryManagement;

public abstract class PersistentHandler {
    public abstract void addUser(User user);
    public abstract void addBook(Book book);
    public abstract void loanBook(int bookID, int userID, int days);
    public abstract void returnBook(int bookID, int userID);
    public abstract void removeBook(int bookID);
    public abstract void displayLoanStatus(int userID);
    public abstract void displayAvailableBooks();
    public abstract void printUserInfo(int userID);
    public abstract void displayUsers();
    public abstract void extend_loan(int bookID, int userID, int days);
    public abstract void displayLoanDetails(int userID);
    public abstract void calculateTotalLoanCost(int userID);
    public abstract void removeUser(int user);
    public abstract Book findBook(int bookID);
    public abstract User findUser(int user_id);
    public abstract void searchBook(int bookid);
    public abstract void searchBook(String title);
    public abstract void searchBookbyAuthor(String Author);
    public abstract void searchBookbyISBN(String isbn);
    public abstract void searchUser(int userid);
    public abstract void searchUser(String username);
    public abstract void sortBooksAlphabetically();
    public abstract void sortUsersAlphabetically();
}
