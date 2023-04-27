import java.util.StringJoiner;

/**
 * Modélisation des paramètres de style d'une balise SVG
 */
public class Style {

    /**
     * Couleur de remplissage
     */
    private String fill;

    /**
     * Couleur du tracé
     */
    private String stroke;

    /**
     * Épaisseur du trait
     */
    private Double strokeWidth;

    /**
     * Transparence du remplissage
     */
    private Double fillOpacity;

    public String getFill() {
        return fill;
    }

    public Style setFill(String fill) {
        this.fill = fill;
        return this;
    }

    public String getStroke() {
        return stroke;
    }

    public Style setStroke(String stroke) {
        this.stroke = stroke;
        return this;
    }

    public Double getStrokeWidth() {
        return strokeWidth;
    }

    public Style setStrokeWidth(Double strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    public Double getFillOpacity() {
        return fillOpacity;
    }

    public Style setFillOpacity(Double fillOpacity) {
        this.fillOpacity = fillOpacity;
        return this;
    }

    /**
     * Renvoie la représentation du style sous forme d'une chaîne de caractères qui peut être insérée dans une
     * balise SVG.
     *
     * @return représentation du style au format SVG
     */
    public String toSVG() {
        StringJoiner elements = new StringJoiner(" ");
        if (fill != null) {
            elements.add("fill=\"" + fill + "\"");
        }
        if (stroke != null) {
            elements.add("stroke=\"" + stroke + "\"");
        }
        if (strokeWidth != null) {
            elements.add("stroke-width=\"" + strokeWidth + "\"");
        }
        if (fillOpacity != null) {
            elements.add("fill-opacity=\"" + fillOpacity + "\"");
        }

        return elements.toString();
    }
}
