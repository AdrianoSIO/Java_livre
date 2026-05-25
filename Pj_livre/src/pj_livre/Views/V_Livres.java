package pj_livre.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import pj_livre.Controllers.C_livre;
import pj_livre.Models.M_Livre;

/**
 * Vue CRUD des livres.
 * Lecture seule pour APP/FOR/ENS, CRUD complet pour GES/ADM.
 */
public class V_Livres extends JFrame {

    private C_livre controller;

    private JTable          table;
    private DefaultTableModel tableModel;
    private JTextField      txtTitre, txtAuteurs, txtIsbn, txtPrix, txtUrl, txtIdEditeur, txtCommentaire;
    private JButton         btnAjouter, btnModifier, btnSupprimer, btnVider;
    private JLabel          lblIdSelectionne;
    private int             idSelectionne = -1;

    private static final String[] COLONNES = {"ID", "Titre", "Auteurs", "ISBN", "Prix", "Éditeur"};

    public V_Livres(C_livre controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Gestion des livres");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 550));

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Table ─────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(COLONNES, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectionnerLigne();
        });
        main.add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Formulaire ────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Détail du livre"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtTitre      = ajouterChamp(form, gbc, "Titre :",       0);
        txtAuteurs    = ajouterChamp(form, gbc, "Auteurs :",     1);
        txtIsbn       = ajouterChamp(form, gbc, "ISBN :",        2);
        txtPrix       = ajouterChamp(form, gbc, "Prix :",        3);
        txtIdEditeur  = ajouterChamp(form, gbc, "ID Éditeur :",  4);
        txtUrl        = ajouterChamp(form, gbc, "URL :",         5);
        txtCommentaire= ajouterChamp(form, gbc, "Commentaire :", 6);

        lblIdSelectionne = new JLabel("Aucun livre sélectionné");
        lblIdSelectionne.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        form.add(lblIdSelectionne, gbc);

        // ── Boutons CRUD ──────────────────────────────────────────────────
        boolean peutGerer = controller.peutGerer();

        btnAjouter   = new JButton("Ajouter");
        btnModifier  = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnVider     = new JButton("Vider");

        btnAjouter.setEnabled(peutGerer);
        btnModifier.setEnabled(peutGerer);
        btnSupprimer.setEnabled(peutGerer);

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panelBtns.add(btnVider);
        panelBtns.add(btnAjouter);
        panelBtns.add(btnModifier);
        panelBtns.add(btnSupprimer);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        form.add(panelBtns, gbc);

        main.add(form, BorderLayout.EAST);

        // ── Actions ───────────────────────────────────────────────────────
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
            LinkedHashMap<Integer, M_Livre> livres = M_Livre.getRecords(controller.getDb());
            for (M_Livre l : livres.values()) {
                tableModel.addRow(new Object[]{
                    l.getId(), l.getTitre(), l.getAuteurs(),
                    l.getCodeIsbn(), l.getPrixAchat(), l.getIdEditeur()
                });
            }
        } catch (Exception e) {
            controller.afficherErreur("Erreur chargement livres : " + e.getMessage());
        }
    }

    private void selectionnerLigne() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        idSelectionne = (int) tableModel.getValueAt(row, 0);
        try {
            M_Livre l = new M_Livre(controller.getDb(), idSelectionne);
            txtTitre.setText(l.getTitre());
            txtAuteurs.setText(l.getAuteurs());
            txtIsbn.setText(l.getCodeIsbn());
            txtPrix.setText(String.valueOf(l.getPrixAchat()));
            txtIdEditeur.setText(String.valueOf(l.getIdEditeur()));
            txtUrl.setText(l.getUrlLivre());
            txtCommentaire.setText(l.getCommentaire());
            lblIdSelectionne.setText("ID sélectionné : " + idSelectionne);
        } catch (Exception e) {
            controller.afficherErreur("Erreur lecture livre : " + e.getMessage());
        }
    }

    private void ajouter() {
        try {
            new M_Livre(controller.getDb(),
                    Integer.parseInt(txtIdEditeur.getText().trim()),
                    txtTitre.getText().trim(),
                    txtAuteurs.getText().trim(),
                    txtUrl.getText().trim(),
                    txtIsbn.getText().trim(),
                    txtCommentaire.getText().trim(),
                    Float.parseFloat(txtPrix.getText().trim()));
            vider();
            chargerDonnees();
        } catch (Exception e) {
            controller.afficherErreur("Erreur ajout livre : " + e.getMessage());
        }
    }

    private void modifier() {
        if (idSelectionne < 0) { controller.afficherErreur("Sélectionnez un livre."); return; }
        try {
            M_Livre l = new M_Livre(controller.getDb(), idSelectionne);
            l.setTitre(txtTitre.getText().trim());
            l.setAuteurs(txtAuteurs.getText().trim());
            l.setCodeIsbn(txtIsbn.getText().trim());
            l.setPrixAchat(Float.parseFloat(txtPrix.getText().trim()));
            l.setIdEditeur(Integer.parseInt(txtIdEditeur.getText().trim()));
            l.setUrlLivre(txtUrl.getText().trim());
            l.setCommentaire(txtCommentaire.getText().trim());
            l.update();
            vider();
            chargerDonnees();
        } catch (Exception e) {
            controller.afficherErreur("Erreur modification livre : " + e.getMessage());
        }
    }

    private void supprimer() {
        if (idSelectionne < 0) { controller.afficherErreur("Sélectionnez un livre."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer le livre id=" + idSelectionne + " et tous ses exemplaires ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            M_Livre l = new M_Livre(controller.getDb(), idSelectionne);
            l.delete();
            vider();
            chargerDonnees();
        } catch (Exception e) {
            controller.afficherErreur("Erreur suppression livre : " + e.getMessage());
        }
    }

    private void vider() {
        txtTitre.setText(""); txtAuteurs.setText(""); txtIsbn.setText("");
        txtPrix.setText(""); txtIdEditeur.setText(""); txtUrl.setText("");
        txtCommentaire.setText("");
        idSelectionne = -1;
        lblIdSelectionne.setText("Aucun livre sélectionné");
        table.clearSelection();
    }

    private JTextField ajouterChamp(JPanel p, GridBagConstraints gbc, String label, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        p.add(new JLabel(label), gbc);
        JTextField tf = new JTextField(18);
        gbc.gridx = 1;
        p.add(tf, gbc);
        return tf;
    }
}
