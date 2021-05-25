package cubes;

import org.joml.Vector3f;

public interface Renderer {
        public void create();
        public void prepare(Shader shader, Camera camera, Vector3f trans, float scale, float rotate, boolean debug);
        public void render(Shader shader, Camera camera, Vector3f trans, float scale, float rotate, boolean debug);
}
