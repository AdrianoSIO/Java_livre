package pj_livre.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import pj_livre.Controllers.C_livre;
import pj_livre.Models.M_Exemplaire;

public class V_Exemplaires extends V_Base {

    private JTable            tbl;
    private DefaultTableModel model;
    private JTextField txtCode        = new JTextField(15);
    private JTextField txtIdLivre     = new JTextField(15);
    private JTextField txtPrixEmprunt = new JTextField(15);
    private JTextField txtPrixRetour  = new JTextField(15);
    private JTextField txtCommentaire = new JTextField(15);
    private JButton    btnAjouter     = new JButton("Ajouter");
    private JButton    btnModifier    = new JButton("Modifier");
    private JButton    btnSupprimer   = new JButton("Supprimer");
    private JButton    btnVider       = new JButton("Vider");
    private int        idSel          = -1;

    public V_Exemplaires(C_livre controller) {
        super(controller, "Exemplaires");

        model = new DefaultTableModel(new String[]{"ID","Code","ID Livre","Prix emprunt","Prix retour"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl = new JTable(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) selectionner(); });

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Détail"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3,5,3,5); c.fill = GridBagConstraints.HORIZONTAL;
        ajouterChamp(form, c, "Code :",         txtCode,        0);
        ajouterChamp(form, c, "ID Livre :",     txtIdLivre,     1);
        ajouterChamp(form, c, "Prix emprunt :", txtPrixEmprunt, 2);
        ajouterChamp(form, c, "Prix retour :",  txtPrixRetour,  3);
        ajouterChamp(form, c, "Commentaire :",  txtCommentaire, 4);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        btns.add(btnVider); btns.add(btnAjouter); btns.add(btnModifier); btns.add(btnSupprimer);
        c.gridx = 0; c.gridy = 5; c.gridwidth = 2; form.add(btns, c);

        panelContenu.setLayout(new BorderLayout(8, 0));
        panelContenu.add(new JScrollPane(tbl), BorderLayout.CENTER);
        panelContenu.add(form, BorderLayout.EAST);

        btnVider.addActionListener(    e -> vider());
        btnAjouter.addActionListener(  e -> ajouter());
        btnModifier.addActionListener( e -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());

        setMinimumSize(new Dimension(750, 420));
        pack();
        setLocationRelativeTo(null);
    }

    public void chargerDonnees() {
        model.setRowCount(0);
        try {
            LinkedHashMap<Integer, M_Exemplaire> exs = M_Exemplaire.getRecords(controller.getDb());
            for (M_Exemplaire ex : exs.values())
                model.addRow(new Object[]{ex.getId(), ex.getCode(), ex.getIdLivre(), ex.getPrixEmprunt(), ex.getPrixRetour()});
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void selectionner() {
        int row = tbl.getSelectedRow(); if (row < 0) return;
        idSel = (int) model.getValueAt(row, 0);
        try {
            M_Exemplaire ex = new M_Exemplaire(controller.getDb(), idSel);
            txtCode.setText(ex.getCode()); txtIdLivre.setText(String.valueOf(ex.getIdLivre()));
            txtPrixEmprunt.setText(String.valueOf(ex.getPrixEmprunt()));
            txtPrixRetour.setText(ex.getPrixRetour()); txtCommentaire.setText(ex.getCommentaire());
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void ajouter() {
        try {
            new M_Exemplaire(controller.getDb(), Integer.parseInt(txtIdLivre.getText().trim()),
                    txtCode.getText().trim(), Float.parseFloat(txtPrixEmprunt.getText().trim()),
                    txtPrixRetour.getText().trim(), txtCommentaire.getText().trim());
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void modifier() {
        if (idSel < 0) { controller.afficherErreur("Sélectionnez un exemplaire."); return; }
        try {
            M_Exemplaire ex = new M_Exemplaire(controller.getDb(), idSel);
            ex.setCode(txtCode.getText().trim()); ex.setIdLivre(Integer.parseInt(txtIdLivre.getText().trim()));
            ex.setPrixEmprunt(Float.parseFloat(txtPrixEmprunt.getText().trim()));
            ex.setPrixRetour(txtPrixRetour.getText().trim()); ex.setCommentaire(txtCommentaire.getText().trim());
            ex.update(); vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void supprimer() {
        if (idSel < 0) { controller.afficherErreur("Sélectionnez un exemplaire."); return; }
        if (JOptionPane.showConfirmDialog(this, "Supprimer cet exemplaire ?", "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try { new M_Exemplaire(controller.getDb(), idSel).delete(); vider(); chargerDonnees(); }
        catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void vider() {
        txtCode.setText(""); txtIdLivre.setText(""); txtPrixEmprunt.setText("");
        txtPrixRetour.setText(""); txtCommentaire.setText(""); idSel = -1; tbl.clearSelection();
    }

    private void ajouterChamp(JPanel p, GridBagConstraints c, String label, JTextField tf, int row) {
        c.gridwidth = 1; c.gridx = 0; c.gridy = row; p.add(new JLabel(label), c);
        c.gridx = 1; p.add(tf, c);
    }
}
