package cubes;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL42;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class ColorRenderer implements Renderer {
        public static final int MAX_INSTANCES = 32767;

        ColoredMesh mesh;
        private boolean created = false;

        private Shader shader;
        private Camera camera;

        private Vector3f trans = new Vector3f(0.0f, 0.0f, 0.0f);
        private float scale = 1.0f;
        private Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);

        public ColorRenderer(ColoredMesh mesh) {
                this.mesh = mesh;
        }

        public void create(Shader shader, Camera camera) {
                this.shader = shader;
                this.camera = camera;

                mesh.setVbos(mesh.getVaos()[0].storeBuffer(0, 3, mesh.getVertices().get(0)), 0);

                //init tex coord buffer as a default value to avoid opengl shader errors even though its not being used
                mesh.setTbos(mesh.getVaos()[0].storeBuffer(1, 2, (FloatBuffer) MemoryUtil.memAllocFloat(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip()), 0);
                mesh.setCbos(mesh.getVaos()[0].storeBuffer(2, 3, mesh.getColors().get(0)), 0);
                mesh.setNbos(mesh.getVaos()[0].storeBuffer(3, 3, mesh.getNormals().get(0)), 0);
                if (mesh.isIndexed()) {
                        mesh.getVaos()[0].storeIndices(mesh.getIndices().get(0));
                }

                mesh.setUaos(GL15.glGenBuffers(), 0);
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.getUaos()[0]);
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 64 * MAX_INSTANCES, GL15.GL_STATIC_DRAW);

                GL30.glBindVertexArray(mesh.getVaos()[0].getHandle());
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
                        System.out.println(proj.toString());
                        System.out.println(view.toString());
                }

                float[] matrix = new float[16];
                for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {
                                matrix[(i * 4) + j] = model.get(i, j);
                        }
                }
                GL30.glBindVertexArray(mesh.getVaos()[0].getHandle());
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.getUaos()[0]);

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

                shader.setUniform("lightPos", Window.currentLightPos);
                shader.setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
                if (!camera.isThirdPerson()) {
                        shader.setUniform("viewPos", camera.playerPos);
                } else {
                        shader.setUniform("viewPos", camera.playerPos.sub(camera.getFront().mul(camera.getZoom() / 10, new Vector3f()), new Vector3f()));
                }
                shader.setUniform("mode", 1);
        }

        public void render(boolean debug) {
                shader.bind();

                prepare(debug);

                mesh.getVaos()[0].bind();
                mesh.getVaos()[0].enableAttribs();
                if (mesh.isIndexed()) {
                        mesh.getVaos()[0].bindIndices();
                        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndexCounts().get(0), GL11.GL_UNSIGNED_INT, 0);
                        mesh.getVaos()[0].unbindIndices();
                } else {
                        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCounts().get(0));
                }
                mesh.getVaos()[0].disableAttribs();
                mesh.getVaos()[0].unbind();

                GL30.glDisableVertexAttribArray(4);
                GL30.glDisableVertexAttribArray(5);
                GL30.glDisableVertexAttribArray(6);
                GL30.glDisableVertexAttribArray(7);
        }

        public ColoredMesh getMesh() {
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

        public ColorRenderer setTrans(Vector3f trans) {
                this.trans = trans;
                return this;
        }

        public ColorRenderer setScale(float scale) {
                this.scale = scale;
                return this;
        }

        public ColorRenderer setRotation(Vector3f rotation) {
                this.rotation = rotation;
                return this;
        }
}
