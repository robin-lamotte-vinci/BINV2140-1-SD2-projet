import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {
    private Map<String, Artiste> correspondanceStringArtiste;
    private Map<Integer, Artiste> correspondanceIdArtiste;
    private Map<Artiste, Set<Mention>> mentionsSortantes;
    private Map<String, List<ArtisteEtPoids>> adjList;
    private Map<String, Integer> mentions;



    public Graph(String pathArtistesTxt, String pathMentionsTxt) {

        correspondanceStringArtiste = new HashMap<>();
        correspondanceIdArtiste = new HashMap<>();
        mentionsSortantes = new HashMap<>();
        mentions = new HashMap<>();
        adjList = new HashMap<>();




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

    public void trouverCheminLePlusCourt(String nomArtiste1, String nomArtiste2) {
        Artiste artiste1 = correspondanceStringArtiste.get(nomArtiste1);
        Artiste artiste2 = correspondanceStringArtiste.get(nomArtiste2);
         // int[] distances = new int[mentionsSortantes.size()];  tableau pour stocker les distances de chaque chemin trouvé
        PriorityQueue<Noeud> filePriorite = new PriorityQueue<>(Comparator.comparingInt(a -> a.cout));
        Map<String,Integer> distances = new HashMap<>();
        Map<String, String> precedent = new HashMap<>(); // pour stocker le chemin
        for(String artiste : adjList.keySet()) {
            distances.put(artiste, Integer.MAX_VALUE); // initialiser toutes les distances à l'infini
        }
        distances.put(artiste1.getNom(), 0);
        filePriorite.add(new Noeud(artiste1.getNom(), 0)); // ajouter le sommet de départ à la file de priorité
        while (!filePriorite.isEmpty()) {
            Noeud noeudCourant = filePriorite.poll();
            if (noeudCourant.artiste.equals(artiste2.getNom())) {
                afficherListeArtistes(precedent, artiste2.getNom());
                system.out.println("Coût du chemin : " + noeudCourant.cout);
                return;
            }
            for (ArtisteEtPoids voisin : adjList.getOrDefault(noeudCourant.artiste, new ArrayList<>())) {
                String voisinNom = voisin.artiste;
                int nouveauCout = noeudCourant.cout + voisin.poids;
                if (nouveauCout < distances.get(voisinNom)) {
                    distances.put(voisinNom, nouveauCout);
                    precedent.put(voisinNom, noeudCourant.artiste); // mettre à jour le chemin
                    filePriorite.add(new Noeud(voisinNom, nouveauCout)); // ajouter le voisin à la file de priorité
                }
            }


        }
        
        // distance de l'artiste de départ à lui-même est 0
      /*   Queue<Artiste> sommetsNonAtteints = new ArrayDeque<>();



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
private void afficherListeArtistes(Map<String, String> precedent, String fin) {
    List<String> path = new ArrayList<>();
    for (String at = fin; at != null; at = precedent.get(at)) {
        path.add(at);
    }
    Collections.reverse(path);
    System.out.println("Chemin :");
    for (String artist : path) {
        System.out.println(artist);
    }
}
private static class ArtisteEtPoids {
    String artiste;
    int poids;

    ArtisteEtPoids(String artiste, int poids) {
        this.artiste = artiste;
        this.poids = poids;
    }
}
private static class Noeud {
    String artiste;
    int cout;

    Noeud(String artiste, int cout) {
        this.artiste = artiste;
        this.cout = cout;
    }
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
