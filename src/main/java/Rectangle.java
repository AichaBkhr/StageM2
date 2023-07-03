import java.util.Locale;

public class Rectangle extends Tag {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private  String type;

    public Rectangle( int x, int y, int width, int height) {
        super("rect");
        //this.type=type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    @Override
    public String getParameters() {
        return String.format(Locale.US, "x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\"", (float) x,(float)  y, (float) width,(float)  height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
