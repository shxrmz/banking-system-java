import java.util.List;

public class Transaction {

    // Private fields so they can only be accessed through getters
    private int id;
    private BankAccount sender;
    private BankAccount receiver;
    private int amount;
    private String hash; // links this transaction to the previous one

    // Creates a transaction and generates its hash using the previous transaction's hash
    public Transaction(int id, BankAccount sender, BankAccount receiver, int amount, String prevHash) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.hash = generateHash(id, sender.getAccNo(), receiver.getAccNo(), amount, prevHash);
    }

    // Getters for each private field
    public int getId() {
        return id;
    }

    public BankAccount getSender() {
        return sender;
    }

    public BankAccount getReceiver() {
        return receiver;
    }

    public int getAmount() {
        return amount;
    }

    public String getHash() {
        return hash;
    }

    // Returns a formatted string of the transaction for printing
    public String get() {
        return id + ": " + sender.getAccNo() + " -> " + receiver.getAccNo() + " | $" + amount + " | " + hash;
    }

    // Checks every transaction's hash matches what it should be, returns false if anything is off
    public static boolean verify(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) return false;
        String prevHash = "null"; // first transaction has no previous hash
        for (Transaction t : transactions) {
            String expected = generateHash(t.getId(), t.getSender().getAccNo(), t.getReceiver().getAccNo(), t.getAmount(), prevHash);
            if (!expected.equals(t.getHash())) return false;
            prevHash = t.getHash(); // move to next hash in the chain
        }
        return true;
    }

    // Combines all transaction details into one string and returns its hash code
    public static String generateHash(int id, int senderAccNo, int receiverAccNo, int amount, String prevHash) {
        String combined = id + "" + senderAccNo + "" + receiverAccNo + "" + amount + "" + prevHash;
        return combined.hashCode() + "";
    }
}
