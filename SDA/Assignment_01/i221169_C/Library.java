package LibraryManagement;

import java.util.ArrayList;
import java.util.List;

public class Library {
    protected List<Book> books;
    protected List<User> users;
    Library(){
        books = new ArrayList<>();
        users = new ArrayList<>();
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
        Book book = findBook(bookID);
        User user = findUser(UserID);

        if(book != null && !book.isLoaned() && user!=null) {
            book.setLoaned(true);
            user.add_loaned_book(book);
            double fee = book.calculateLoanFee(book.getDays_loan());
            book.setCurrent_loan(fee);
            user.addLoanFee(fee);

            System.out.println(book.getTitle() + " loaned as a user.");
        }
        else{
            System.out.println("Book or User not found, or Book cannot be loaned.");
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
            book.extend_Days_loan(days);
            if(book.get_extension()) {
                double loan_fee = book.calculateLoanFee(days);
                //modify user total loan fee
                user.addLoanFee(loan_fee);
                System.out.println("Book loan has been extended.");
                System.out.println("Loan fee: " + user.getTotalLoanFees());
            }
            else{
                System.out.println("Extension not available\n");
            }
        }
    }
    public void displayLoanDetails(){
        boolean no_loan=true;
        for (User user : users) {
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
