import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * PIN entry screen with a numeric keypad.
 * Allows 3 attempts before blocking.
 */
public class PinPanel extends JPanel {

    private final ATMGUI app;
    private JLabel  greetLabel;
    private JLabel  attemptsLabel;
    private JLabel  pinDisplay;      // shows dots
    private JLabel  msgLabel;
    private StringBuilder pinBuffer = new StringBuilder();
    private int     attemptsLeft = 3;
    private User    user;

    public PinPanel(ATMGUI app) {
        this.app = app;
        setBackground(ATMGUI.BG);
        setLayout(new GridBagLayout());
        buildUI();
    }

    public void prepareForUser(User u) {
        this.user         = u;
        this.attemptsLeft = 3;
        pinBuffer.setLength(0);
        greetLabel.setText("Hello, " + u.getIdentifier());
        attemptsLabel.setText("Attempts remaining: 3");
        pinDisplay.setText("----");
        msgLabel.setText("");
    }

    private void buildUI() {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 40, 8, 40);

        // Icon
        JLabel icon = new JLabel("🔒", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        g.gridy = 0; g.insets = new Insets(36, 40, 4, 40);
        add(icon, g);

        // Title
        JLabel title = LoginPanel.styledLabel("ENTER YOUR PIN", ATMGUI.F_TITLE, ATMGUI.TEXT_PRI);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 1; g.insets = new Insets(4, 40, 2, 40);
        add(title, g);

        // Greeting
        greetLabel = LoginPanel.styledLabel("", ATMGUI.F_SUB, ATMGUI.TEXT_SEC);
        greetLabel.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 2; g.insets = new Insets(0, 40, 4, 40);
        add(greetLabel, g);

        // Attempts
        attemptsLabel = LoginPanel.styledLabel("Attempts remaining: 3", ATMGUI.F_SMALL, ATMGUI.TEXT_SEC);
        attemptsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 3; g.insets = new Insets(0, 40, 14, 40);
        add(attemptsLabel, g);

        // PIN dot display
        pinDisplay = LoginPanel.styledLabel("----", ATMGUI.F_MONO, ATMGUI.ACCENT);
        pinDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 4; g.insets = new Insets(0, 40, 6, 40);
        add(pinDisplay, g);

        // Message
        msgLabel = LoginPanel.styledLabel("", ATMGUI.F_SMALL, ATMGUI.DANGER);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 5; g.insets = new Insets(0, 40, 14, 40);
        add(msgLabel, g);

        // Numpad
        g.gridy = 6; g.insets = new Insets(0, 60, 20, 60);
        add(buildNumpad(), g);
    }

    private JPanel buildNumpad() {
        JPanel pad = new JPanel(new GridLayout(4, 3, 10, 10));
        pad.setOpaque(false);
        String[] keys = {"1","2","3","4","5","6","7","8","9","CLR","0","OK"};
        for (String key : keys) {
            Color bg  = key.equals("OK")  ? ATMGUI.SUCCESS :
                        key.equals("CLR") ? ATMGUI.DANGER  : ATMGUI.PANEL_BG;
            Color fg  = ATMGUI.TEXT_PRI;
            JButton b = numButton(key, bg, fg);
            b.addActionListener(e -> handleKey(key));
            pad.add(b);
        }
        return pad;
    }

    private JButton numButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() :
                            getModel().isRollover() ? bg.brighter() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(72, 56));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void handleKey(String key) {
        if (attemptsLeft <= 0) return;
        switch (key) {
            case "CLR":
                pinBuffer.setLength(0);
                break;
            case "OK":
                validatePin();
                return;
            default:
                if (pinBuffer.length() < 4) pinBuffer.append(key);
        }
        updateDisplay();
    }

    private void updateDisplay() {
        int len = pinBuffer.length();
        StringBuilder dots = new StringBuilder();
        for (int i = 0; i < 4; i++) dots.append(i < len ? "●" : "-");
        pinDisplay.setText(dots.toString());
    }

    private void validatePin() {
        if (pinBuffer.length() < 4) {
            showMsg("Enter all 4 digits.", ATMGUI.DANGER); return;
        }
        int entered = Integer.parseInt(pinBuffer.toString());
        pinBuffer.setLength(0);
        updateDisplay();
        if (app.getCurrentAccount().validatePin(entered)) {
            showMsg("PIN accepted!", ATMGUI.SUCCESS);
            Timer t = new Timer(600, ev -> app.openDashboard());
            t.setRepeats(false); t.start();
        } else {
            attemptsLeft--;
            attemptsLabel.setText("Attempts remaining: " + attemptsLeft);
            if (attemptsLeft <= 0) {
                showMsg("Card blocked! Too many wrong attempts.", ATMGUI.DANGER);
                Timer t = new Timer(2500, ev -> app.logout());
                t.setRepeats(false); t.start();
            } else {
                showMsg("Wrong PIN! " + attemptsLeft + " attempt(s) left.", ATMGUI.DANGER);
            }
        }
    }

    private void showMsg(String msg, Color c) {
        msgLabel.setText(msg);
        msgLabel.setForeground(c);
    }
}
