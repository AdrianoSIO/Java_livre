package pj_livre.Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import pj_livre.Controllers.C_livre;
import pj_livre.Models.M_User;

public class V_Users extends JFrame {

    private C_livre controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNom, txtPrenom, txtName, txtEmail, txtPassword, txtIdRole, txtCommentaire;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnVider;
    private JLabel lblIdSelectionne;
    private int idSelectionne = -1;

    private static final String[] COLONNES = {"ID", "Nom", "Prénom", "Email", "Rôle"};

    public V_Users(C_livre controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Gestion des utilisateurs  [ADMIN]");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800, 450));

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
        form.setBorder(BorderFactory.createTitledBorder("Détail utilisateur"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtNom         = ajouterChamp(form, gbc, "Nom :",          0);
        txtPrenom      = ajouterChamp(form, gbc, "Prénom :",       1);
        txtName        = ajouterChamp(form, gbc, "Username :",     2);
        txtEmail       = ajouterChamp(form, gbc, "Email :",        3);
        txtPassword    = ajouterChamp(form, gbc, "Password :",     4);
        txtIdRole      = ajouterChamp(form, gbc, "ID Rôle :",      5);
        txtCommentaire = ajouterChamp(form, gbc, "Commentaire :",  6);

        lblIdSelectionne = new JLabel("Aucun utilisateur sélectionné");
        lblIdSelectionne.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        form.add(lblIdSelectionne, gbc);

        btnAjouter   = new JButton("Ajouter");
        btnModifier  = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnVider     = new JButton("Vider");

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panelBtns.add(btnVider); panelBtns.add(btnAjouter);
        panelBtns.add(btnModifier); panelBtns.add(btnSupprimer);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
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
            LinkedHashMap<Integer, M_User> users = M_User.getRecords(controller.getDb());
            for (M_User u : users.values()) {
                tableModel.addRow(new Object[]{u.getId(), u.getNom(), u.getPrenom(), u.getEmail(), u.getIdRole()});
            }
        } catch (Exception e) { controller.afficherErreur("Erreur chargement users : " + e.getMessage()); }
    }

    private void selectionnerLigne() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        idSelectionne = (int) tableModel.getValueAt(row, 0);
        try {
            M_User u = new M_User(controller.getDb(), idSelectionne);
            txtNom.setText(u.getNom()); txtPrenom.setText(u.getPrenom());
            txtName.setText(u.getName()); txtEmail.setText(u.getEmail());
            txtPassword.setText(u.getPassword());
            txtIdRole.setText(String.valueOf(u.getIdRole()));
            txtCommentaire.setText(u.getCommentaire());
            lblIdSelectionne.setText("ID sélectionné : " + idSelectionne);
        } catch (Exception e) { controller.afficherErreur(e.getMessage()); }
    }

    private void ajouter() {
        try {
            new M_User(controller.getDb(),
                    Integer.parseInt(txtIdRole.getText().trim()),
                    txtNom.getText().trim(), txtPrenom.getText().trim(),
                    txtName.getText().trim(), txtEmail.getText().trim(),
                    txtPassword.getText().trim(), txtCommentaire.getText().trim());
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur ajout : " + e.getMessage()); }
    }

    private void modifier() {
        if (idSelectionne < 0) { controller.afficherErreur("Sélectionnez un utilisateur."); return; }
        try {
            M_User u = new M_User(controller.getDb(), idSelectionne);
            u.setNom(txtNom.getText().trim()); u.setPrenom(txtPrenom.getText().trim());
            u.setName(txtName.getText().trim()); u.setEmail(txtEmail.getText().trim());
            u.setPassword(txtPassword.getText().trim());
            u.setIdRole(Integer.parseInt(txtIdRole.getText().trim()));
            u.setCommentaire(txtCommentaire.getText().trim());
            u.update(); vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur modification : " + e.getMessage()); }
    }

    private void supprimer() {
        if (idSelectionne < 0) { controller.afficherErreur("Sélectionnez un utilisateur."); return; }
        // Sécurité : on ne peut pas supprimer le compte connecté
        if (idSelectionne == controller.getSessionId()) {
            controller.afficherErreur("Vous ne pouvez pas supprimer votre propre compte.");
            return;
        }
        int c = JOptionPane.showConfirmDialog(this, "Supprimer l'utilisateur id=" + idSelectionne + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            new M_User(controller.getDb(), idSelectionne).delete();
            vider(); chargerDonnees();
        } catch (Exception e) { controller.afficherErreur("Erreur suppression : " + e.getMessage()); }
    }

    private void vider() {
        txtNom.setText(""); txtPrenom.setText(""); txtName.setText("");
        txtEmail.setText(""); txtPassword.setText(""); txtIdRole.setText("");
        txtCommentaire.setText("");
        idSelectionne = -1; lblIdSelectionne.setText("Aucun utilisateur sélectionné");
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
