import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

class Transaction {
    String type;
    String description;
    double amount;
    long timestamp;
    private static String currency = "IDR";

    public Transaction(String type, String description, double amount) {
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
    }

    public static String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return currency + " " + formatter.format(amount);
    }

    @Override
    public String toString() {
        return String.format("%s | %s: %s | %s",
                type.equals("income") ? "Pemasukan" : "Pengeluaran",
                description,
                formatCurrency(amount),
                new java.util.Date(timestamp));
    }

    public static void setCurrency(String currency) {
        Transaction.currency = currency;
    }
}

public class FinancialManagement {
    private static final String DATA_FILE = "transactions.txt";
    private double currentMoney;
    private ArrayList<Transaction> transactions;
    private String currency;

    public FinancialManagement() {
        this.currentMoney = 0;
        this.transactions = new ArrayList<>();
        this.currency = "IDR";
        loadTransactions();
    }

    public void loadTransactions() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String type = parts[0];
                String description = parts[1];
                double amount = Double.parseDouble(parts[2]);
                Transaction transaction = new Transaction(type, description, amount);
                transactions.add(transaction);
                if (type.equals("income")) {
                    currentMoney += amount;
                } else {
                    currentMoney -= amount;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }

    public void saveTransactions() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Transaction transaction : transactions) {
                writer.write(transaction.type + "," + transaction.description + "," + transaction.amount);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    public String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return currency + " " + formatter.format(amount);
    }

    private void displayHome() {
        Scanner scanner = new Scanner(System.in);
        boolean continueProgram = true; // Mengontrol loop utama

        while (continueProgram) {
            System.out.println("=====================================");
            System.out.println("\nUang Sekarang: " + formatCurrency(currentMoney));
            System.out.println("1. Tambah Pemasukan");
            System.out.println("2. Tambah Pengeluaran");
            System.out.println("3. Lihat Data Pemasukan");
            System.out.println("4. Lihat Data Pengeluaran");
            System.out.println("5. Pengaturan Mata Uang");
            System.out.println("6. Keluar");
            System.out.print("Pilih opsi: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // clear newline

            switch (choice) {
                case 1:
                    // Menambahkan pemasukan
                    addTransaction(scanner, "income");
                    break;

                case 2:
                    // Menambahkan pengeluaran
                    addTransaction(scanner, "expense");
                    break;

                case 3:
                    // Menampilkan data pemasukan
                    System.out.println("Data Pemasukan:");
                    transactions.stream()
                            .filter(t -> t.type.equals("income"))
                            .forEach(System.out::println);
                    break;

                case 4:
                    // Menampilkan data pengeluaran
                    System.out.println("Data Pengeluaran:");
                    transactions.stream()
                            .filter(t -> t.type.equals("expense"))
                            .forEach(System.out::println);
                    break;

                case 5:
                    // Mengatur mata uang
                    configureCurrency(scanner);
                    break;

                case 6:
                    System.out.println("Keluar dari program.");
                    continueProgram = false; // Keluar dari loop
                    break;

                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private void addTransaction(Scanner scanner, String type) {
        String description;
        double amount = 0;
        boolean validInput;

        // Meminta pengguna untuk memasukkan keterangan
        System.out.print("Masukkan keterangan " + (type.equals("income") ? "pemasukan: " : "pengeluaran: "));
        description = scanner.nextLine();

        // Loop untuk memastikan input jumlah valid
        while (true) {
            validInput = false; // Reset validInput setiap kali loop
            while (!validInput) {
                try {
                    System.out.print("Masukkan jumlah " + (type.equals("income") ? "pemasukan: " : "pengeluaran: "));
                    amount = scanner.nextDouble();
                    scanner.nextLine(); // clear newline
                    validInput = true; // Input valid
                } catch (InputMismatchException e) {
                    System.out.println("Input Invalid, masukkan angka dengan benar.");
                    scanner.nextLine(); // clear invalid input
                }
            }

            // Mengupdate jumlah uang dan transaksi
            if (type.equals("income")) {
                currentMoney += amount;
                transactions.add(new Transaction("income", description, amount));
            } else {
                currentMoney -= amount;
                transactions.add(new Transaction("expense", description, amount));
            }

            saveTransactions();
            System.out.println("Sisa Uang Sekarang: " + formatCurrency(currentMoney));

            // Langsung kembali ke menu utama tanpa menanyakan input lagi
            break;
        }
    }

    private void configureCurrency(Scanner scanner) {
        System.out.println("Pilih mata uang:");
        System.out.println("1. IDR");
        System.out.println("2. USD");
        System.out.println("3. EUR");
        System.out.print("Pilih opsi: ");
        int currencyChoice = scanner.nextInt();

        switch (currencyChoice) {
            case 1:
                currency = "IDR"; // Indonesian Rupiah
                Transaction.setCurrency(currency);
                break;
            case 2:
                currency = "USD"; // US Dollar
                Transaction.setCurrency(currency);
                break;
            case 3:
                currency = "EUR"; // Euro
                Transaction.setCurrency(currency);
                break;
            default:
                System.out.println("Pilihan tidak valid. Menggunakan IDR sebagai default.");
                currency = "IDR"; // Default currency
                Transaction.setCurrency(currency);
                break;
        }
        System.out.println("Mata uang saat ini: " + currency);
    }

    public static void main(String[] args) {
        FinancialManagement app = new FinancialManagement();
        app.displayHome();
    }
}
