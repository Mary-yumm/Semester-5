package LibraryManagement;

public abstract class Book {
    private String id;
    private String title;
    private String author;
    private String ISBN;
    private int year;
    private String genre;
    private boolean is_loaned;
    private boolean extension;
    private double current_loan=0.0;
    private int days_loan=14;
    Book(String id, String title, String author, String ISBN, int year, String genre, boolean loan_status,double base_loan_fee,boolean extension) {
        setBookId(id);
        setTitle(title);
        setAuthor(author);
        setISBN(ISBN);
        setYear(year);
        setGenre(genre);
        setExtension(extension);
    }

    //getters and setters
    public int getDays_loan() {
        return days_loan;
    }
    public void extend_Days_loan(int days_loan) {
        this.days_loan += days_loan;
    }
    public void setBookId(String id) {
        this.id=id;
    }
    public void setTitle(String title) {
        this.title=title;
    }
    public void setAuthor(String author) {
        this.author=author;
    }
    public void setISBN(String ISBN) {
        this.ISBN=ISBN;
    }
    public void setYear(int year) {
        this.year=year;
    }
    public void setGenre(String genre) {
        this.genre=genre;
    }
    public void setExtension(boolean extension) {
        this.extension=extension;
    }
    public void setLoaned(boolean loaned) {
        is_loaned = loaned;
    }
    public void setCurrent_loan(double current_loan) {
        this.current_loan=current_loan;
    }

    public double getCurrent_loan() {
        return current_loan;
    }
    public String getBookId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return ISBN;
    }

    public int getPublicationYear() {
        return year;
    }

    public boolean get_extension() {
        return extension;
    }
    public String getGenre() {
        return genre;
    }

    public boolean isLoaned() {
        return is_loaned;
    }



    //other functions

    public void print_info(){
        System.out.println("Book ID: " + id);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("ISBN: " + ISBN);
        System.out.println("Year: " + year);
        System.out.println("Genre: " + genre);
        System.out.println("Extension: " + extension);
        System.out.println("Loaned? " + is_loaned);

    }

    public abstract double calculateLoanFee(int days);

    public abstract double getBaseFee();
}
