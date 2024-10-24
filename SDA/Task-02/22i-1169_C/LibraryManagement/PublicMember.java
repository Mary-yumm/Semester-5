package LibraryManagement;


import java.util.ArrayList;
import java.util.List;

public class PublicMember extends User {
    private final int borrow_limit;
    {
        borrow_limit = 3;
    }
    PublicMember(int userID, String name, String email, String phone_number, String address) {
        super(userID, name, email, phone_number, address,"publicmember");
    }

    @Override
    public int getMaxBooks() {
        return borrow_limit;
    }
}
