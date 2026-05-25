package pj_livre.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import pj_livre.Db_mariadb;

public class M_Location {

    private Db_mariadb db;
    private int id;
    private int idUser;
    private int idExemplaire;
    private LocalDateTime dateEmprunt;
    private float prixEmprunt;
    private LocalDateTime dateRetour;
    private String prixRetour;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==============================
    //  GETTERS / SETTERS
    // ==============================

    public int getId()                                       { return id; }
    public void setId(int id)                                { this.id = id; }

    public int getIdUser()                                   { return idUser; }
    public void setIdUser(int idUser)                        { this.idUser = idUser; }

    public int getIdExemplaire()                             { return idExemplaire; }
    public void setIdExemplaire(int idExemplaire)            { this.idExemplaire = idExemplaire; }

    public LocalDateTime getDateEmprunt()                    { return dateEmprunt; }
    public void setDateEmprunt(LocalDateTime dateEmprunt)    { this.dateEmprunt = dateEmprunt; }

    public float getPrixEmprunt()                            { return prixEmprunt; }
    public void setPrixEmprunt(float prixEmprunt)            { this.prixEmprunt = prixEmprunt; }

    public LocalDateTime getDateRetour()                     { return dateRetour; }
    public void setDateRetour(LocalDateTime dateRetour)      { this.dateRetour = dateRetour; }

    public String getPrixRetour()                            { return prixRetour; }
    public void setPrixRetour(String prixRetour)             { this.prixRetour = prixRetour; }

    public String getCommentaire()                           { return commentaire; }
    public void setCommentaire(String commentaire)           { this.commentaire = commentaire; }

    public LocalDateTime getCreatedAt()                      { return createdAt; }
    public LocalDateTime getUpdatedAt()                      { return updatedAt; }

    // ==============================
    //  CONSTRUCTEUR COMPLET
    // ==============================

    public M_Location(Db_mariadb db, int id, int idUser, int idExemplaire,
                      LocalDateTime dateEmprunt, float prixEmprunt,
                      LocalDateTime dateRetour, String prixRetour, String commentaire,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.db            = db;
        this.id            = id;
        this.idUser        = idUser;
        this.idExemplaire  = idExemplaire;
        this.dateEmprunt   = dateEmprunt;
        this.prixEmprunt   = prixEmprunt;
        this.dateRetour    = dateRetour;
        this.prixRetour    = prixRetour;
        this.commentaire   = commentaire;
        this.createdAt     = createdAt;
        this.updatedAt     = updatedAt;
    }

    // ==============================
    //  CONSTRUCTEUR INSERT
    // ==============================

    public M_Location(Db_mariadb db, int idUser, int idExemplaire,
                      LocalDateTime dateEmprunt, float prixEmprunt,
                      LocalDateTime dateRetour, String prixRetour, String commentaire) throws SQLException {
        this.db           = db;
        this.idUser       = idUser;
        this.idExemplaire = idExemplaire;
        this.dateEmprunt  = dateEmprunt;
        this.prixEmprunt  = prixEmprunt;
        this.dateRetour   = dateRetour;
        this.prixRetour   = prixRetour;
        this.commentaire  = commentaire;

        String sql = "INSERT INTO mcd_location "
                + "(id_user, id_exemplaire, date_emprunt, prix_emprunt, date_retour, prix_retour, commentaire, created_at, updated_at) "
                + "VALUES ("
                + idUser + ", " + idExemplaire + ", '"
                + dateEmprunt + "', " + prixEmprunt + ", '"
                + dateRetour + "', '" + esc(prixRetour) + "', '"
                + esc(commentaire) + "', NOW(), NOW())";
        db.sqlExec(sql);

        ResultSet res = db.sqlLastId();
        res.first();
        this.id = res.getInt("id");

        ResultSet resDate = db.sqlSelect("SELECT created_at, updated_at FROM mcd_location WHERE id = " + this.id);
        resDate.first();
        this.createdAt = resDate.getTimestamp("created_at") != null ? resDate.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt = resDate.getTimestamp("updated_at") != null ? resDate.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  CONSTRUCTEUR SELECT par id
    // ==============================

    public M_Location(Db_mariadb db, int id) throws SQLException {
        this.db = db;
        this.id = id;

        ResultSet res = db.sqlSelect("SELECT * FROM mcd_location WHERE id = " + id);
        if (!res.first()) throw new SQLException("Aucune location trouvee avec id = " + id);

        this.idUser       = res.getInt("id_user");
        this.idExemplaire = res.getInt("id_exemplaire");
        this.dateEmprunt  = res.getTimestamp("date_emprunt") != null ? res.getTimestamp("date_emprunt").toLocalDateTime() : null;
        this.prixEmprunt  = res.getFloat("prix_emprunt");
        this.dateRetour   = res.getTimestamp("date_retour") != null ? res.getTimestamp("date_retour").toLocalDateTime() : null;
        this.prixRetour   = res.getString("prix_retour");
        this.commentaire  = res.getString("commentaire");
        this.createdAt    = res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt    = res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  UPDATE
    // ==============================

    public void update() throws SQLException {
        String sql = "UPDATE mcd_location SET "
                + "id_user = " + idUser + ", "
                + "id_exemplaire = " + idExemplaire + ", "
                + "date_emprunt = '" + dateEmprunt + "', "
                + "prix_emprunt = " + prixEmprunt + ", "
                + "date_retour = '" + dateRetour + "', "
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
        db.sqlExec("DELETE FROM mcd_payer WHERE id_location = " + id);
        db.sqlExec("DELETE FROM mcd_location WHERE id = " + id);
    }

    // ==============================
    //  SELECT ALL
    // ==============================

    public static LinkedHashMap<Integer, M_Location> getRecords(Db_mariadb db) throws SQLException {
        LinkedHashMap<Integer, M_Location> liste = new LinkedHashMap<>();
        ResultSet res = db.sqlSelect("SELECT * FROM mcd_location ORDER BY id");
        while (res.next()) {
            M_Location loc = new M_Location(db,
                    res.getInt("id"),
                    res.getInt("id_user"),
                    res.getInt("id_exemplaire"),
                    res.getTimestamp("date_emprunt") != null ? res.getTimestamp("date_emprunt").toLocalDateTime() : null,
                    res.getFloat("prix_emprunt"),
                    res.getTimestamp("date_retour") != null ? res.getTimestamp("date_retour").toLocalDateTime() : null,
                    res.getString("prix_retour"),
                    res.getString("commentaire"),
                    res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null,
                    res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null);
            liste.put(loc.id, loc);
        }
        return liste;
    }

    private static String esc(String s) { return s == null ? "" : s.replace("'", "\\'"); }

    @Override
    public String toString() {
        return "Location {id=" + id + ", idUser=" + idUser + ", idExemplaire=" + idExemplaire
                + ", dateEmprunt=" + dateEmprunt + ", prixEmprunt=" + prixEmprunt + "}";
    }


public static void main(String[] args) {
    try {

        Db_mariadb db = new Db_mariadb(
                pj_livre.CL_connexion.url,
                pj_livre.CL_connexion.login,
                pj_livre.CL_connexion.password
        );

        db.sqlSelect("SELECT 1");
        System.out.println("Connexion OK\n");

        // ================= INSERT =================
        System.out.println("=== INSERT ===");

        M_Location loc = new M_Location(
                db,
                1,
                1,
                LocalDateTime.now(),
                2.5f,
                null,
                "OK",
                "test location"
        );

        System.out.println(loc);

        // ================= SELECT =================
        System.out.println("\n=== SELECT ===");

        M_Location l2 = new M_Location(db, loc.getId());
        System.out.println(l2);

        // ================= UPDATE =================
        System.out.println("\n=== UPDATE ===");

        l2.setPrixEmprunt(3.0f);
        l2.setPrixRetour("MODIF");
        l2.setCommentaire("update OK");
        l2.setDateRetour(LocalDateTime.now());

        l2.update();

        System.out.println(l2);

        System.out.println("\n=== SELECT ALL ===");

        LinkedHashMap<Integer, M_Location> all = M_Location.getRecords(db);
        all.values().forEach(System.out::println);

        // ================= DELETE =================
        System.out.println("\n=== DELETE ===");

        l2.delete();
        System.out.println("Supprimé id=" + l2.getId());

        db.closeBase();

    } catch (Exception e) {
        System.err.println("Erreur : " + e.getMessage());
    }
}
}
