
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public class SVG extends Tag {
    /**
     * Largeur de l'image
     */
    private double width;

    /**
     * Hauteur de l'image
     */
    private double height;

    /**
     * Liste d'éléments contenus dans la balise SVG
     */
    private List<Tag> content;

    public SVG(double width, double height) {
        super("svg");
        this.width = width;
        this.height = height;
        this.content = new LinkedList<>();
    }
    @Override
    public String getParameters() {
        return "xmlns=\"http://www.w3.org/2000/svg\" width=\"" + width + "\" height=\"" + height + "\"";
    }

    @Override
    public String getContent() {
        StringBuilder builder = new StringBuilder();
        for (Tag t: content) {
            builder.append(t.toSVG());
        }
        return builder.toString();

    }

    /**
     * Ajoute une balise à l'image
     * @param tag balise à ajouter à l'image
     */
    public void add(Tag tag) {
        content.add(tag);
    }

    /**
     * Exporte l'image au format SVG dans un fichier
     *
     * @param filename le nom du fichier SVG à créer représentant l'image
     */
    public void saveAsFile(String filename) {
        PrintStream out;
        try {
            out = new PrintStream(new FileOutputStream(filename));
            out.print("<?xml version=\"1.0\" standalone=\"no\"?><!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
            out.print(toSVG());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
