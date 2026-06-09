import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class DashboardPanel extends JPanel {

    private final ATMGUI app;
    private JLabel balanceLabel;
    private JLabel userLabel;
    private JLabel msgLabel;

    public DashboardPanel(ATMGUI app) {
        this.app = app;
        setBackground(ATMGUI.BG);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 28, 6, 28);

        JPanel header = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics gr) {
                super.paintComponent(gr);
                Graphics2D g2 = (Graphics2D) gr;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ATMGUI.CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        userLabel = LoginPanel.styledLabel("Account", ATMGUI.F_LABEL, ATMGUI.TEXT_SEC);
        JLabel atmLabel = LoginPanel.styledLabel("🏧  ATM", new Font("Segoe UI Emoji", Font.BOLD, 15), ATMGUI.ACCENT);
        header.add(userLabel, BorderLayout.WEST);
        header.add(atmLabel,  BorderLayout.EAST);

        g.gridy = 0; g.insets = new Insets(28, 28, 6, 28);
        add(header, g);

        JPanel balCard = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics gr) {
                super.paintComponent(gr);
                Graphics2D g2 = (Graphics2D) gr;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 80, 160), getWidth(), 0, new Color(0, 160, 220));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 22, 22));
            }
        };
        balCard.setOpaque(false);
        balCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0; bc.fill = GridBagConstraints.HORIZONTAL;

        bc.gridy = 0;
        balCard.add(LoginPanel.styledLabel("Available Balance", ATMGUI.F_LABEL, new Color(200, 235, 255)), bc);
        bc.gridy = 1;
        balanceLabel = LoginPanel.styledLabel("৳ 0.00", new Font("Consolas", Font.BOLD, 34), Color.WHITE);
        balCard.add(balanceLabel, bc);

        g.gridy = 1; g.insets = new Insets(4, 28, 14, 28);
        add(balCard, g);

        msgLabel = LoginPanel.styledLabel("", ATMGUI.F_SMALL, ATMGUI.SUCCESS);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 2; g.insets = new Insets(0, 28, 0, 28);
        add(msgLabel, g);

        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);
        grid.add(opButton("💰", "Check Balance",    new Color(0,160,100),  e -> checkBalance()));
        grid.add(opButton("⬆️", "Withdraw",         new Color(200,80,40),  e -> withdraw()));
        grid.add(opButton("⬇️", "Deposit",           new Color(30,140,210), e -> deposit()));
        grid.add(opButton("📋", "Transaction History",new Color(120,60,200),e -> openHistory()));

        g.gridy = 3; g.insets = new Insets(8, 28, 10, 28);
        add(grid, g);

        JButton logout = LoginPanel.roundButton("LOGOUT", new Color(60, 60, 90), ATMGUI.TEXT_PRI, e -> {
            int r = JOptionPane.showConfirmDialog(app,
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) app.logout();
        });
        g.gridy = 4; g.insets = new Insets(0, 80, 28, 80);
        add(logout, g);
    }

    private JButton opButton(String emoji, String label, Color bg, ActionListener al) {
        JButton btn = new JButton("<html><center>" + emoji + "<br>" + label + "</center></html>") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() :
                            getModel().isPressed()  ? bg.darker()   : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 90));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(al);
        return btn;
    }

    public void refresh() {
        if (app.getCurrentUser() == null) return;
        userLabel.setText(app.getCurrentUser().getIdentifier());
        balanceLabel.setText(String.format("৳ %,.2f", app.getCurrentAccount().getBalance()));
        msgLabel.setText("");
    }

    private void checkBalance() {
        double bal = app.getCurrentAccount().getBalance();
        showMsg(String.format("Your balance is ৳ %,.2f", bal), ATMGUI.SUCCESS);
        balanceLabel.setText(String.format("৳ %,.2f", bal));
    }

    private void withdraw() {
        String input = JOptionPane.showInputDialog(app,
            "Enter withdrawal amount (৳):", "Withdraw", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;
        try {
            double amt = Double.parseDouble(input.trim());
            TransactionRecord rec = app.getCurrentAccount().withdraw(amt);
            if (rec == null) {
                showMsg("Insufficient balance or invalid amount!", ATMGUI.DANGER);
            } else {
                app.recordTransaction(rec);
                balanceLabel.setText(String.format("৳ %,.2f", app.getCurrentAccount().getBalance()));
                showMsg(String.format("Withdrawal of ৳ %,.2f successful!", amt), ATMGUI.SUCCESS);
            }
        } catch (NumberFormatException ex) {
            showMsg("Please enter a valid number.", ATMGUI.DANGER);
        }
    }

    private void deposit() {
        String input = JOptionPane.showInputDialog(app,
            "Enter deposit amount (৳):", "Deposit", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;
        try {
            double amt = Double.parseDouble(input.trim());
            TransactionRecord rec = app.getCurrentAccount().deposit(amt);
            if (rec == null) {
                showMsg("Invalid amount!", ATMGUI.DANGER);
            } else {
                app.recordTransaction(rec);
                balanceLabel.setText(String.format("৳ %,.2f", app.getCurrentAccount().getBalance()));
                showMsg(String.format("Deposit of ৳ %,.2f successful!", amt), ATMGUI.SUCCESS);
            }
        } catch (NumberFormatException ex) {
            showMsg("Please enter a valid number.", ATMGUI.DANGER);
        }
    }

    private void openHistory() {
        app.showHistory();
    }

    private void showMsg(String msg, Color c) {
        msgLabel.setText(msg);
        msgLabel.setForeground(c);
    }
}
