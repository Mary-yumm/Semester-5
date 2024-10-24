package LibraryManagement;

import java.util.ArrayList;
import java.util.List;

public abstract class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String user_type;
    private double fine;

    private List<Book> Loaned_books;

    User(int id, String name, String email, String phone, String address, String user_type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.Loaned_books = new ArrayList<>();
        this.phone = phone;
        this.address = address;
        this.user_type = user_type;
        this.fine=0.0;
    }
    public abstract int getMaxBooks();

    public String toCSVString() {
        return String.format("%d,%s,%s,%s,%s,%s,%f",
                id, name, email, phone, address,user_type,fine);
    }

    //getters and setters
    public void setUserID(int id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPhone_number(String phone){
        this.phone = phone;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setLoaned_books(List<Book> books) {
        this.Loaned_books = books;
    }
    public void setType(String user_type){
        this.user_type = user_type;
    }
    public void addFine(double fine){
        this.fine+=fine;
    }

    public double getFine(){
        return this.fine;
    }
    public int getUserId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public List<Book> getLoanedBooks() {
        return Loaned_books;
    }

    public String getType() {
        return user_type;
    }


    public void print_LoanedBooks() {
        int count = 1;
        double total_fee=0.0;
        System.out.println("\nLoaned Books:\n");
        for(Book book : Loaned_books) {
            //book.print_info();
            System.out.println(count+") "+book.getBookId() + ") " + book.getTitle() +" , Loan: " + book.loan.get_fee());
            total_fee+=book.loan.get_fee();
            count++;
        }
        System.out.println("\nTotal Loan Fees: "+total_fee);

    }

   public void add_loaned_book(Book book) {
        this.Loaned_books.add(book);

   }
   public void remove_loaned_book(Book book) {
        this.Loaned_books.remove(book);
   }

    public void print_info(){
        System.out.println("User ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Phone number: " + phone);
        System.out.println("Address: " + address);
        System.out.println("UserType: " + user_type);

        print_LoanedBooks();

        System.out.println("Total Fine: "+ fine);
        System.out.println("---------------------------------------------------------"+"\n");
    }

    
}
