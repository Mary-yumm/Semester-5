package LibraryManagement;

import java.util.ArrayList;
import java.util.List;

public class Faculty extends User{
    private final int borrow_limit;
    {
        borrow_limit = 10;
    }
    Faculty(int userID, String name, String email, String phone_number, String address) {
        super(userID, name, email, phone_number, address,"faculty");
    }

    @Override
    public int getMaxBooks() {
        return borrow_limit;
    }
}
