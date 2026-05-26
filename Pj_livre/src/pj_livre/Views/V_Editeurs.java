package pj_livre.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import pj_livre.Controllers.C_livre;
import pj_livre.Models.M_Editeur;

public class V_Editeurs extends V_Base {

    private JTable            tbl;
    private DefaultTableModel model;
    private JTextField txtNom         = new JTextField(15);
    private JTextField txtUrl         = new JTextField(15);
    private JTextField txtCommentaire = new JTextField(15);
    private JButton    btnAjouter     = new JButton("Ajouter");
    private JButton    btnModifier    = new JButton("Modifier");
    private JButton    btnSupprimer   = new JButton("Supprimer");
    private JButton    btnVider       = new JButton("Vider");
    private int        idSel          = -1;

    public V_Editeurs(C_livre controller) {
        super(controller, "Éditeurs");

        model = new DefaultTableModel(new String[]{"ID","Nom","Site web","Commentaire"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl = new JTable(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) selectionner(); });

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Détail"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3,5,3,5); c.fill = GridBagConstraints.HORIZONTAL;
        ajouterChamp(form, c, "Nom :",          txtNom,          0);
        ajouterChamp(form, c, "Site web :",     txtUrl,          1);
        ajouterChamp(form, c, "Commentaire :",  txtCommentaire,  2);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        btns.add(btnVider); btns.add(btnAjouter); btns.add(btnModifier); btns.add(btnSupprimer);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; form.add(btns, c);

        panelContenu.setLayout(new BorderLayout(8, 0));
        panelContenu.add(new JScrollPane(tbl), BorderLayout.CENTER);
        panelContenu.add(form, BorderLayout.EAST);

        btnVider.addActionListener(    e -> vider());
        btnAjouter.addActionListener(  e -> ajouter());
        btnModifier.addActionListener( e -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());

        setMinimumSize(new Dimension(700, 400));
        pack();
        setLocationRelativeTo(null);
    }

    public void chargerDonnees() {
        model.setRowCount(0);
        try {
            LinkedHashMap<Integer, M_Editeur> eds = M_Editeur.getRecords(controller.getDb());
            for (M_Editeur ed : eds.values())
                model.addRow(new Object[]{ed.getId(), ed.getNom(), ed.getUrlSite(), ed.getCommentaire()});
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void selectionner() {
        int row = tbl.getSelectedRow(); if (row < 0) return;
        idSel = (int) model.getValueAt(row, 0);
        try {
            M_Editeur ed = new M_Editeur(controller.getDb(), idSel);
            txtNom.setText(ed.getNom()); txtUrl.setText(ed.getUrlSite()); txtCommentaire.setText(ed.getCommentaire());
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void ajouter() {
        try {
            new M_Editeur(controller.getDb(), txtNom.getText().trim(), txtUrl.getText().trim(), txtCommentaire.getText().trim());
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void modifier() {
        if (idSel < 0) { controller.afficherErreur("Sélectionnez un éditeur."); return; }
        try {
            M_Editeur ed = new M_Editeur(controller.getDb(), idSel);
            ed.setNom(txtNom.getText().trim()); ed.setUrlSite(txtUrl.getText().trim()); ed.setCommentaire(txtCommentaire.getText().trim());
            ed.update(); vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void supprimer() {
        if (idSel < 0) { controller.afficherErreur("Sélectionnez un éditeur."); return; }
        if (JOptionPane.showConfirmDialog(this, "Supprimer cet éditeur ?", "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try { new M_Editeur(controller.getDb(), idSel).delete(); vider(); chargerDonnees(); }
        catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void vider() {
        txtNom.setText(""); txtUrl.setText(""); txtCommentaire.setText(""); idSel = -1; tbl.clearSelection();
    }

    private void ajouterChamp(JPanel p, GridBagConstraints c, String label, JTextField tf, int row) {
        c.gridwidth = 1; c.gridx = 0; c.gridy = row; p.add(new JLabel(label), c);
        c.gridx = 1; p.add(tf, c);
    }
}
