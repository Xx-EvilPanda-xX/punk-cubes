package cubes;

import org.joml.Vector3f;

public interface Renderer {
        void create(Shader shader, Camera camera);

        void prepare();

        void render();

        Vector3f getTrans();

        Renderer setTrans(Vector3f trans);

        Vector3f getRotation();

        Renderer setRotation(Vector3f rotation);

        float getScale();

        Renderer setScale(float scale);
}
