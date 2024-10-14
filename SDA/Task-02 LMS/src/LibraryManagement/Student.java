package LibraryManagement;

import java.util.List;

public class Student extends User {
    private final int borrow_limit;
    {
        borrow_limit = 5;
    }
    Student(int userID, String name, String email, List<Book> books, double loan_fees, String phone_number, String address,String type) {
        super(userID, name, email, books, loan_fees, phone_number, address,type);

    }

    @Override
    public int getMaxBooks() {
        return borrow_limit;
    }
}
