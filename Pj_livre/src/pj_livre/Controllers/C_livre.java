package pj_livre.Controllers;

import pj_livre.Views.V_Main;
import java.sql.ResultSet;
import java.sql.SQLException;
import pj_livre.CL_connexion;
import pj_livre.Db_mariadb;
import pj_livre.Views.*;

/**
 * Contrôleur principal de l'application.
 * Gère la connexion, la session utilisateur et la navigation entre les vues.
 */
public class C_livre {

    // ── Base de données ──────────────────────────────────────────────────────
    private Db_mariadb db;

    // ── Session utilisateur connecté ─────────────────────────────────────────
    private int    sessionId;
    private int    sessionIdRole;
    private String sessionNom;
    private String sessionPrenom;
    private String sessionEmail;
    private String sessionRoleCode;  // APP / FOR / ENS / GES / ADM
    private String sessionRoleNom;

    // ── Vues ─────────────────────────────────────────────────────────────────
    private V_Main        vMain;
    private V_Accueil     vAccueil;
    private V_Livres      vLivres;
    private V_Editeurs    vEditeurs;
    private V_Exemplaires vExemplaires;
    private V_Users       vUsers;
    private V_Locations   vLocations;
    private V_Paiements   vPaiements;

    // ════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTEUR
    // ════════════════════════════════════════════════════════════════════════

    public C_livre() {
        try {
            db = new Db_mariadb(CL_connexion.url, CL_connexion.login, CL_connexion.password);
        } catch (Exception e) {
            afficherErreur("Impossible de se connecter à la base de données :\n" + e.getMessage());
            System.exit(1);
        }

        // Lancement sur l'écran de connexion
        vMain = new V_Main(this);
        vMain.setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CONNEXION / DÉCONNEXION
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Tente d'authentifier l'utilisateur via la procédure stockée.
     * Si OK → stocke la session et ouvre V_Accueil.
     * Si KO → renvoie un message d'erreur à la vue.
     */
    public void connecter(String emailOuName, String password) {
    try {
        String sql = "CALL authentifier_user('" + esc(emailOuName) + "', '" + esc(password) + "')";
        ResultSet res = db.sqlSelect(sql);

        if (!res.next()) {
            vMain.afficherErreur("Email/Name ou mot de passe incorrect.");
            return;
        }

        // Hydratation de la session
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
        // Réinitialisation session
        sessionId       = 0;
        sessionIdRole   = 0;
        sessionNom      = null;
        sessionPrenom   = null;
        sessionEmail    = null;
        sessionRoleCode = null;
        sessionRoleNom  = null;

        // Fermer toutes les vues ouvertes
        fermerToutesLesVues();

        // Retour à la connexion
        vMain = new V_Main(this);
        vMain.setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  NAVIGATION
    // ════════════════════════════════════════════════════════════════════════

    private void ouvrirAccueil() {
        vAccueil = new V_Accueil(this);
        vAccueil.setVisible(true);
    }

    public void ouvrirLivres() {
        if (vLivres == null || !vLivres.isDisplayable()) {
            vLivres = new V_Livres(this);
        }
        vLivres.chargerDonnees();
        vLivres.setVisible(true);
        vLivres.toFront();
    }

    public void ouvrirEditeurs() {
        if (!peutGerer()) { accesRefuse(); return; }
        if (vEditeurs == null || !vEditeurs.isDisplayable()) {
            vEditeurs = new V_Editeurs(this);
        }
        vEditeurs.chargerDonnees();
        vEditeurs.setVisible(true);
        vEditeurs.toFront();
    }

    public void ouvrirExemplaires() {
        if (!peutGerer()) { accesRefuse(); return; }
        if (vExemplaires == null || !vExemplaires.isDisplayable()) {
            vExemplaires = new V_Exemplaires(this);
        }
        vExemplaires.chargerDonnees();
        vExemplaires.setVisible(true);
        vExemplaires.toFront();
    }

    public void ouvrirUsers() {
        if (!estAdmin()) { accesRefuse(); return; }
        if (vUsers == null || !vUsers.isDisplayable()) {
            vUsers = new V_Users(this);
        }
        vUsers.chargerDonnees();
        vUsers.setVisible(true);
        vUsers.toFront();
    }

    public void ouvrirLocations() {
        if (vLocations == null || !vLocations.isDisplayable()) {
            vLocations = new V_Locations(this);
        }
        vLocations.chargerDonnees();
        vLocations.setVisible(true);
        vLocations.toFront();
    }

    public void ouvrirPaiements() {
        if (!peutGerer()) { accesRefuse(); return; }
        if (vPaiements == null || !vPaiements.isDisplayable()) {
            vPaiements = new V_Paiements(this);
        }
        vPaiements.chargerDonnees();
        vPaiements.setVisible(true);
        vPaiements.toFront();
    }

    private void fermerToutesLesVues() {
        if (vAccueil     != null) vAccueil.dispose();
        if (vLivres      != null) vLivres.dispose();
        if (vEditeurs    != null) vEditeurs.dispose();
        if (vExemplaires != null) vExemplaires.dispose();
        if (vUsers       != null) vUsers.dispose();
        if (vLocations   != null) vLocations.dispose();
        if (vPaiements   != null) vPaiements.dispose();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DROITS D'ACCÈS
    // ════════════════════════════════════════════════════════════════════════

    /** APP / FOR / ENS / GES / ADM → tout le monde peut lire */
    public boolean estConnecte()  { return sessionId > 0; }

    /** GES ou ADM → peut gérer livres, exemplaires, éditeurs, paiements */
    public boolean peutGerer() {
        return "GES".equals(sessionRoleCode) || estAdmin();
    }

    /** ADM uniquement → gestion users, rôles */
    public boolean estAdmin() {
        return "ADM".equals(sessionRoleCode);
    }

    private void accesRefuse() {
        afficherErreur("Accès refusé : votre rôle (" + sessionRoleNom + ") ne permet pas cette action.");
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

    private static String esc(String s) { return s == null ? "" : s.replace("'", "\\'"); }

    public void afficherErreur(String msg) {
        javax.swing.JOptionPane.showMessageDialog(null, msg, "Erreur",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    public static void main(String[] args) {
        // Look & Feel natif
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        java.awt.EventQueue.invokeLater(() -> new C_livre());
    }
}
