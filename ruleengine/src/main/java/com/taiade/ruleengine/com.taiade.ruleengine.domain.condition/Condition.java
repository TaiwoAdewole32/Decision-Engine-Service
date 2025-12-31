public interface Condition {
    boolean evaluate(CaseData caseData);
    String explain (CaseData caseData);
}