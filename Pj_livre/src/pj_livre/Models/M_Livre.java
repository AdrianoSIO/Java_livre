package pj_livre.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import pj_livre.Db_mariadb;

public class M_Livre {

    private Db_mariadb db;
    private int id;
    private int idEditeur;
    private String titre;
    private String auteurs;
    private String urlLivre;
    private String codeIsbn;
    private String commentaire;
    private float prixAchat;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Db_mariadb getDb() {
        return db;
    }

    public void setDb(Db_mariadb db) {
        this.db = db;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEditeur() {
        return idEditeur;
    }

    public void setIdEditeur(int idEditeur) {
        this.idEditeur = idEditeur;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteurs() {
        return auteurs;
    }

    public void setAuteurs(String auteurs) {
        this.auteurs = auteurs;
    }

    public String getUrlLivre() {
        return urlLivre;
    }

    public void setUrlLivre(String urlLivre) {
        this.urlLivre = urlLivre;
    }

    public String getCodeIsbn() {
        return codeIsbn;
    }

    public void setCodeIsbn(String codeIsbn) {
        this.codeIsbn = codeIsbn;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public float getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(float prixAchat) {
        this.prixAchat = prixAchat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public M_Livre(Db_mariadb db, int id, int idEditeur, String titre, String auteurs, String urlLivre, String codeIsbn, String commentaire, float prixAchat, LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.db = db;
        this.id = id;
        this.idEditeur = idEditeur;
        this.titre = titre;
        this.auteurs = auteurs;
        this.urlLivre = urlLivre;
        this.codeIsbn = codeIsbn;
        this.commentaire = commentaire;
        this.prixAchat = prixAchat;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public M_Livre(Db_mariadb db, int idEditeur, String titre,
            String auteurs, String urlLivre, String codeIsbn,
            String commentaire, float prixAchat) throws SQLException {

        this.db = db;

        String sql = "INSERT INTO mcd_livre (id_editeur, titre, auteurs, url_livre, code_isbn, commentaire, prix_achat, created_at, updated_at) "
                + "VALUES ("
                + idEditeur + ", '"
                + titre + "', '"
                + auteurs + "', '"
                + urlLivre + "', '"
                + codeIsbn + "', '"
                + commentaire + "', "
                + prixAchat + ", NOW(), NOW())";

        db.sqlExec(sql);

        ResultSet res = db.sqlLastId();
        res.first();
        this.id = res.getInt(1);
    }

    public M_Livre(Db_mariadb db, int id) throws SQLException {
        this.db = db;
        this.id = id;

        String sql = "SELECT * FROM mcd_livre WHERE id = " + id;
        ResultSet res = db.sqlSelect(sql);
        res.first();

        this.idEditeur = res.getInt("id_editeur");
        this.titre = res.getString("titre");
        this.auteurs = res.getString("auteurs");
        this.urlLivre = res.getString("url_livre");
        this.codeIsbn = res.getString("code_isbn");
        this.commentaire = res.getString("commentaire");
        this.prixAchat = res.getFloat("prix_achat");
        this.createdAt = res.getTimestamp("created_at").toLocalDateTime();
        this.updatedAt = res.getTimestamp("updated_at").toLocalDateTime();
    }

    // -------- UPDATE --------
    public void update() throws SQLException {
        String sql = "UPDATE mcd_livre SET "
                + "id_editeur = " + idEditeur + ", "
                + "titre = '" + titre + "', "
                + "auteurs = '" + auteurs + "', "
                + "url_livre = '" + urlLivre + "', "
                + "code_isbn = '" + codeIsbn + "', "
                + "commentaire = '" + commentaire + "', "
                + "prix_achat = " + prixAchat + ", "
                + "updated_at = NOW() "
                + "WHERE id = " + id;
        db.sqlExec(sql);
    }

    // -------- DELETE --------
    public void delete() throws SQLException {
        
        String sql = "CALL supprimer_livre_complet("+id+");";
        db.sqlExec(sql);
    }

    // -------- SELECT ALL --------
    public static LinkedHashMap<Integer, M_Livre> getRecords(Db_mariadb db) throws SQLException {

        LinkedHashMap<Integer, M_Livre> liste = new LinkedHashMap<>();

        String sql = "SELECT * FROM mcd_livre ORDER BY id";
        ResultSet res = db.sqlSelect(sql);

        while (res.next()) {
            M_Livre livre = new M_Livre(
                    db,
                    res.getInt("id"),
                    res.getInt("id_editeur"),
                    res.getString("titre"),
                    res.getString("auteurs"),
                    res.getString("url_livre"),
                    res.getString("code_isbn"),
                    res.getString("commentaire"),
                    res.getFloat("prix_achat"),
                    res.getTimestamp("created_at").toLocalDateTime(),
                    res.getTimestamp("updated_at").toLocalDateTime()
            );
            liste.put(livre.id, livre);
        }

        return liste;
    }

    // -------- TOSTRING --------
    @Override
    public String toString() {
        return "Livre {"
                + "id=" + id
                + ", titre='" + titre + '\''
                + ", auteurs='" + auteurs + '\''
                + ", prix=" + prixAchat
                + ", isbn='" + codeIsbn + '\''
                + '}';
    }
}
