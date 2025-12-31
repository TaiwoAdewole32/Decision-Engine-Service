public class CaseData{
    
    @NotNull
    String applicantID;

    @NotNull
    @Min(18)
    Integer age;

    @NotNull
    @Min(0)
    Integer income;

    @NotNull
    @Min(300)
    @Max(850)
    Integer creditScore;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    Double debtToIncome;

    @NotNull
    Boolean hasLatePayments;

    @NotNull
    @Min(1)
    Integer requestedAmount;

    public CaseData(String applicantID, Integer age, Integer income, Integer creditScore, Double debtToIncome, Boolean hasLatePayments, Integer requestedAmount) {
        this.applicantID = applicantID;
        this.age = age;
        this.income = income;
        this.creditScore = creditScore;
        this.debtToIncome = debtToIncome;
        this.hasLatePayments = hasLatePayments;
        this.requestedAmount = requestedAmount;
    }
}