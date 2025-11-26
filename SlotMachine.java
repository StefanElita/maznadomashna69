import java.util.Random;
import java.util.Scanner;

public class SlotMachine {
    // Символи за барабаните (можеш да добавиш повече)
    private static final String[] SYMBOLS = { "🍒", "🍋", "🔔", "💎", "7️⃣", "⭐" };
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int balance = 100; // начална сума
        System.out.println("=== Добре дошъл в Java Slot Machine ===");
        System.out.println("Започваш с баланс: " + balance + " монети.");
        System.out.println("Команди: 'spin' за завъртане, 'quit' за край.");

        while (true) {
            System.out.println("\nБаланс: " + balance + " | Въведи залог (или напиши 'quit'):");
            String input = sc.nextLine().trim().toLowerCase();

            if (input.equals("quit")) {
                System.out.println("Край на играта. Финален баланс: " + balance);
                break;
            }

            int bet;
            try {
                bet = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Моля въведи число за залог или 'quit'.");
                continue;
            }

            if (bet <= 0) {
                System.out.println("Залогът трябва да е положително число.");
                continue;
            }
            if (bet > balance) {
                System.out.println("Нямаш достатъчен баланс за този залог.");
                continue;
            }

            // Правим завъртане (3 барабана)
            String[] result = spinReels(3);
            System.out.println("Резултат: [" + result[0] + "][" + result[1] + "][" + result[2] + "]");

            int payout = calculatePayout(result, bet);
            if (payout > 0) {
                System.out.println("Печалба: " + payout + " монети!");
                balance += payout;
            } else {
                System.out.println("Загуба: -" + bet + " монети.");
                balance -= bet;
            }

            if (balance <= 0) {
                System.out.println("Изгуби всички монети. Край на играта.");
                break;
            }
        }

        sc.close();
    }

    // Завърта n барабана и връща масив със символите
    private static String[] spinReels(int n) {
        String[] out = new String[n];
        for (int i = 0; i < n; i++) {
            int idx = RANDOM.nextInt(SYMBOLS.length);
            out[i] = SYMBOLS[idx];
        }
        return out;
    }

    // Изчислява печалбата: връща положителна сума ако печелиш, иначе 0 (загубата е -bet, обработва се в main)
    private static int calculatePayout(String[] reels, int bet) {
        // Примерна логика:
        // - Три еднакви: джакпот -> 10x залога (ако е "7️⃣" -> 50x)
        // - Две еднакви: 2x залога
        // - Иначе: 0
        if (reels.length < 3) return 0; // безопасност

        boolean allSame = reels[0].equals(reels[1]) && reels[1].equals(reels[2]);
        boolean twoSame = reels[0].equals(reels[1]) || reels[0].equals(reels[2]) || reels[1].equals(reels[2]);

        if (allSame) {
            if (reels[0].equals("7️⃣")) {
                return bet * 50; // специален голям множител за 7
            }
            if (reels[0].equals("💎")) {
                return bet * 20; // диамант — голяма печалба
            }
            return bet * 10; // общ джакпот
        } else if (twoSame) {
            return bet * 2;
        } else {
            return 0;
        }
    }
}
