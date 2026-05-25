package pj_livre.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import pj_livre.CL_connexion;
import pj_livre.Db_mariadb;

public class M_Editeur {

    private Db_mariadb db;
    private int id;
    private String nom;
    private String urlSite;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==============================
    //  GETTERS / SETTERS
    // ==============================

    public int getId()                               { return id; }
    public void setId(int id)                        { this.id = id; }

    public String getNom()                           { return nom; }
    public void setNom(String nom)                   { this.nom = nom; }

    public String getUrlSite()                       { return urlSite; }
    public void setUrlSite(String urlSite)           { this.urlSite = urlSite; }

    public String getCommentaire()                   { return commentaire; }
    public void setCommentaire(String commentaire)   { this.commentaire = commentaire; }

    public LocalDateTime getCreatedAt()              { return createdAt; }
    public LocalDateTime getUpdatedAt()              { return updatedAt; }

    // ==============================
    //  CONSTRUCTEUR COMPLET
    // ==============================

    public M_Editeur(Db_mariadb db, int id, String nom, String urlSite,String commentaire, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.db          = db;
        this.id          = id;
        this.nom         = nom;
        this.urlSite     = urlSite;
        this.commentaire = commentaire;
        this.createdAt   = createdAt;
        this.updatedAt   = updatedAt;
    }

    // ==============================
    //  CONSTRUCTEUR INSERT
    // ==============================

    public M_Editeur(Db_mariadb db, String nom, String urlSite, String commentaire) throws SQLException {
        this.db          = db;
        this.nom         = nom;
        this.urlSite     = urlSite;
        this.commentaire = commentaire;

        String sql = "INSERT INTO mcd_editeurs (nom, url_site, commentaire, created_at, updated_at) "
                + "VALUES ('" + esc(nom) + "', '" + esc(urlSite) + "', '" + esc(commentaire) + "', NOW(), NOW())";
        db.sqlExec(sql);

        ResultSet res = db.sqlLastId();
        res.first();
        this.id = res.getInt("id");

        ResultSet resDate = db.sqlSelect("SELECT created_at, updated_at FROM mcd_editeurs WHERE id = " + this.id);
        resDate.first();
        this.createdAt = resDate.getTimestamp("created_at") != null ? resDate.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt = resDate.getTimestamp("updated_at") != null ? resDate.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  CONSTRUCTEUR SELECT par id
    // ==============================

    public M_Editeur(Db_mariadb db, int id) throws SQLException {
        this.db = db;
        this.id = id;

        ResultSet res = db.sqlSelect("SELECT * FROM mcd_editeurs WHERE id = " + id);
        if (!res.first()) throw new SQLException("Aucun editeur trouve avec id = " + id);

        this.nom         = res.getString("nom");
        this.urlSite     = res.getString("url_site");
        this.commentaire = res.getString("commentaire");
        this.createdAt   = res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt   = res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  UPDATE
    // ==============================

    public void update() throws SQLException {
        String sql = "UPDATE mcd_editeurs SET "
                + "nom = '" + esc(nom) + "', "
                + "url_site = '" + esc(urlSite) + "', "
                + "commentaire = '" + esc(commentaire) + "' "
                + "WHERE id = " + id;
        db.sqlExec(sql);
        this.updatedAt = LocalDateTime.now();
    }

    // ==============================
    //  DELETE
    // ==============================

    public void delete() throws SQLException {
        db.sqlExec("DELETE FROM mcd_editeurs WHERE id = " + id);
    }

    // ==============================
    //  SELECT ALL
    // ==============================

    public static LinkedHashMap<Integer, M_Editeur> getRecords(Db_mariadb db) throws SQLException {
        LinkedHashMap<Integer, M_Editeur> liste = new LinkedHashMap<>();
        ResultSet res = db.sqlSelect("SELECT * FROM mcd_editeurs ORDER BY id");
        while (res.next()) {
            M_Editeur e = new M_Editeur(db,
                    res.getInt("id"),
                    res.getString("nom"),
                    res.getString("url_site"),
                    res.getString("commentaire"),
                    res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null,
                    res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null);
            liste.put(e.id, e);
        }
        return liste;
    }

    private static String esc(String s) { return s == null ? "" : s.replace("'", "\\'"); }

    @Override
    public String toString() {
        return "Editeur {id=" + id + ", nom='" + nom + "', urlSite='" + urlSite + "'}";
    }

//  MAIN avec test

    public static void main(String[] args) {
        try {
            // --- TEST BRUT JDBC pour voir la vraie erreur ---
            String fullUrl = "jdbc:mariadb:" + CL_connexion.url + "?useTimezone=true&serverTimezone=UTC";
            System.out.println("Tentative de connexion sur : " + fullUrl);
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                java.sql.Connection testConn = java.sql.DriverManager.getConnection(fullUrl, CL_connexion.login, CL_connexion.password);
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
            M_Editeur nouveau = new M_Editeur(db,"Manga","Manga.com","Site du manga");
            System.out.println("Créé : " + nouveau);

            // ---- READ par id ----
            System.out.println("\n=== SELECT par id ===");
            M_Editeur trouve = new M_Editeur(db, nouveau.getId());
            System.out.println("Trouvé : " + trouve);

            // ---- UPDATE ----
            System.out.println("\n=== UPDATE ===");
            trouve.setNom("BD");
            trouve.setUrlSite("BD.com");
            trouve.update();
            System.out.println("Mis à jour : " + trouve);

            // ---- SELECT ALL ----
            System.out.println("\n=== SELECT ALL ===");
            LinkedHashMap<Integer, M_Editeur> tous = M_Editeur.getRecords(db);
            tous.values().forEach(System.out::println);

            // ---- DELETE ----
            System.out.println("\n=== DELETE ===");
            trouve.delete();
            System.out.println("Editeurs id=" + trouve.getId() + " supprimé.");

            db.closeBase();

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
          
        }
    }
}
