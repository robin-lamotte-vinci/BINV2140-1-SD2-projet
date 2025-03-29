import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {
    private Map<String, Artiste> correspondanceStringArtiste;
    private Map<Integer, Artiste> correspondanceIdArtiste;
    private Map<Artiste, Set<Mention>> mentionsSortantes;
    //private Map<String, List<ArtisteEtPoids>> adjList;
    private Map<String, Integer> mentions;



    public Graph(String pathArtistesTxt, String pathMentionsTxt) {

        correspondanceStringArtiste = new HashMap<>();
        correspondanceIdArtiste = new HashMap<>();
        mentionsSortantes = new HashMap<>();
        mentions = new HashMap<>();
        //adjList = new HashMap<>();




        completerArtistes(pathArtistesTxt);

        completerMentions(pathMentionsTxt);


    }



    /**
     * Lis le fichier référencé par pathArtistesTxt et pour chaque ligne, ajoute un artiste au graph
     *
     * @param pathArtistesTxt
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
     * @param pathMentionsTxt
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
     * @param artiste
     */
    private void ajouterArtiste(Artiste artiste) {
        // faut-il checker si l'artiste est deja present ??
        // il faudra peut etre l'ajouter la gestion de ce cas...

        // il n'y a pas 2 fois le meme noms d'artiste, mais si c'etait le cas ca poserait probleme
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
     * @param mention
     */
    private void ajouterMention(Mention mention) {
        // pareil, j'ai pas mis de gestion des exceptions
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
    public boolean estMentionne(Artiste artiste1, Artiste artiste2) {
        // TODO rendre la methode privee;
        // elle reste publique pour l'instant pour mes tests
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
         // int[] distances = new int[mentionsSortantes.size()];  tableau pour stocker les distances de chaque chemin trouvé
        Mention mention = null;
        Artiste artisteCourant = artiste1;
        Queue<Artiste> sommetsNonAtteints = new ArrayDeque<>();



        Set<Artiste> sommets = new HashSet<>();
        int longueurChemin = 1;
        do {
            for (Mention m : mentionsSortantes.get(artisteCourant)) {
                if (artiste2.equals(m.getArtiste2())) {
                    System.out.println("Longueur du chemin : " + longueurChemin);
                    // imprimer le chemin
                    return;
                }
                if (!sommets.contains(m.getArtiste2())) {
                    sommetsNonAtteints.add(m.getArtiste2());
                }

            }
            artisteCourant = sommetsNonAtteints.poll();
            sommets.add(artisteCourant);
            longueurChemin++;

        } while (!sommetsNonAtteints.isEmpty());
        System.out.println("Longueur du chemin : " + longueurChemin);
        // aucun chemin entre ls 2
        // Mettre une exception car si y a pas de chemin il faut le mettre
        /*
        for(Mention m : mentionsSortantes.get(artiste2)) {
            sommets.add(m.getArtiste2());
        } */

        /*
        for(Mention m : mentionsSortantes.get(artiste1)) {
            for (Artiste a : sommets) {
                if (m.getArtiste1().equals(artiste1) && !m.getArtiste2().equals(a)) {
                    sommetsNonAtteints.add(a);
                }
            }
        }
        while (!mentionsSortantes.get(artiste1).contains(mention) || mention==null) {
            Artiste ar = sommetsNonAtteints.poll();
            for(Mention m : mentionsSortantes.get(ar)) {
                for (Artiste a : sommets) {
                    if (m.getArtiste1().equals(ar) && !m.getArtiste2().equals(a)) {
                        sommetsNonAtteints.add(a);
                    }
                }
            }
            if(sommetsNonAtteints.isEmpty() || mention!=null) {
                assert mention != null;
                System.out.println("Longueur du chemin : " + mentionsSortantes.get(artiste1).size());
            }

        }

         */








    // TODO trouver le chemin le plus court
        // artiste courant = sommet de depart
        // fixer smmet depart
        // parcourir les adjacences de ce somment
            // pour touts les artists mentionnés
            // si on ne l'a pas encore visité, l'joutr a la file
            // si l'artiste == destionation => returns
        // artiste courant == premier de la file
        // recommence la boucle
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
