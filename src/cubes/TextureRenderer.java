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

        private Shader shader;
        private Camera camera;

        private Vector3f trans = new Vector3f(0.0f, 0.0f, 0.0f);
        private float scale = 1.0f;
        private Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);

        public TextureRenderer(TexturedMesh mesh) {
                this.mesh = mesh;
        }

        public void create(Shader shader, Camera camera) {
                this.shader = shader;
                this.camera = camera;

                if (mesh.isIndexed()) {
                        mesh.setVbo(mesh.getVao().storeBuffer(0, 3, mesh.getVertices()));
                        mesh.setTbo(mesh.getVao().storeBuffer(1, 2, mesh.getTextureCoords()));

                        //init color buffer as a default value to avoid opengl shader errors even though its not being used
                        mesh.setCbo(mesh.getVao().storeBuffer(2, 3, (FloatBuffer) MemoryUtil.memAllocFloat(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip()));
                        mesh.setNbo(mesh.getVao().storeBuffer(3, 3, mesh.getNormals()));

                        mesh.getVao().storeIndices(mesh.getIndices());
                } else {
                        mesh.setVbo(mesh.getVao().storeBuffer(0, 3, mesh.getVertices()));
                        mesh.setTbo(mesh.getVao().storeBuffer(1, 2, mesh.getTextureCoords()));

                        //init color buffer as a default value to avoid opengl shader errors even though its not being used
                        mesh.setCbo(mesh.getVao().storeBuffer(2, 3, (FloatBuffer) MemoryUtil.memAllocFloat(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip()));
                        mesh.setNbo(mesh.getVao().storeBuffer(3, 3, mesh.getNormals()));
                }

                mesh.setUao(GL15.glGenBuffers());
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.getUao());
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 64 * MAX_INSTANCES, GL15.GL_STATIC_DRAW);

                GL30.glBindVertexArray(mesh.getVao().getHandle());
                GL30.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, 64, 0);
                GL30.glVertexAttribPointer(5, 4, GL11.GL_FLOAT, false, 64, 16);
                GL30.glVertexAttribPointer(6, 4, GL11.GL_FLOAT, false, 64, 32);
                GL30.glVertexAttribPointer(7, 4, GL11.GL_FLOAT, false, 64, 48);

                GL42.glVertexAttribDivisor(4, 1);
                GL42.glVertexAttribDivisor(5, 1);
                GL42.glVertexAttribDivisor(6, 1);
                GL42.glVertexAttribDivisor(7, 1);

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                GL30.glBindVertexArray(0);

                created = true;
        }

        public void prepare(boolean debug) {
                if (!created)
                        throw new IllegalStateException("Attempted to call render pass without initializing renderer");
                if (debug) System.out.println("yaw: " + camera.getYaw() + "\npitch: " + camera.getPitch());

                Matrix4f model = new Matrix4f().translate(trans).scale(scale, scale, scale).rotate(rotation.x, 1.0f, 1.0f, 0.0f).rotate(rotation.y, 0.0f, 1.0f, 0.0f).rotate(rotation.z, 0.0f, 0.0f, 1.0f);

                Matrix4f proj = camera.getProjectionMatrix();

                Matrix4f view = camera.getViewMatrix();

                if (debug) {
                        System.out.println(model.toString());
                        float[] matrix = new float[16];
                        for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 4; j++) {
                                        matrix[(i * 4) + j] = model.get(i, j);
                                }
                        }
                        GL30.glBindVertexArray(mesh.getVao().getHandle());
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.getUao());

                        FloatBuffer buf = (FloatBuffer) MemoryUtil.memAllocFloat(16).put(matrix).flip();
                        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buf);
                        MemoryUtil.memFree(buf);

                        GL30.glEnableVertexAttribArray(4);
                        GL30.glEnableVertexAttribArray(5);
                        GL30.glEnableVertexAttribArray(6);
                        GL30.glEnableVertexAttribArray(7);

                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                        GL30.glBindVertexArray(0);

                        System.out.println(proj.toString());
                        shader.setUniform("projection", proj, true);
                        System.out.println(view.toString());
                        shader.setUniform("view", view, true);
                } else {
                        float[] matrix = new float[16];
                        for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 4; j++) {
                                        matrix[(i * 4) + j] = model.get(i, j);
                                }
                        }
                        GL30.glBindVertexArray(mesh.getVao().getHandle());
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.getUao());

                        FloatBuffer buf = (FloatBuffer) MemoryUtil.memAllocFloat(16).put(matrix).flip();
                        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buf);
                        MemoryUtil.memFree(buf);

                        GL30.glEnableVertexAttribArray(4);
                        GL30.glEnableVertexAttribArray(5);
                        GL30.glEnableVertexAttribArray(6);
                        GL30.glEnableVertexAttribArray(7);

                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                        GL30.glBindVertexArray(0);

                        shader.setUniform("projection", proj, false);
                        shader.setUniform("view", view, false);
                }

                shader.setUniform("lightPos", Window.currentLightPos);
                shader.setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
                if (!camera.isThirdPerson()) {
                        shader.setUniform("viewPos", camera.playerPos);
                } else {
                        shader.setUniform("viewPos", camera.playerPos.sub(camera.getFront().mul(camera.getZoom() / 10, new Vector3f()), new Vector3f()));
                }
                shader.setUniform("mode", 0);
        }

        public void render(boolean debug) {
                shader.bind();
                mesh.getTexture().bind();

                prepare(debug);
                Vao vao = mesh.getVao();
                if (mesh.isIndexed()) {
                        vao.bind();
                        vao.enableAttribs();
                        vao.bindIndices();
                        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
                        vao.unbindIndices();
                        vao.disableAttribs();
                        vao.unbind();
                } else {
                        vao.bind();
                        vao.enableAttribs();
                        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCount());
                        vao.disableAttribs();
                        vao.unbind();
                }
                GL30.glDisableVertexAttribArray(4);
                GL30.glDisableVertexAttribArray(5);
                GL30.glDisableVertexAttribArray(6);
                GL30.glDisableVertexAttribArray(7);
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
