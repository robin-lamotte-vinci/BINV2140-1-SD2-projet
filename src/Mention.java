public class Mention {
    private final Artiste artiste1;
    private final Artiste artiste2;

    private final int nbMentions;

    public Mention(Artiste artiste1, Artiste artiste2, int nbMentions) {
        this.artiste1 = artiste1;
        this.artiste2 = artiste2;
        this.nbMentions = nbMentions;
    }

    public Artiste getArtiste1() {
        return artiste1;
    }

    public Artiste getArtiste2() {
        return artiste2;
    }

    public int getNbMentions() {
        return nbMentions;
    }

    public double getPoids(){
        return (double) 1 /nbMentions;
    }

    @Override
    public String toString() {
        return "Mention{" +
            "artiste1=" + artiste1 +
            ", artiste2=" + artiste2 +
            ", nbMentions=" + nbMentions +
            '}';
    }
}

