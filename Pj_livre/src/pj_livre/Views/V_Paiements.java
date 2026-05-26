package pj_livre.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import pj_livre.Controllers.C_livre;
import pj_livre.Models.M_Payer;

public class V_Paiements extends V_Base {

    private JTable            tbl;
    private DefaultTableModel model;
    private JTextField txtIdUser       = new JTextField(15);
    private JTextField txtIdExemplaire = new JTextField(15);
    private JTextField txtIdMethode    = new JTextField(15);
    private JTextField txtMontant      = new JTextField(15);
    private JButton    btnPayer        = new JButton("Enregistrer");
    private JButton    btnSupprimer    = new JButton("Supprimer");
    private JButton    btnVider        = new JButton("Vider");
    private int        selIdLocation   = -1;
    private int        selIdMethode    = -1;

    public V_Paiements(C_livre controller) {
        super(controller, "Paiements");

        model = new DefaultTableModel(new String[]{"ID Location","ID Méthode","Date","Montant","Emprunt ?"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl = new JTable(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) selectionner(); });

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Nouveau paiement"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3,5,3,5); c.fill = GridBagConstraints.HORIZONTAL;
        ajouterChamp(form, c, "ID User :",        txtIdUser,       0);
        ajouterChamp(form, c, "ID Exemplaire :",  txtIdExemplaire, 1);
        ajouterChamp(form, c, "ID Méthode :",     txtIdMethode,    2);
        ajouterChamp(form, c, "Montant :",        txtMontant,      3);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        btns.add(btnVider); btns.add(btnPayer); btns.add(btnSupprimer);
        c.gridx = 0; c.gridy = 4; c.gridwidth = 2; form.add(btns, c);

        panelContenu.setLayout(new BorderLayout(8, 0));
        panelContenu.add(new JScrollPane(tbl), BorderLayout.CENTER);
        panelContenu.add(form, BorderLayout.EAST);

        btnVider.addActionListener(    e -> vider());
        btnPayer.addActionListener(    e -> enregistrer());
        btnSupprimer.addActionListener(e -> supprimer());

        setMinimumSize(new Dimension(780, 400));
        pack();
        setLocationRelativeTo(null);
    }

    public void chargerDonnees() {
        model.setRowCount(0);
        try {
            List<M_Payer> paiements = M_Payer.getRecords(controller.getDb());
            for (M_Payer p : paiements)
                model.addRow(new Object[]{p.getIdLocation(), p.getIdMethode(),
                    p.getDatePaiement(), p.getMontant(), p.isEstEmprunt() ? "Oui" : "Non"});
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void selectionner() {
        int row = tbl.getSelectedRow(); if (row < 0) return;
        selIdLocation = (int) model.getValueAt(row, 0);
        selIdMethode  = (int) model.getValueAt(row, 1);
    }

    private void enregistrer() {
        try {
            new M_Payer(controller.getDb(),
                    Integer.parseInt(txtIdUser.getText().trim()),
                    Integer.parseInt(txtIdExemplaire.getText().trim()),
                    Integer.parseInt(txtIdMethode.getText().trim()),
                    Float.parseFloat(txtMontant.getText().trim()));
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void supprimer() {
        if (selIdLocation < 0) { controller.afficherErreur("Sélectionnez un paiement."); return; }
        if (JOptionPane.showConfirmDialog(this, "Supprimer ce paiement ?", "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try { new M_Payer(controller.getDb(), selIdLocation, selIdMethode).delete(); vider(); chargerDonnees(); }
        catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void vider() {
        txtIdUser.setText(""); txtIdExemplaire.setText(""); txtIdMethode.setText(""); txtMontant.setText("");
        selIdLocation = -1; selIdMethode = -1; tbl.clearSelection();
    }

    private void ajouterChamp(JPanel p, GridBagConstraints c, String label, JTextField tf, int row) {
        c.gridwidth = 1; c.gridx = 0; c.gridy = row; p.add(new JLabel(label), c);
        c.gridx = 1; p.add(tf, c);
    }
}
