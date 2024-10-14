package LibraryManagement;

public class Novel extends Book implements Loanable{
//    private final boolean extendable;
//    {
//        this.extendable = false;
//    }

    private final double FLAT_RATE;
    {
        this.FLAT_RATE=5.0;
    }
    Novel(int id, String title, String author, String ISBN, int year, String genre, boolean loan_status,double base_loan_fee,String book_type) {
        super(id, title, author, ISBN, year, genre, loan_status,base_loan_fee,false,true,book_type);
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
