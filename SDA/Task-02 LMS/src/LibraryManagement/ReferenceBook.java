package LibraryManagement;

public class ReferenceBook extends Book{

    ReferenceBook(int id, String title, String author, String ISBN, int year, String genre, boolean loan_status,double base_loan_fee,String book_type) {
        super(id, title, author, ISBN, year, genre, loan_status,base_loan_fee,false,false,book_type);
    }

    @Override
    public double calculateLoanFee(int days) {
        return 0;
    }

    @Override
    public double getBaseFee() {
        return 0;
    }
}
