package LibraryManagement;

import java.time.LocalDate;

public abstract class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private int publication_year;
    private String genre;
    private boolean IsLoanable;
    private boolean IsExtendable;
    private String book_type;

    private boolean is_loaned;
    Loan loan;

    Book(int id, String title, String author, String isbn, int publication_year, String genre, boolean loan_status,
            double base_loan_fee, boolean extension, boolean IsLoanable, String book_type) {
        setBookId(id);
        setTitle(title);
        setAuthor(author);
        setisbn(isbn);
        setpublication_year(publication_year);
        setGenre(genre);
        setExtension(extension);
        set_IsLoanable(IsLoanable);
        this.book_type = book_type;
        loan=new Loan();
        loan.Set_Values(LocalDate.now(),LocalDate.now(),0.0,false);
    }

    public String toCSVString() {
        return String.format("%d,%s,%s,%s,%d,%s,%d,%d,%s",
                id, title, author, isbn, publication_year, genre, IsLoanable ? 1 : 0, IsExtendable ? 1 : 0, book_type);
    }


    // getters and setters
    public void set_IsLoanable(boolean is_IsLoanable) {
        IsLoanable = is_IsLoanable;
    }

    public boolean get_IsLoanable() {
        return IsLoanable;
    }

    public void setBookId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setisbn(String isbn) {
        this.isbn = isbn;
    }

    public void setpublication_year(int publication_year) {
        this.publication_year = publication_year;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setExtension(boolean extension) {
        this.IsExtendable = extension;
    }

    public void setLoaned(boolean loaned) {
        is_loaned = loaned;
    }


    public int getpublication_year() {
        return publication_year;
    }

    public int getBookId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPublicationpublication_year() {
        return publication_year;
    }

    public boolean IsExtendable() {
        return IsExtendable;
    }

    public String getGenre() {
        return genre;
    }

    public boolean isLoaned() {
        return is_loaned;
    }

    public String getType() {
        return book_type;
    }

    // other functions

    public void print_info() {
        System.out.println("Book ID: " + id);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("isbn: " + isbn);
        System.out.println("publication_year: " + publication_year);
        System.out.println("Genre: " + genre);
        System.out.println("Loanable? " + IsLoanable);
        if(IsLoanable){
            System.out.println("Loaned? " + is_loaned);
        }
        System.out.println("Extendable? "+ IsExtendable);
        System.out.println("Book Type: "+ book_type);

    }

    public abstract double calculateLoanFee(int days);

    public abstract double getBaseFee();
}
