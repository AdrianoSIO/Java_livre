package pj_livre.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import pj_livre.Controllers.C_livre;
import pj_livre.Models.M_User;

public class V_Users extends V_Base {

    private JTable            tbl;
    private DefaultTableModel model;
    private JTextField txtNom         = new JTextField(15);
    private JTextField txtPrenom      = new JTextField(15);
    private JTextField txtName        = new JTextField(15);
    private JTextField txtEmail       = new JTextField(15);
    private JTextField txtPassword    = new JTextField(15);
    private JTextField txtIdRole      = new JTextField(15);
    private JTextField txtCommentaire = new JTextField(15);
    private JButton    btnAjouter     = new JButton("Ajouter");
    private JButton    btnModifier    = new JButton("Modifier");
    private JButton    btnSupprimer   = new JButton("Supprimer");
    private JButton    btnVider       = new JButton("Vider");
    private int        idSel          = -1;

    public V_Users(C_livre controller) {
        super(controller, "Utilisateurs");

        model = new DefaultTableModel(new String[]{"ID","Nom","Prénom","Email","Rôle"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl = new JTable(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) selectionner(); });

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Détail"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3,5,3,5); c.fill = GridBagConstraints.HORIZONTAL;
        ajouterChamp(form, c, "Nom :",          txtNom,         0);
        ajouterChamp(form, c, "Prénom :",       txtPrenom,      1);
        ajouterChamp(form, c, "Username :",     txtName,        2);
        ajouterChamp(form, c, "Email :",        txtEmail,       3);
        ajouterChamp(form, c, "Password :",     txtPassword,    4);
        ajouterChamp(form, c, "ID Rôle :",      txtIdRole,      5);
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

        setMinimumSize(new Dimension(800, 450));
        pack();
        setLocationRelativeTo(null);
    }

    public void chargerDonnees() {
        model.setRowCount(0);
        try {
            LinkedHashMap<Integer, M_User> users = M_User.getRecords(controller.getDb());
            for (M_User u : users.values())
                model.addRow(new Object[]{u.getId(), u.getNom(), u.getPrenom(), u.getEmail(), u.getIdRole()});
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void selectionner() {
        int row = tbl.getSelectedRow(); if (row < 0) return;
        idSel = (int) model.getValueAt(row, 0);
        try {
            M_User u = new M_User(controller.getDb(), idSel);
            txtNom.setText(u.getNom()); txtPrenom.setText(u.getPrenom());
            txtName.setText(u.getName()); txtEmail.setText(u.getEmail());
            txtPassword.setText(u.getPassword()); txtIdRole.setText(String.valueOf(u.getIdRole()));
            txtCommentaire.setText(u.getCommentaire());
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void ajouter() {
        try {
            new M_User(controller.getDb(), Integer.parseInt(txtIdRole.getText().trim()),
                    txtNom.getText().trim(), txtPrenom.getText().trim(),
                    txtName.getText().trim(), txtEmail.getText().trim(),
                    txtPassword.getText().trim(), txtCommentaire.getText().trim());
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void modifier() {
        if (idSel < 0) { controller.afficherErreur("Sélectionnez un utilisateur."); return; }
        try {
            M_User u = new M_User(controller.getDb(), idSel);
            u.setNom(txtNom.getText().trim()); u.setPrenom(txtPrenom.getText().trim());
            u.setName(txtName.getText().trim()); u.setEmail(txtEmail.getText().trim());
            u.setPassword(txtPassword.getText().trim());
            u.setIdRole(Integer.parseInt(txtIdRole.getText().trim()));
            u.setCommentaire(txtCommentaire.getText().trim());
            u.update(); vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void supprimer() {
        if (idSel < 0) { controller.afficherErreur("Sélectionnez un utilisateur."); return; }
        if (idSel == controller.getSessionId()) { controller.afficherErreur("Impossible de supprimer votre propre compte."); return; }
        if (JOptionPane.showConfirmDialog(this, "Supprimer cet utilisateur ?", "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try { new M_User(controller.getDb(), idSel).delete(); vider(); chargerDonnees(); }
        catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void vider() {
        txtNom.setText(""); txtPrenom.setText(""); txtName.setText(""); txtEmail.setText("");
        txtPassword.setText(""); txtIdRole.setText(""); txtCommentaire.setText("");
        idSel = -1; tbl.clearSelection();
    }

    private void ajouterChamp(JPanel p, GridBagConstraints c, String label, JTextField tf, int row) {
        c.gridwidth = 1; c.gridx = 0; c.gridy = row; p.add(new JLabel(label), c);
        c.gridx = 1; p.add(tf, c);
    }
}
