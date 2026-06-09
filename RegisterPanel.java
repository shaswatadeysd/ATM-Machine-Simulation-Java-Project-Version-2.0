import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/** Registration screen */
public class RegisterPanel extends JPanel {

    private final ATMGUI   app;
    private JTextField     idField;
    private JPasswordField passField;
    private JPasswordField confirmField;
    private JPasswordField pinField;
    private JLabel         msgLabel;

    public RegisterPanel(ATMGUI app) {
        this.app = app;
        setBackground(ATMGUI.BG);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 30, 6, 30);
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.gridx  = 0;

        // Back button row
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        JButton back = LoginPanel.linkButton("← Back to Login");
        back.addActionListener(e -> app.showCard(ATMGUI.CARD_LOGIN));
        topRow.add(back);
        g.gridy = 0; g.insets = new Insets(24, 24, 0, 24);
        add(topRow, g);

        // Title
        JLabel title = LoginPanel.styledLabel("CREATE ACCOUNT", ATMGUI.F_TITLE, ATMGUI.TEXT_PRI);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 1; g.insets = new Insets(8, 30, 4, 30);
        add(title, g);

        JLabel sub = LoginPanel.styledLabel("New accounts start with ৳1,000.00 balance", ATMGUI.F_SUB, ATMGUI.TEXT_SEC);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 2; g.insets = new Insets(0, 30, 16, 30);
        add(sub, g);

        // Card
        JPanel card = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g2d) {
                super.paintComponent(g2d);
                Graphics2D g2 = (Graphics2D) g2d;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ATMGUI.CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
            }
        };
        card.setOpaque(false);
        GridBagConstraints cg = new GridBagConstraints();
        cg.insets = new Insets(7, 20, 7, 20);
        cg.fill   = GridBagConstraints.HORIZONTAL;
        cg.gridx  = 0;

        cg.gridy = 0; card.add(LoginPanel.styledLabel("Email or Phone Number", ATMGUI.F_LABEL, ATMGUI.TEXT_SEC), cg);
        cg.gridy = 1; idField = LoginPanel.styledField(); card.add(idField, cg);

        cg.gridy = 2; card.add(LoginPanel.styledLabel("Password", ATMGUI.F_LABEL, ATMGUI.TEXT_SEC), cg);
        cg.gridy = 3; passField = LoginPanel.styledPassField(); card.add(passField, cg);

        cg.gridy = 4; card.add(LoginPanel.styledLabel("Confirm Password", ATMGUI.F_LABEL, ATMGUI.TEXT_SEC), cg);
        cg.gridy = 5; confirmField = LoginPanel.styledPassField(); card.add(confirmField, cg);

        cg.gridy = 6; card.add(LoginPanel.styledLabel("Set ATM PIN (4 digits)", ATMGUI.F_LABEL, ATMGUI.TEXT_SEC), cg);
        cg.gridy = 7; pinField = LoginPanel.styledPassField(); card.add(pinField, cg);

        cg.gridy = 8;
        msgLabel = LoginPanel.styledLabel("", ATMGUI.F_SMALL, ATMGUI.DANGER);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(msgLabel, cg);

        cg.gridy = 9; cg.insets = new Insets(12, 20, 20, 20);
        card.add(LoginPanel.roundButton("REGISTER", ATMGUI.SUCCESS, ATMGUI.BG, e -> doRegister()), cg);

        g.gridy = 3; g.insets = new Insets(0, 28, 20, 28);
        add(card, g);
    }

    private void doRegister() {
        String id      = idField.getText().trim();
        String pass    = new String(passField.getPassword());
        String confirm = new String(confirmField.getPassword());
        String pinStr  = new String(pinField.getPassword()).trim();

        if (id.isEmpty() || pass.isEmpty() || pinStr.isEmpty()) {
            showMsg("Please fill all fields.", ATMGUI.DANGER); return;
        }
        if (!pass.equals(confirm)) {
            showMsg("Passwords do not match.", ATMGUI.DANGER); return;
        }
        if (!pinStr.matches("\\d{4}")) {
            showMsg("PIN must be exactly 4 digits.", ATMGUI.DANGER); return;
        }
        int pin = Integer.parseInt(pinStr);
        if (!AuthService.register(id, pass, pin)) {
            showMsg("Account already exists!", ATMGUI.DANGER); return;
        }
        showMsg("Registered! Please log in.", ATMGUI.SUCCESS);
        Timer t = new Timer(1200, ev -> { clearFields(); app.showCard(ATMGUI.CARD_LOGIN); });
        t.setRepeats(false); t.start();
    }

    private void showMsg(String msg, Color c) {
        msgLabel.setText(msg);
        msgLabel.setForeground(c);
    }

    private void clearFields() {
        idField.setText(""); passField.setText("");
        confirmField.setText(""); pinField.setText("");
        msgLabel.setText("");
    }
}
