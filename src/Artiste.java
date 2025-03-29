import java.util.Objects;

/**
 * Un artiste corrspond à un sommet.
 */
public class Artiste {

  private final int id;
  private final String nom;
  private final String categories;

  public Artiste(int id, String nom, String categories) {
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

  @Override
  public String toString() {
    return nom + " (" + categories + ")";
  }

}
