import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginPanel extends JPanel {

    private final ATMGUI app;
    private JTextField     idField;
    private JPasswordField passField;
    private JLabel         msgLabel;

    public LoginPanel(ATMGUI app) {
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
        
        JLabel icon = new JLabel("🏧", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        g.gridy = 0; g.insets = new Insets(40, 30, 4, 30);
        add(icon, g);

    
        JLabel title = styledLabel("WELCOME TO ATM", ATMGUI.F_TITLE, ATMGUI.TEXT_PRI);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 1; g.insets = new Insets(4, 30, 2, 30);
        add(title, g);

        JLabel sub = styledLabel("Secure · Fast · Reliable", ATMGUI.F_SUB, ATMGUI.TEXT_SEC);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 2; g.insets = new Insets(0, 30, 24, 30);
        add(sub, g);

    
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
        cg.insets = new Insets(8, 20, 8, 20);
        cg.fill   = GridBagConstraints.HORIZONTAL;
        cg.gridx  = 0;

    
        cg.gridy = 0;
        card.add(styledLabel("Email or Phone", ATMGUI.F_LABEL, ATMGUI.TEXT_SEC), cg);
        cg.gridy = 1;
        idField = styledField();
        idField.setToolTipText("Enter email or phone number");
        card.add(idField, cg);

    
        cg.gridy = 2;
        card.add(styledLabel("Password", ATMGUI.F_LABEL, ATMGUI.TEXT_SEC), cg);
        cg.gridy = 3;
        passField = styledPassField();
        card.add(passField, cg);

    
        cg.gridy = 4;
        msgLabel = styledLabel("", ATMGUI.F_SMALL, ATMGUI.DANGER);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(msgLabel, cg);

    
        cg.gridy = 5; cg.insets = new Insets(12, 20, 6, 20);
        card.add(roundButton("LOG IN", ATMGUI.ACCENT, ATMGUI.BG, e -> doLogin()), cg);
        cg.gridy = 6; cg.insets = new Insets(4, 20, 20, 20);
        JButton reg = linkButton("Don't have an account?  Register →");
        reg.addActionListener(e -> { reset(); app.showCard(ATMGUI.CARD_REGISTER); });
        card.add(reg, cg);

        g.gridy = 3; g.insets = new Insets(0, 28, 0, 28);
        add(card, g);
    }

    private void doLogin() {
        String id   = idField.getText().trim();
        String pass = new String(passField.getPassword());
        if (id.isEmpty() || pass.isEmpty()) {
            showMsg("Please fill all fields.", ATMGUI.DANGER); return;
        }
        User user = AuthService.authenticate(id, pass);
        if (user == null) {
            if (AuthService.userExists(id))
                showMsg("Wrong password. Try again.", ATMGUI.DANGER);
            else
                showMsg("User not found. Please register.", ATMGUI.DANGER);
            return;
        }
        showMsg("Login successful!", ATMGUI.SUCCESS);
        app.startSession(user);
    }

    public void reset() {
        idField.setText("");
        passField.setText("");
        msgLabel.setText("");
    }

    private void showMsg(String msg, Color c) {
        msgLabel.setText(msg);
        msgLabel.setForeground(c);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    static JLabel styledLabel(String text, Font f, Color c) {
        JLabel l = new JLabel(text);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    static JTextField styledField() {
        JTextField f = new JTextField(20);
        f.setBackground(ATMGUI.FIELD_BG);
        f.setForeground(ATMGUI.TEXT_PRI);
        f.setCaretColor(ATMGUI.ACCENT);
        f.setFont(ATMGUI.F_FIELD);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ATMGUI.BORDER_C, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return f;
    }

    static JPasswordField styledPassField() {
        JPasswordField f = new JPasswordField(20);
        f.setBackground(ATMGUI.FIELD_BG);
        f.setForeground(ATMGUI.TEXT_PRI);
        f.setCaretColor(ATMGUI.ACCENT);
        f.setFont(ATMGUI.F_FIELD);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ATMGUI.BORDER_C, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return f;
    }

    static JButton roundButton(String text, Color bg, Color fg, ActionListener al) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() :
                            getModel().isRollover() ? bg.brighter() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setFont(ATMGUI.F_BTN);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(260, 44));
        btn.addActionListener(al);
        return btn;
    }

    static JButton linkButton(String text) {
        JButton b = new JButton(text);
        b.setForeground(ATMGUI.ACCENT);
        b.setFont(ATMGUI.F_SMALL);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
