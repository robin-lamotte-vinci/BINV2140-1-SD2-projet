public class Arc {
    private final Artiste artiste1;
    private final Artiste artiste2;

    private final int nbMentions;

    public Arc(Artiste artiste1, Artiste artiste2, int nbMentions) {
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

    @Override
    public String toString() {
        return "Arc{" +
                "artiste1=" + artiste1 +
                ", artiste2=" + artiste2 +
                ", nbMentions=" + nbMentions +
                '}';
    }
}

