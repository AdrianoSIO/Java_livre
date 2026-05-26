package pj_livre.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import pj_livre.CL_connexion;
import pj_livre.Db_mariadb;

public class M_Exemplaire {

    private Db_mariadb db;
    private int id;
    private int idLivre;
    private String code;
    private float prixEmprunt;
    private String prixRetour;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==============================
    //  GETTERS / SETTERS
    // ==============================

    public int getId()                               { return id; }
    public void setId(int id)                        { this.id = id; }

    public int getIdLivre()                          { return idLivre; }
    public void setIdLivre(int idLivre)              { this.idLivre = idLivre; }

    public String getCode()                          { return code; }
    public void setCode(String code)                 { this.code = code; }

    public float getPrixEmprunt()                    { return prixEmprunt; }
    public void setPrixEmprunt(float prixEmprunt)    { this.prixEmprunt = prixEmprunt; }

    public String getPrixRetour()                    { return prixRetour; }
    public void setPrixRetour(String prixRetour)     { this.prixRetour = prixRetour; }

    public String getCommentaire()                   { return commentaire; }
    public void setCommentaire(String commentaire)   { this.commentaire = commentaire; }

    public LocalDateTime getCreatedAt()              { return createdAt; }
    public LocalDateTime getUpdatedAt()              { return updatedAt; }

    // ==============================
    //  CONSTRUCTEUR COMPLET
    // ==============================

    public M_Exemplaire(Db_mariadb db, int id, int idLivre, String code,
                        float prixEmprunt, String prixRetour, String commentaire,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.db          = db;
        this.id          = id;
        this.idLivre     = idLivre;
        this.code        = code;
        this.prixEmprunt = prixEmprunt;
        this.prixRetour  = prixRetour;
        this.commentaire = commentaire;
        this.createdAt   = createdAt;
        this.updatedAt   = updatedAt;
    }

    // ==============================
    //  CONSTRUCTEUR INSERT
    // ==============================

    public M_Exemplaire(Db_mariadb db, int idLivre, String code,
                        float prixEmprunt, String prixRetour, String commentaire) throws SQLException {
        this.db          = db;
        this.idLivre     = idLivre;
        this.code        = code;
        this.prixEmprunt = prixEmprunt;
        this.prixRetour  = prixRetour;
        this.commentaire = commentaire;

        String sql = "INSERT INTO mcd_exemplaires (id_livre, code, prix_emprunt, prix_retour, commentaire, created_at, updated_at) "
                + "VALUES (" + idLivre + ", '"
                + esc(code) + "', " + prixEmprunt + ", '"
                + esc(prixRetour) + "', '" + esc(commentaire) + "', NOW(), NOW())";
        db.sqlExec(sql);

        ResultSet res = db.sqlLastId();
        res.first();
        this.id = res.getInt("id");

        ResultSet resDate = db.sqlSelect("SELECT created_at, updated_at FROM mcd_exemplaires WHERE id = " + this.id);
        resDate.first();
        this.createdAt = resDate.getTimestamp("created_at") != null ? resDate.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt = resDate.getTimestamp("updated_at") != null ? resDate.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  CONSTRUCTEUR SELECT par id
    // ==============================

    public M_Exemplaire(Db_mariadb db, int id) throws SQLException {
        this.db = db;
        this.id = id;

        ResultSet res = db.sqlSelect("SELECT * FROM mcd_exemplaires WHERE id = " + id);
        if (!res.first()) throw new SQLException("Aucun exemplaire trouve avec id = " + id);

        this.idLivre     = res.getInt("id_livre");
        this.code        = res.getString("code");
        this.prixEmprunt = res.getFloat("prix_emprunt");
        this.prixRetour  = res.getString("prix_retour");
        this.commentaire = res.getString("commentaire");
        this.createdAt   = res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt   = res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  UPDATE
    // ==============================

    public void update() throws SQLException {
        String sql = "UPDATE mcd_exemplaires SET "
                + "id_livre = " + idLivre + ", "
                + "code = '" + esc(code) + "', "
                + "prix_emprunt = " + prixEmprunt + ", "
                + "prix_retour = '" + esc(prixRetour) + "', "
                + "commentaire = '" + esc(commentaire) + "' "
                + "WHERE id = " + id;
        db.sqlExec(sql);
        this.updatedAt = LocalDateTime.now();
    }

    // ==============================
    //  DELETE
    // ==============================

    public void delete() throws SQLException {
        db.sqlExec("DELETE FROM mcd_exemplaires WHERE id = " + id);
    }

    // ==============================
    //  SELECT ALL
    // ==============================

    public static LinkedHashMap<Integer, M_Exemplaire> getRecords(Db_mariadb db) throws SQLException {
        LinkedHashMap<Integer, M_Exemplaire> liste = new LinkedHashMap<>();
        ResultSet res = db.sqlSelect("SELECT * FROM mcd_exemplaires ORDER BY id");
        while (res.next()) {
            M_Exemplaire ex = new M_Exemplaire(db,
                    res.getInt("id"),
                    res.getInt("id_livre"),
                    res.getString("code"),
                    res.getFloat("prix_emprunt"),
                    res.getString("prix_retour"),
                    res.getString("commentaire"),
                    res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null,
                    res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null);
            liste.put(ex.id, ex);
        }
        return liste;
    }

    private static String esc(String s) { return s == null ? "" : s.replace("'", "\\'"); }

    @Override
    public String toString() {
        return "Exemplaire {id=" + id + ", code='" + code + "', idLivre=" + idLivre + ", prixEmprunt=" + prixEmprunt + "}";
    }

//  MAIN avec test

    public static void main(String[] args) {
    try {
        // --- TEST BRUT JDBC pour voir la vraie erreur ---
        String fullUrl = "jdbc:mariadb:" + CL_connexion.url + "?useTimezone=true&serverTimezone=UTC";
        System.out.println("Tentative de connexion sur : " + fullUrl);

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            java.sql.Connection testConn =
                    java.sql.DriverManager.getConnection(fullUrl, CL_connexion.login, CL_connexion.password);
            testConn.close();
            System.out.println("Connexion JDBC brute OK !");
        } catch (Exception e) {
            System.err.println("VRAIE ERREUR : " + e.getMessage());
            return;
        }

        // Connexion avec ma base de donnée
        Db_mariadb db = new Db_mariadb(CL_connexion.url, CL_connexion.login, CL_connexion.password);

        try {
            db.sqlSelect("SELECT 1");
        } catch (Exception e) {
            System.err.println("CONNEXION ÉCHOUÉE. Cause: " + e.getMessage());
            return;
        }

        System.out.println("Connexion OK\n");

        // ---- CREATE ----
        System.out.println("=== INSERT ===");

        M_Exemplaire nouveau = new M_Exemplaire(
                db,
                1,
                "TEST-CODE",
                2.5f,
                "retour-test",
                "commentaire test"
        );

        System.out.println("Créé : " + nouveau);

        // ---- READ par id ----
        System.out.println("\n=== SELECT par id ===");

        M_Exemplaire trouve = new M_Exemplaire(db, nouveau.getId());
        System.out.println("Trouvé : " + trouve);

        // ---- UPDATE ----
        System.out.println("\n=== UPDATE ===");

        trouve.setCode("TEST-CODE-UPDATE");
        trouve.setPrixEmprunt(3.0f);
        trouve.setPrixRetour("retour modifié");
        trouve.setCommentaire("commentaire modifié");

        trouve.update();
        System.out.println("Mis à jour : " + trouve);

        // ---- SELECT ALL ----
        System.out.println("\n=== SELECT ALL ===");

        LinkedHashMap<Integer, M_Exemplaire> tous = M_Exemplaire.getRecords(db);
        tous.values().forEach(System.out::println);

        // ---- DELETE ----
        System.out.println("\n=== DELETE ===");

        trouve.delete();
        System.out.println("Exemplaire id=" + trouve.getId() + " supprimé.");

        db.closeBase();

    } catch (Exception e) {
        System.err.println("Erreur : " + e.getMessage());
    }
}
}
