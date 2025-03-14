import java.util.HashSet;
import java.util.Set;

public class Artiste {

    int id;
    String nom;
    Set<String> categories;

    public Artiste(int id, String nom, Set<String> categories){
        categories = new HashSet<>();
        this.id = id;
        this.nom = nom;
        this.categories = categories;
    }

    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }

    public Set<String> getCategories() {
        return categories;
    }
}
