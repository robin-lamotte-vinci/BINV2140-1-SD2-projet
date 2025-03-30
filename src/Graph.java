import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Graph {

  private final Map<String, Artiste> correspondanceStringArtiste;
  private final Map<Integer, Artiste> correspondanceIdArtiste;
  private final Map<Artiste, Set<Mention>> mentionsSortantes;


  public Graph(String pathArtistesTxt, String pathMentionsTxt) {
    correspondanceStringArtiste = new HashMap<>();
    correspondanceIdArtiste = new HashMap<>();
    mentionsSortantes = new HashMap<>();

    completerArtistes(pathArtistesTxt);
    completerMentions(pathMentionsTxt);
  }

  public Map<Artiste, Set<Mention>> getMentionsSortantes() {
    return mentionsSortantes;
  }

  /**
   * Trouve et affiche le chemin le plus court entre 2 artistes
   *
   * @param nomArtiste1 le nom de l'artiste source
   * @param nomArtiste2 le nom de l'artiste d destination
   */
  public void trouverCheminLePlusCourt(String nomArtiste1, String nomArtiste2) {
    Artiste artisteSource = correspondanceStringArtiste.get(nomArtiste1);
    Artiste artisteDestination = correspondanceStringArtiste.get(nomArtiste2);

    Queue<Artiste> file = new LinkedList<>();
    Set<Artiste> artistesVisites = new HashSet<>(); // ensemble des sommets deja atteints
    Map<Artiste, Mention> chemins = new HashMap<>();

    file.add(artisteSource);

    Artiste artisteCourant;
    while ((artisteCourant = file.poll()) != null) {
      for (Mention mention : mentionsArtiste(artisteCourant)) {
        Artiste artisteMentionne = mention.getArtiste2();
        if (artistesVisites.contains(artisteMentionne)) {
          continue;
        }
        file.add(artisteMentionne);
        chemins.put(artisteMentionne, mention);
        artistesVisites.add(artisteMentionne);
        if (artisteDestination.equals(artisteMentionne)) {
          break;
        }
      }
    }
    if (!artistesVisites.contains(artisteDestination)) {
      // exception s'il n'existe pas de chemin entre artisteSource et artisteDestination
      String message = String.format("Aucun chemin entre %s et %s", nomArtiste1, nomArtiste2);
      throw new RuntimeException(message);
    }
    afficherChemin(chemins, artisteSource, artisteDestination);

  }

  /**
   * Trouve et affiche le chemin qui contient le maximum de mentions entre 2 artistes
   *
   * @param nomArtiste1 le nom de l'artiste source
   * @param nomArtiste2 le nom de l'artiste d destination
   */
  public void trouverCheminMaxMentions(String nomArtiste1, String nomArtiste2) {
    Artiste artiste1 = correspondanceStringArtiste.get(nomArtiste1);
    Artiste artiste2 = correspondanceStringArtiste.get(nomArtiste2);

    Dijkstra dijkstra = new Dijkstra(this, artiste1, artiste2);
    Map<Artiste, Mention> chemins = dijkstra.trouverCheminMaxMentions();

    if (chemins==null) {
      // exception s'il n'existe pas de chemin entre artisteSource et artisteDestination
      String message = String.format("Aucun chemin entre %s et %s", nomArtiste1, nomArtiste2);
      throw new RuntimeException(message);
    }

    afficherChemin(chemins, artiste1, artiste2);

  }

  /**
   * Lis le fichier référencé par pathArtistesTxt et pour chaque ligne, ajoute un artiste au graph
   *
   * @param pathArtistesTxt path du fichier txt qui contient les artistes
   */
  private void completerArtistes(String pathArtistesTxt) {
    try (BufferedReader lecteur = new BufferedReader(new FileReader(pathArtistesTxt))) {
      String artisteString;
      while ((artisteString = lecteur.readLine()) != null) {
        String[] artisteStringTab = artisteString.split(",");

        int id = Integer.parseInt(artisteStringTab[0]);
        String nom = artisteStringTab[1];
        String categories = artisteStringTab[2];

        Artiste artiste = new Artiste(id, nom, categories);
        ajouterArtiste(artiste);
      }
    } catch (IOException e) {
      e.printStackTrace(); // Gère l'exception en cas d'erreur (ex : fichier introuvable)
    }
  }

  /**
   * Lis le fichier référencé par pathMentionsTxt et pour chaque ligne, ajoute une mention au graph
   *
   * @param pathMentionsTxt path du fichier txt qui contient les mentions
   */
  private void completerMentions(String pathMentionsTxt) {
    try (BufferedReader lecteur = new BufferedReader(new FileReader(pathMentionsTxt))) {
      String mentionString;
      while ((mentionString = lecteur.readLine()) != null) {
        String[] mentionStringTab = mentionString.split(",");

        int idArtiste1 = Integer.parseInt(mentionStringTab[0]);
        int idArtiste2 = Integer.parseInt(mentionStringTab[1]);
        int nbMentions = Integer.parseInt(mentionStringTab[2]);

        Artiste artiste1 = correspondanceIdArtiste.get(idArtiste1);
        Artiste artiste2 = correspondanceIdArtiste.get(idArtiste2);

        Mention mention = new Mention(artiste1, artiste2, nbMentions);

        ajouterMention(mention);

      }
    } catch (IOException e) {
      e.printStackTrace(); // Gère l'exception en cas d'erreur (ex : fichier introuvable)
    }
  }

  /**
   * Ajoute l'artiste au graph
   *
   * @param artiste l'artiste à ajouter
   */
  private void ajouterArtiste(Artiste artiste) {
    correspondanceStringArtiste.put(artiste.getNom(), artiste);
    correspondanceIdArtiste.put(artiste.getId(), artiste);

    mentionsSortantes.put(artiste, new HashSet<>());
  }

  /**
   * Ajoute la mention au graph
   *
   * @param mention la mention à ajouter
   */
  private void ajouterMention(Mention mention) {
    Artiste artiste = mention.getArtiste1();
    mentionsArtiste(artiste).add(mention);
  }

  /**
   * Renvoie l'ensemble des mentions sortantes de l'artiste
   *
   * @param artiste
   * @return ensemble des mentions sortantes d'artiste
   */
  private Set<Mention> mentionsArtiste(Artiste artiste) {
    return mentionsSortantes.get(artiste);
  }

  /**
   * Affiche le chemin entre trouvé entre 2 artistes
   *
   * @param chemins
   * @param artisteSource
   * @param artisteDestination
   */
  private void afficherChemin(Map<Artiste, Mention> chemins, Artiste artisteSource,
      Artiste artisteDestination) {
    // reconstruire le chemin
    Deque<Artiste> chemin = new ArrayDeque<>();
    Artiste a = artisteDestination;
    Mention m;
    double poidsChemin = 0.0;
    do {
      chemin.addLast(a);
      m = chemins.get(a);
      a = m.getArtiste1();
      poidsChemin += (double) 1 / m.getNbMentions();
    } while (a != artisteSource);
    chemin.add(artisteSource);

    // afficher le chemin
    System.out.println("Longueur du chemin : " + (chemin.size() - 1));
    System.out.println(
        "Coût total du chemin : " + poidsChemin);
    System.out.println("Chemin : ");

    while ((a = chemin.pollLast()) != null) {
      System.out.println(a);
    }
  }

}
