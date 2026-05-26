package pj_livre.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import pj_livre.CL_connexion;
import pj_livre.Db_mariadb;

public class M_Payer {

    // Clé primaire composite : (id_location, id_methode)
    private Db_mariadb db;
    private int idLocation;
    private int idMethode;
    private LocalDateTime datePaiement;
    private float montant;
    private boolean estEmprunt;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==============================
    //  GETTERS / SETTERS
    // ==============================

    public int getIdLocation()                           { return idLocation; }
    public void setIdLocation(int idLocation)            { this.idLocation = idLocation; }

    public int getIdMethode()                            { return idMethode; }
    public void setIdMethode(int idMethode)              { this.idMethode = idMethode; }

    public LocalDateTime getDatePaiement()               { return datePaiement; }
    public void setDatePaiement(LocalDateTime d)         { this.datePaiement = d; }

    public float getMontant()                            { return montant; }
    public void setMontant(float montant)                { this.montant = montant; }

    public boolean isEstEmprunt()                        { return estEmprunt; }
    public void setEstEmprunt(boolean estEmprunt)        { this.estEmprunt = estEmprunt; }

    public String getCommentaire()                       { return commentaire; }
    public void setCommentaire(String commentaire)       { this.commentaire = commentaire; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public LocalDateTime getUpdatedAt()                  { return updatedAt; }

    // ==============================
    //  CONSTRUCTEUR COMPLET
    // ==============================

    public M_Payer(Db_mariadb db, int idLocation, int idMethode,
                   LocalDateTime datePaiement, float montant, boolean estEmprunt,
                   String commentaire, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.db           = db;
        this.idLocation   = idLocation;
        this.idMethode    = idMethode;
        this.datePaiement = datePaiement;
        this.montant      = montant;
        this.estEmprunt   = estEmprunt;
        this.commentaire  = commentaire;
        this.createdAt    = createdAt;
        this.updatedAt    = updatedAt;
    }

    // ==============================
    //  CONSTRUCTEUR INSERT via procédure stockée
    //  Crée la location ET le paiement en une seule procédure
    //  Récupère ensuite le paiement créé via LAST_INSERT_ID sur mcd_location
    // ==============================

    public M_Payer(Db_mariadb db, int idUser, int idExemplaire,
                   int idMethode, float montant) throws SQLException {
        this.db         = db;
        this.idMethode  = idMethode;
        this.montant    = montant;
        this.estEmprunt = true;

        // Appel de la procédure stockée qui crée la location + le paiement
        String sql = "CALL creer_location_paiement("
                + idUser + ", "
                + idExemplaire + ", "
                + idMethode + ", "
                + montant + ")";
        db.sqlExec(sql);

        // Récupération de l'id de la location créée par la procédure
        ResultSet resId = db.sqlLastId();
        resId.first();
        // LAST_INSERT_ID() retourne le dernier INSERT de la session,
        // ici c'est celui de mcd_payer (dernier INSERT de la procédure)
        // mais la PK de mcd_payer est composite donc on récupère id_location
        // via le SELECT du dernier enregistrement inséré dans mcd_location
        ResultSet resLoc = db.sqlSelect(
                "SELECT id FROM mcd_location WHERE id_user = " + idUser
                + " AND id_exemplaire = " + idExemplaire
                + " ORDER BY id DESC LIMIT 1");
        resLoc.first();
        this.idLocation = resLoc.getInt("id");

        // Relecture du paiement pour hydrater tous les champs
        ResultSet res = db.sqlSelect(
                "SELECT * FROM mcd_payer WHERE id_location = " + this.idLocation
                + " AND id_methode = " + idMethode);
        res.first();
        this.datePaiement = res.getTimestamp("date_paiement") != null ? res.getTimestamp("date_paiement").toLocalDateTime() : null;
        this.createdAt    = res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt    = res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null;
        this.commentaire  = res.getString("commentaire");
    }

    // ==============================
    //  CONSTRUCTEUR SELECT par clé composite
    // ==============================

    public M_Payer(Db_mariadb db, int idLocation, int idMethode) throws SQLException {
        this.db         = db;
        this.idLocation = idLocation;
        this.idMethode  = idMethode;

        ResultSet res = db.sqlSelect(
                "SELECT * FROM mcd_payer WHERE id_location = " + idLocation
                + " AND id_methode = " + idMethode);
        if (!res.first()) throw new SQLException(
                "Aucun paiement trouve pour id_location=" + idLocation + " id_methode=" + idMethode);

        this.datePaiement = res.getTimestamp("date_paiement") != null ? res.getTimestamp("date_paiement").toLocalDateTime() : null;
        this.montant      = res.getFloat("montant");
        this.estEmprunt   = res.getInt("est_emprunt") == 1;
        this.commentaire  = res.getString("commentaire");
        this.createdAt    = res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt    = res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  UPDATE
    // ==============================

    public void update() throws SQLException {
        String sql = "UPDATE mcd_payer SET "
                + "date_paiement = '" + datePaiement + "', "
                + "montant = " + montant + ", "
                + "est_emprunt = " + (estEmprunt ? 1 : 0) + ", "
                + "commentaire = '" + esc(commentaire) + "', "
                + "updated_at = NOW() "
                + "WHERE id_location = " + idLocation + " AND id_methode = " + idMethode;
        db.sqlExec(sql);
        this.updatedAt = LocalDateTime.now();
    }

    // ==============================
    //  DELETE
    // ==============================

    public void delete() throws SQLException {
        db.sqlExec("DELETE FROM mcd_payer WHERE id_location = " + idLocation
                + " AND id_methode = " + idMethode);
    }

    // ==============================
    //  SELECT ALL
    // ==============================

    public static List<M_Payer> getRecords(Db_mariadb db) throws SQLException {
        List<M_Payer> liste = new ArrayList<>();
        ResultSet res = db.sqlSelect("SELECT * FROM mcd_payer ORDER BY id_location, id_methode");
        while (res.next()) {
            M_Payer p = new M_Payer(db,
                    res.getInt("id_location"),
                    res.getInt("id_methode"),
                    res.getTimestamp("date_paiement") != null ? res.getTimestamp("date_paiement").toLocalDateTime() : null,
                    res.getFloat("montant"),
                    res.getInt("est_emprunt") == 1,
                    res.getString("commentaire"),
                    res.getTimestamp("created_at") != null ? res.getTimestamp("created_at").toLocalDateTime() : null,
                    res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null);
            liste.add(p);
        }
        return liste;
    }

    // ==============================
    //  UTILITAIRE
    // ==============================

    private static String esc(String s) { return s == null ? "" : s.replace("'", "\\'"); }

    @Override
    public String toString() {
        return "Payer {idLocation=" + idLocation + ", idMethode=" + idMethode
                + ", montant=" + montant + ", estEmprunt=" + estEmprunt
                + ", datePaiement=" + datePaiement + "}";
    }

    // ==============================
    //  MAIN
    // ==============================

    public static void main(String[] args) {
        try {
            Db_mariadb db = new Db_mariadb(CL_connexion.url, CL_connexion.login, CL_connexion.password);
            System.out.println("Connexion OK\n");

            System.out.println("=== SELECT ALL ===");
            M_Payer.getRecords(db).forEach(System.out::println);

            System.out.println("\n=== INSERT via procedure creer_location_paiement ===");
            // id_user=1, id_exemplaire=1, id_methode=1 doivent exister en base
            M_Payer p = new M_Payer(db, 1, 1, 1, 5.0f);
            System.out.println("Cree : " + p);

            System.out.println("\n=== SELECT par cle composite ===");
            M_Payer pLu = new M_Payer(db, p.getIdLocation(), p.getIdMethode());
            System.out.println("Trouve : " + pLu);

            System.out.println("\n=== UPDATE ===");
            pLu.setMontant(7.5f);
            pLu.update();
            System.out.println("Mis a jour : " + pLu);

            System.out.println("\n=== DELETE ===");
            pLu.delete();
            System.out.println("Paiement supprime (id_location=" + pLu.getIdLocation()
                    + ", id_methode=" + pLu.getIdMethode() + ")");

            // Nettoyage de la location créée par la procédure
            db.sqlExec("DELETE FROM mcd_location WHERE id = " + p.getIdLocation());
            System.out.println("Location de test supprimee");

            db.closeBase();
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}