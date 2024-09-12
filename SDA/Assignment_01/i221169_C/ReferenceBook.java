package LibraryManagement;

public class ReferenceBook extends Book{

    ReferenceBook(String id, String title, String author, String ISBN, int year, String genre, boolean loan_status,double base_loan_fee) {
        super(id, title, author, ISBN, year, genre, loan_status,base_loan_fee,false);
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
