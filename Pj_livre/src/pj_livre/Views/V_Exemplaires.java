package pj_livre.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import pj_livre.Controllers.C_livre;
import pj_livre.Models.M_Exemplaire;

public class V_Exemplaires extends JFrame {

    private C_livre controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtCode, txtIdLivre, txtPrixEmprunt, txtPrixRetour, txtCommentaire;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnVider;
    private JLabel lblIdSelectionne;
    private int idSelectionne = -1;

    private static final String[] COLONNES = {"ID", "Code", "ID Livre", "Prix emprunt", "Prix retour"};

    public V_Exemplaires(C_livre controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Gestion des exemplaires");
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
        form.setBorder(BorderFactory.createTitledBorder("Détail exemplaire"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtCode        = ajouterChamp(form, gbc, "Code :",          0);
        txtIdLivre     = ajouterChamp(form, gbc, "ID Livre :",      1);
        txtPrixEmprunt = ajouterChamp(form, gbc, "Prix emprunt :",  2);
        txtPrixRetour  = ajouterChamp(form, gbc, "Prix retour :",   3);
        txtCommentaire = ajouterChamp(form, gbc, "Commentaire :",   4);

        lblIdSelectionne = new JLabel("Aucun exemplaire sélectionné");
        lblIdSelectionne.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        form.add(lblIdSelectionne, gbc);

        btnAjouter   = new JButton("Ajouter");
        btnModifier  = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnVider     = new JButton("Vider");

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panelBtns.add(btnVider); panelBtns.add(btnAjouter);
        panelBtns.add(btnModifier); panelBtns.add(btnSupprimer);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
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
            LinkedHashMap<Integer, M_Exemplaire> exs = M_Exemplaire.getRecords(controller.getDb());
            for (M_Exemplaire ex : exs.values()) {
                tableModel.addRow(new Object[]{
                    ex.getId(), ex.getCode(), ex.getIdLivre(),
                    ex.getPrixEmprunt(), ex.getPrixRetour()
                });
            }
        } catch (Exception e) { controller.afficherErreur("Erreur chargement exemplaires : " + e.getMessage()); }
    }

    private void selectionnerLigne() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        idSelectionne = (int) tableModel.getValueAt(row, 0);
        try {
            M_Exemplaire ex = new M_Exemplaire(controller.getDb(), idSelectionne);
            txtCode.setText(ex.getCode());
            txtIdLivre.setText(String.valueOf(ex.getIdLivre()));
            txtPrixEmprunt.setText(String.valueOf(ex.getPrixEmprunt()));
            txtPrixRetour.setText(ex.getPrixRetour());
            txtCommentaire.setText(ex.getCommentaire());
            lblIdSelectionne.setText("ID sélectionné : " + idSelectionne);
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void ajouter() {
        try {
            new M_Exemplaire(controller.getDb(),
                    Integer.parseInt(txtIdLivre.getText().trim()),
                    txtCode.getText().trim(),
                    Float.parseFloat(txtPrixEmprunt.getText().trim()),
                    txtPrixRetour.getText().trim(),
                    txtCommentaire.getText().trim());
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur ajout : " + e.getMessage()); }
    }

    private void modifier() {
        if (idSelectionne < 0) { controller.afficherErreur("Sélectionnez un exemplaire."); return; }
        try {
            M_Exemplaire ex = new M_Exemplaire(controller.getDb(), idSelectionne);
            ex.setCode(txtCode.getText().trim());
            ex.setIdLivre(Integer.parseInt(txtIdLivre.getText().trim()));
            ex.setPrixEmprunt(Float.parseFloat(txtPrixEmprunt.getText().trim()));
            ex.setPrixRetour(txtPrixRetour.getText().trim());
            ex.setCommentaire(txtCommentaire.getText().trim());
            ex.update(); vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur modification : " + e.getMessage()); }
    }

    private void supprimer() {
        if (idSelectionne < 0) { controller.afficherErreur("Sélectionnez un exemplaire."); return; }
        int c = JOptionPane.showConfirmDialog(this, "Supprimer l'exemplaire id=" + idSelectionne + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            new M_Exemplaire(controller.getDb(), idSelectionne).delete();
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur suppression : " + e.getMessage()); }
    }

    private void vider() {
        txtCode.setText(""); txtIdLivre.setText(""); txtPrixEmprunt.setText("");
        txtPrixRetour.setText(""); txtCommentaire.setText("");
        idSelectionne = -1; lblIdSelectionne.setText("Aucun exemplaire sélectionné");
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
