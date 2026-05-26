package pj_livre.Controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import pj_livre.CL_connexion;
import pj_livre.Db_mariadb;
import pj_livre.Views.*;

public class C_livre {

    private Db_mariadb db;

    // Session
    private int    sessionId;
    private int    sessionIdRole;
    private String sessionNom;
    private String sessionPrenom;
    private String sessionEmail;
    private String sessionRoleCode;
    private String sessionRoleNom;

    // Vues
    private V_Main        vMain;
    private V_Accueil     vAccueil;
    private V_Livres      vLivres;
    private V_Editeurs    vEditeurs;
    private V_Exemplaires vExemplaires;
    private V_Users       vUsers;
    private V_Locations   vLocations;
    private V_Paiements   vPaiements;
    private V_MonCompte   vMonCompte;

    // ════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTEUR
    // ════════════════════════════════════════════════════════════════════════

    public C_livre() {
        // Connexion AVANT de créer la vue (JDialog modal bloque le thread)
        try {
            db = new Db_mariadb(CL_connexion.url, CL_connexion.login, CL_connexion.password);
            // Vérification que la connexion est réellement établie
            db.sqlSelect("SELECT 1");
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Impossible de se connecter à la base de données.\n" + e.getMessage(),
                    "Erreur de connexion", javax.swing.JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        vMain = new V_Main(this);
        vMain.setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CONNEXION / DÉCONNEXION
    // ════════════════════════════════════════════════════════════════════════

    public void connecter(String identifiant, String password) {
        try {
            String sql = "CALL authentifier_user('" + esc(identifiant) + "', '" + esc(password) + "')";
            ResultSet res = db.sqlSelect(sql);

            if (!res.first()) {
                vMain.afficherErreur("Identifiant ou mot de passe incorrect.");
                return;
            }

            sessionId       = res.getInt("id");
            sessionIdRole   = res.getInt("id_role");
            sessionNom      = res.getString("nom");
            sessionPrenom   = res.getString("prenom");
            sessionEmail    = res.getString("email");
            sessionRoleCode = res.getString("role_code");
            sessionRoleNom  = res.getString("role_nom");

            vMain.dispose();
            ouvrirAccueil();

        } catch (SQLException e) {
            vMain.afficherErreur("Erreur SQL : " + e.getMessage());
        }
    }

    public void deconnecter() {
        sessionId = 0; sessionIdRole = 0;
        sessionNom = null; sessionPrenom = null; sessionEmail = null;
        sessionRoleCode = null; sessionRoleNom = null;
        fermerToutesLesVues();
        vMain = new V_Main(this);
        vMain.setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CHANGEMENT MOT DE PASSE
    //  Retourne true si OK, false si ancien mdp incorrect
    // ════════════════════════════════════════════════════════════════════════

    public boolean changerMotDePasse(String ancien, String nouveau) {
        try {
            // Vérification de l'ancien mot de passe
            ResultSet res = db.sqlSelect(
                    "SELECT id FROM mcd_users WHERE id = " + sessionId
                    + " AND password = '" + esc(ancien) + "'");
            if (!res.first()) return false;

            // Mise à jour
            db.sqlExec("UPDATE mcd_users SET password = '" + esc(nouveau)
                    + "', updated_at = NOW() WHERE id = " + sessionId);
            return true;

        } catch (SQLException e) {
            afficherErreur("Erreur changement mot de passe : " + e.getMessage());
            return false;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  NAVIGATION
    // ════════════════════════════════════════════════════════════════════════

    public void ouvrirAccueil() {
        if (vAccueil != null && vAccueil.isDisplayable()) vAccueil.dispose();
        vAccueil = new V_Accueil(this);
        vAccueil.setVisible(true);
    }

    public void ouvrirLivres() {
        if (vLivres == null || !vLivres.isDisplayable()) vLivres = new V_Livres(this);
        vLivres.chargerDonnees(); vLivres.setVisible(true); vLivres.toFront();
    }

    public void ouvrirEditeurs() {
        if (!peutGerer()) { accesRefuse(); return; }
        if (vEditeurs == null || !vEditeurs.isDisplayable()) vEditeurs = new V_Editeurs(this);
        vEditeurs.chargerDonnees(); vEditeurs.setVisible(true); vEditeurs.toFront();
    }

    public void ouvrirExemplaires() {
        if (!peutGerer()) { accesRefuse(); return; }
        if (vExemplaires == null || !vExemplaires.isDisplayable()) vExemplaires = new V_Exemplaires(this);
        vExemplaires.chargerDonnees(); vExemplaires.setVisible(true); vExemplaires.toFront();
    }

    public void ouvrirUsers() {
        if (!estAdmin()) { accesRefuse(); return; }
        if (vUsers == null || !vUsers.isDisplayable()) vUsers = new V_Users(this);
        vUsers.chargerDonnees(); vUsers.setVisible(true); vUsers.toFront();
    }

    public void ouvrirLocations() {
        if (vLocations == null || !vLocations.isDisplayable()) vLocations = new V_Locations(this);
        vLocations.chargerDonnees(); vLocations.setVisible(true); vLocations.toFront();
    }

    public void ouvrirPaiements() {
        if (!peutGerer()) { accesRefuse(); return; }
        if (vPaiements == null || !vPaiements.isDisplayable()) vPaiements = new V_Paiements(this);
        vPaiements.chargerDonnees(); vPaiements.setVisible(true); vPaiements.toFront();
    }

    public void ouvrirMonCompte() {
        if (vMonCompte == null || !vMonCompte.isDisplayable()) vMonCompte = new V_MonCompte(this);
        vMonCompte.setVisible(true); vMonCompte.toFront();
    }

    private void fermerToutesLesVues() {
        if (vAccueil     != null) vAccueil.dispose();
        if (vLivres      != null) vLivres.dispose();
        if (vEditeurs    != null) vEditeurs.dispose();
        if (vExemplaires != null) vExemplaires.dispose();
        if (vUsers       != null) vUsers.dispose();
        if (vLocations   != null) vLocations.dispose();
        if (vPaiements   != null) vPaiements.dispose();
        if (vMonCompte   != null) vMonCompte.dispose();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DROITS D'ACCÈS
    // ════════════════════════════════════════════════════════════════════════

    public boolean estConnecte()  { return sessionId > 0; }
    public boolean peutGerer()    { return "GES".equals(sessionRoleCode) || estAdmin(); }
    public boolean estAdmin()     { return "ADM".equals(sessionRoleCode); }

    private void accesRefuse() {
        afficherErreur("Accès refusé — rôle insuffisant (" + sessionRoleNom + ").");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  GETTERS SESSION
    // ════════════════════════════════════════════════════════════════════════

    public Db_mariadb getDb()          { return db; }
    public int    getSessionId()       { return sessionId; }
    public int    getSessionIdRole()   { return sessionIdRole; }
    public String getSessionNom()      { return sessionNom; }
    public String getSessionPrenom()   { return sessionPrenom; }
    public String getSessionEmail()    { return sessionEmail; }
    public String getSessionRoleCode() { return sessionRoleCode; }
    public String getSessionRoleNom()  { return sessionRoleNom; }

    // ════════════════════════════════════════════════════════════════════════
    //  UTILITAIRES
    // ════════════════════════════════════════════════════════════════════════

    private static String esc(String s) { return s == null ? "" : s.replace("'", "\\'"); }

    public void afficherErreur(String msg) {
        javax.swing.JOptionPane.showMessageDialog(null, msg, "Erreur",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  POINT D'ENTRÉE
    // ════════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        java.awt.EventQueue.invokeLater(() -> new C_livre());
    }
}