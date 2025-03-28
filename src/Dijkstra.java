import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Dijkstra {

  private final Artiste source;
  private final Artiste destination;

  private final Map<Artiste, Set<Mention>> mentionsSortantes;

  private final Map<Artiste, Double> etiquettesProvisoires;
  private final Map<Artiste, Double> etiquettesDefinitives;
  TreeSet<Etiquette> queue ;

  Map<Artiste, Artiste> chemins;

  public Dijkstra(Graph graph, Artiste source, Artiste destination) {
    this.mentionsSortantes = graph.getMentionsSortantes();
    this.source = source;
    this.destination = destination;

    this.etiquettesDefinitives = new HashMap<>();
    this.etiquettesProvisoires = new HashMap<>();

    this.chemins = new HashMap<>();
    //this.queue = new TreeSet<>(Comparator.comparingDouble(Etiquette::getPoids));

    this.queue = new TreeSet<>(
        Comparator.comparingDouble(
            etiquette -> etiquette.getPoids() != null ? etiquette.getPoids() : Double.MAX_VALUE
        )
    );

    trouverCheminMaxMentions();
    afficherCheminMaxMentions();

  }

  private double poidsMention(Mention m) {
    return (double) 1 / m.getNbMentions();
  }

  private void trouverCheminMaxMentions(){


    //this.cheminsProvisoires = new TreeMap<>((a1, a2) -> Double.compare(etiquettesProvisoires.getOrDefault(a1, Double.MAX_VALUE), etiquettesProvisoires.getOrDefault(a2, Double.MAX_VALUE)));

    etiquettesProvisoires.put(source, 0.0);
    //queue.add(new Etiquette(source, 0.0));

    Artiste artisteCourant = source;

    while (!destination.equals(artisteCourant) && !etiquettesProvisoires.isEmpty()){
      // trouver l'artiste de poids min

      Artiste a = etiquettesProvisoires
          .keySet()
          .stream()
          .min(Comparator.comparing((art) -> etiquettesProvisoires.get(art)))
          .orElseThrow(() -> new IllegalStateException(
              "La collection ne devrait jamais être vide à ce stade"));


      //Etiquette e = queue.pollFirst();
      artisteCourant = a;

      etiquettesDefinitives.put(artisteCourant, etiquettesProvisoires.get(artisteCourant));
      etiquettesProvisoires.remove(artisteCourant);
      //queue.remove(new Etiquette(artisteCourant, null));

      if (destination.equals(artisteCourant)) break;

      for (Mention mention : mentionsSortantes.get(artisteCourant)){
        Artiste artisteVoisin = mention.getArtiste2();
        if (etiquettesDefinitives.containsKey(artisteVoisin)){
          continue;
        }

        if (etiquettesDefinitives.get(artisteCourant) == null) {
          System.out.println("OHO");
        }
        double nouveauPoids = etiquettesDefinitives.get(artisteCourant) + poidsMention(mention);
        double ancienPoids = etiquettesProvisoires.getOrDefault(artisteVoisin, Double.MAX_VALUE);
        if (nouveauPoids < ancienPoids){
          //etiquettesProvisoires.remove(artisteVoisin); //probablement inutile
          //queue.remove(new Etiquette(artisteVoisin, null)); //probablement inutile
          etiquettesProvisoires.put(artisteVoisin, nouveauPoids);
          //queue.add(new Etiquette(artisteVoisin, nouveauPoids));
          chemins.put(artisteVoisin, artisteCourant);
        }
      }

    }

    if (!etiquettesDefinitives.containsKey(destination)) {
      String message = String.format("Aucun chemin entre %s et %s", source.getNom(), destination.getNom());
      throw new RuntimeException(message);
    }

  }

  private void afficherCheminMaxMentions(){

    // reconstruire le chemin
    Deque<Artiste> chemin = new ArrayDeque<>();
    Artiste a = destination;
    do {
      chemin.addLast(a);
    } while ( (a=chemins.get(a)) != null );

    // afficher le chemin
    System.out.println("Longueur du chemin : " + (chemin.size() - 1) );
    System.out.println("Coût total du chemin : " + etiquettesDefinitives.get(destination));
    System.out.println("Chemin : ");

    while ((a = chemin.pollLast()) != null){
      System.out.println(a);
    }

  }


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
