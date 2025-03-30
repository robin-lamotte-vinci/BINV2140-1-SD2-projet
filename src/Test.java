public class Test {

  public static void main(String[] args) {

    Graph graph = new Graph("artists.txt", "mentions.txt");

    // graph.trouverCheminLePlusCourt("Kendji Girac", "The Beatles");

    System.out.println("--------------------------");

    // graph.trouverCheminMaxMentions("The Beatles", "Kendji Girac");

    System.out.println("--------------------------");

    graph.trouverCheminMaxMentions("Juliette Armanet", "The Beatles");
    graph.trouverCheminLePlusCourt("Juliette Armanet", "The Beatles");

  }
}
