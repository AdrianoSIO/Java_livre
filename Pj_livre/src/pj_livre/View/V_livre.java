/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package pj_livre.View;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import pj_livre.Controller.C_livre;
import pj_livre.Models.M_Livre;

/**
 *
 * @author razanateraa
 */
public class V_livre extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(V_livre.class.getName());
    //collection de M_livre
    private LinkedHashMap<Integer, M_Livre> lesLivres;
    private DefaultTableModel dm_tb_livres;
    //models
    private M_Livre unLivre;
    //controller
    private C_livre leControlleurl;
    //attribut
    private int idEditeur, id;
    private char choix;
    private int ligne;
    private float prixAchat;
    private String titre, auteurs, urlLivre, codeIsbn, commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Creates new form V_livre
     */
    public V_livre(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public void afficher(LinkedHashMap<Integer, M_Livre> lesLivres, C_livre leControlleurl) {
        this.leControlleurl = leControlleurl;
        this.lesLivres = lesLivres;
        this.setLocationRelativeTo(null);
        pn_table_livre.setBounds(50, 100, 500, 400);
        dm_tb_livres = (DefaultTableModel) tb_livre.getModel();
        dm_tb_livres.setRowCount(lesLivres.size());
        ligne = 0;
        ed_id.setVisible(false);
        for (Integer uneCle : lesLivres.keySet()) {
            unLivre = lesLivres.get(uneCle);
            tb_livre.setValueAt(unLivre.getId(), ligne, 0);
            tb_livre.setValueAt(unLivre.getTitre(), ligne, 1);
            tb_livre.setValueAt(unLivre.getCodeIsbn(), ligne, 2);
            tb_livre.setValueAt(unLivre.getPrixAchat(), ligne, 3);
            ligne++;
        }
        this.setSize(1000, 800);
        this.initIhm('T');
        this.setVisible(true);
    }

    private void initIhm(char mode) {

        choix = mode;

        // état par défaut
        pn_table_livre.setVisible(false);
        pn_champs.setVisible(true);
        bt_enregistrer.setVisible(false);
        da_create.setVisible(true);
        da_update.setVisible(true);
        ed_id.setVisible(true);
        lb_id.setVisible(true);

        switch (mode) {

            case 'T' -> {
                pn_table_livre.setVisible(true);
                pn_champs.setVisible(false);
            }

            case 'C' -> {
                lb_mode.setText("Consultation");
                bt_annuler.setText("Retour");
                da_create.setVisible(true);
                da_update.setVisible(true);
                da_create.setEnabled(false);
                da_update.setEnabled(false);
            }

            case 'A' -> {
                lb_mode.setText("Ajout");
                bt_annuler.setText("Annuler");
                bt_enregistrer.setVisible(true);
                bt_enregistrer.setText("Enregistrer");
                ed_id.setVisible(false);
                lb_id.setVisible(false);
                da_create.setVisible(true);
                da_update.setEnabled(false);
            }

            case 'M' -> {
                lb_mode.setText("Modification");
                bt_annuler.setText("Annuler");
                bt_enregistrer.setVisible(true);
                bt_enregistrer.setText("Enregistrer");
                da_update.setVisible(true);
                da_create.setVisible(true);
                da_update.setEnabled(false);
            }

            case 'S' -> {
                lb_mode.setText("Suppression");
                pn_table_livre.setVisible(true);
                pn_champs.setVisible(false);
                da_update.setEnabled(false);
            }
        }
    }

    public void enabled() {

        boolean editable = false;
        boolean vrai = true;

        // état par défaut : tout bloqué
        ed_id.setEditable(editable);
        ed_titre.setEditable(editable);
        ed_auteur.setEditable(editable);
        ed_isbn.setEditable(editable);
        ed_url.setEditable(editable);
        ed_prix.setEditable(editable);
        ed_com.setEditable(editable);
        ed_edid.setEditable(editable);
        switch (choix) {

            case 'C' -> {
                ed_id.setEditable(editable);
                ed_titre.setEditable(editable);
                ed_auteur.setEditable(editable);
                ed_isbn.setEditable(editable);
                ed_url.setEditable(editable);
                ed_prix.setEditable(editable);
                ed_com.setEditable(editable);
                ed_edid.setEditable(editable);
            }

            case 'A' -> {
                ed_titre.setEditable(vrai);
                ed_auteur.setEditable(vrai);
                ed_isbn.setEditable(vrai);
                ed_url.setEditable(vrai);
                ed_prix.setEditable(vrai);
                da_update.setEnabled(vrai);
                da_create.setEnabled(vrai);
                ed_com.setEditable(vrai);
                ed_edid.setEditable(vrai);
            }

            case 'M' -> {
                ed_titre.setEditable(vrai);
                ed_auteur.setEditable(vrai);
                ed_isbn.setEditable(vrai);
                ed_url.setEditable(vrai);
                ed_prix.setEditable(vrai);
                da_create.setEnabled(vrai);
                da_update.setEnabled(vrai);
                ed_com.setEditable(vrai);
                ed_edid.setEditable(vrai);
            }
        }
    }

    public void vider() {
        ed_titre.setText("");
        ed_id.setText("");
        ed_auteur.setText("");
        ed_isbn.setText("");
        ed_url.setText("");
        ed_prix.setText("");
        lb_mode.setText("");
        da_create.cleanup();
        da_update.cleanup();
        ed_com.setText("");
        ed_edid.setText("");
        initIhm('T');

    }

    public void sePlacer() {

        int ligne = tb_livre.getSelectedRow();
        if (ligne == -1) {
            return;
        }

        int id = (int) tb_livre.getValueAt(ligne, 0);
        unLivre = lesLivres.get(id);

        if (unLivre == null) {
            return;
        }

        ed_id.setText(String.valueOf(unLivre.getId()));
        ed_titre.setText(unLivre.getTitre());
        ed_url.setText(unLivre.getUrlLivre());
        ed_auteur.setText(unLivre.getAuteurs());
        ed_isbn.setText(unLivre.getCodeIsbn());
        ed_prix.setText(String.valueOf(unLivre.getPrixAchat()));
        ed_com.setText(unLivre.getCommentaire());
        ed_edid.setText(String.valueOf(unLivre.getIdEditeur()));
        da_create.setDate(Date.from(
                unLivre.getCreatedAt()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        ));

        da_update.setDate(Date.from(
                unLivre.getUpdatedAt()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        ));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pn_table_livre = new javax.swing.JPanel();
        sp_tb_livres = new javax.swing.JScrollPane();
        tb_livre = new javax.swing.JTable();
        bt_consulter = new javax.swing.JButton();
        bt_modifier = new javax.swing.JButton();
        bt_ajouter = new javax.swing.JButton();
        bt_supprimer = new javax.swing.JButton();
        pn_champs = new javax.swing.JPanel();
        lb_mode = new javax.swing.JLabel();
        lb_id = new javax.swing.JLabel();
        ed_id = new javax.swing.JTextField();
        lb_auteur = new javax.swing.JLabel();
        ed_auteur = new javax.swing.JTextField();
        ed_url = new javax.swing.JTextField();
        lb_url = new javax.swing.JLabel();
        lb_isbn = new javax.swing.JLabel();
        ed_isbn = new javax.swing.JTextField();
        lb_prix = new javax.swing.JLabel();
        ed_prix = new javax.swing.JTextField();
        da_create = new com.toedter.calendar.JDateChooser();
        lb_create = new javax.swing.JLabel();
        lb_update = new javax.swing.JLabel();
        da_update = new com.toedter.calendar.JDateChooser();
        bt_annuler = new javax.swing.JButton();
        bt_enregistrer = new javax.swing.JButton();
        lb_titre = new javax.swing.JLabel();
        ed_titre = new javax.swing.JTextField();
        lb_com = new javax.swing.JLabel();
        ed_com = new javax.swing.JTextField();
        lb_edid = new javax.swing.JLabel();
        ed_edid = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        mb_menu = new javax.swing.JMenuBar();
        mn_quitter = new javax.swing.JMenu();
        mi_accueil = new javax.swing.JMenu();
        mn_direction = new javax.swing.JMenu();
        mi_livre = new javax.swing.JMenuItem();
        mn_gestion = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("V_Livre");
        setBackground(new java.awt.Color(0, 0, 0));
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        setForeground(java.awt.Color.red);
        setIconImage(null);
        setIconImages(null);

        tb_livre.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "id", "Titre", "Code isbn", "prix"
            }
        ));
        sp_tb_livres.setViewportView(tb_livre);
        if (tb_livre.getColumnModel().getColumnCount() > 0) {
            tb_livre.getColumnModel().getColumn(0).setMinWidth(45);
            tb_livre.getColumnModel().getColumn(0).setPreferredWidth(40);
            tb_livre.getColumnModel().getColumn(0).setMaxWidth(50);
            tb_livre.getColumnModel().getColumn(3).setMinWidth(35);
            tb_livre.getColumnModel().getColumn(3).setPreferredWidth(60);
            tb_livre.getColumnModel().getColumn(3).setMaxWidth(80);
        }

        bt_consulter.setText("Consulter");
        bt_consulter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_consulterActionPerformed(evt);
            }
        });

        bt_modifier.setText("Modifier");
        bt_modifier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_modifierActionPerformed(evt);
            }
        });

        bt_ajouter.setText("Ajouter");
        bt_ajouter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_ajouterActionPerformed(evt);
            }
        });

        bt_supprimer.setText("Supprimer");
        bt_supprimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_supprimerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pn_table_livreLayout = new javax.swing.GroupLayout(pn_table_livre);
        pn_table_livre.setLayout(pn_table_livreLayout);
        pn_table_livreLayout.setHorizontalGroup(
            pn_table_livreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pn_table_livreLayout.createSequentialGroup()
                .addGroup(pn_table_livreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pn_table_livreLayout.createSequentialGroup()
                        .addContainerGap(33, Short.MAX_VALUE)
                        .addComponent(sp_tb_livres, javax.swing.GroupLayout.PREFERRED_SIZE, 853, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pn_table_livreLayout.createSequentialGroup()
                        .addGap(142, 142, 142)
                        .addComponent(bt_consulter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bt_modifier)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bt_ajouter)
                        .addGap(18, 18, 18)
                        .addComponent(bt_supprimer)))
                .addContainerGap())
        );
        pn_table_livreLayout.setVerticalGroup(
            pn_table_livreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pn_table_livreLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sp_tb_livres, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pn_table_livreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bt_consulter)
                    .addComponent(bt_modifier)
                    .addComponent(bt_ajouter)
                    .addComponent(bt_supprimer))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pn_champs.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lb_id.setText("id");

        lb_auteur.setText("auteur");

        lb_url.setText("url");

        lb_isbn.setText("Code Isbn");

        lb_prix.setText("prix");

        lb_create.setText("crée le");

        lb_update.setText("MAJ le ");

        bt_annuler.setText("Annuler");
        bt_annuler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_annulerActionPerformed(evt);
            }
        });

        bt_enregistrer.setText("Enregistrer");
        bt_enregistrer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_enregistrerActionPerformed(evt);
            }
        });

        lb_titre.setText("titre");

        lb_com.setText("commentaire");

        lb_edid.setText("Editeur");

        jLabel1.setText("€");

        javax.swing.GroupLayout pn_champsLayout = new javax.swing.GroupLayout(pn_champs);
        pn_champs.setLayout(pn_champsLayout);
        pn_champsLayout.setHorizontalGroup(
            pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pn_champsLayout.createSequentialGroup()
                .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pn_champsLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(lb_auteur)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pn_champsLayout.createSequentialGroup()
                                .addComponent(lb_isbn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ed_isbn, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pn_champsLayout.createSequentialGroup()
                                .addComponent(bt_enregistrer, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(87, 87, 87)
                                .addComponent(bt_annuler, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(272, 272, 272))))
                    .addGroup(pn_champsLayout.createSequentialGroup()
                        .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pn_champsLayout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(lb_id))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pn_champsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lb_url)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pn_champsLayout.createSequentialGroup()
                                .addComponent(ed_url, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lb_prix)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ed_prix, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pn_champsLayout.createSequentialGroup()
                                .addComponent(ed_auteur, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(pn_champsLayout.createSequentialGroup()
                                .addComponent(ed_id, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lb_titre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ed_titre, javax.swing.GroupLayout.PREFERRED_SIZE, 516, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pn_champsLayout.createSequentialGroup()
                        .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pn_champsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lb_com)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ed_com, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pn_champsLayout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(lb_edid)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ed_edid, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pn_champsLayout.createSequentialGroup()
                                .addComponent(lb_create)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(da_create, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pn_champsLayout.createSequentialGroup()
                                .addComponent(lb_update)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(da_update, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(19, 19, 19))
            .addGroup(pn_champsLayout.createSequentialGroup()
                .addGap(359, 359, 359)
                .addComponent(lb_mode, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pn_champsLayout.setVerticalGroup(
            pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pn_champsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pn_champsLayout.createSequentialGroup()
                        .addComponent(lb_mode, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 26, Short.MAX_VALUE)
                        .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lb_titre)
                            .addComponent(ed_titre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ed_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lb_id)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_isbn)
                            .addComponent(ed_isbn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pn_champsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ed_auteur, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lb_auteur))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lb_prix)
                        .addComponent(ed_prix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ed_url, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lb_url))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pn_champsLayout.createSequentialGroup()
                        .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_create)
                            .addComponent(lb_com)
                            .addComponent(ed_com, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_edid)
                            .addComponent(ed_edid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lb_update)
                        .addGroup(pn_champsLayout.createSequentialGroup()
                            .addComponent(da_create, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(da_update, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(65, 65, 65)
                .addGroup(pn_champsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bt_annuler)
                    .addComponent(bt_enregistrer))
                .addGap(23, 23, 23))
        );

        mn_quitter.setText("Quitter");
        mn_quitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mn_quitterActionPerformed(evt);
            }
        });
        mb_menu.add(mn_quitter);

        mi_accueil.setText("Accueil");
        mb_menu.add(mi_accueil);

        mn_direction.setText("Direction");

        mi_livre.setText("Livre");
        mn_direction.add(mi_livre);

        mb_menu.add(mn_direction);

        mn_gestion.setText("Gestion");
        mb_menu.add(mn_gestion);

        setJMenuBar(mb_menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(183, Short.MAX_VALUE)
                .addComponent(pn_table_livre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
            .addComponent(pn_champs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(pn_table_livre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pn_champs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mn_quitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mn_quitterActionPerformed
        setVisible(false);
        exit();
    }//GEN-LAST:event_mn_quitterActionPerformed

    private void bt_consulterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_consulterActionPerformed
        initIhm('C');
        enabled();

        if (tb_livre.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                    "Sélectionnez un livre.",
                    "Sélection obligatoire",
                    JOptionPane.WARNING_MESSAGE);
            initIhm('T');
        }

        try {
            sePlacer();

        } catch (Exception ex) {
            System.getLogger(V_livre.class.getName())
                    .log(System.Logger.Level.ERROR, "Erreur sélection site", ex);
        }
    }//GEN-LAST:event_bt_consulterActionPerformed

    private void bt_annulerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_annulerActionPerformed
        vider();
    }//GEN-LAST:event_bt_annulerActionPerformed

    private void bt_modifierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_modifierActionPerformed
        initIhm('M');
        enabled();

        if (tb_livre.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                    "Sélectionnez un livre.",
                    "Sélection obligatoire",
                    JOptionPane.WARNING_MESSAGE);
            initIhm('T');
        }

        try {
            sePlacer();

        } catch (Exception ex) {
            System.getLogger(V_livre.class.getName())
                    .log(System.Logger.Level.ERROR, "Erreur sélection site", ex);
        }
    }//GEN-LAST:event_bt_modifierActionPerformed

    private void bt_enregistrerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_enregistrerActionPerformed

        switch (choix) {

            // Ajouter un livre
            case 'A' -> {
                try {
                    int idEditeur = Integer.parseInt(ed_edid.getText());
                    String titre = ed_titre.getText();
                    String auteurs = ed_auteur.getText();
                    String urlLivre = ed_url.getText();
                    String codeIsbn = ed_isbn.getText();
                    String commentaire = ed_com.getText();
                    String prix = ed_prix.getText();

                    LocalDateTime created = LocalDateTime.now();
                    LocalDateTime maj = LocalDateTime.now();

                    leControlleurl.ajout_livre(
                            idEditeur, titre, auteurs, urlLivre,
                            codeIsbn, commentaire, prix,
                            maj, created
                    );

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Veuillez saisir des nombres valides (éditeur, prix)",
                            "Erreur de saisie",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    System.getLogger(V_livre.class.getName())
                            .log(System.Logger.Level.ERROR, "Erreur ajout livre", ex);
                }
            }

            // Modifier un livre
            case 'M' -> {
                try {
                    int id = Integer.parseInt(ed_id.getText());
                    int idEditeur = Integer.parseInt(ed_edid.getText());
                    String titre = ed_titre.getText();
                    String auteurs = ed_auteur.getText();
                    String urlLivre = ed_url.getText();
                    String codeIsbn = ed_isbn.getText();
                    String commentaire = ed_com.getText();
                    String prix = ed_prix.getText();

                    LocalDateTime created = LocalDateTime.now();
                    LocalDateTime maj = LocalDateTime.now();

                    leControlleurl.modif_livre(id, idEditeur, titre, auteurs, urlLivre, codeIsbn, commentaire, prixAchat, maj, created);

                } catch (Exception ex) {
                    System.getLogger(V_livre.class.getName())
                            .log(System.Logger.Level.ERROR, "Erreur modification livre", ex);
                }
            }

            // Supprimer un livre
            case 'S' -> {
                try {
                    int id = Integer.parseInt(ed_id.getText());
                    leControlleurl.supp_livre(id);

                } catch (Exception ex) {
                    System.getLogger(V_livre.class.getName())
                            .log(System.Logger.Level.ERROR, "Erreur suppression livre", ex);
                }
            }

        }

    }//GEN-LAST:event_bt_enregistrerActionPerformed

    private void bt_ajouterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_ajouterActionPerformed
        initIhm('A');

        enabled();
        try {
            vider();
            choix = 'A';
            initIhm(choix);
        } catch (Exception ex) {
            System.getLogger(V_livre.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            initIhm('T');
        }
    }//GEN-LAST:event_bt_ajouterActionPerformed

    private void bt_supprimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_supprimerActionPerformed

        if (tb_livre.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                    "Sélectionnez un site.",
                    "Sélection obligatoire",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ligne = tb_livre.getSelectedRow();
        Object valeur = tb_livre.getValueAt(ligne, 0);
        id = Integer.parseInt(valeur.toString());
        int reponse = JOptionPane.showConfirmDialog(
                this, "Tu veux vraiment supprimer ce livre?", "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );
        if (reponse == JOptionPane.YES_OPTION) {
            try {
                leControlleurl.supp_livre(id);
            } catch (Exception ex) {
                System.getLogger(V_livre.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            JOptionPane.showMessageDialog(this,
                    "La suppression c'est bien passé je valide",
                    "Suppression",
                    JOptionPane.PLAIN_MESSAGE);
        }

    }//GEN-LAST:event_bt_supprimerActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                V_livre dialog = new V_livre(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt_ajouter;
    private javax.swing.JButton bt_annuler;
    private javax.swing.JButton bt_consulter;
    private javax.swing.JButton bt_enregistrer;
    private javax.swing.JButton bt_modifier;
    private javax.swing.JButton bt_supprimer;
    private com.toedter.calendar.JDateChooser da_create;
    private com.toedter.calendar.JDateChooser da_update;
    private javax.swing.JTextField ed_auteur;
    private javax.swing.JTextField ed_com;
    private javax.swing.JTextField ed_edid;
    private javax.swing.JTextField ed_id;
    private javax.swing.JTextField ed_isbn;
    private javax.swing.JTextField ed_prix;
    private javax.swing.JTextField ed_titre;
    private javax.swing.JTextField ed_url;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lb_auteur;
    private javax.swing.JLabel lb_com;
    private javax.swing.JLabel lb_create;
    private javax.swing.JLabel lb_edid;
    private javax.swing.JLabel lb_id;
    private javax.swing.JLabel lb_isbn;
    private javax.swing.JLabel lb_mode;
    private javax.swing.JLabel lb_prix;
    private javax.swing.JLabel lb_titre;
    private javax.swing.JLabel lb_update;
    private javax.swing.JLabel lb_url;
    private javax.swing.JMenuBar mb_menu;
    private javax.swing.JMenu mi_accueil;
    private javax.swing.JMenuItem mi_livre;
    private javax.swing.JMenu mn_direction;
    private javax.swing.JMenu mn_gestion;
    private javax.swing.JMenu mn_quitter;
    private javax.swing.JPanel pn_champs;
    private javax.swing.JPanel pn_table_livre;
    private javax.swing.JScrollPane sp_tb_livres;
    private javax.swing.JTable tb_livre;
    // End of variables declaration//GEN-END:variables
private void exit() {
        System.exit(0);
    }

}
