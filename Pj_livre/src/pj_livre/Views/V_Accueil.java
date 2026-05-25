package pj_livre.Views;

import javax.swing.*;
import java.awt.*;
import pj_livre.Controllers.C_livre;

/**
 * Tableau de bord affiché après connexion.
 * Les boutons disponibles dépendent du rôle de l'utilisateur connecté.
 */
public class V_Accueil extends JFrame {

    private C_livre controller;

    public V_Accueil(C_livre controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Bibliothèque — Accueil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 350));

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ── En-tête ───────────────────────────────────────────────────────
        JLabel lblBienvenue = new JLabel(
                "Bonjour, " + controller.getSessionPrenom()
                + " " + controller.getSessionNom()
                + "  [" + controller.getSessionRoleNom() + "]",
                SwingConstants.CENTER);
        lblBienvenue.setFont(new Font("SansSerif", Font.BOLD, 15));
        main.add(lblBienvenue, BorderLayout.NORTH);

        // ── Boutons de navigation ─────────────────────────────────────────
        JPanel panelBoutons = new JPanel(new GridLayout(0, 1, 8, 8));

        // Tout le monde
        ajouterBouton(panelBoutons, "📚  Livres",       () -> controller.ouvrirLivres());
        ajouterBouton(panelBoutons, "📖  Mes emprunts", () -> controller.ouvrirLocations());

        // Gestionnaire + Admin
        if (controller.peutGerer()) {
            ajouterBouton(panelBoutons, "🏢  Éditeurs",    () -> controller.ouvrirEditeurs());
            ajouterBouton(panelBoutons, "📋  Exemplaires", () -> controller.ouvrirExemplaires());
            ajouterBouton(panelBoutons, "💳  Paiements",   () -> controller.ouvrirPaiements());
        }

        // Admin uniquement
        if (controller.estAdmin()) {
            ajouterBouton(panelBoutons, "👥  Utilisateurs", () -> controller.ouvrirUsers());
        }

        main.add(panelBoutons, BorderLayout.CENTER);

        // ── Déconnexion ───────────────────────────────────────────────────
        JButton btnDeco = new JButton("Se déconnecter");
        btnDeco.setForeground(Color.RED);
        btnDeco.addActionListener(e -> controller.deconnecter());
        main.add(btnDeco, BorderLayout.SOUTH);

        add(main);
        pack();
        setLocationRelativeTo(null);
    }

    private void ajouterBouton(JPanel panel, String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.addActionListener(e -> action.run());
        panel.add(btn);
    }
}
