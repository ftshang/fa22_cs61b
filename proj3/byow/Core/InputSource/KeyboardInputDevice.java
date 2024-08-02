package byow.Core.InputSource;

import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

public class KeyboardInputDevice implements InputSource {

    private final int width;
    private final int height;

    public KeyboardInputDevice(int w, int h) {
        width = w;
        height = h;
    }

    @Override
    public char getNextKey() {
        while (true) {
            if (!StdDraw.hasNextKeyTyped() && isMouseOnScreen()) {
                return '+';
            }

            if (!StdDraw.hasNextKeyTyped() && !isMouseOnScreen()) {
                return '-';
            }

            if (StdDraw.hasNextKeyTyped()) {
                return Character.toUpperCase(StdDraw.nextKeyTyped());
            }
        }
    }

    private boolean isMouseOnScreen() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();

        return x >= 0 && x < width && y >= 3 && y < height;
    }

    @Override
    public boolean possibleNextInput() {
        return true;
    }
}
