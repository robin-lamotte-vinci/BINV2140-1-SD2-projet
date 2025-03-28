import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Dijkstra {

  private final Artiste source;
  private final Artiste destination;

  private Map<Artiste, Set<Mention>> mentionsSortantes;

  private Map<Artiste, Double> etiquettesProvisoires;
  private Map<Artiste, Double> etiquettesDefinitives;

  Map<Artiste, Artiste> chemins;

  public Dijkstra(Graph graph, Artiste source, Artiste destination) {
    this.mentionsSortantes = graph.getMentionsSortantes();
    this.source = source;
    this.destination = destination;


    trouverCheminMaxMentions();

    if (!etiquettesDefinitives.containsKey(destination)){
      String message = String.format("Aucun chemin entre %s et %s", source.getNom(), destination.getNom());
      throw new RuntimeException(message);
    } else {
      System.out.println("poids trouvé : " + etiquettesDefinitives.get(destination));

      afficherCheminMaxMentions();
    }

  }

  private double poidsMention(Mention m) {
    return (double) 1 / m.getNbMentions();
  }

  private void trouverCheminMaxMentions(){
    this.etiquettesDefinitives = new HashMap<>();
    this.etiquettesProvisoires = new HashMap<>();

    this.chemins = new HashMap<>();

    //this.cheminsProvisoires = new TreeMap<>((a1, a2) -> Double.compare(etiquettesProvisoires.getOrDefault(a1, Double.MAX_VALUE), etiquettesProvisoires.getOrDefault(a2, Double.MAX_VALUE)));

    etiquettesProvisoires.put(source, 0.0);

    Artiste artisteCourant = source;

    while (!destination.equals(artisteCourant) && !etiquettesProvisoires.isEmpty()){
      // trouver l'artiste de poids min
      Artiste a = etiquettesProvisoires
          .keySet()
          .stream()
          .min(Comparator.comparing((art) -> etiquettesProvisoires.get(art)))
          .orElseThrow(() -> new IllegalStateException(
              "La collection ne devrait jamais être vide à ce stade"));

      artisteCourant = a;

      etiquettesDefinitives.put(artisteCourant, etiquettesProvisoires.get(artisteCourant));
      etiquettesProvisoires.remove(artisteCourant);

      if (destination.equals(artisteCourant)) break;

      for (Mention mention : mentionsSortantes.get(artisteCourant)){
        Artiste artisteVoisin = mention.getArtiste2();
        if (etiquettesDefinitives.containsKey(artisteVoisin)){
          continue;
        }

        double nouveauPoids = etiquettesDefinitives.get(artisteCourant) + poidsMention(mention);
        double ancienPoids = etiquettesProvisoires.getOrDefault(artisteVoisin, Double.MAX_VALUE);
        if (nouveauPoids < ancienPoids){
          etiquettesProvisoires.remove(artisteVoisin); //probablement inutile
          etiquettesProvisoires.put(artisteVoisin, nouveauPoids);
          chemins.put(artisteVoisin, artisteCourant);
        }
      }

    }
  }

  private void afficherCheminMaxMentions(){

    // reconstruire le chemin
    Deque<Artiste> chemin = new ArrayDeque<>();
    Artiste a = destination;
    do {
      chemin.addLast(a);
    } while ( (a=chemins.get(a)) != null );
    //chemin.addLast(source);

    // afficher le chemin
    System.out.println("Longueur du chemin : " + (chemin.size() - 1) );
    System.out.println("Coût total du chemin : " + etiquettesDefinitives.get(destination));
    System.out.println("Chemin : ");

    while ((a = chemin.pollLast()) != null){
      System.out.println(a);
    }

  }
}
