package LibraryManagement;

public class Novel extends Book implements Loanable{
//    private final boolean extendable;
//    {
//        this.extendable = false;
//    }

    private static final double FLAT_RATE = 100.0;
    Novel(int id, String title, String author, String ISBN, int year, String genre) {
        super(id, title, author, ISBN, year, genre, false,FLAT_RATE,false,true,"novel");
    }

    @Override
    public void loan_book(User user) {

    }

    @Override
    public void returnBook(User user) {

    }

    @Override
    public double calculateLoanFee(int days) {
        return FLAT_RATE;
    }

    @Override
    public double getBaseFee() {
        return FLAT_RATE;
    }
}
