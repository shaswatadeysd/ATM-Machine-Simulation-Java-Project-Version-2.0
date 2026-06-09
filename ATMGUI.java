import javax.swing.*;
import java.awt.*;

public class ATMGUI extends JFrame {

    public static final Color BG        = new Color(10,  20,  35);
    public static final Color CARD_BG   = new Color(18,  32,  52);
    public static final Color PANEL_BG  = new Color(24,  42,  68);
    public static final Color ACCENT    = new Color(0,  191, 255);
    public static final Color ACCENT2   = new Color(0,  120, 200);
    public static final Color SUCCESS   = new Color(40, 200, 120);
    public static final Color DANGER    = new Color(255, 80,  80);
    public static final Color TEXT_PRI  = Color.WHITE;
    public static final Color TEXT_SEC  = new Color(150, 190, 220);
    public static final Color FIELD_BG  = new Color(14,  30,  50);
    public static final Color BORDER_C  = new Color(40,  80, 120);

    public static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  26);
    public static final Font F_SUB   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font F_LABEL = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font F_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font F_BTN   = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font F_MONO  = new Font("Consolas",  Font.BOLD,  30);
    public static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    public static final String CARD_LOGIN     = "login";
    public static final String CARD_REGISTER  = "register";
    public static final String CARD_PIN       = "pin";
    public static final String CARD_DASHBOARD = "dashboard";
    public static final String CARD_HISTORY   = "history";

    private CardLayout    cardLayout;
    private JPanel        mainPanel;

    private LoginPanel     loginPanel;
    private RegisterPanel  registerPanel;
    private PinPanel       pinPanel;
    private DashboardPanel dashboardPanel;
    private HistoryPanel   historyPanel;

    private User    currentUser;
    private Account currentAccount;

    public ATMGUI() {
        setTitle("ATM Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 700);
        setMinimumSize(new Dimension(480, 700));
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setBackground(BG);

        loginPanel     = new LoginPanel(this);
        registerPanel  = new RegisterPanel(this);
        pinPanel       = new PinPanel(this);
        dashboardPanel = new DashboardPanel(this);
        historyPanel   = new HistoryPanel(this);

        mainPanel.add(loginPanel,     CARD_LOGIN);
        mainPanel.add(registerPanel,  CARD_REGISTER);
        mainPanel.add(pinPanel,       CARD_PIN);
        mainPanel.add(dashboardPanel, CARD_DASHBOARD);
        mainPanel.add(historyPanel,   CARD_HISTORY);

        add(mainPanel);
        showCard(CARD_LOGIN);
        setVisible(true);
    }

    public void showCard(String name) {
        cardLayout.show(mainPanel, name);
    }

    public void startSession(User user) {
        this.currentUser    = user;
        this.currentAccount = new Account(user);
        String key = user.getIdentifier();
        for (TransactionRecord tr : AuthService.loadTransactions(key))
            currentAccount.addHistoryRecord(tr);
        pinPanel.prepareForUser(user);
        showCard(CARD_PIN);
    }

    public void openDashboard() {
        dashboardPanel.refresh();
        showCard(CARD_DASHBOARD);
    }

    public void showHistory() {
        historyPanel.populate();
        showCard(CARD_HISTORY);
    }

    public void recordTransaction(TransactionRecord rec) {
        AuthService.saveUsers();
        AuthService.saveTransaction(currentUser.getIdentifier(), rec);
    }

    public void logout() {
        currentUser    = null;
        currentAccount = null;
        loginPanel.reset();
        showCard(CARD_LOGIN);
    }

    public User    getCurrentUser()    { return currentUser; }
    public Account getCurrentAccount() { return currentAccount; }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        AuthService.loadUsers();
        SwingUtilities.invokeLater(ATMGUI::new);
    }
}
