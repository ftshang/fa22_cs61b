package byow.Core.Map;

import byow.Core.Engine;
import byow.TileEngine.TERenderer;

public class MapVisualTest {

    public static void main(String[] args) {

        TERenderer ter = new TERenderer();

        ter.initialize(80, 30);
        Engine engine = new Engine();

        MapGenerator map = new MapGenerator(80, 30, 3225);

        ter.renderFrame(map.getWorld());
    }
}
