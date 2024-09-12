package LibraryManagement;

import java.util.List;

public class Faculty extends User{
    private final int borrow_limit;
    {
        borrow_limit = 10;
    }
    Faculty(String userID, String name, String email, List<Book> books, double loan_fees, String phone_number, String address) {
        super(userID, name, email, books, loan_fees, phone_number, address);
    }

    @Override
    public int getMaxBooks() {
        return borrow_limit;
    }
}
