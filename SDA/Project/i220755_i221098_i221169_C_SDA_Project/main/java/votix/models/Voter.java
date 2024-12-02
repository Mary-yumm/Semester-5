package votix.models;

public class Voter {
    private String cnic;
    private String name;
    private String gender;
    private boolean status;

    // Constructor, getters, and setters for Voter class
    public Voter(String cnic, String name,String gender, boolean status) {
        this.cnic = cnic;
        this.name = name;
        this.gender = gender;
        this.status = status;
    }

    public String getId() {
        return cnic;
    }

    public void setId(String id) {
        this.cnic = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public boolean getStatus(){
        return status;
    }

}
