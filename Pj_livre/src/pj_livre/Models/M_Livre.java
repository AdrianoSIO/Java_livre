package pj_livre.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import pj_livre.CL_connexion;
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

    // ==============================
    //  GETTERS / SETTERS
    // ==============================

    public Db_mariadb getDb()                        { return db; }
    public void setDb(Db_mariadb db)                 { this.db = db; }

    public int getId()                               { return id; }
    public void setId(int id)                        { this.id = id; }

    public int getIdEditeur()                        { return idEditeur; }
    public void setIdEditeur(int idEditeur)          { this.idEditeur = idEditeur; }

    public String getTitre()                         { return titre; }
    public void setTitre(String titre)               { this.titre = titre; }

    public String getAuteurs()                       { return auteurs; }
    public void setAuteurs(String auteurs)           { this.auteurs = auteurs; }

    public String getUrlLivre()                      { return urlLivre; }
    public void setUrlLivre(String urlLivre)         { this.urlLivre = urlLivre; }

    public String getCodeIsbn()                      { return codeIsbn; }
    public void setCodeIsbn(String codeIsbn)         { this.codeIsbn = codeIsbn; }

    public String getCommentaire()                   { return commentaire; }
    public void setCommentaire(String commentaire)   { this.commentaire = commentaire; }

    public float getPrixAchat()                      { return prixAchat; }
    public void setPrixAchat(float prixAchat)        { this.prixAchat = prixAchat; }

    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt()              { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt){ this.updatedAt = updatedAt; }

    // ==============================
    //  CONSTRUCTEUR COMPLET (hydratation mémoire)
    // ==============================

    public M_Livre(Db_mariadb db, int id, int idEditeur, String titre,
                   String auteurs, String urlLivre, String codeIsbn,
                   String commentaire, float prixAchat,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.db          = db;
        this.id          = id;
        this.idEditeur   = idEditeur;
        this.titre       = titre;
        this.auteurs     = auteurs;
        this.urlLivre    = urlLivre;
        this.codeIsbn    = codeIsbn;
        this.commentaire = commentaire;
        this.prixAchat   = prixAchat;
        this.createdAt   = createdAt;
        this.updatedAt   = updatedAt;
    }

    // ==============================
    //  CONSTRUCTEUR INSERT
    //  CORRECTION : échappement des apostrophes (replace ' par \')
    //  pour éviter les crashs et injections SQL basiques
    // ==============================

    public M_Livre(Db_mariadb db, int idEditeur, String titre,
                   String auteurs, String urlLivre, String codeIsbn,
                   String commentaire, float prixAchat) throws SQLException {

        this.db          = db;
        this.idEditeur   = idEditeur;
        this.titre       = titre;
        this.auteurs     = auteurs;
        this.urlLivre    = urlLivre;
        this.codeIsbn    = codeIsbn;
        this.commentaire = commentaire;
        this.prixAchat   = prixAchat;

        String sql = "INSERT INTO mcd_livre "
                + "(id_editeur, titre, auteurs, url_livre, code_isbn, commentaire, prix_achat, created_at, updated_at) "
                + "VALUES ("
                + idEditeur + ", '"
                + esc(titre) + "', '"
                + esc(auteurs) + "', '"
                + esc(urlLivre) + "', '"
                + esc(codeIsbn) + "', '"
                + esc(commentaire) + "', "
                + prixAchat + ", NOW(), NOW())";

        db.sqlExec(sql);

        // CORRECTION : first() fonctionne car sqlSelect retourne un ResultSet
        // TYPE_SCROLL_INSENSITIVE — on peut appeler first() sans problème
        ResultSet res = db.sqlLastId();
        res.first();
        this.id = res.getInt("id");

        // Récupération des timestamps générés par la BDD
        ResultSet resDate = db.sqlSelect("SELECT created_at, updated_at FROM mcd_livre WHERE id = " + this.id);
        resDate.first();
        this.createdAt = resDate.getTimestamp("created_at") != null
                ? resDate.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt = resDate.getTimestamp("updated_at") != null
                ? resDate.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  CONSTRUCTEUR SELECT par id
    //  CORRECTION : vérification si le ResultSet est vide
    //               + null-check sur les timestamps
    // ==============================

    public M_Livre(Db_mariadb db, int id) throws SQLException {
        this.db = db;
        this.id = id;

        ResultSet res = db.sqlSelect("SELECT * FROM mcd_livre WHERE id = " + id);

        // CORRECTION : first() retourne false si aucun enregistrement trouvé
        if (!res.first()) {
            throw new SQLException("Aucun livre trouvé avec l'id = " + id);
        }

        this.idEditeur   = res.getInt("id_editeur");
        this.titre       = res.getString("titre");
        this.auteurs     = res.getString("auteurs");
        this.urlLivre    = res.getString("url_livre");
        this.codeIsbn    = res.getString("code_isbn");
        this.commentaire = res.getString("commentaire");
        this.prixAchat   = res.getFloat("prix_achat");

        // CORRECTION : null-check avant toLocalDateTime()
        this.createdAt = res.getTimestamp("created_at") != null
                ? res.getTimestamp("created_at").toLocalDateTime() : null;
        this.updatedAt = res.getTimestamp("updated_at") != null
                ? res.getTimestamp("updated_at").toLocalDateTime() : null;
    }

    // ==============================
    //  UPDATE
    //  CORRECTION : échappement des apostrophes
    // ==============================

    public void update() throws SQLException {
        String sql = "UPDATE mcd_livre SET "
                + "id_editeur = " + idEditeur + ", "
                + "titre = '" + esc(titre) + "', "
                + "auteurs = '" + esc(auteurs) + "', "
                + "url_livre = '" + esc(urlLivre) + "', "
                + "code_isbn = '" + esc(codeIsbn) + "', "
                + "commentaire = '" + esc(commentaire) + "', "
                + "prix_achat = " + prixAchat + ", "
                + "updated_at = NOW() "
                + "WHERE id = " + id;
        db.sqlExec(sql);

        this.updatedAt = LocalDateTime.now();
    }

    // ==============================
    //  DELETE — inchangé, correct
    // ==============================

    public void delete() throws SQLException {
        db.sqlExec("CALL supprimer_livre_complet(" + id + ")");
    }

    // ==============================
    //  SELECT ALL
    //  CORRECTION : null-check sur les timestamps
    // ==============================

    public static LinkedHashMap<Integer, M_Livre> getRecords(Db_mariadb db) throws SQLException {

        LinkedHashMap<Integer, M_Livre> liste = new LinkedHashMap<>();
        ResultSet res = db.sqlSelect("SELECT * FROM mcd_livre ORDER BY id");

        while (res.next()) {
            LocalDateTime createdAt = res.getTimestamp("created_at") != null
                    ? res.getTimestamp("created_at").toLocalDateTime() : null;
            LocalDateTime updatedAt = res.getTimestamp("updated_at") != null
                    ? res.getTimestamp("updated_at").toLocalDateTime() : null;

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
                    createdAt,
                    updatedAt
            );
            liste.put(livre.id, livre);
        }

        return liste;
    }

    // ==============================
    //  UTILITAIRE : échappement SQL basique
    //  Remplace ' par \' pour éviter les crashs sur les chaînes avec apostrophes
    // ==============================

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("'", "\\'");
    }

    // ==============================
    //  TOSTRING
    // ==============================

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
            M_Livre nouveau = new M_Livre(db, 1,
                    "L'art du code propre",
                    "Robert C. Martin",
                    "https://exemple.com/clean-code",
                    "978-2-01-234567-8",
                    "Un classique incontournable",
                    34.90f);
            System.out.println("Créé : " + nouveau);

            // ---- READ par id ----
            System.out.println("\n=== SELECT par id ===");
            M_Livre trouve = new M_Livre(db, nouveau.getId());
            System.out.println("Trouvé : " + trouve);

            // ---- UPDATE ----
            System.out.println("\n=== UPDATE ===");
            trouve.setTitre("L'art du code propre — 2e édition");
            trouve.setPrixAchat(39.90f);
            trouve.update();
            System.out.println("Mis à jour : " + trouve);

            // ---- SELECT ALL ----
            System.out.println("\n=== SELECT ALL ===");
            LinkedHashMap<Integer, M_Livre> tous = M_Livre.getRecords(db);
            tous.values().forEach(System.out::println);

            // ---- DELETE ----
            System.out.println("\n=== DELETE ===");
            trouve.delete();
            System.out.println("Livre id=" + trouve.getId() + " supprimé.");

            db.closeBase();

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
          
        }
    }
}
