public class Test {

  public static void main(String[] args) {
    Graph graph = new Graph("artists.txt", "mentions.txt");


    Artiste artiste1 = new Artiste(114,"2000 Won");
    Artiste artiste2 = new Artiste(149,"2NE1");
    Mention mention = new Mention(artiste1,artiste2,0);


    Artiste artiste3 = new Artiste(234,"David Guetta");
    Mention mention1 = new Mention(artiste3,artiste1,1);
    Mention mention2 = new Mention(artiste3,artiste2,2);
    graph.ajouterArtiste(artiste3);
    graph.ajouterMention(mention1);
    graph.mentionsArtiste(artiste3).add(mention1);
    graph.mentionsArtiste(artiste3).add(mention2);
    System.out.println(graph.mentionsArtiste(artiste3).size());




    boolean estMentionne = graph.estMentionne(artiste1, artiste2);
    System.out.println("RESULT: " + estMentionne);
  }
}
