import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    if (correspondanceStringArtiste.containsKey(artiste.getNom())) {
      System.out.println("DOUBLONS");
    }

    String nom = artiste.getNom();
    correspondanceStringArtiste.put(nom, artiste);

    int id = artiste.getId();
    correspondanceIdArtiste.put(id, artiste);

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
   * Verifie si l'artiste1 contient une mention vers l'artiste2
   *
   * @param artiste1
   * @param artiste2
   * @return true si artiste1 contient une mention vers artiste2
   */
  private boolean estMentionne(Artiste artiste1, Artiste artiste2) {
    // TODO rendre la methode privee;
    for (Mention mention : mentionsArtiste(artiste1)) {
      if (mention.getArtiste2().equals(artiste2)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Trouve et affiche le chemin le plus court entre 2 artistes
   *
   * @param nomArtiste1 le nom de l'artiste source
   * @param nomArtiste2 le nom de l'artiste d destination
   */
  public void trouverCheminLePlusCourt(String nomArtiste1, String nomArtiste2) {
    Artiste artiste1 = correspondanceStringArtiste.get(nomArtiste1);
    Artiste artiste2 = correspondanceStringArtiste.get(nomArtiste2);

    // TODO trouver le chemin le plus court
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

  }


}
