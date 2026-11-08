package ruipokoim.neonclient.utils.render.color;

public class Color {
    public static final Color CLEAR = new Color(0, 0, 0, 0);
    public int r, g, b, a;

    public Color(int r, int g, int b, int a) {
        this.r = r; this.g = g; this.b = b; this.a = a;
    }
    public Color() {
        this(255, 255, 255, 255);
    }
    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }
    public int getArgb(int alphaOverride) {
        return ((alphaOverride & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
    public int getRgb() {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
    public void set(Color color){
        this.a = color.a;
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
    }
    public void set(int a, int r, int g, int b){
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
    }
}