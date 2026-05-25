package pj_livre.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import pj_livre.Controllers.C_livre;
import pj_livre.Models.M_Location;

public class V_Locations extends JFrame {

    private C_livre controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtIdExemplaire, txtPrixEmprunt, txtCommentaire;
    private JButton btnEmprunter, btnRetour, btnSupprimer, btnVider;
    private JLabel lblIdSelectionne;
    private int idSelectionne = -1;

    // Colonnes affichées
    private static final String[] COLONNES = {
        "ID", "ID User", "ID Exemplaire", "Date emprunt", "Prix", "Date retour"
    };

    public V_Locations(C_livre controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Gestion des emprunts");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(850, 450));

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

        // ── Formulaire nouvel emprunt ─────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Nouvel emprunt"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtIdExemplaire = ajouterChamp(form, gbc, "ID Exemplaire :", 0);
        txtPrixEmprunt  = ajouterChamp(form, gbc, "Prix emprunt :",  1);
        txtCommentaire  = ajouterChamp(form, gbc, "Commentaire :",   2);

        lblIdSelectionne = new JLabel("Aucun emprunt sélectionné");
        lblIdSelectionne.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(lblIdSelectionne, gbc);

        btnEmprunter = new JButton("Emprunter");
        btnRetour    = new JButton("Enregistrer retour");
        btnSupprimer = new JButton("Supprimer");
        btnVider     = new JButton("Vider");

        // APP/FOR/ENS peuvent emprunter, GES/ADM peuvent tout faire
        btnSupprimer.setEnabled(controller.peutGerer());

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panelBtns.add(btnVider);
        panelBtns.add(btnEmprunter);
        panelBtns.add(btnRetour);
        panelBtns.add(btnSupprimer);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        form.add(panelBtns, gbc);

        main.add(form, BorderLayout.EAST);

        btnVider.addActionListener(    e -> vider());
        btnEmprunter.addActionListener(e -> emprunter());
        btnRetour.addActionListener(   e -> enregistrerRetour());
        btnSupprimer.addActionListener(e -> supprimer());

        add(main);
        pack();
        setLocationRelativeTo(null);
    }

    public void chargerDonnees() {
        tableModel.setRowCount(0);
        try {
            LinkedHashMap<Integer, M_Location> locs;

            // Un apprenant/formateur/enseignant ne voit que ses propres emprunts
            if (controller.peutGerer()) {
                locs = M_Location.getRecords(controller.getDb());
            } else {
                // Filtrage par id_user connecté
                locs = new LinkedHashMap<>();
                java.sql.ResultSet res = controller.getDb().sqlSelect(
                        "SELECT * FROM mcd_location WHERE id_user = "
                        + controller.getSessionId() + " ORDER BY id");
                while (res.next()) {
                    LocalDateTime de = res.getTimestamp("date_emprunt") != null
                            ? res.getTimestamp("date_emprunt").toLocalDateTime() : null;
                    LocalDateTime dr = res.getTimestamp("date_retour") != null
                            ? res.getTimestamp("date_retour").toLocalDateTime() : null;
                    M_Location loc = new M_Location(controller.getDb(),
                            res.getInt("id"), res.getInt("id_user"),
                            res.getInt("id_exemplaire"), de,
                            res.getFloat("prix_emprunt"), dr,
                            res.getString("prix_retour"), res.getString("commentaire"),
                            res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null,
                            res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null);
                    locs.put(loc.getId(), loc);
                }
            }

            for (M_Location loc : locs.values()) {
                tableModel.addRow(new Object[]{
                    loc.getId(), loc.getIdUser(), loc.getIdExemplaire(),
                    loc.getDateEmprunt(), loc.getPrixEmprunt(), loc.getDateRetour()
                });
            }
        } catch (Exception e) { controller.afficherErreur("Erreur chargement emprunts : " + e.getMessage()); }
    }

    private void selectionnerLigne() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        idSelectionne = (int) tableModel.getValueAt(row, 0);
        lblIdSelectionne.setText("ID sélectionné : " + idSelectionne);
        try {
            M_Location loc = new M_Location(controller.getDb(), idSelectionne);
            txtIdExemplaire.setText(String.valueOf(loc.getIdExemplaire()));
            txtPrixEmprunt.setText(String.valueOf(loc.getPrixEmprunt()));
            txtCommentaire.setText(loc.getCommentaire());
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void emprunter() {
        try {
            int   idEx   = Integer.parseInt(txtIdExemplaire.getText().trim());
            float prix   = Float.parseFloat(txtPrixEmprunt.getText().trim());
            String comm  = txtCommentaire.getText().trim();

            new M_Location(controller.getDb(),
                    controller.getSessionId(), idEx,
                    LocalDateTime.now(), prix,
                    LocalDateTime.now().plusDays(14), "0", comm);
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur emprunt : " + e.getMessage()); }
    }

    private void enregistrerRetour() {
        if (idSelectionne < 0) { controller.afficherErreur("Sélectionnez un emprunt."); return; }
        try {
            M_Location loc = new M_Location(controller.getDb(), idSelectionne);
            loc.setDateRetour(LocalDateTime.now());
            loc.update();
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur retour : " + e.getMessage()); }
    }

    private void supprimer() {
        if (idSelectionne < 0) { controller.afficherErreur("Sélectionnez un emprunt."); return; }
        int c = JOptionPane.showConfirmDialog(this,
                "Supprimer l'emprunt id=" + idSelectionne + " et ses paiements liés ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            new M_Location(controller.getDb(), idSelectionne).delete();
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur suppression : " + e.getMessage()); }
    }

    private void vider() {
        txtIdExemplaire.setText(""); txtPrixEmprunt.setText(""); txtCommentaire.setText("");
        idSelectionne = -1; lblIdSelectionne.setText("Aucun emprunt sélectionné");
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
