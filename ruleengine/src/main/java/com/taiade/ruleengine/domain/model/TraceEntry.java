public class TraceEntry{

    private String ruleId;
    private boolean matched;
    private String details;

     public TraceEntry(String ruleId, boolean matched, String details) {
        this.ruleId = ruleId;
        this.matched = matched;
        this.details = details;
    }

    // Getters
    public String getRuleId() {
        return ruleId;
    }

    public boolean isMatched() {
        return matched;
    }

    public String getDetails() {
        return details;
    }
}