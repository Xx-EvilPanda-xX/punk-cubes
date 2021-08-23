package cubes;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL42;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class TextureRenderer implements Renderer {
        public static final int MAX_INSTANCES = 32767;

        TexturedMesh mesh;
        private boolean created = false;
        private int meshItr;

        private Shader shader;
        private Camera camera;

        private Vector3f trans = new Vector3f(0.0f, 0.0f, 0.0f);
        private float scale = 1.0f;
        private Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);

        public TextureRenderer(TexturedMesh mesh) {
                this.mesh = mesh;
        }

        public TextureRenderer(String modelPath) {
                TexturedMesh mesh = new TexturedMesh(modelPath);
                this.mesh = mesh;
        }

        public void create(Shader shader, Camera camera) {
                this.shader = shader;
                this.camera = camera;

                for (int i = 0; i < mesh.meshes.size(); i++) {
                        mesh.setVbos(mesh.getVaos()[i].storeBuffer(0, 3, mesh.getVertices().get(i)), i);
                        mesh.setTbos(mesh.getVaos()[i].storeBuffer(1, 2, mesh.getTextureCoords().get(i)), i);
                        mesh.setNbos(mesh.getVaos()[i].storeBuffer(2, 3, mesh.getNormals().get(i)), i);
                        if (mesh.isIndexed()) {
                                mesh.getVaos()[i].storeIndices(mesh.getIndices().get(i));
                        }

                        mesh.setUaos(GL15.glGenBuffers(), i);
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.getUaos()[i]);
                        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 64 * MAX_INSTANCES, GL15.GL_STATIC_DRAW);

                        GL30.glBindVertexArray(mesh.getVaos()[i].getHandle());
                        GL30.glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, 64, 0);
                        GL30.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, 64, 16);
                        GL30.glVertexAttribPointer(5, 4, GL11.GL_FLOAT, false, 64, 32);
                        GL30.glVertexAttribPointer(6, 4, GL11.GL_FLOAT, false, 64, 48);

                        GL42.glVertexAttribDivisor(3, 1);
                        GL42.glVertexAttribDivisor(4, 1);
                        GL42.glVertexAttribDivisor(5, 1);
                        GL42.glVertexAttribDivisor(6, 1);

                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                        GL30.glBindVertexArray(0);
                }

                created = true;
        }

        public void prepare() {
                Matrix4f model = new Matrix4f().translate(trans).scale(scale, scale, scale).rotate(rotation.x, 1.0f, 1.0f, 0.0f).rotate(rotation.y, 0.0f, 1.0f, 0.0f).rotate(rotation.z, 0.0f, 0.0f, 1.0f);

                Matrix4f proj = camera.getProjectionMatrix();

                Matrix4f view = camera.getViewMatrix();

                float[] matrix = new float[16];
                for (int j = 0; j < 4; j++) {
                        for (int k = 0; k < 4; k++) {
                                matrix[(j * 4) + k] = model.get(j, k);
                        }
                }
                GL30.glBindVertexArray(mesh.getVaos()[meshItr].getHandle());
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.getUaos()[meshItr]);

                FloatBuffer buf = (FloatBuffer) MemoryUtil.memAllocFloat(16).put(matrix).flip();
                GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buf);
                MemoryUtil.memFree(buf);

                GL30.glEnableVertexAttribArray(3);
                GL30.glEnableVertexAttribArray(4);
                GL30.glEnableVertexAttribArray(5);
                GL30.glEnableVertexAttribArray(6);

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                GL30.glBindVertexArray(0);

                shader.setUniform("projection", proj);
                shader.setUniform("view", view);

                shader.setUniform("lightPos", Window.currentLightPos);
                shader.setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
                shader.setUniform("viewPos", camera.isThirdPerson() ? camera.playerPos.sub(camera.getFront().mul(camera.getZoom() / 10, new Vector3f()), new Vector3f()) : camera.playerPos);
                shader.setUniform("mode", 0);

                shader.setUniform("material.Ka", mesh.meshes.get(meshItr).material.Ka);
                shader.setUniform("material.Kd", mesh.meshes.get(meshItr).material.Kd);
                shader.setUniform("material.Ks", mesh.meshes.get(meshItr).material.Ks);
                shader.setUniform("material.spec", mesh.meshes.get(meshItr).material.specular);
        }

        public void render() {
                if (!created)
                        throw new IllegalStateException("Attempted to call render pass without initializing renderer");
                shader.bind();

                for (int i = 0; i < mesh.meshes.size(); i++) {
                        meshItr = i;

                        prepare();

                        if (mesh.meshes.get(i).material.texture != null){
                                mesh.meshes.get(i).material.texture.bind();
                                getShader().setUniform("useMaterialDiffuse", false);
                        } else {
                                getShader().setUniform("useMaterialDiffuse", true);
                        }

                        Vao vao = mesh.getVaos()[i];

                        vao.bind();
                        vao.enableAttribs();
                        if (mesh.isIndexed()) {
                                vao.bindIndices();
                                GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndexCounts().get(i), GL11.GL_UNSIGNED_INT, 0);
                                vao.unbindIndices();
                        } else {
                                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCounts().get(i));
                        }
                        vao.disableAttribs();
                        vao.unbind();

                        GL30.glDisableVertexAttribArray(3);
                        GL30.glDisableVertexAttribArray(4);
                        GL30.glDisableVertexAttribArray(5);
                        GL30.glDisableVertexAttribArray(6);
                }
        }

        public TexturedMesh getMesh() {
                return mesh;
        }

        public boolean isCreated() {
                return created;
        }

        public Shader getShader() {
                return shader;
        }

        public Camera getCamera() {
                return camera;
        }

        public Vector3f getTrans() {
                return trans;
        }

        public float getScale() {
                return scale;
        }

        public Vector3f getRotation() {
                return rotation;
        }

        public TextureRenderer setTrans(Vector3f trans) {
                this.trans = trans;
                return this;
        }

        public TextureRenderer setScale(float scale) {
                this.scale = scale;
                return this;
        }

        public TextureRenderer setRotation(Vector3f rotation) {
                this.rotation = rotation;
                return this;
        }
}
