package pj_livre.Views;

import javax.swing.*;
import java.awt.*;
import pj_livre.Controllers.C_livre;

public class V_Main extends JDialog {

    private C_livre controller;
    private JTextField     txtIdentifiant = new JTextField(20);
    private JPasswordField txtPassword    = new JPasswordField(20);
    private JButton        btnConnecter   = new JButton("Se connecter");
    private JLabel         lblErreur      = new JLabel(" ");

    public V_Main(C_livre controller) {
        super(new JFrame(), true);
        this.controller = controller;
        setTitle("Réservation de livres scolaires — Connexion");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        buildUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill   = GridBagConstraints.HORIZONTAL;

        JLabel titre = new JLabel("Réservation de livres scolaires", SwingConstants.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 15));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        p.add(titre, c);

        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 1; p.add(new JLabel("Identifiant :"), c);
        c.gridx = 1;               p.add(txtIdentifiant, c);

        c.gridx = 0; c.gridy = 2; p.add(new JLabel("Mot de passe :"), c);
        c.gridx = 1;               p.add(txtPassword, c);

        lblErreur.setForeground(Color.RED);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        p.add(lblErreur, c);

        c.gridy = 4; p.add(btnConnecter, c);

        add(p);

        btnConnecter.addActionListener(e -> connecter());
        txtPassword.addActionListener(e -> connecter());
    }

    private void connecter() {
        String id  = txtIdentifiant.getText().trim();
        String mdp = new String(txtPassword.getPassword());
        if (id.isEmpty() || mdp.isEmpty()) { lblErreur.setText("Champs obligatoires."); return; }
        lblErreur.setText(" ");
        controller.connecter(id, mdp);
    }

    public void afficherErreur(String msg) {
        lblErreur.setText(msg);
        txtPassword.setText("");
        txtPassword.requestFocus();
    }
}
