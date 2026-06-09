import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class HistoryPanel extends JPanel {

    private final ATMGUI  app;
    private JTable        table;
    private DefaultTableModel model;
    private JLabel        titleLabel;

    public HistoryPanel(ATMGUI app) {
        this.app = app;
        setBackground(ATMGUI.BG);
        setLayout(new BorderLayout(0, 0));
        buildUI();
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(28, 28, 10, 28));

        JButton back = LoginPanel.linkButton("← Back to Dashboard");
        back.addActionListener(e -> app.showCard(ATMGUI.CARD_DASHBOARD));

        titleLabel = LoginPanel.styledLabel("TRANSACTION HISTORY", ATMGUI.F_TITLE, ATMGUI.TEXT_PRI);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topBar.add(back, BorderLayout.WEST);
        topBar.add(titleLabel, BorderLayout.CENTER);
        add(topBar, BorderLayout.NORTH);

        String[] cols = {"Date & Time", "Type", "Amount (৳)", "Balance After (৳)"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));
        scroll.getViewport().setBackground(ATMGUI.CARD_BG);
        scroll.setBackground(ATMGUI.BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        JLabel empty = LoginPanel.styledLabel(
            "No transactions yet.", ATMGUI.F_SUB, ATMGUI.TEXT_SEC);
        empty.setHorizontalAlignment(SwingConstants.CENTER);
        empty.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
    }

    private void styleTable() {
        table.setBackground(ATMGUI.CARD_BG);
        table.setForeground(ATMGUI.TEXT_PRI);
        table.setFont(ATMGUI.F_FIELD);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0, 100, 180));
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);

        JTableHeader h = table.getTableHeader();
        h.setBackground(ATMGUI.PANEL_BG);
        h.setForeground(ATMGUI.ACCENT);
        h.setFont(ATMGUI.F_LABEL);
        h.setPreferredSize(new Dimension(h.getWidth(), 38));
        h.setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(170);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? ATMGUI.CARD_BG : new Color(22, 40, 62));
                
                    if (col == 2) {
                        String type = (String) tbl.getValueAt(row, 1);
                        setForeground("Deposit".equals(type) ? ATMGUI.SUCCESS : ATMGUI.DANGER);
                    } else {
                        setForeground(ATMGUI.TEXT_PRI);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }

    public void populate() {
        model.setRowCount(0);
        List<TransactionRecord> list = app.getCurrentAccount().getHistory();
        if (list.isEmpty()) return;
        
        for (int i = list.size() - 1; i >= 0; i--) {
            TransactionRecord r = list.get(i);
            model.addRow(new Object[]{
                r.getDateTime(),
                r.getType(),
                String.format("%,.2f", r.getAmount()),
                String.format("%,.2f", r.getBalanceAfter())
            });
        }
    }
}
