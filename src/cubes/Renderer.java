package cubes;

import org.joml.Vector3f;

public interface Renderer {
        void create(Shader shader, Camera camera);

        void prepare(boolean debug);

        void render(boolean debug);

        Vector3f getTrans();

        Renderer setTrans(Vector3f trans);

        float getRotation();

        Renderer setRotation(float rotation);

        float getScale();

        Renderer setScale(float scale);
}
