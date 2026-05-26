package pj_livre.Views;

import javax.swing.*;
import java.awt.*;
import pj_livre.Controllers.C_livre;

public class V_Accueil extends V_Base {

    public V_Accueil(C_livre controller) {
        super(controller, "Accueil");

        panelContenu.setLayout(new GridBagLayout());
        panelContenu.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill   = GridBagConstraints.HORIZONTAL;

        JLabel lblBienvenue = new JLabel(
                "Bonjour " + controller.getSessionPrenom()
                + " " + controller.getSessionNom()
                + "  [" + controller.getSessionRoleNom() + "]",
                SwingConstants.CENTER);
        lblBienvenue.setFont(new Font("SansSerif", Font.BOLD, 14));
        c.gridx = 0; c.gridy = 0; p(panelContenu, lblBienvenue, c);

        // Tout le monde
        p(panelContenu, bouton("Livres",        () -> controller.ouvrirLivres()),    c, 1);
        p(panelContenu, bouton("Mes emprunts",  () -> controller.ouvrirLocations()), c, 2);

        if (controller.peutGerer()) {
            p(panelContenu, bouton("Editeurs",    () -> controller.ouvrirEditeurs()),    c, 3);
            p(panelContenu, bouton("Exemplaires", () -> controller.ouvrirExemplaires()), c, 4);
            p(panelContenu, bouton("Paiements",   () -> controller.ouvrirPaiements()),   c, 5);
        }

        if (controller.estAdmin()) {
            p(panelContenu, bouton("Utilisateurs", () -> controller.ouvrirUsers()), c, 6);
        }

        JButton btnDeco = bouton("Se déconnecter", () -> controller.deconnecter());
        btnDeco.setForeground(Color.RED);
        p(panelContenu, btnDeco, c, 7);

        pack();
        setMinimumSize(new Dimension(320, 200));
        setLocationRelativeTo(null);
    }

    private JButton bouton(String label, Runnable action) {
        JButton b = new JButton(label);
        b.addActionListener(e -> action.run());
        return b;
    }

    private void p(JPanel panel, JComponent comp, GridBagConstraints c, int row) {
        c.gridy = row; panel.add(comp, c);
    }
    private void p(JPanel panel, JComponent comp, GridBagConstraints c) {
        panel.add(comp, c);
    }
}