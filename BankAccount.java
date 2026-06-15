import java.util.ArrayList;
import java.util.List;

public class BankAccount {

    // Default starting balance if none is provided
    public static final int DEFAULT = 10000;

    // Private fields so they can only be accessed through getters and nothing outside the clas can change it
    private int accNo;
    private String firstName;
    private String lastName;
    private int balance;
    private List<Transaction> transactions; // all transactions this account was involved in

    // Sets up the account with the given details and an empty transaction list Constructer
    public BankAccount(int accNo, String first, String last, int balance) {
        this.accNo = accNo;
        this.firstName = first;
        this.lastName = last;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    // Getters for each private field
    public int getAccNo() {
        return accNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getBalance() {
        return balance;
    }

    // Returns a formatted string of the account details for printing
    public String details() {
        return accNo + " - " + firstName + " " + lastName + " - $" + balance;
    }

    // Returns a copy of all transactions, or null if there are none
    public List<Transaction> history() {
        if (transactions.isEmpty()) {
            return null;
        }
        return new ArrayList<>(transactions);
    }

    // Deducts or adds the amount depending on if this account is the sender or receiver. sender deducts, receiver adds
    public boolean processTransaction(Transaction transaction) {
        if (transaction.getSender() == this) {
            // check sender has enough money
            if (balance - transaction.getAmount() < 0) {
                return false;
            }
            balance -= transaction.getAmount();
        } else {
            balance += transaction.getAmount();
        }
        transactions.add(transaction);
        return true;
    }

    // Adds a transaction without changing the balance, used during recovery
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    // Returns only transactions where this account was the sender
    public List<Transaction> outgoing() {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getSender() == this) {
                result.add(t);
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    // Returns only transactions where this account was the receiver
    public List<Transaction> incoming() {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getReceiver() == this) {
                result.add(t);
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    // Updates the account's first and last name
    public void rename(String first, String last) {
        this.firstName = first;
        this.lastName = last;
    }

    // Returns the highest balance across all accounts, or -1 if list is empty
    public static int findMax(List<BankAccount> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return -1;
        }
        int max = accounts.get(0).getBalance();
        for (BankAccount a : accounts) {
            if (a.getBalance() > max) {
                max = a.getBalance();
            }
        }
        return max;
    }

    // Returns the lowest balance across all accounts, or -1 if list is empty
    public static int findMin(List<BankAccount> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return -1;
        }
        int min = accounts.get(0).getBalance();
        for (BankAccount a : accounts) {
            if (a.getBalance() < min) {
                min = a.getBalance();
            }
        }
        return min;
    }

    // Returns the average balance across all accounts
    public static int mean(List<BankAccount> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return -1;
        }
        int sum = 0;
        for (BankAccount a : accounts) {
            sum += a.getBalance();
        }
        return sum / accounts.size();
    }

    // Returns the total of all balances combined
    public static int totalBalance(List<BankAccount> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return -1;
        }
        int total = 0;
        for (BankAccount a : accounts) {
            total += a.getBalance();
        }
        return total;
    }
}
