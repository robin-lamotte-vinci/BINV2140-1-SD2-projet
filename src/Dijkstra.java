import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Le constructeur de la classe fait appel à l'algorithme de Dijkstra pour trouver et afficher le
 * chemin entre 2 artistes qui contient le maximum de mentions.
 */
public class Dijkstra {

  private final Artiste artisteSource;
  private final Artiste artisteDestination;

  private final Map<Artiste, Artiste> chemins;
  private final Map<Artiste, Set<Mention>> mentionsSortantes;
  private final Map<Artiste, Etiquette> correspondanceArtisteEtiquette; // pour retrouver une etiquette provisoire facilement

  private final TreeSet<Etiquette> etiquettesProvisoires;
  private final Set<Etiquette> etiquettesDefinitives;


  public Dijkstra(Graph graph, Artiste artisteSource, Artiste artisteDestination) {

    this.artisteSource = artisteSource;
    this.artisteDestination = artisteDestination;

    this.chemins = new HashMap<>();
    this.mentionsSortantes = graph.getMentionsSortantes();
    this.correspondanceArtisteEtiquette = new HashMap<>();

    this.etiquettesDefinitives = new HashSet<>();
    this.etiquettesProvisoires = new TreeSet<>((e1, e2) -> {
      if (e1.getPoids().equals(e2.getPoids())) {
        return e1.getArtiste().getNom().compareTo(e2.getArtiste().getNom());
      }
      return e1.getPoids().compareTo(e2.getPoids());
    });

    trouverCheminMaxMentions();
    afficherCheminMaxMentions();

  }

  /**
   * Renvoie le poids d'une mention
   *
   * @param m
   * @return l'inverse du nombre de mentions
   */
  private double poidsMention(Mention m) {
    return (double) 1 / m.getNbMentions();
  }

  private void trouverCheminMaxMentions() {

    Etiquette etiquetteSource = new Etiquette(artisteSource, 0.0);
    etiquettesProvisoires.add(etiquetteSource);
    correspondanceArtisteEtiquette.put(artisteSource, etiquetteSource);

    Etiquette etiquetteCourante;
    Artiste artisteCourant;

    while (!etiquettesProvisoires.isEmpty()) {
      // trouver l'etiquette provisoire de poids min et la deplacer vers les etiquettes definitives
      etiquetteCourante = etiquettesProvisoires.pollFirst();
      assert etiquetteCourante != null;
      artisteCourant = etiquetteCourante.getArtiste();

      etiquettesDefinitives.add(etiquetteCourante);

      // arreter la boucle si on a trouvé l'etiquette definitive de artisteDestination
      if (artisteDestination.equals(artisteCourant)) {
        break;
      }

      for (Mention mention : mentionsSortantes.get(artisteCourant)) {
        Artiste artisteVoisin = mention.getArtiste2();

        // voir methodes equals et hashCode de la classe interne Etiquette
        // => seul l'artiste est pris en compte, pas le poids
        // => peu importe le poids de l'etiquette que je passe a la methode contains
        if (etiquettesDefinitives.contains(new Etiquette(artisteVoisin, null))) {
          continue;
        }

        double nouveauPoids = etiquetteCourante.getPoids() + poidsMention(mention);

        // getOrDefault renvoie une valeur par default si get renvoie null
        Etiquette etiquetteVoisine = correspondanceArtisteEtiquette.getOrDefault(artisteVoisin,
            new Etiquette(artisteVoisin, Double.MAX_VALUE));

        double ancienPoids = etiquetteVoisine.getPoids();

        // met a jour le poids de l'etiquette provisoire si on a trouvé un poids plus faible
        if (nouveauPoids < ancienPoids) {
          // voir methodes equals et hashCode de la classe interne Etiquette
          // => s'il existe deja une etiquette avec cet artiste, elle sera remplacée
          //    quand on ajoutera une nouvelle etiquette avec ce meme artiste
          Etiquette nouvelleEtiquette = new Etiquette(artisteVoisin, nouveauPoids);
          etiquettesProvisoires.add(nouvelleEtiquette);
          correspondanceArtisteEtiquette.put(artisteVoisin, nouvelleEtiquette);
          chemins.put(artisteVoisin, artisteCourant);
        }
      }
    }

    // lance une excepetion si on a pas trouvé de chemin entre artisteSource et artisteDestination
    if (!etiquettesDefinitives.contains(new Etiquette(artisteDestination, null))) {
      String message = String.format("Aucun chemin entre %s et %s", artisteSource.getNom(),
          artisteDestination.getNom());
      throw new RuntimeException(message);
    }

  }

  private void afficherCheminMaxMentions() {
    // reconstruire le chemin
    Deque<Artiste> chemin = new ArrayDeque<>();
    Artiste a = artisteDestination;
    do {
      chemin.addLast(a);
    } while ((a = chemins.get(a)) != null);

    // afficher le chemin
    System.out.println("Longueur du chemin : " + (chemin.size() - 1));
    System.out.println(
        "Coût total du chemin : " + correspondanceArtisteEtiquette.get(artisteDestination)
            .getPoids());
    System.out.println("Chemin : ");

    while ((a = chemin.pollLast()) != null) {
      System.out.println(a);
    }

  }

  // Les methodes equals et hashCode de cette classe interne
  // ne prennent en compte que l'artiste, pas le poids
  static class Etiquette {

    private final Artiste artiste;
    private Double poids;

    public Etiquette(Artiste artiste, Double poids) {
      this.artiste = artiste;
      this.poids = poids;
    }

    public Artiste getArtiste() {
      return artiste;
    }

    public Double getPoids() {
      return poids;
    }

    public void setPoids(Double poids) {
      this.poids = poids;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Etiquette etiquette = (Etiquette) o;
      return Objects.equals(artiste, etiquette.artiste);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(artiste);
    }
  }

}
