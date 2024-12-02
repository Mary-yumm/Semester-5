package votix.models;

public class Administrator {
    // Attributes
    private int adminID; // default value should be set when creating an instance
    private long cnic;
    private String name;
    private long phone;
    private String email;
    private String username;
    private String password;

    // Constructor
    public Administrator(int adminID, long cnic, String name, long phone, String email, String username, String password) {
        this.adminID = adminID;
        this.cnic = cnic;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // Methods

    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public long getCnic() {
        return cnic;
    }

    public void setCnic(long cnic) {
        this.cnic = cnic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


