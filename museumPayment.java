package src.payment;


public class MuseumPayment extends PaymentFramework {


    public MuseumPayment(double baseAmount, String customerType,
                         String paymentMethod, double creditBalance) {
        super(baseAmount, customerType, paymentMethod, creditBalance);
    }


    // VALIDATION
    @Override
    public boolean validatePayment() {
        return hasValidPaymentMethod();
    }


    // FINALIZE TRANSACTION
    @Override
    public void finalizeTransaction(double total) {


        creditBalance -= total;


        System.out.println("\n=================================");
        System.out.println("      MUSEUM PAYMENT SUCCESS");
        System.out.println("=================================");
        System.out.println("Amount Paid : ₱" + total);
        System.out.println("Remaining Balance : ₱" + creditBalance);
        System.out.println("Status : PAID");
        System.out.println("=================================");
    }


    // OPTIONAL OVERRIDE
    @Override
    public void processInvoice() {
        System.out.println("\n--- MUSEUM RECEIPT ---");
        super.processInvoice();
    }
}

