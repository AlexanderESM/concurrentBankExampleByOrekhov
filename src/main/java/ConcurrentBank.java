import java.util.HashMap;
import java.util.Map;

class ConcurrentBank {
    private final Map<Integer, BankAccount> accounts = new HashMap<>();

    // Метод для создания нового счета
    public BankAccount createAccount(double initialBalance) {
        // Генерация уникального номера счета. Мы используем количество уже существующих счетов.
        int accountNumber = accounts.size() + 1;

        // Создание нового счета с начальным балансом
        BankAccount account = new BankAccount(accountNumber, initialBalance);

        // Сохраняем счет в коллекцию, используя номер счета как ключ
        accounts.put(accountNumber, account);

        // Возвращаем созданный объект счета
        return account;
    }

    // Метод для перевода средств между двумя счетами
    public void transfer(BankAccount fromAccount, BankAccount toAccount, double amount) {
        // Чтобы избежать deadlock, всегда блокируем сначала счет с меньшим номером
        // Это предотвращает ситуации, когда два потока одновременно блокируют два счета в разных порядках
        BankAccount firstLock = fromAccount.getAccountNumber() < toAccount.getAccountNumber() ? fromAccount : toAccount;
        BankAccount secondLock = fromAccount.getAccountNumber() < toAccount.getAccountNumber() ? toAccount : fromAccount;

        // Блокировка первого счета
        firstLock.lock.lock();
        // Блокировка второго счета
        secondLock.lock.lock();

        try {
            // Проверка, достаточно ли средств на исходном счете
            if (fromAccount.getBalance() >= amount) {
                // Снятие средств с исходного счета
                fromAccount.withdraw(amount);
                // Пополнение второго счета
                toAccount.deposit(amount);
            } else {
                // Если средств недостаточно, выводим сообщение
                System.out.println("Недостаточно средств на счете " + fromAccount.getAccountNumber());
            }
        } finally {
            // Освобождаем блокировки после завершения работы
            firstLock.lock.unlock();
            secondLock.lock.unlock();
        }
    }

    // Метод для вывода балансов всех счетов
    public void printAccountBalances() {
        // Проходим по всем счетам и выводим их номера и балансы
        for (BankAccount account : accounts.values()) {
            System.out.println("Счет №" + account.getAccountNumber() + " - Остаток: " + account.getBalance());
        }
    }

    // Метод для вычисления общего баланса всех счетов в банке
    public double getTotalBalance() {
        double totalBalance = 0;
        // Проходим по всем счетам и суммируем их балансы
        for (BankAccount account : accounts.values()) {
            totalBalance += account.getBalance();
        }
        // Возвращаем итоговую сумму балансов
        return totalBalance;
    }
}
