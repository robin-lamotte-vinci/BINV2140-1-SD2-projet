public class Test2 {

  public static void main(String[] args) {
    Graph graph = new Graph("artists.txt", "mentions.txt");

    graph.trouverCheminMaxMentions("The Beatles", "Kendji Girac");

    //graph.trouverCheminMaxMentionsTer("Juliette Armanet",  "The Beatles");

    //graph.chemin("The Beatles", "Kendji Girac");
  }
}
