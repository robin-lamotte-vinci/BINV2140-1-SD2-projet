import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {
    private Map<String, Artiste> correspondanceStringArtiste;
    private Map<Integer, Artiste> correspondanceIdArtiste;
    private Map<Artiste, Set<Mention>> mentionsSortantes;
    private LinkedList<Mention>[] listeAdjacence;

    public Graph(String pathArtistesTxt, String pathMentionsTxt) {

        correspondanceStringArtiste = new HashMap<>();
        correspondanceIdArtiste = new HashMap<>();
        mentionsSortantes = new HashMap<>();
        listeAdjacence = new LinkedList[35300];
        for (int i = 0; i < listeAdjacence.length; i++) {
            listeAdjacence[i] = new LinkedList<>();
        }


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

                for(int i=0;i<mentionStringTab.length;i++) {
                    listeAdjacence[i%mentionStringTab.length].add(mention); // Construit la liste d'adjacence
                }
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

    public void trouverCheminLePlusCourt(String nomArtiste1, String nomArtiste2) {
        Artiste artiste1 = correspondanceStringArtiste.get(nomArtiste1);
        Artiste artiste2 = correspondanceStringArtiste.get(nomArtiste2);
        int[] distances = new int[listeAdjacence.length]; // tableau pour stocker les distances de chaque chemin trouvé
        boolean[] visited = new boolean[listeAdjacence.length]; // tableau pour connaître les sommets visités
        PriorityQueue<Mention> pq = new PriorityQueue<>(listeAdjacence.length,Comparator.comparing(Mention::getPoids));
        Arrays.fill(distances, Integer.MAX_VALUE); // initialise toutes les distances de chaque chemin à Integer.MAX_VALUE
        distances[artiste1.getId()]=0; // on commence au sommet de départ (l'artiste 1)
        while (!pq.isEmpty()) { // on boucle jusqu'assez qu'on trouve le chemin le plus court

            Artiste ar = pq.poll().getArtiste2(); // on récupère l'artiste auquel il est lié
            if (!visited[ar.getId()]) {
                visited[ar.getId()] = true; // on suppose qu'il a été visité car on a besoin de trouver le chemin
                for (Mention m : listeAdjacence[ar.getId()]) {
                    Artiste voisin = m.getArtiste2();  // on récupère chaque mention de l'artiste 2 et chaque poids pour trouver un chemin
                    Double poids = m.getPoids();
                    if (!visited[voisin.getId()] && distances[ar.getId()] + poids.intValue() // Si on trouve un chemin plus court entre l'artiste 1
                            < distances[voisin.getId()]) {  // et le voisin de l'artiste 2 qui revient jusqu'à l'artiste 1 on l'ajoute à la PriorityQueue
                       distances[voisin.getId()] = distances[ar.getId()] + poids.intValue();
                       pq.add(m);
                    }
                }
            }



    }






    // TODO trouver le chemin le plus court
}

public void trouverCheminMaxMentions(String nomArtiste1, String nomArtiste2) {
    Artiste artiste1 = correspondanceStringArtiste.get(nomArtiste1);
    Artiste artiste2 = correspondanceStringArtiste.get(nomArtiste2);

    // TODO trouver le chemin le plus fortement connecté

    Map<Artiste, Double> coutChemin = new HashMap<>();
    coutChemin.put(artiste1, 0.0);

    //PriorityQueue<Artiste> queue = new PriorityQueue<>(Comparator.comparingInt(maxMentions::get).reversed());

    PriorityQueue<Artiste> queue = new PriorityQueue<>((a1, a2) -> {
        int compareCout = Double.compare(coutChemin.get(a2), coutChemin.get(a1));
        if (compareCout == 0) {
            return a1.getNom().compareTo(a2.getNom());
        }
        return compareCout;
    });

    Set<Artiste> dejaVisite = new HashSet<>();
    Map<Artiste, Artiste> predecesseur = new HashMap<>();

    queue.add(artiste1);

    while (!queue.isEmpty()) {
        Artiste currentArtiste = queue.poll();
        if(!dejaVisite.add(currentArtiste)) continue;

        double currentCout = coutChemin.get(currentArtiste);

        if (currentArtiste.equals(artiste2)) {
            afficherChemin(predecesseur, artiste1, artiste2, currentCout);
            return;
        }

        for (Mention mention : mentionsArtiste(currentArtiste)) {
            Artiste voisin = mention.getArtiste2();
            if(dejaVisite.contains(voisin)) continue;

            double newCout = currentCout + (1.0 / mention.getNbMentions());

            if (!coutChemin.containsKey(voisin) || newCout > coutChemin.get(voisin)) {
                coutChemin.put(voisin, newCout);
                predecesseur.put(voisin, currentArtiste);

                queue.add(voisin);
            }
        }
    }
    throw new RuntimeException("Aucun chemin trouvé entre " + nomArtiste1 + " et " + nomArtiste2);
}

private void afficherChemin(Map<Artiste, Artiste> predecesseur, Artiste debut, Artiste fin, double coutTotal) {
    List<String> chemin = new ArrayList<>();
    Artiste courant = fin;
    int longueur = 0;

    while (courant != null) {
        chemin.add(courant.getNom());
        courant = predecesseur.get(courant);
        if (courant != null) longueur++;
    }
    Collections.reverse(chemin);

    System.out.println("Longueur du chemin : "+longueur + " \nCoût total du chemin : " + coutTotal + " Chemin : \n" + String.join("\n", chemin));
}

public void trouverCheminMaxMentionsBis(String nomArtiste1, String nomArtiste2){
    Artiste artiste1 = correspondanceStringArtiste.get(nomArtiste1);
    Artiste artiste2 = correspondanceStringArtiste.get(nomArtiste2);

    Map<Artiste, Double> etiquettesProvisoires = new HashMap<>();
    Map<Artiste, Double> etiquettesDefinitives = new HashMap<>();

    Queue<Artiste> chemin = new ArrayDeque<>();

    for (Mention mention : mentionsSortantes.get(artiste1)){
        Artiste a = mention.getArtiste2();
        etiquettesProvisoires.put(a, mention.getPoids());
    }

    etiquettesDefinitives.put(artiste1, 0.0);
    chemin.add(artiste1);

    while (!etiquettesProvisoires.isEmpty()) {
        Map.Entry<Artiste, Double> etiquetteMin = etiquettesProvisoires
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow(() -> new IllegalStateException("La collection ne devrait jamais être vide à ce stade"));

        Artiste artistePoidsMin = etiquetteMin.getKey();
        Double poidsMin = etiquetteMin.getValue();

        etiquettesProvisoires.remove(artistePoidsMin);
        etiquettesDefinitives.put(artistePoidsMin, poidsMin);
        chemin.add(artistePoidsMin);

        if (artistePoidsMin.equals(artiste2)) {
            //on a trouvé le chemin definitif
            System.out.println("Longueur du chemin : " + (chemin.size()-1));
            System.out.println("Coût total du chemin : " + poidsMin);
            System.out.println("Chemin :");

            Artiste a;
            while( ( a = chemin.poll()) != null) {
                System.out.println(a);
            }
            return;
        }

        for (Mention mention : mentionsSortantes.get(artistePoidsMin)){
            Artiste aMentionne = mention.getArtiste2();

            // si etiquettesDefinitives contient deja cet artiste, continue
            if (etiquettesDefinitives.containsKey(aMentionne)) continue;

            double nouveauPoids = poidsMin + mention.getPoids();

            Double poidsActuel = etiquettesProvisoires.get(aMentionne); // null si artiste pas encore présent
            if (poidsActuel == null ||  poidsActuel > nouveauPoids){
                etiquettesProvisoires.put(aMentionne, nouveauPoids);
            }
        }
    }

    //si on arrive ici, c'est qu'on a pas trouvé de chemin
    String message = String.format("Aucun chemin entre %s et %s", nomArtiste1, nomArtiste2);
    throw new RuntimeException( message );

}
}
