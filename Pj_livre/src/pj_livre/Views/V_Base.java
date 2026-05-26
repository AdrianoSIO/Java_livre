package pj_livre.Views;

import javax.swing.*;
import java.awt.*;
import pj_livre.Controllers.C_livre;

/**
 * Classe de base pour toutes les vues (sauf V_Main).
 * Fournit la barre de navigation commune en haut :
 *   [Accueil]  [Mon compte]  [Quitter]
 */
public abstract class V_Base extends JDialog {

    protected C_livre controller;
    protected JPanel  panelContenu; // à remplir par chaque vue

    public V_Base(C_livre controller, String titre) {
        super(new JFrame(), false);
        this.controller = controller;
        setTitle("Livres scolaires — " + titre);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(buildNavBar(), BorderLayout.NORTH);
        panelContenu = new JPanel();
        add(panelContenu, BorderLayout.CENTER);
    }

    private JToolBar buildNavBar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JButton btnAccueil   = new JButton("Accueil");
        JButton btnMonCompte = new JButton("Mon compte");
        JButton btnQuitter   = new JButton("Quitter");

        btnAccueil.setFocusPainted(false);
        btnMonCompte.setFocusPainted(false);
        btnQuitter.setFocusPainted(false);
        btnQuitter.setForeground(Color.RED);

        btnAccueil.addActionListener(e -> {
            dispose();
            controller.ouvrirAccueil();
        });
        btnMonCompte.addActionListener(e -> controller.ouvrirMonCompte());
        btnQuitter.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                    "Quitter l'application ?", "Quitter",
                    JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) System.exit(0);
        });

        bar.add(btnAccueil);
        bar.addSeparator();
        bar.add(btnMonCompte);
        bar.add(Box.createHorizontalGlue());
        bar.add(btnQuitter);
        return bar;
    }
}