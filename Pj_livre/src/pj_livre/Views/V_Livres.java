package pj_livre.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import pj_livre.Controllers.C_livre;
import pj_livre.Models.M_Livre;

public class V_Livres extends V_Base {

    private JTable            tbl;
    private DefaultTableModel model;
    private JTextField txtTitre      = new JTextField(15);
    private JTextField txtAuteurs    = new JTextField(15);
    private JTextField txtIsbn       = new JTextField(15);
    private JTextField txtPrix       = new JTextField(15);
    private JTextField txtIdEditeur  = new JTextField(15);
    private JTextField txtUrl        = new JTextField(15);
    private JTextField txtCommentaire= new JTextField(15);
    private JButton    btnAjouter    = new JButton("Ajouter");
    private JButton    btnModifier   = new JButton("Modifier");
    private JButton    btnSupprimer  = new JButton("Supprimer");
    private JButton    btnVider      = new JButton("Vider");
    private int        idSel         = -1;

    public V_Livres(C_livre controller) {
        super(controller, "Livres");

        boolean peutGerer = controller.peutGerer();
        btnAjouter.setEnabled(peutGerer);
        btnModifier.setEnabled(peutGerer);
        btnSupprimer.setEnabled(peutGerer);

        // Table
        model = new DefaultTableModel(new String[]{"ID","Titre","Auteurs","ISBN","Prix","Éditeur"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl = new JTable(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) selectionner(); });

        // Formulaire
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Détail"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3,5,3,5); c.fill = GridBagConstraints.HORIZONTAL;
        ajouterChamp(form, c, "Titre :",        txtTitre,       0);
        ajouterChamp(form, c, "Auteurs :",      txtAuteurs,     1);
        ajouterChamp(form, c, "ISBN :",         txtIsbn,        2);
        ajouterChamp(form, c, "Prix :",         txtPrix,        3);
        ajouterChamp(form, c, "ID Éditeur :",   txtIdEditeur,   4);
        ajouterChamp(form, c, "URL :",          txtUrl,         5);
        ajouterChamp(form, c, "Commentaire :",  txtCommentaire, 6);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        btns.add(btnVider); btns.add(btnAjouter); btns.add(btnModifier); btns.add(btnSupprimer);
        c.gridx = 0; c.gridy = 7; c.gridwidth = 2; form.add(btns, c);

        panelContenu.setLayout(new BorderLayout(8, 0));
        panelContenu.add(new JScrollPane(tbl), BorderLayout.CENTER);
        panelContenu.add(form, BorderLayout.EAST);

        btnVider.addActionListener(    e -> vider());
        btnAjouter.addActionListener(  e -> ajouter());
        btnModifier.addActionListener( e -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());

        setMinimumSize(new Dimension(850, 480));
        pack();
        setLocationRelativeTo(null);
    }

    public void chargerDonnees() {
        model.setRowCount(0);
        try {
            LinkedHashMap<Integer, M_Livre> livres = M_Livre.getRecords(controller.getDb());
            for (M_Livre l : livres.values())
                model.addRow(new Object[]{l.getId(),l.getTitre(),l.getAuteurs(),l.getCodeIsbn(),l.getPrixAchat(),l.getIdEditeur()});
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void selectionner() {
        int row = tbl.getSelectedRow(); if (row < 0) return;
        idSel = (int) model.getValueAt(row, 0);
        try {
            M_Livre l = new M_Livre(controller.getDb(), idSel);
            txtTitre.setText(l.getTitre()); txtAuteurs.setText(l.getAuteurs());
            txtIsbn.setText(l.getCodeIsbn()); txtPrix.setText(String.valueOf(l.getPrixAchat()));
            txtIdEditeur.setText(String.valueOf(l.getIdEditeur()));
            txtUrl.setText(l.getUrlLivre()); txtCommentaire.setText(l.getCommentaire());
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void ajouter() {
        try {
            new M_Livre(controller.getDb(), Integer.parseInt(txtIdEditeur.getText().trim()),
                    txtTitre.getText().trim(), txtAuteurs.getText().trim(),
                    txtUrl.getText().trim(), txtIsbn.getText().trim(),
                    txtCommentaire.getText().trim(), Float.parseFloat(txtPrix.getText().trim()));
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void modifier() {
        if (idSel < 0) { controller.afficherErreur("Sélectionnez un livre."); return; }
        try {
            M_Livre l = new M_Livre(controller.getDb(), idSel);
            l.setTitre(txtTitre.getText().trim()); l.setAuteurs(txtAuteurs.getText().trim());
            l.setCodeIsbn(txtIsbn.getText().trim()); l.setPrixAchat(Float.parseFloat(txtPrix.getText().trim()));
            l.setIdEditeur(Integer.parseInt(txtIdEditeur.getText().trim()));
            l.setUrlLivre(txtUrl.getText().trim()); l.setCommentaire(txtCommentaire.getText().trim());
            l.update(); vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void supprimer() {
        if (idSel < 0) { controller.afficherErreur("Sélectionnez un livre."); return; }
        if (JOptionPane.showConfirmDialog(this, "Supprimer ce livre et ses exemplaires ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try { new M_Livre(controller.getDb(), idSel).delete(); vider(); chargerDonnees(); }
        catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void vider() {
        txtTitre.setText(""); txtAuteurs.setText(""); txtIsbn.setText("");
        txtPrix.setText(""); txtIdEditeur.setText(""); txtUrl.setText("");
        txtCommentaire.setText(""); idSel = -1; tbl.clearSelection();
    }

    private void ajouterChamp(JPanel p, GridBagConstraints c, String label, JTextField tf, int row) {
        c.gridwidth = 1; c.gridx = 0; c.gridy = row; p.add(new JLabel(label), c);
        c.gridx = 1; p.add(tf, c);
    }
}
