package LibraryManagement;

import java.util.ArrayList;
import java.util.List;

public abstract class User {
    private int userID;
    private String name;
    private String email;
    private List<Book> Loaned_books;
    private double loan_fees;
    private String phone_number;
    private String address;
    private String type;
    User(int userID, String name, String email,List<Book> books, double loan_fees, String phone_number, String address, String type) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.Loaned_books = new ArrayList<>();
        this.loan_fees = 0.0;
        this.phone_number = phone_number;
        this.address = address;
        this.type = type;
    }
    public abstract int getMaxBooks();

    //getters and setters
    public void setUserID(int userID){
        this.userID = userID;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setLoan_fees(double loan_fees){
        this.loan_fees = loan_fees;
    }
    public void setPhone_number(String phone_number){
        this.phone_number = phone_number;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setLoaned_books(List<Book> books) {
        this.Loaned_books = books;
    }
    public void setType(String type){
        this.type = type;
    }
    public int getUserId() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public String getAddress() {
        return address;
    }

    public List<Book> getLoanedBooks() {
        return Loaned_books;
    }

    public double getTotalLoanFees() {
        return loan_fees;
    }
    public String getType() {
        return type;
    }


    //other functions
    public void addLoanFee(double fee) {
        this.loan_fees += fee;
    }
    public void subtract_loanFee(double fee){
        this.loan_fees -= fee;
    }
    public void print_LoanedBooks() {
        int count = 1;
        System.out.println("\nLoaned Books:\n");
        for(Book book : Loaned_books) {
            //book.print_info();
            System.out.println(count+") "+book.getBookId() + ") " + book.getTitle() +" , Loan: " + book.getCurrent_loan());
            count++;
        }

    }

   public void add_loaned_book(Book book) {
        this.Loaned_books.add(book);

   }
   public void remove_loaned_book(Book book) {
        this.Loaned_books.remove(book);
   }

    public void print_info(){
        System.out.println("User ID: " + userID);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Phone number: " + phone_number);
        System.out.println("Address: " + address);
        System.out.println("Total Loan Fees: " + loan_fees);

        print_LoanedBooks();
    }
}
