import java.util.HashSet;
import java.util.Set;

public class Artiste {

    int id;
    String nom;
    Set categorie = new HashSet<>();

    public Artiste(int id, String nom, Set categorie){
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
    }

    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }

    public Set getCategorie() {
        return categorie;
    }
}
