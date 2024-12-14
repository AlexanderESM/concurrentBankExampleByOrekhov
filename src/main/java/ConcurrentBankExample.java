public class ConcurrentBankExample {
    public static void main(String[] args) {
        ConcurrentBank bank = new ConcurrentBank();

        // Создание счетов
        BankAccount account1 = bank.createAccount(1500);
        BankAccount account2 = bank.createAccount(4500);

        // Перевод между счетами
        Thread transferThread1 = new Thread(() -> bank.transfer(account1, account2, 150));
        Thread transferThread2 = new Thread(() -> bank.transfer(account2, account1, 450));

        transferThread1.start();
        transferThread2.start();

        try {
            transferThread1.join();
            transferThread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Вывод баланса каждого счета
        bank.printAccountBalances();

        // Вывод общего баланса
        System.out.println("Total balance: " + bank.getTotalBalance());
    }
}
