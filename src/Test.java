public class Test {

  public static void main(String[] args) {
    Graph graph = new Graph("artists.txt", "mentions.txt");


    graph.trouverCheminMaxMentions("The Beatles", "Kendji Girac");

    graph.trouverCheminMaxMentions("Juliette Armanet",  "The Beatles");


  }
}
