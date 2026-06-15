import java.util.*;
import java.io.*;

public class Banker {

    // Lists to store all accounts and transactions in the system
    private ArrayList<BankAccount> accounts;
    private ArrayList<Transaction> transactions;
    private int nextAccNo = 100000; // account numbers start at 100000 and increment

    // All available commands displayed to the user
    public static final String helpString =
        "EXIT exit from application\n" +
        "COMMANDS display the command list\n\n" +
        "LIST ACCOUNTS displays all accounts in system\n" +
        "LIST TRANSACTIONS displays all transactions in system\n\n" +
        "DETAILS <accno> displays all details about bank account\n" +
        "BALANCE <accno> displays the current balance of bank account\n\n" +
        "HISTORY <accno> displays all transactions involving an account\n" +
        "OUTGOING <accno> displays all transactions paid by account\n" +
        "INCOMING <accno> displays all transactions received by account\n\n" +
        "CREATE <first> <last> [<balance>] creates a bank account\n" +
        "RENAME <accno> <first> <last> renames a bank account\n\n" +
        "PAY <sender> <receiver> <amount> transfers money between account\n" +
        "TRANSACTION <id> displays the transaction details\n" +
        "CANCEL <id> makes a copy of the transaction with receiver/sender swapped\n\n" +
        "ARCHIVE <ledgerFile> <accountFile> stores the transaction history as a ledger\n" +
        "RECOVER <ledgerFile> <accountFile> restores a ledger\n\n" +
        "MERGE <accno \u2026> transfers all funds from listed accounts into the first account\n\n" +
        "MAX displays the highest balance from all accounts\n" +
        "MIN displays the lowest balance from all accounts\n" +
        "MEAN displays the average balance\n" +
        "TOTAL displays the amount of money stored by bank";

    // Initialises both lists as empty
    public Banker() {
        accounts = new ArrayList<>();
        transactions = new ArrayList<>();
    }

    // Searches the accounts list by account number, returns null if not found. priavte helper method
    private BankAccount findAccount(int accNo) {
        for (BankAccount acc : accounts) {
            if (acc.getAccNo() == accNo) {
                return acc;
            }
        }
        return null;
    }

    // Prints the full command list
    public static void commands() {
        System.out.println(Banker.helpString);
    }

    public static void exit() {
        System.out.println("bye");
    }

    // Sorts accounts by account number then prints each one
    public void listAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("no accounts");
            return;
        }
        // Bubble sort by account number
        for (int i = 0; i < accounts.size() - 1; i++) {
            for (int j = i + 1; j < accounts.size(); j++) {
                if (accounts.get(i).getAccNo() > accounts.get(j).getAccNo()) {
                    BankAccount temp = accounts.get(i);
                    accounts.set(i, accounts.get(j));
                    accounts.set(j, temp);
                }
            }
        }
        for (BankAccount acc : accounts) {
            System.out.println(acc.getAccNo());
        }
    }

    // Prints all transactions or a message if there are none
    public void listTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("no transactions");
            return;
        }
        for (Transaction t : transactions) {
            System.out.println(t.get());
        }
    }

    // Prints full details of an account
    public void details(int accNo) {
        BankAccount acc = findAccount(accNo);
        if (acc == null) {
            System.out.println("no such account");
            return;
        }
        System.out.println(acc.details());
    }

    // Prints just the balance of an account
    public void balance(int accNo) {
        BankAccount acc = findAccount(accNo);
        if (acc == null) {
            System.out.println("no such account");
            return;
        }
        System.out.println("$" + acc.getBalance());
    }

    // Prints all transactions an account was involved in
    public void history(int accNo) {
        BankAccount acc = findAccount(accNo);
        if (acc == null) {
            System.out.println("no such account");
            return;
        }
        List<Transaction> hist = acc.history();
        if (hist == null) {
            System.out.println("no transactions");
            return;
        }
        for (Transaction t : hist) {
            System.out.println(t.get());
        }
    }

    // Prints transactions where this account was the sender
    public void outgoing(int accNo) {
        BankAccount acc = findAccount(accNo);
        if (acc == null) {
            System.out.println("no such account");
            return;
        }
        List<Transaction> out = acc.outgoing();
        if (out == null) {
            System.out.println("no transactions");
            return;
        }
        for (Transaction t : out) {
            System.out.println(t.get());
        }
    }

    // Prints transactions where this account was the receiver
    public void incoming(int accNo) {
        BankAccount acc = findAccount(accNo);
        if (acc == null) {
            System.out.println("no such account");
            return;
        }
        List<Transaction> in = acc.incoming();
        if (in == null) {
            System.out.println("no transactions");
            return;
        }
        for (Transaction t : in) {
            System.out.println(t.get());
        }
    }

    // Creates a new account with the next available account number
    public void createAccount(String first, String last, int balance) {
        BankAccount acc = new BankAccount(nextAccNo, first, last, balance);
        nextAccNo++; // increment so next account gets a unique number
        accounts.add(acc);
        System.out.println("success");
    }

    // Finds the account and updates its name
    public void rename(int accNo, String first, String last) {
        BankAccount acc = findAccount(accNo);
        if (acc == null) {
            System.out.println("no such account");
            return;
        }
        acc.rename(first, last);
        System.out.println("success");
    }

    // Returns the hash of the last transaction, or "null" if there are none
    private String getPrevHash() {
        if (transactions.isEmpty()) {
            return "null";
        }
        return transactions.get(transactions.size() - 1).getHash();
    }

    // Validates inputs then creates a transaction between two accounts
    public void pay(int senderNo, int receiverNo, int amount) {
        if (amount <= 0) {
            System.out.println("amount must be positive");
            return;
        }
        if (senderNo == receiverNo) {
            System.out.println("sender cannot be receiver");
            return;
        }
        BankAccount sender = findAccount(senderNo);
        if (sender == null) {
            System.out.println("no such account");
            return;
        }
        BankAccount receiver = findAccount(receiverNo);
        if (receiver == null) {
            System.out.println("no such account");
            return;
        }
        if (sender.getBalance() < amount) {
            System.out.println("insufficient funds");
            return;
        }
        int id = transactions.size() + 1;
        Transaction t = new Transaction(id, sender, receiver, amount, getPrevHash());
        sender.processTransaction(t);
        receiver.processTransaction(t);
        transactions.add(t);
        System.out.println("success");
    }

    // Prints a single transaction by its id
    public void transaction(int id) {
        if (id < 1 || id > transactions.size()) {
            System.out.println("no such transaction");
            return;
        }
        System.out.println(transactions.get(id - 1).get());
    }

    // Reverses a transaction by swapping sender and receiver
    public void cancel(int id) {
        if (id < 1 || id > transactions.size()) {
            System.out.println("no such transaction");
            return;
        }
        Transaction orig = transactions.get(id - 1);
        BankAccount sender = orig.getReceiver(); // swap sender and receiver
        BankAccount receiver = orig.getSender();
        int amount = orig.getAmount();

        if (sender.getBalance() < amount) {
            System.out.println("insufficient funds");
            return;
        }
        int newId = transactions.size() + 1;
        Transaction t = new Transaction(newId, sender, receiver, amount, getPrevHash());
        sender.processTransaction(t);
        receiver.processTransaction(t);
        transactions.add(t);
        System.out.println("success");
    }

    // Writes all transactions and accounts to separate files
    public void archive(String ledgerFile, String accFile) {
        try {
            // Write each transaction to the ledger file
            FileWriter lw = new FileWriter(ledgerFile);
            for (Transaction t : transactions) {
                lw.write(t.getId() + ", " + t.getReceiver().getAccNo() + ", " + t.getSender().getAccNo() + ", " + t.getAmount() + ", " + t.getHash() + "\n");
            }
            lw.close();

            // Write each account to the accounts file
            FileWriter aw = new FileWriter(accFile);
            for (BankAccount acc : accounts) {
                aw.write(acc.getAccNo() + ", " + acc.getFirstName() + ", " + acc.getLastName() + ", " + acc.getBalance() + "\n");
            }
            aw.close();

            System.out.println("success");
        } catch (IOException e) {
            System.out.println("error");
        }
    }

    // Reads accounts and transactions back from files and restores the system state
    public void recover(String ledgerFile, String accFile) {
        File lf = new File(ledgerFile);
        File af = new File(accFile);
        if (!lf.exists() || !af.exists()) {
            System.out.println("no such file");
            return;
        }

        try {
            // Read accounts file and rebuild BankAccount objects
            ArrayList<BankAccount> newAccounts = new ArrayList<>();
            int maxAccNo = 99999;
            Scanner accScanner = new Scanner(af);
            while (accScanner.hasNextLine()) {
                String line = accScanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",\\s*");
                int accNo = Integer.parseInt(parts[0].trim());
                String first = parts[1].trim();
                String last = parts[2].trim();
                int bal = Integer.parseInt(parts[3].trim());
                newAccounts.add(new BankAccount(accNo, first, last, bal));
                if (accNo > maxAccNo) maxAccNo = accNo;
            }
            accScanner.close();

            // Read ledger file and verify each transaction's hash
            ArrayList<Transaction> newTransactions = new ArrayList<>();
            String prevHash = "null";
            Scanner ledgerScanner = new Scanner(lf);
            while (ledgerScanner.hasNextLine()) {
                String line = ledgerScanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",\\s*");
                int tid = Integer.parseInt(parts[0].trim());
                int receiverNo = Integer.parseInt(parts[1].trim());
                int senderNo = Integer.parseInt(parts[2].trim());
                int amount = Integer.parseInt(parts[3].trim());
                String hash = parts[4].trim();

                // If hash doesn't match the ledger is invalid
                String expected = Transaction.generateHash(tid, senderNo, receiverNo, amount, prevHash);
                if (!expected.equals(hash)) {
                    ledgerScanner.close();
                    System.out.println("invalid ledger");
                    return;
                }

                // Match account numbers to the recovered account objects
                BankAccount sender = null;
                BankAccount receiver = null;
                for (BankAccount acc : newAccounts) {
                    if (acc.getAccNo() == senderNo) sender = acc;
                    if (acc.getAccNo() == receiverNo) receiver = acc;
                }

                if (sender == null || receiver == null) {
                    ledgerScanner.close();
                    System.out.println("invalid ledger");
                    return;
                }

                Transaction t = new Transaction(tid, sender, receiver, amount, prevHash);
                sender.addTransaction(t);   // use addTransaction so balance isn't changed again
                receiver.addTransaction(t);
                newTransactions.add(t);
                prevHash = hash;
            }
            ledgerScanner.close();

            // Replace current state with recovered data
            this.accounts = newAccounts;
            this.transactions = newTransactions;
            this.nextAccNo = maxAccNo + 1;
            System.out.println("success");

        } catch (IOException e) {
            System.out.println("no such file");
        } catch (Exception e) {
            System.out.println("invalid ledger");
        }
    }

    // Moves all balances from the other accounts into the destination account
    public void merge(int dest, int[] others) {
        BankAccount destAcc = findAccount(dest);
        if (destAcc == null) {
            System.out.println("no such account");
            return;
        }
        // Check all accounts exist before doing anything
        for (int o : others) {
            if (findAccount(o) == null) {
                System.out.println("no such account");
                return;
            }
        }
        for (int o : others) {
            BankAccount other = findAccount(o);
            if (other.getBalance() > 0) { // skip accounts with nothing to transfer
                int id = transactions.size() + 1;
                Transaction t = new Transaction(id, other, destAcc, other.getBalance(), getPrevHash());
                other.processTransaction(t);
                destAcc.processTransaction(t);
                transactions.add(t);
            }
        }
        System.out.println("success");
    }

    // These four just call the matching static methods from BankAccount
    public void min() {
        System.out.println("$" + BankAccount.findMin(accounts));
    }

    public void max() {
        System.out.println("$" + BankAccount.findMax(accounts));
    }

    public void mean() {
        System.out.println("$" + BankAccount.mean(accounts));
    }

    public void total() {
        System.out.println("$" + BankAccount.totalBalance(accounts));
    }

    public static void main(String[] args) {
        Banker banker = new Banker();
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> tokens = new ArrayList<>();

        // Read all input lines and split into individual tokens
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("$ ")) line = line.substring(2); // strip prompt prefix
            else if (line.equals("$")) line = "";
            String[] parts = line.trim().split("\\s+");
            for (String part : parts) {
                if (!part.isEmpty()) {
                    tokens.add(part);
                }
            }
        }

        // Go through tokens one by one and call the right method for each command
        int i = 0;
        while (i < tokens.size()) {
            String cmd = tokens.get(i).toUpperCase();
            i++;

            if (cmd.equals("EXIT")) {
                Banker.exit();
                return;
            } else if (cmd.equals("COMMANDS")) {
                Banker.commands();
            } else if (cmd.equals("LIST")) {
                String sub = tokens.get(i).toUpperCase();
                i++;
                if (sub.equals("ACCOUNTS")) banker.listAccounts();
                else if (sub.equals("TRANSACTIONS")) banker.listTransactions();
            } else if (cmd.equals("DETAILS")) {
                banker.details(Integer.parseInt(tokens.get(i++)));
            } else if (cmd.equals("BALANCE")) {
                banker.balance(Integer.parseInt(tokens.get(i++)));
            } else if (cmd.equals("HISTORY")) {
                banker.history(Integer.parseInt(tokens.get(i++)));
            } else if (cmd.equals("OUTGOING")) {
                banker.outgoing(Integer.parseInt(tokens.get(i++)));
            } else if (cmd.equals("INCOMING")) {
                banker.incoming(Integer.parseInt(tokens.get(i++)));
            } else if (cmd.equals("CREATE")) {
                String first = tokens.get(i++);
                String last = tokens.get(i++);
                int bal = BankAccount.DEFAULT; // use default if no balance given
                if (i < tokens.size()) {
                    try {
                        bal = Integer.parseInt(tokens.get(i));
                        i++;
                    } catch (NumberFormatException e) {
                        // no balance given, use default
                    }
                }
                banker.createAccount(first, last, bal);
            } else if (cmd.equals("RENAME")) {
                int accNo = Integer.parseInt(tokens.get(i++));
                String first = tokens.get(i++);
                String last = tokens.get(i++);
                banker.rename(accNo, first, last);
            } else if (cmd.equals("PAY")) {
                int sender = Integer.parseInt(tokens.get(i++));
                int receiver = Integer.parseInt(tokens.get(i++));
                int amount = Integer.parseInt(tokens.get(i++));
                banker.pay(sender, receiver, amount);
            } else if (cmd.equals("TRANSACTION")) {
                banker.transaction(Integer.parseInt(tokens.get(i++)));
            } else if (cmd.equals("CANCEL")) {
                banker.cancel(Integer.parseInt(tokens.get(i++)));
            } else if (cmd.equals("ARCHIVE")) {
                String ledger = tokens.get(i++);
                String acc = tokens.get(i++);
                banker.archive(ledger, acc);
            } else if (cmd.equals("RECOVER")) {
                String ledger = tokens.get(i++);
                String acc = tokens.get(i++);
                banker.recover(ledger, acc);
            } else if (cmd.equals("MERGE")) {
                int dest = Integer.parseInt(tokens.get(i++));
                ArrayList<Integer> otherList = new ArrayList<>();
                // Keep reading account numbers until we hit something that isn't one
                while (i < tokens.size()) {
                    try {
                        int val = Integer.parseInt(tokens.get(i));
                        if (val >= 100000) {
                            otherList.add(val);
                            i++;
                        } else {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        break;
                    }
                }
                int[] others = new int[otherList.size()];
                for (int j = 0; j < otherList.size(); j++) {
                    others[j] = otherList.get(j);
                }
                banker.merge(dest, others);
            } else if (cmd.equals("MAX")) {
                banker.max();
            } else if (cmd.equals("MIN")) {
                banker.min();
            } else if (cmd.equals("MEAN")) {
                banker.mean();
            } else if (cmd.equals("TOTAL")) {
                banker.total();
            }
        }
    }
}
