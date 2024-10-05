package LibraryManagement;

public class Textbook extends Book implements Loanable{

    private final double cost;
    {
        this.cost = 2.0;
    }
    private static final double BASE_FEE = 10.0;

    Textbook(String id, String title, String author, String ISBN, int year, String genre, boolean loan_status,double base_loan_fee) {
        super(id, title, author, ISBN, year, genre, loan_status,base_loan_fee,true,true);

    }

    @Override
    public void loan_book(User user) {
        if (user.getLoanedBooks().size() < user.getMaxBooks()) {
            user.getLoanedBooks().add(this);
            setLoaned(true);
            System.out.println(getTitle() + " loaned to " + user.getName());
        } else {
            System.out.println("Loan limit reached for user: " + user.getName());
        }

    }

    @Override
    public void returnBook(User user) {
        if (!user.getLoanedBooks().isEmpty()) {
            user.getLoanedBooks().remove(this);
            setLoaned(false);
            System.out.println(getTitle() + " loaned to " + user.getName());

        }
        else{
            System.out.println("Loan limit reached for user: " + user.getName());
        }

    }

    @Override
    public double calculateLoanFee(int days) {
        //System.out.println("in calc loan textbook ftn" + BASE_FEE + (days * cost));
        return BASE_FEE + (days * cost);
    }

    @Override
    public double getBaseFee() {
        return BASE_FEE;
    }
}
