import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class SlotMachine {

    private static final double WIN_CHANCE = 0.15; // можеш да го променяш
    private static final String[] SYMBOLS = {
        "boris", "grizli", "krum", "presli", "stefan", "stoev"
    };
    private static final double[] MULTIPLIERS = {
        2.5, 5, 1.5, 3, 2, 10
    };
    private static final Random RANDOM = new Random();

    public static void startGUI() {
        JFrame frame = new JFrame("Java Slot Machine");
        frame.setSize(500, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel balanceLabel = new JLabel("Баланс: 100 монети");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        balanceLabel.setForeground(Color.WHITE);
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(30, 30, 30));
        topPanel.add(balanceLabel);
        frame.add(topPanel, BorderLayout.NORTH);

        // Панел за барабани с фон
        JPanel reelsPanel = new JPanel() {
            Image background = new ImageIcon("images/fon.png").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
            }
        };
        reelsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        reelsPanel.setOpaque(false);

        // Барабани
        JLabel reel1 = new JLabel(loadIcon(SYMBOLS[0]));
        JLabel reel2 = new JLabel(loadIcon(SYMBOLS[1]));
        JLabel reel3 = new JLabel(loadIcon(SYMBOLS[2]));

        reel1.setHorizontalAlignment(SwingConstants.CENTER);
        reel2.setHorizontalAlignment(SwingConstants.CENTER);
        reel3.setHorizontalAlignment(SwingConstants.CENTER);

        reelsPanel.add(reel1);
        reelsPanel.add(reel2);
        reelsPanel.add(reel3);
        frame.add(reelsPanel, BorderLayout.CENTER);

        // Панел за залог
        JPanel bottomPanel = new JPanel();
        JTextField betField = new JTextField(5);

        // SPIN бутон с изображение 50x50
        ImageIcon originalIcon = new ImageIcon("images/spin.png");
        Image img = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon spinIcon = new ImageIcon(img);

        JButton spinBtn = new JButton(spinIcon);
        spinBtn.setPreferredSize(new Dimension(50, 50));
        spinBtn.setBorderPainted(false);
        spinBtn.setContentAreaFilled(false);
        spinBtn.setFocusPainted(false);

        JButton quitBtn = new JButton("Quit");
        quitBtn.setBackground(new Color(200, 0, 0));
        quitBtn.setForeground(Color.WHITE);
        quitBtn.setFont(new Font("Arial", Font.BOLD, 16));

        bottomPanel.add(new JLabel("Залог:"));
        bottomPanel.add(betField);
        bottomPanel.add(spinBtn);
        bottomPanel.add(quitBtn);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Лог панел
        JTextArea logArea = new JTextArea(6, 20);
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        logArea.setFont(new Font("Consolas", Font.BOLD, 14));
        frame.add(new JScrollPane(logArea), BorderLayout.EAST);

        final int[] balance = {100};
        final int[] losingStreak = {0}; // брояч за поредни загуби

        spinBtn.addActionListener((ActionEvent e) -> {
            int bet;
            try {
                bet = Integer.parseInt(betField.getText());
            } catch (Exception ex) {
                logArea.append("Моля въведи валиден залог.\n");
                return;
            }

            if (bet <= 0 || bet > balance[0]) {
                logArea.append("Невалиден залог.\n");
                return;
            }

            String[] finalResult = spinReels(3);

            Timer reel1Timer = createReelTimer(reel1, finalResult[0], 5);
            Timer reel2Timer = createReelTimer(reel2, finalResult[1], 5);
            Timer reel3Timer = createReelTimer(reel3, finalResult[2], 5);

            reel1Timer.start();
            reel2Timer.start();
            reel3Timer.start();

            Timer finalTimer = new Timer(100 * 6, ev -> {
                int payout = calculatePayout(finalResult, bet);
                if (payout > 0) {
                    playSound("sounds/mazna.wav"); // звук при победа
                    logArea.append("Печелиш " + payout + " монети!\n");
                    balance[0] += payout;
                    losingStreak[0] = 0; // нулираме поредицата при печалба
                } else {
                    logArea.append("Загуби " + bet + " монети.\n");
                    balance[0] -= bet;
                    losingStreak[0]++; // увеличаваме брояча на загубите

                    if (losingStreak[0] >= 5) {
                        playSound("sounds/kambana.wav"); // звук при 5 поредни загуби
                        logArea.append("⚠️ 5 поредни загуби! Камбаната бие!\n");
                        losingStreak[0] = 0;
                    }
                }
                balanceLabel.setText("Баланс: " + balance[0] + " монети");

                if (balance[0] <= 0) {
                    JOptionPane.showMessageDialog(frame, "Изгуби всичките си монети. Край!");
                    System.exit(0);
                }
            });
            finalTimer.setRepeats(false);
            finalTimer.start();
        });

        quitBtn.addActionListener(ev -> System.exit(0));
        frame.setVisible(true);
    }

    private static Timer createReelTimer(JLabel reel, String finalSymbol, int spins) {
        final int[] count = {0};
        Timer timer = new Timer(100, null);
        timer.addActionListener(ev -> {
            if (count[0] < spins) {
                String randomSymbol = SYMBOLS[RANDOM.nextInt(SYMBOLS.length)];
                reel.setIcon(loadIcon(randomSymbol));
                count[0]++;
            } else {
                reel.setIcon(loadIcon(finalSymbol));
                timer.stop();
            }
        });
        return timer;
    }

    public static void main(String[] args) {
        startGUI();
    }

    private static String[] spinReels(int n) {
        String[] out = new String[n];
        boolean shouldWin = RANDOM.nextDouble() < WIN_CHANCE;


        if (shouldWin) {
            String symbol = SYMBOLS[RANDOM.nextInt(SYMBOLS.length)];
            if (RANDOM.nextBoolean()) {
                Arrays.fill(out, symbol);
            } else {
                out[0] = symbol;
                out[1] = symbol;
                out[2] = SYMBOLS[RANDOM.nextInt(SYMBOLS.length)];
                if (out[2].equals(symbol)) {
                    out[2] = SYMBOLS[(RANDOM.nextInt(SYMBOLS.length - 1) + 1)];
                }
            }
        } else {
            // Генерираме истински губещи комбинации
            for (int i = 0; i < n; i++) {
                out[i] = SYMBOLS[RANDOM.nextInt(SYMBOLS.length)];
            }
        }
        return out;
    }

    private static int calculatePayout(String[] reels, int bet) {
        boolean allSame = reels[0].equals(reels[1]) && reels[1].equals(reels[2]);
        if (allSame) {
            int index = Arrays.asList(SYMBOLS).indexOf(reels[0]);
            return (int) (bet * MULTIPLIERS[index]);
        }
        return 0;
    }

    private static ImageIcon loadIcon(String filename) {
        ImageIcon base = new ImageIcon("images/presli.png");
        int width = base.getIconWidth();
        int height = base.getIconHeight();

        ImageIcon icon = new ImageIcon("images/" + filename + ".png");
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private static void playSound(String soundFile) {
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File(soundFile));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        } catch (Exception e) {
            System.out.println("Грешка при зареждане на звук: " + e.getMessage());
        }
    }
}
