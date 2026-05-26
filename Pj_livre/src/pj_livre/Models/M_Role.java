package pj_livre.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import pj_livre.CL_connexion;
import pj_livre.Db_mariadb;

public class M_Role {

    private Db_mariadb db;
    private int id;
    private String code;
    private String nom;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==============================
    //  GETTERS / SETTERS
    // ==============================

    public int getId()                               { return id; }
    public void setId(int id)                        { this.id = id; }

    public String getCode()                          { return code; }
    public void setCode(String code)                 { this.code = code; }

    public String getNom()                           { return nom; }
    public void setNom(String nom)                   { this.nom = nom; }

    public String getCommentaire()                   { return commentaire; }
    public void setCommentaire(String commentaire)   { this.commentaire = commentaire; }

    public LocalDateTime getCreatedAt()              { return createdAt; }
    public LocalDateTime getUpdatedAt()              { return updatedAt; }

    // ==============================
    //  CONSTRUCTEUR COMPLET
    // ==============================

    public M_Role(Db_mariadb db, int id, String code, String nom,
                  String commentaire, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.db          = db;
        this.id          = id;
        this.code        = code;
        this.nom         = nom;
        this.commentaire = commentaire;
        this.createdAt   = createdAt;
        this.updatedAt   = updatedAt;
    }

    // ==============================
    //  CONSTRUCTEUR INSERT
    // ==============================

    public M_Role(Db_mariadb db, String code, String nom, String commentaire) throws SQLException {
        this.db          = db;
        this.code        = code;
        this.nom         = nom;
        this.commentaire = commentaire;

        String sql = "INSERT INTO mcd_roles (code, nom, commentaire, created_at, updated_at) "
                + "VALUES ('" + esc(code) + "', '" + esc(nom) + "', '" + esc(commentaire) + "', NOW(), NOW())";
        db.sqlExec(sql);

        ResultSet res = db.sqlLastId();
        res.first();
        this.id = res.getInt("id");

        ResultSet resDate = db.sqlSelect("SELECT created_at, updated_at FROM mcd_roles WHERE id = " + this.id);
        resDate.first();
        this.createdAt = resDate.getTimestamp("created_at") != null ? resDate.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt = resDate.getTimestamp("updated_at") != null ? resDate.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  CONSTRUCTEUR SELECT par id
    // ==============================

    public M_Role(Db_mariadb db, int id) throws SQLException {
        this.db = db;
        this.id = id;

        ResultSet res = db.sqlSelect("SELECT * FROM mcd_roles WHERE id = " + id);
        if (!res.first()) throw new SQLException("Aucun role trouve avec id = " + id);

        this.code        = res.getString("code");
        this.nom         = res.getString("nom");
        this.commentaire = res.getString("commentaire");
        this.createdAt   = res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt   = res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  UPDATE
    // ==============================

    public void update() throws SQLException {
        String sql = "UPDATE mcd_roles SET "
                + "code = '" + esc(code) + "', "
                + "nom = '" + esc(nom) + "', "
                + "commentaire = '" + esc(commentaire) + "' "
                + "WHERE id = " + id;
        db.sqlExec(sql);
        this.updatedAt = LocalDateTime.now();
    }

    // ==============================
    //  DELETE
    // ==============================

    public void delete() throws SQLException {
        db.sqlExec("DELETE FROM mcd_roles WHERE id = " + id);
    }

    // ==============================
    //  SELECT ALL
    // ==============================

    public static LinkedHashMap<Integer, M_Role> getRecords(Db_mariadb db) throws SQLException {
        LinkedHashMap<Integer, M_Role> liste = new LinkedHashMap<>();
        ResultSet res = db.sqlSelect("SELECT * FROM mcd_roles ORDER BY id");
        while (res.next()) {
            M_Role r = new M_Role(db,
                    res.getInt("id"),
                    res.getString("code"),
                    res.getString("nom"),
                    res.getString("commentaire"),
                    res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null,
                    res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null);
            liste.put(r.id, r);
        }
        return liste;
    }

    // ==============================
    //  UTILITAIRE
    // ==============================

    private static String esc(String s) { return s == null ? "" : s.replace("'", "\\'"); }

    @Override
    public String toString() {
        return "Role {id=" + id + ", code='" + code + "', nom='" + nom + "'}";
    }

    // ==============================
    //  MAIN
    // ==============================

    public static void main(String[] args) {
        try {
            Db_mariadb db = new Db_mariadb(CL_connexion.url, CL_connexion.login, CL_connexion.password);
            System.out.println("Connexion OK\n");

            System.out.println("=== SELECT ALL ===");
            M_Role.getRecords(db).values().forEach(System.out::println);

            System.out.println("\n=== INSERT ===");
            M_Role r = new M_Role(db, "TST", "Testeur", "Role de test");
            System.out.println("Cree : " + r);

            System.out.println("\n=== SELECT par id ===");
            M_Role rLu = new M_Role(db, r.getId());
            System.out.println("Trouve : " + rLu);

            System.out.println("\n=== UPDATE ===");
            rLu.setNom("Testeur v2");
            rLu.update();
            System.out.println("Mis a jour : " + rLu);

            System.out.println("\n=== DELETE ===");
            rLu.delete();
            System.out.println("Role id=" + rLu.getId() + " supprime");

            db.closeBase();
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
           
        }
    }
}