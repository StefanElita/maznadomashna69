import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SlotMachine {

    private static final String[] SYMBOLS = { "🍒", "🍋", "🔔", "💎", "7️⃣", "⭐" };
    private static final Random RANDOM = new Random();

    // =========================
    //   Г Р А Ф И Ч Е Н  М О Д
    // =========================
    public static void startGUI() {
        JFrame frame = new JFrame("Java Slot Machine");
        frame.setSize(480, 420);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Баланс
        JPanel topPanel = new JPanel();
        JLabel balanceLabel = new JLabel("Баланс: 100 монети");
        topPanel.add(balanceLabel);
        frame.add(topPanel, BorderLayout.NORTH);

        // Барабани
        JPanel reelsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        Font reelFont = new Font("Segoe UI Emoji", Font.PLAIN, 48);

        JLabel reel1 = new JLabel("❔", SwingConstants.CENTER);
        JLabel reel2 = new JLabel("❔", SwingConstants.CENTER);
        JLabel reel3 = new JLabel("❔", SwingConstants.CENTER);

        reel1.setFont(reelFont);
        reel2.setFont(reelFont);
        reel3.setFont(reelFont);

        reelsPanel.add(reel1);
        reelsPanel.add(reel2);
        reelsPanel.add(reel3);

        frame.add(reelsPanel, BorderLayout.CENTER);

        // Панел за залози
        JPanel bottomPanel = new JPanel();
        JTextField betField = new JTextField(5);
        JButton spinBtn = new JButton("SPIN");
        JButton quitBtn = new JButton("Quit");

        bottomPanel.add(new JLabel("Залог:"));
        bottomPanel.add(betField);
        bottomPanel.add(spinBtn);
        bottomPanel.add(quitBtn);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Лог панел
        JTextArea logArea = new JTextArea(6, 20);
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea), BorderLayout.EAST);

        // Баланс за GUI
        final int[] balance = {100};

        // SPIN LOGIC
        spinBtn.addActionListener((ActionEvent e) -> {
            int bet;
            try {
                bet = Integer.parseInt(betField.getText());
            } catch (Exception ex) {
                logArea.append("Моля въведи валиден залог.\n");
                return;
            }

            if (bet <= 0) {
                logArea.append("Залогът трябва да е положително число.\n");
                return;
            }
            if (bet > balance[0]) {
                logArea.append("Нямаш достатъчен баланс.\n");
                return;
            }

            // Spin
            String[] result = spinReels(3);

            reel1.setText(result[0]);
            reel2.setText(result[1]);
            reel3.setText(result[2]);

            int payout = calculatePayout(result, bet);

            if (payout > 0) {
                long diamonds = Arrays.stream(result).filter(s -> s.equals("💎")).count();
                if (diamonds == 2) {
                    logArea.append("💎 БОНУС: Безплатно завъртане!\n");
                    bet = 0;
                }
                logArea.append("Печелиш " + payout + " монети!\n");
                balance[0] += payout;
            } else {
                logArea.append("Загуби " + bet + " монети.\n");
                balance[0] -= bet;
            }

            balanceLabel.setText("Баланс: " + balance[0] + " монети");

            if (balance[0] <= 0) {
                JOptionPane.showMessageDialog(frame, "Изгуби всичките си монети. Край!");
                System.exit(0);
            }
        });

        quitBtn.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    // ============================
    //    К О Н З О Л Е Н   М О Д
    // ============================
    public static void main(String[] args) {

        // Ако искаш GUI → махни // отдолу
        startGUI();
        // return;  // ако искаш само GUI, премахни този ред

        // Конзолният режим остава непокътнат
        Scanner sc = new Scanner(System.in);
        int balance = 100;
        System.out.println("=== Добре дошъл в Java Slot Machine ===");
        System.out.println("Започваш с баланс: " + balance + " монети.");

        while (true) {
            System.out.println("\nБаланс: " + balance + " | Въведи залог:");
            String input = sc.nextLine().trim().toLowerCase();

            if (input.equals("quit")) break;

            int bet;
            try {
                bet = Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("Моля въведи валиден залог.");
                continue;
            }

            if (bet <= 0 || bet > balance) {
                System.out.println("Невалиден залог.");
                continue;
            }

            String[] result = spinReels(3);
            System.out.println("Резултат: " + Arrays.toString(result));

            int payout = calculatePayout(result, bet);

            if (payout > 0) {
                balance += payout;
                System.out.println("Печелиш " + payout + " монети!");
            } else {
                balance -= bet;
                System.out.println("Загуби " + bet + " монети.");
            }

            if (balance <= 0) {
                System.out.println("Край! Нямаш монети.");
                break;
            }
        }
    }

    // ================================
    //       О Р И Г И Н А Л Н О
    // ================================
    private static String[] spinReels(int n) {
        String[] out = new String[n];
        for (int i = 0; i < n; i++) {
            out[i] = SYMBOLS[RANDOM.nextInt(SYMBOLS.length)];
        }
        return out;
    }

    private static int calculatePayout(String[] reels, int bet) {
        boolean allSame = reels[0].equals(reels[1]) && reels[1].equals(reels[2]);
        boolean twoSame = reels[0].equals(reels[1]) ||
                reels[0].equals(reels[2]) ||
                reels[1].equals(reels[2]);

        if (allSame) {
            if (reels[0].equals("7️⃣")) return bet * 50;
            if (reels[0].equals("💎")) return bet * 20;
            return bet * 10;
        } else if (twoSame) {
            return bet * 2;
        }
        return 0;
    }
}
