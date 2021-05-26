package cubes;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ColorRenderer implements Renderer {
        private FloatBuffer vertices, colors, normals;
        private IntBuffer indices;
        private boolean indexed;
        private Vao vao;
        private int vbo, tbo, cbo, nbo;
        public int indexCount, vertexCount;

        public ColorRenderer(float[] vertexData, float[] colorData, float[] normalData, int[] indexData) {
                this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
                this.colors = (FloatBuffer) MemoryUtil.memAllocFloat(colorData.length).put(colorData).flip();

                this.normals = (FloatBuffer) MemoryUtil.memAllocFloat(normalData.length).put(normalData).flip();
                this.indices = (IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip();
                vertexCount = vertexData.length / 3;
                indexCount = indexData.length;
                indexed = true;
        }

        public ColorRenderer(float[] vertexData, float[] colorData, float[] normalData) {
                this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
                this.colors = (FloatBuffer) MemoryUtil.memAllocFloat(colorData.length).put(colorData).flip();
                this.normals = (FloatBuffer) MemoryUtil.memAllocFloat(normalData.length).put(normalData).flip();
                this.indices = null;
                vertexCount = vertexData.length / 3;
                indexCount = 0;
                indexed = false;
        }

        public void create() {
                vao = new Vao();

                if (indexed) {
                        vbo = vao.storeBuffer(0, 3, vertices);

                        //init tex coord buffer as a default value to avoid opengl shader errors even though its not being used
                        tbo = vao.storeBuffer(1, 2, (FloatBuffer) MemoryUtil.memAllocFloat(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip());
                        cbo = vao.storeBuffer(2, 3, colors);
                        nbo = vao.storeBuffer(3, 3, normals);

                        vao.storeIndices(indices);
                } else {
                        vbo = vao.storeBuffer(0, 3, vertices);

                        //init tex coord buffer as a default value to avoid opengl shader errors even though its not being used
                        tbo = vao.storeBuffer(1, 2, (FloatBuffer) MemoryUtil.memAllocFloat(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip());
                        cbo = vao.storeBuffer(2, 3, colors);
                        nbo = vao.storeBuffer(3, 3, normals);
                }
        }

        public void prepare(Shader shader, Camera camera, Vector3f trans, float scale, float rotate, boolean debug) {
                if (debug) System.out.println("yaw: " + camera.yaw + "\npitch: " + camera.pitch);

                Matrix4f model = new Matrix4f().translate(trans).scale(scale, scale, scale).rotate(rotate, 0.0f, 1.0f, 0.0f);

                Matrix4f proj = camera.getProjectionMatrix();

                Matrix4f view = camera.getViewMatrix();

                if (debug) {
                        System.out.println(model.toString());
                        shader.setUniform("model", model, true);
                        System.out.println(proj.toString());
                        shader.setUniform("projection", proj, true);
                        System.out.println(view.toString());
                        shader.setUniform("view", view, true);
                } else {
                        shader.setUniform("model", model, false);
                        shader.setUniform("projection", proj, false);
                        shader.setUniform("view", view, false);
                }

                shader.setUniform("lightPos", Window.currentLightPos);
                shader.setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
                if (!camera.getThirdPerson()) {
                        shader.setUniform("viewPos", camera.playerPos);
                } else {
                        shader.setUniform("viewPos", camera.playerPos.sub(camera.front.mul(camera.zoom / 10, new Vector3f()), new Vector3f()));
                }
                shader.setUniform("mode", 1);
        }

        public void render(Shader shader, Camera camera, Vector3f trans, float scale, float rotate, boolean debug) {
                prepare(shader, camera, trans, scale, rotate, debug);

                if (indexed) {
                        vao.bind();
                        vao.enableAttribs();
                        vao.bindIndices();
                        GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT, 0);
                        vao.unbindIndices();
                        vao.disableAttribs();
                        vao.unbind();
                } else {
                        vao.bind();
                        vao.enableAttribs();
                        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
                        vao.disableAttribs();
                        vao.unbind();
                }
        }

        public IntBuffer getIndices() {
                return indices;
        }

        public FloatBuffer getVertices() {
                return vertices;
        }

        public FloatBuffer getColors() {
                return colors;
        }

        public FloatBuffer getNormals() {
                return normals;
        }

        public boolean isIndexed() {
                return indexed;
        }

        public Vao getVao() {
                return vao;
        }
}
