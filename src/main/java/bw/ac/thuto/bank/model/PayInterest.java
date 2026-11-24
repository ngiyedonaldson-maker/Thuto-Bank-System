package bw.ac.thuto.bank.model;

public class PayInterest {
    private String paymentId;
    private String paymentDate;
    private double totalAmount;
    private String status;

    public PayInterest() {
        this.paymentId = "PI-" + System.currentTimeMillis();
        this.paymentDate = java.time.LocalDate.now().toString();
        this.status = "Pending";
    }

    public void initiate() { this.status = "Calculating"; }
    public void calculateSuccess() { this.status = "Calculated"; }
    public void validateEligibility() { this.status = "Validated"; }
    public void postInterest() { this.status = "Posted"; }
    public void confirmPosting() { this.status = "Completed"; }
    public void calculationError() { this.status = "Failed"; }
    public void retry() { this.status = "Calculating"; }
    public void cancel() { this.status = "Cancelled"; }

    public String getStatus() { return status; }
}
