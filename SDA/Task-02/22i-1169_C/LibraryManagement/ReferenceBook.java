package LibraryManagement;

public class ReferenceBook extends Book{

    ReferenceBook(int id, String title, String author, String ISBN, int year, String genre) {
        super(id, title, author, ISBN, year, genre, false,0.0,false,false,"referencebook");
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
