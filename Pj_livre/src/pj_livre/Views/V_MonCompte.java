package pj_livre.Views;

import javax.swing.*;
import java.awt.*;
import pj_livre.Controllers.C_livre;

public class V_MonCompte extends V_Base {

    private JPasswordField txtAncien  = new JPasswordField(20);
    private JPasswordField txtNouveau = new JPasswordField(20);
    private JPasswordField txtConfirm = new JPasswordField(20);
    private JLabel         lblMsg     = new JLabel(" ");

    public V_MonCompte(C_livre controller) {
        super(controller, "Mon compte");

        panelContenu.setLayout(new GridBagLayout());
        panelContenu.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill   = GridBagConstraints.HORIZONTAL;

        // Infos compte
        JLabel lblInfo = new JLabel(controller.getSessionPrenom() + " "
                + controller.getSessionNom() + " — " + controller.getSessionEmail());
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        panelContenu.add(lblInfo, c);

        JSeparator sep = new JSeparator();
        c.gridy = 1; panelContenu.add(sep, c);

        JLabel lblTitre = new JLabel("Changer le mot de passe");
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 13));
        c.gridy = 2; panelContenu.add(lblTitre, c);

        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 3; panelContenu.add(new JLabel("Ancien mot de passe :"), c);
        c.gridx = 1;               panelContenu.add(txtAncien, c);

        c.gridx = 0; c.gridy = 4; panelContenu.add(new JLabel("Nouveau mot de passe :"), c);
        c.gridx = 1;               panelContenu.add(txtNouveau, c);

        c.gridx = 0; c.gridy = 5; panelContenu.add(new JLabel("Confirmer :"), c);
        c.gridx = 1;               panelContenu.add(txtConfirm, c);

        lblMsg.setForeground(Color.RED);
        c.gridx = 0; c.gridy = 6; c.gridwidth = 2;
        panelContenu.add(lblMsg, c);

        JButton btnValider = new JButton("Valider");
        c.gridy = 7; panelContenu.add(btnValider, c);

        btnValider.addActionListener(e -> changerMotDePasse());

        pack();
        setLocationRelativeTo(null);
    }

    private void changerMotDePasse() {
        String ancien  = new String(txtAncien.getPassword());
        String nouveau = new String(txtNouveau.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (ancien.isEmpty() || nouveau.isEmpty() || confirm.isEmpty()) {
            affMsg("Tous les champs sont obligatoires.", Color.RED); return;
        }
        if (!nouveau.equals(confirm)) {
            affMsg("Les mots de passe ne correspondent pas.", Color.RED); return;
        }
        if (nouveau.length() < 6) {
            affMsg("Le mot de passe doit faire au moins 6 caractères.", Color.RED); return;
        }

        boolean ok = controller.changerMotDePasse(ancien, nouveau);
        if (ok) {
            affMsg("Mot de passe modifié avec succès.", new Color(0, 128, 0));
            txtAncien.setText(""); txtNouveau.setText(""); txtConfirm.setText("");
        } else {
            affMsg("Ancien mot de passe incorrect.", Color.RED);
        }
    }

    private void affMsg(String msg, Color couleur) {
        lblMsg.setForeground(couleur);
        lblMsg.setText(msg);
    }
}
