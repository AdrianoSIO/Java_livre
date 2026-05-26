package pj_livre.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import pj_livre.Controllers.C_livre;
import pj_livre.Models.M_Location;

public class V_Locations extends V_Base {

    private JTable            tbl;
    private DefaultTableModel model;
    private JTextField txtIdExemplaire = new JTextField(15);
    private JTextField txtPrixEmprunt  = new JTextField(15);
    private JTextField txtCommentaire  = new JTextField(15);
    private JButton    btnEmprunter    = new JButton("Emprunter");
    private JButton    btnRetour       = new JButton("Retour");
    private JButton    btnSupprimer    = new JButton("Supprimer");
    private JButton    btnVider        = new JButton("Vider");
    private int        idSel           = -1;

    public V_Locations(C_livre controller) {
        super(controller, "Emprunts");

        btnSupprimer.setEnabled(controller.peutGerer());

        model = new DefaultTableModel(new String[]{"ID","User","Exemplaire","Date emprunt","Prix","Date retour"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl = new JTable(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) selectionner(); });

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Nouvel emprunt"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3,5,3,5); c.fill = GridBagConstraints.HORIZONTAL;
        ajouterChamp(form, c, "ID Exemplaire :", txtIdExemplaire, 0);
        ajouterChamp(form, c, "Prix emprunt :",  txtPrixEmprunt,  1);
        ajouterChamp(form, c, "Commentaire :",   txtCommentaire,  2);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        btns.add(btnVider); btns.add(btnEmprunter); btns.add(btnRetour); btns.add(btnSupprimer);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; form.add(btns, c);

        panelContenu.setLayout(new BorderLayout(8, 0));
        panelContenu.add(new JScrollPane(tbl), BorderLayout.CENTER);
        panelContenu.add(form, BorderLayout.EAST);

        btnVider.addActionListener(    e -> vider());
        btnEmprunter.addActionListener(e -> emprunter());
        btnRetour.addActionListener(   e -> enregistrerRetour());
        btnSupprimer.addActionListener(e -> supprimer());

        setMinimumSize(new Dimension(800, 420));
        pack();
        setLocationRelativeTo(null);
    }

    public void chargerDonnees() {
        model.setRowCount(0);
        try {
            if (controller.peutGerer()) {
                LinkedHashMap<Integer, M_Location> locs = M_Location.getRecords(controller.getDb());
                for (M_Location loc : locs.values())
                    model.addRow(new Object[]{loc.getId(), loc.getIdUser(), loc.getIdExemplaire(),
                        loc.getDateEmprunt(), loc.getPrixEmprunt(), loc.getDateRetour()});
            } else {
                ResultSet res = controller.getDb().sqlSelect(
                        "SELECT * FROM mcd_location WHERE id_user = " + controller.getSessionId() + " ORDER BY id");
                while (res.next()) {
                    model.addRow(new Object[]{res.getInt("id"), res.getInt("id_user"),
                        res.getInt("id_exemplaire"), res.getTimestamp("date_emprunt"),
                        res.getFloat("prix_emprunt"), res.getTimestamp("date_retour")});
                }
            }
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void selectionner() {
        int row = tbl.getSelectedRow(); if (row < 0) return;
        idSel = (int) model.getValueAt(row, 0);
        txtIdExemplaire.setText(String.valueOf(model.getValueAt(row, 2)));
        txtPrixEmprunt.setText(String.valueOf(model.getValueAt(row, 4)));
    }

    private void emprunter() {
        try {
            new M_Location(controller.getDb(), controller.getSessionId(),
                    Integer.parseInt(txtIdExemplaire.getText().trim()),
                    LocalDateTime.now(), Float.parseFloat(txtPrixEmprunt.getText().trim()),
                    LocalDateTime.now().plusDays(14), "0", txtCommentaire.getText().trim());
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void enregistrerRetour() {
        if (idSel < 0) { controller.afficherErreur("Sélectionnez un emprunt."); return; }
        try {
            M_Location loc = new M_Location(controller.getDb(), idSel);
            loc.setDateRetour(LocalDateTime.now()); loc.update();
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void supprimer() {
        if (idSel < 0) { controller.afficherErreur("Sélectionnez un emprunt."); return; }
        if (JOptionPane.showConfirmDialog(this, "Supprimer cet emprunt et ses paiements ?", "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try { new M_Location(controller.getDb(), idSel).delete(); vider(); chargerDonnees(); }
        catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void vider() {
        txtIdExemplaire.setText(""); txtPrixEmprunt.setText(""); txtCommentaire.setText("");
        idSel = -1; tbl.clearSelection();
    }

    private void ajouterChamp(JPanel p, GridBagConstraints c, String label, JTextField tf, int row) {
        c.gridwidth = 1; c.gridx = 0; c.gridy = row; p.add(new JLabel(label), c);
        c.gridx = 1; p.add(tf, c);
    }
}
