public class Test {

  public static void main(String[] args) {
    Graph graph = new Graph("artists.txt", "mentions.txt");


    Artiste artiste1 = new Artiste(114,"2000 Won");
    Artiste artiste2 = new Artiste(149,"2NE1");

    boolean estMentionne = graph.estMentionne(artiste1, artiste2);
    System.out.println("RESULT: " + estMentionne);
  }
}
