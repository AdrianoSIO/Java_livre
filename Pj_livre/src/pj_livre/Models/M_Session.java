package pj_livre.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import pj_livre.Db_mariadb;

/**
 * Modèle de session — gère uniquement la connexion via la procédure stockée.
 * Porte les infos de l'utilisateur connecté.
 */
public class M_Session {

    private int    id;
    private int    idRole;
    private String nom;
    private String prenom;
    private String name;
    private String email;
    private String commentaire;
    private String roleCode;
    private String roleNom;

    // ==============================
    //  GETTERS
    // ==============================

    public int    getId()          { return id; }
    public int    getIdRole()      { return idRole; }
    public String getNom()         { return nom; }
    public String getPrenom()      { return prenom; }
    public String getName()        { return name; }
    public String getEmail()       { return email; }
    public String getCommentaire() { return commentaire; }
    public String getRoleCode()    { return roleCode; }
    public String getRoleNom()     { return roleNom; }

    // ==============================
    //  CONSTRUCTEUR PRIVÉ
    // ==============================

    private M_Session() {}

    // ==============================
    //  MÉTHODE STATIQUE DE CONNEXION
    //  Retourne un M_Session si OK, null si identifiants incorrects
    // ==============================

    public static M_Session connecter(Db_mariadb db, String identifiant, String password) throws SQLException {
        String sql = "CALL authentifier_user('"
                + esc(identifiant) + "', '"
                + esc(password) + "')";

        ResultSet res = db.sqlSelect(sql);

        if (!res.first()) return null;  // identifiants incorrects

        M_Session session  = new M_Session();
        session.id         = res.getInt("id");
        session.idRole     = res.getInt("id_role");
        session.nom        = res.getString("nom");
        session.prenom     = res.getString("prenom");
        session.name       = res.getString("name");
        session.email      = res.getString("email");
        session.commentaire= res.getString("commentaire");
        session.roleCode   = res.getString("role_code");
        session.roleNom    = res.getString("role_nom");

        return session;
    }

    // ==============================
    //  UTILITAIRE
    // ==============================

    private static String esc(String s) { return s == null ? "" : s.replace("'", "\\'"); }

    @Override
    public String toString() {
        return "Session {id=" + id + ", name='" + name + "', email='" + email
                + "', role=" + roleCode + "}";
    }

    // ==============================
    //  MAIN — test connexion
    // ==============================

    public static void main(String[] args) {
        try {
            pj_livre.Db_mariadb db = new pj_livre.Db_mariadb(
                    pj_livre.CL_connexion.url,
                    pj_livre.CL_connexion.login,
                    pj_livre.CL_connexion.password);

            // Test avec email
            System.out.println("=== Connexion par email ===");
            M_Session s1 = M_Session.connecter(db, "admin@test.fr", "motdepasse");
            System.out.println(s1 != null ? "OK : " + s1 : "ECHEC : identifiants incorrects");

            // Test avec name (username)
            System.out.println("\n=== Connexion par username ===");
            M_Session s2 = M_Session.connecter(db, "admin", "motdepasse");
            System.out.println(s2 != null ? "OK : " + s2 : "ECHEC : identifiants incorrects");

            // Test identifiants incorrects
            System.out.println("\n=== Mauvais mot de passe ===");
            M_Session s3 = M_Session.connecter(db, "admin", "mauvais");
            System.out.println(s3 != null ? "OK : " + s3 : "ECHEC : identifiants incorrects");

            db.closeBase();
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
