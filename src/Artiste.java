import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Artiste {

    int id;
    String nom;
    String categories;

    public Artiste(int id, String nom, String categories){
        this.id = id;
        this.nom = nom;
        this.categories = categories;
    }

    public Artiste(String str){
        // Séparer les sous-chaînes par des virgules
        String[] array = str.split(",");

        this.id = Integer.parseInt(array[0]);
        this.nom = array[1];
        this.categories = array[2];
    }

    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }

    public String getCategories() {
        return categories;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Artiste artiste = (Artiste) o;
        return id == artiste.id && Objects.equals(nom, artiste.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom);
    }
}
