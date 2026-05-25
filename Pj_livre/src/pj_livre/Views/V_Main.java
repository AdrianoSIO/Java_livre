package pj_livre.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import pj_livre.Controllers.C_livre;

/**
 * Vue de connexion — point d'entrée de l'application.
 */
public class V_Main extends JFrame {

    private C_livre controller;

    // Composants
    private JTextField  txtEmail;
    private JPasswordField txtPassword;
    private JButton     btnConnecter;
    private JLabel      lblTitre;
    private JLabel      lblEmail;
    private JLabel      lblPassword;
    private JLabel      lblErreur;

    public V_Main(C_livre controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Bibliothèque — Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // ── Panel principal ──────────────────────────────────────────────
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Titre
        lblTitre = new JLabel("Connexion à la Bibliothèque", SwingConstants.CENTER);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitre, gbc);

        // Email
        gbc.gridwidth = 1;
        lblEmail = new JLabel("Email ou login :");
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblEmail, gbc);

        txtEmail = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(txtEmail, gbc);

        // Password
        lblPassword = new JLabel("Mot de passe :");
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(txtPassword, gbc);

        // Message erreur
        lblErreur = new JLabel("", SwingConstants.CENTER);
        lblErreur.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(lblErreur, gbc);

        // Bouton
        btnConnecter = new JButton("Se connecter");
        btnConnecter.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(btnConnecter, gbc);

        // ── Actions ──────────────────────────────────────────────────────
        ActionListener actionConnecter = e -> connecter();
        btnConnecter.addActionListener(actionConnecter);
        // Enter dans le champ password déclenche la connexion
        txtPassword.addActionListener(actionConnecter);

        add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private void connecter() {
        String email    = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            lblErreur.setText("Veuillez remplir tous les champs.");
            return;
        }
        lblErreur.setText("");
        controller.connecter(email, password);
    }

    /** Appelé par le contrôleur si les identifiants sont incorrects */
    public void afficherErreur(String msg) {
        lblErreur.setText(msg);
        txtPassword.setText("");
        txtPassword.requestFocus();
    }
}
