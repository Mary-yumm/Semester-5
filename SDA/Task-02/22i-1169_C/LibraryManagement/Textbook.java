package LibraryManagement;

public class Textbook extends Book implements Loanable{

    private final double COST;
    {
        this.COST = 100;
    }
    private static final double BASE_FEE = 150.0;

    Textbook(int id, String title, String author, String ISBN, int year, String genre) {
        super(id, title, author, ISBN, year, genre, false,BASE_FEE,true,true,"textbook");

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
        return getBaseFee() + (days * COST);
    }

    @Override
    public double getBaseFee() {
        return BASE_FEE;
    }
}
