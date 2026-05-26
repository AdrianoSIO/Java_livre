package pj_livre.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import pj_livre.CL_connexion;
import pj_livre.Db_mariadb;

public class M_User {

    private Db_mariadb db;
    private int id;
    private int idRole;
    private String nom;
    private String prenom;
    private String name;
    private String email;
    private String password;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==============================
    //  GETTERS / SETTERS
    // ==============================

    public int getId()                               { return id; }
    public void setId(int id)                        { this.id = id; }

    public int getIdRole()                           { return idRole; }
    public void setIdRole(int idRole)                { this.idRole = idRole; }

    public String getNom()                           { return nom; }
    public void setNom(String nom)                   { this.nom = nom; }

    public String getPrenom()                        { return prenom; }
    public void setPrenom(String prenom)             { this.prenom = prenom; }

    public String getName()                          { return name; }
    public void setName(String name)                 { this.name = name; }

    public String getEmail()                         { return email; }
    public void setEmail(String email)               { this.email = email; }

    public String getPassword()                      { return password; }
    public void setPassword(String password)         { this.password = password; }

    public String getCommentaire()                   { return commentaire; }
    public void setCommentaire(String commentaire)   { this.commentaire = commentaire; }

    public LocalDateTime getCreatedAt()              { return createdAt; }
    public LocalDateTime getUpdatedAt()              { return updatedAt; }

    // ==============================
    //  CONSTRUCTEUR COMPLET
    // ==============================

    public M_User(Db_mariadb db, int id, int idRole, String nom, String prenom,
                  String name, String email, String password, String commentaire,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.db          = db;
        this.id          = id;
        this.idRole      = idRole;
        this.nom         = nom;
        this.prenom      = prenom;
        this.name        = name;
        this.email       = email;
        this.password    = password;
        this.commentaire = commentaire;
        this.createdAt   = createdAt;
        this.updatedAt   = updatedAt;
    }

    // ==============================
    //  CONSTRUCTEUR INSERT
    // ==============================

    public M_User(Db_mariadb db, int idRole, String nom, String prenom,
                  String name, String email, String password, String commentaire) throws SQLException {
        this.db          = db;
        this.idRole      = idRole;
        this.nom         = nom;
        this.prenom      = prenom;
        this.name        = name;
        this.email       = email;
        this.password    = password;
        this.commentaire = commentaire;

        String sql = "INSERT INTO mcd_users (id_role, nom, prenom, name, email, password, commentaire, created_at, updated_at) "
                + "VALUES (" + idRole + ", '"
                + esc(nom) + "', '" + esc(prenom) + "', '" + esc(name) + "', '"
                + esc(email) + "', '" + esc(password) + "', '" + esc(commentaire) + "', NOW(), NOW())";
        db.sqlExec(sql);

        ResultSet res = db.sqlLastId();
        res.first();
        this.id = res.getInt("id");

        ResultSet resDate = db.sqlSelect("SELECT created_at, updated_at FROM mcd_users WHERE id = " + this.id);
        resDate.first();
        this.createdAt = resDate.getTimestamp("created_at") != null ? resDate.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt = resDate.getTimestamp("updated_at") != null ? resDate.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  CONSTRUCTEUR SELECT par id
    // ==============================

    public M_User(Db_mariadb db, int id) throws SQLException {
        this.db = db;
        this.id = id;

        ResultSet res = db.sqlSelect("SELECT * FROM mcd_users WHERE id = " + id);
        if (!res.first()) throw new SQLException("Aucun user trouve avec id = " + id);

        this.idRole      = res.getInt("id_role");
        this.nom         = res.getString("nom");
        this.prenom      = res.getString("prenom");
        this.name        = res.getString("name");
        this.email       = res.getString("email");
        this.password    = res.getString("password");
        this.commentaire = res.getString("commentaire");
        this.createdAt   = res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt   = res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  UPDATE
    // ==============================

    public void update() throws SQLException {
        String sql = "UPDATE mcd_users SET "
                + "id_role = " + idRole + ", "
                + "nom = '" + esc(nom) + "', "
                + "prenom = '" + esc(prenom) + "', "
                + "name = '" + esc(name) + "', "
                + "email = '" + esc(email) + "', "
                + "password = '" + esc(password) + "', "
                + "commentaire = '" + esc(commentaire) + "' "
                + "WHERE id = " + id;
        db.sqlExec(sql);
        this.updatedAt = LocalDateTime.now();
    }

    // ==============================
    //  DELETE
    // ==============================

    public void delete() throws SQLException {
        db.sqlExec("DELETE FROM mcd_users WHERE id = " + id);
    }

    // ==============================
    //  SELECT ALL
    // ==============================

    public static LinkedHashMap<Integer, M_User> getRecords(Db_mariadb db) throws SQLException {
        LinkedHashMap<Integer, M_User> liste = new LinkedHashMap<>();
        ResultSet res = db.sqlSelect("SELECT * FROM mcd_users ORDER BY id");
        while (res.next()) {
            M_User u = new M_User(db,
                    res.getInt("id"),
                    res.getInt("id_role"),
                    res.getString("nom"),
                    res.getString("prenom"),
                    res.getString("name"),
                    res.getString("email"),
                    res.getString("password"),
                    res.getString("commentaire"),
                    res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null,
                    res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null);
            liste.put(u.id, u);
        }
        return liste;
    }

    private static String esc(String s) { return s == null ? "" : s.replace("'", "\\'"); }

    @Override
    public String toString() {
        return "User {id=" + id + ", nom='" + nom + "', prenom='" + prenom + "', email='" + email + "', idRole=" + idRole + "}";
    }

// MAIN Avec les test
public static void main(String[] args) {
        try {
            Db_mariadb db = new Db_mariadb(CL_connexion.url, CL_connexion.login, CL_connexion.password);
            System.out.println("Connexion OK\n");

            System.out.println("=== SELECT ALL ===");
            M_User.getRecords(db).values().forEach(System.out::println);

            System.out.println("\n=== INSERT ===");
            M_User u = new M_User(db,1,"Raza","Adriano","Adri","GOAT","GOAT","Essai");
            System.out.println("Cree : " + u);

            System.out.println("\n=== SELECT par id ===");
            M_User uLu = new M_User(db, u.getId());
            System.out.println("Trouve : " + uLu);

            System.out.println("\n=== UPDATE ===");
            uLu.setNom("GOAT v2");
            uLu.update();
            System.out.println("Mis a jour : " + uLu);

            System.out.println("\n=== DELETE ===");
            uLu.delete();
            System.out.println("User id=" + uLu.getId() + " supprime");

            db.closeBase();
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
           
        }
    }
}