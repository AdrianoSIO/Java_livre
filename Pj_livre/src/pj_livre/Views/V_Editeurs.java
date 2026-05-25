package pj_livre.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import pj_livre.Controllers.C_livre;
import pj_livre.Models.M_Editeur;

public class V_Editeurs extends JFrame {

    private C_livre controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtUrl, txtCommentaire;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnVider;
    private JLabel lblIdSelectionne;
    private int idSelectionne = -1;

    private static final String[] COLONNES = {"ID", "Nom", "Site web", "Commentaire"};

    public V_Editeurs(C_livre controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Gestion des éditeurs");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(750, 400));

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

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Détail éditeur"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtNom        = ajouterChamp(form, gbc, "Nom :",         0);
        txtUrl        = ajouterChamp(form, gbc, "Site web :",    1);
        txtCommentaire= ajouterChamp(form, gbc, "Commentaire :", 2);

        lblIdSelectionne = new JLabel("Aucun éditeur sélectionné");
        lblIdSelectionne.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(lblIdSelectionne, gbc);

        btnAjouter   = new JButton("Ajouter");
        btnModifier  = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnVider     = new JButton("Vider");

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panelBtns.add(btnVider); panelBtns.add(btnAjouter);
        panelBtns.add(btnModifier); panelBtns.add(btnSupprimer);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        form.add(panelBtns, gbc);

        main.add(form, BorderLayout.EAST);

        btnVider.addActionListener(    e -> vider());
        btnAjouter.addActionListener(  e -> ajouter());
        btnModifier.addActionListener( e -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());

        add(main);
        pack();
        setLocationRelativeTo(null);
    }

    public void chargerDonnees() {
        tableModel.setRowCount(0);
        try {
            LinkedHashMap<Integer, M_Editeur> eds = M_Editeur.getRecords(controller.getDb());
            for (M_Editeur ed : eds.values()) {
                tableModel.addRow(new Object[]{ed.getId(), ed.getNom(), ed.getUrlSite(), ed.getCommentaire()});
            }
        } catch (Exception e) {
            controller.afficherErreur("Erreur chargement éditeurs : " + e.getMessage());
        }
    }

    private void selectionnerLigne() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        idSelectionne = (int) tableModel.getValueAt(row, 0);
        try {
            M_Editeur ed = new M_Editeur(controller.getDb(), idSelectionne);
            txtNom.setText(ed.getNom());
            txtUrl.setText(ed.getUrlSite());
            txtCommentaire.setText(ed.getCommentaire());
            lblIdSelectionne.setText("ID sélectionné : " + idSelectionne);
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void ajouter() {
        try {
            new M_Editeur(controller.getDb(),
                    txtNom.getText().trim(), txtUrl.getText().trim(), txtCommentaire.getText().trim());
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur ajout : " + e.getMessage()); }
    }

    private void modifier() {
        if (idSelectionne < 0) { controller.afficherErreur("Sélectionnez un éditeur."); return; }
        try {
            M_Editeur ed = new M_Editeur(controller.getDb(), idSelectionne);
            ed.setNom(txtNom.getText().trim());
            ed.setUrlSite(txtUrl.getText().trim());
            ed.setCommentaire(txtCommentaire.getText().trim());
            ed.update(); vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur modification : " + e.getMessage()); }
    }

    private void supprimer() {
        if (idSelectionne < 0) { controller.afficherErreur("Sélectionnez un éditeur."); return; }
        int c = JOptionPane.showConfirmDialog(this, "Supprimer l'éditeur id=" + idSelectionne + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            new M_Editeur(controller.getDb(), idSelectionne).delete();
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur suppression : " + e.getMessage()); }
    }

    private void vider() {
        txtNom.setText(""); txtUrl.setText(""); txtCommentaire.setText("");
        idSelectionne = -1; lblIdSelectionne.setText("Aucun éditeur sélectionné");
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
