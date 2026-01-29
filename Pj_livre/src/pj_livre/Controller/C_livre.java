/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pj_livre.Controller;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import pj_livre.CL_connexion;
import pj_livre.Db_mariadb;
import pj_livre.View.V_Main;
import pj_livre.View.V_livre;
import pj_livre.Models.M_Livre;

public class C_livre {

    //attribut
    public int idSite, capacite;
    private String nom, ville, cp, adRue;
    //bdd
    private Db_mariadb baseRR;
    //vue
    private V_Main fm_Main;
    private V_livre fm_livre;
    //models
    private M_Livre unLivre;
    //collection de models
    private LinkedHashMap<Integer, M_Livre> lesLivres;

    public C_livre() throws Exception {

        connexion();
        this.fm_Main = new V_Main(this);
        this.fm_livre = new V_livre(fm_Main, true);
        fm_Main.afficher();
    }

    private void connexion() throws Exception {
        baseRR = new Db_mariadb(CL_connexion.url, CL_connexion.login, CL_connexion.password);
    }

    public void aff_V_livre() throws SQLException {
        lesLivres = M_Livre.getRecords(baseRR); 
        fm_livre.afficher(lesLivres, this);
    }

    public void accueil() throws Exception {
        fm_Main.afficher();
    }
    public void modif_livre(int id, int idEditeur, String titre, String auteurs,
                       String urlLivre, String codeIsbn,
                       String commentaire, float prixAchat,LocalDateTime maj, LocalDateTime created) throws Exception {

    M_Livre unLivre = lesLivres.get(id);

    if (unLivre == null) {
        throw new Exception("Livre introuvable avec l'id : " + id);
    }

    unLivre.setIdEditeur(idEditeur);
    unLivre.setTitre(titre);
    unLivre.setAuteurs(auteurs);
    unLivre.setUrlLivre(urlLivre);
    unLivre.setCodeIsbn(codeIsbn);
    unLivre.setCommentaire(commentaire);
    unLivre.setPrixAchat(prixAchat);
    unLivre.setUpdatedAt(LocalDateTime.now());

    unLivre.update(); 
    aff_V_livre();        
}
    public void ajout_livre(int idEditeur, String titre, String auteurs,
                        String urlLivre, String codeIsbn,
                        String commentaire, String prixAchat,LocalDateTime maj, LocalDateTime created) throws Exception {
    M_Livre nouveau = new M_Livre(
        baseRR, 
        idEditeur,
        titre,
        auteurs,
        urlLivre,
        codeIsbn,
        commentaire,
        Float.parseFloat(prixAchat)
    );
    lesLivres.put(nouveau.getId(), nouveau);

    aff_V_livre();
}


    
    public void supp_livre(int id) throws Exception {

    M_Livre unLivre = lesLivres.get(id);

    if (unLivre == null) {
        throw new Exception("Livre introuvable avec l'id : " + id);
    }

    unLivre.delete();
    lesLivres.remove(id); 

    aff_V_livre();
}



    

    public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(() -> {
        try {
            new C_livre();
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}


}

