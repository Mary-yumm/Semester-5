package LibraryManagement;
import java.time.LocalDate;

public class Loan {
    private LocalDate loanDate;
    private LocalDate returnDate;
    private double loan_fee;
    private boolean isExtended;

    Loan() {
        this.isExtended=false;
        this.loan_fee=0;
    }

    public String toCSVString() {
        return String.format("%s,%s,%f,%d",
                loanDate, returnDate, loan_fee, isExtended);
    }
    public void Set_Values(LocalDate loanDate,LocalDate returnDate,double loan_fee,boolean isExtended){
        this.loanDate=loanDate;
        this.returnDate=returnDate;
        this.loan_fee = loan_fee;
        this.isExtended = isExtended;
    }

    public void set_loanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public void set_returnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void set_isExtended(boolean isExtended) {
        this.isExtended = isExtended;
    }
    public void set_fee(double fee) {
        this.loan_fee = fee;
    }
    public LocalDate get_loanDate() {
        return loanDate;
    }
    public LocalDate get_returnDate() {
        return returnDate;
    }
    public boolean get_isExtended() {
        return isExtended;
    }
    public double get_fee() {
        return loan_fee;
    }
}
