package cubes;

import org.joml.Vector3f;

public interface Renderer {
        void create();

        void prepare(Shader shader, Camera camera, Vector3f trans, float scale, float rotate, boolean debug);

        void render(Shader shader, Camera camera, Vector3f trans, float scale, float rotate, boolean debug);
}
