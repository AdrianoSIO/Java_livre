package pj_livre.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import pj_livre.Controllers.C_livre;
import pj_livre.Models.M_Payer;

public class V_Paiements extends JFrame {

    private C_livre controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtIdUser, txtIdExemplaire, txtIdMethode, txtMontant;
    private JButton btnPayer, btnSupprimer, btnVider;
    private JLabel lblSelectionne;

    // Clé composite sélectionnée
    private int selIdLocation = -1;
    private int selIdMethode  = -1;

    private static final String[] COLONNES = {
        "ID Location", "ID Méthode", "Date paiement", "Montant", "Emprunt ?"
    };

    public V_Paiements(C_livre controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Gestion des paiements  [GES/ADM]");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800, 420));

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(COLONNES, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectionnerLigne();
        });
        main.add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Formulaire nouveau paiement via procédure stockée ─────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Nouveau paiement (via procédure)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtIdUser       = ajouterChamp(form, gbc, "ID User :",        0);
        txtIdExemplaire = ajouterChamp(form, gbc, "ID Exemplaire :",  1);
        txtIdMethode    = ajouterChamp(form, gbc, "ID Méthode :",     2);
        txtMontant      = ajouterChamp(form, gbc, "Montant :",        3);

        lblSelectionne = new JLabel("Aucun paiement sélectionné");
        lblSelectionne.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        form.add(lblSelectionne, gbc);

        btnPayer     = new JButton("Enregistrer paiement");
        btnSupprimer = new JButton("Supprimer");
        btnVider     = new JButton("Vider");

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panelBtns.add(btnVider); panelBtns.add(btnPayer); panelBtns.add(btnSupprimer);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        form.add(panelBtns, gbc);

        main.add(form, BorderLayout.EAST);

        btnVider.addActionListener(    e -> vider());
        btnPayer.addActionListener(    e -> enregistrerPaiement());
        btnSupprimer.addActionListener(e -> supprimer());

        add(main);
        pack();
        setLocationRelativeTo(null);
    }

    public void chargerDonnees() {
        tableModel.setRowCount(0);
        try {
            List<M_Payer> paiements = M_Payer.getRecords(controller.getDb());
            for (M_Payer p : paiements) {
                tableModel.addRow(new Object[]{
                    p.getIdLocation(), p.getIdMethode(),
                    p.getDatePaiement(), p.getMontant(), p.isEstEmprunt() ? "Oui" : "Non"
                });
            }
        } catch (Exception e) { controller.afficherErreur("Erreur chargement paiements : " + e.getMessage()); }
    }

    private void selectionnerLigne() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selIdLocation = (int) tableModel.getValueAt(row, 0);
        selIdMethode  = (int) tableModel.getValueAt(row, 1);
        lblSelectionne.setText("Sélectionné : location=" + selIdLocation + " méthode=" + selIdMethode);
    }

    /** Crée une location + paiement via la procédure stockée */
    private void enregistrerPaiement() {
        try {
            int   idUser = Integer.parseInt(txtIdUser.getText().trim());
            int   idEx   = Integer.parseInt(txtIdExemplaire.getText().trim());
            int   idMeth = Integer.parseInt(txtIdMethode.getText().trim());
            float mont   = Float.parseFloat(txtMontant.getText().trim());

            new M_Payer(controller.getDb(), idUser, idEx, idMeth, mont);
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur paiement : " + e.getMessage()); }
    }

    private void supprimer() {
        if (selIdLocation < 0) { controller.afficherErreur("Sélectionnez un paiement."); return; }
        int c = JOptionPane.showConfirmDialog(this,
                "Supprimer ce paiement ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            new M_Payer(controller.getDb(), selIdLocation, selIdMethode).delete();
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur suppression : " + e.getMessage()); }
    }

    private void vider() {
        txtIdUser.setText(""); txtIdExemplaire.setText("");
        txtIdMethode.setText(""); txtMontant.setText("");
        selIdLocation = -1; selIdMethode = -1;
        lblSelectionne.setText("Aucun paiement sélectionné");
        table.clearSelection();
    }

    private JTextField ajouterChamp(JPanel p, GridBagConstraints gbc, String label, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        p.add(new JLabel(label), gbc);
        JTextField tf = new JTextField(18);
        gbc.gridx = 1; p.add(tf, gbc);
        return tf;
    }
}
