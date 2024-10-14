package LibraryManagement;

public interface Loanable {
    void loan_book(User user);
    void returnBook(User user);
    double calculateLoanFee(int days);
}
