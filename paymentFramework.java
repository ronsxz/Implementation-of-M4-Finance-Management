package src.payment;


// ABSTRACT CLASS (cannot be instantiated directly)
public abstract class PaymentFramework {


    protected double baseAmount;
    protected double vatRate = 0.12;


    protected String customerType; // STUDENT, SENIOR, REGULAR
    protected String paymentMethod;
    protected double creditBalance;


    protected boolean paymentSuccess = false;


    public PaymentFramework(double baseAmount, String customerType,
                            String paymentMethod, double creditBalance) {
        this.baseAmount = baseAmount;
        this.customerType = customerType;
        this.paymentMethod = paymentMethod;
        this.creditBalance = creditBalance;
    }


    // ================= VALIDATION =================
    public abstract boolean validatePayment();


    protected boolean hasValidPaymentMethod() {
        return paymentMethod != null &&
                (paymentMethod.equalsIgnoreCase("CASH")
                        || paymentMethod.equalsIgnoreCase("CREDIT"));
    }


    protected boolean hasEnoughCredit(double total) {
        return creditBalance >= total;
    }


    // ================= VAT =================
    public double computeVAT(double amount) {
        return amount * vatRate;
    }


    // ================= DISCOUNT =================
    public double getDiscountRate() {
        if (customerType.equalsIgnoreCase("STUDENT")) return 0.10;
        if (customerType.equalsIgnoreCase("SENIOR")) return 0.20;
        return 0.0;
    }


    // ================= TOTAL =================
    public double computeTotal() {
        double vat = computeVAT(baseAmount);
        double withVAT = baseAmount + vat;
        double discount = withVAT * getDiscountRate();
        return withVAT - discount;
    }


    // ================= INVOICE =================
    // REQUIRED: concrete method (can be overridden)
    public void processInvoice() {


        double vat = computeVAT(baseAmount);
        double withVAT = baseAmount + vat;
        double discount = withVAT * getDiscountRate();
        double total = withVAT - discount;


        System.out.println("\n========== INVOICE ==========");
        System.out.println("Customer Type : " + customerType);
        System.out.println("Base Price    : ₱" + baseAmount);
        System.out.println("VAT (12%)     : ₱" + vat);
        System.out.println("Discount      : ₱" + discount);
        System.out.println("-----------------------------");
        System.out.println("TOTAL         : ₱" + total);
        System.out.println("=============================");
    }


    // ================= FINALIZE =================
    public abstract void finalizeTransaction(double total);


    // ================= MAIN PROCESS =================
    public boolean process() {


        System.out.println("\n[START PAYMENT PROCESS]");


        double total = computeTotal();


        if (!validatePayment() || !hasEnoughCredit(total)) {
            System.out.println("❌ Payment failed (invalid or insufficient balance).");
            return false;
        }


        finalizeTransaction(total);
        processInvoice();


        paymentSuccess = true;


        System.out.println("✔ Payment successful.");
        return true;
    }
}