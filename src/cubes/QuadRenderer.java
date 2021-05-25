package cubes;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;

public class QuadRenderer{
        public static boolean USE_PROJ_VIEW_MAT = true;

        public final float SCALE;
        public final float LIMIT;
        public final float SPEED;
        public final float YPOS;

        private FloatBuffer vertices, colors;
        private IntBuffer indices;
        private boolean indexed;
        private float uniformRed, colorOffset, trans, transOffset, rotation;
        private Vao vao;
        private int vbo, cbo;
        public int vertexCount, indexCount, colorCount;

        public QuadRenderer(float[] vertexData, float[] colorData, int[] indexData, float scale, float speed, float ypos, float rotation) {
                SCALE = scale;
                LIMIT = 1 - (scale / 2);
                SPEED = speed;
                YPOS = ypos;
                this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
                this.colors = (FloatBuffer) MemoryUtil.memAllocFloat(colorData.length).put(colorData).flip();
                this.indices = (IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip();
                indexCount = indexData.length;
                vertexCount = vertexData.length / 3;
                colorCount = colorData.length / 3;
                uniformRed = 0.0f;
                colorOffset = 0.5f;
                transOffset = SPEED;
                trans = 0.0f;
                this.rotation = rotation;
                this.indexed = true;
        }

        public QuadRenderer(float[] vertexData, float[] colorData, float scale, float speed, float ypos) {
                SCALE = scale;
                LIMIT = 1 - (scale / 2);
                SPEED = speed;
                YPOS = ypos;
                this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
                this.colors = (FloatBuffer) MemoryUtil.memAllocFloat(colorData.length).put(colorData).flip();
                this.indices = null;
                vertexCount = vertexData.length / 3;
                colorCount = colorData.length / 3;
                indexCount = 0;
                uniformRed = 0.0f;
                colorOffset = 0.5f;
                transOffset = SPEED;
                trans = 0.0f;
                this.indexed = false;
        }

        public void create() {
                vao = new Vao();

                if (indexed) {
                        vbo = vao.storeBuffer(0, 3, vertices);
                        cbo = vao.storeBuffer(2, 3, colors);
                        vao.storeIndices(indices);
                } else {
                        vbo = vao.storeBuffer(0, 3, vertices);
                        cbo = vao.storeBuffer(2, 3, colors);
                }
        }

        public void prepare(Shader shader, Camera camera, boolean debug){
                if (debug) System.out.println("yaw: " + camera.yaw + "\npitch: " + camera.pitch);

                this.trans += transOffset * Window.deltaTime;
                if (this.trans > LIMIT) {
                        transOffset = -SPEED;
                        this.trans = LIMIT;
                }
                if (this.trans < -LIMIT) {
                        transOffset = SPEED;
                        this.trans = -LIMIT;
                }

                Matrix4f model;

                if (USE_PROJ_VIEW_MAT) {
                        model = new Matrix4f().translate(this.trans, YPOS, -3.0f).scale(SCALE, SCALE, SCALE).rotate(rotation * this.trans, 0.0f, 0.0f, 1.0f);
                } else {
                        model = new Matrix4f().translate(this.trans, YPOS, 0.0f).scale(SCALE, SCALE, SCALE).rotate(rotation * this.trans, 0.0f, 0.0f, 1.0f);
                }

                Matrix4f proj = camera.getProjectionMatrix();

                Matrix4f view = camera.getViewMatrix();

                if (debug) {
                        System.out.println(model.toString());
                        shader.setUniform("model", model, true);
                        if (USE_PROJ_VIEW_MAT) {
                                System.out.println(proj.toString());
                                shader.setUniform("projection", proj, true);
                                System.out.println(view.toString());
                                shader.setUniform("view", view, true);
                                shader.setUniform("mode", 5);
                        }
                        else{
                                shader.setUniform("mode", 3);
                        }
                } else {
                        shader.setUniform("model", model, false);
                        if (USE_PROJ_VIEW_MAT) {
                                shader.setUniform("projection", proj, false);
                                shader.setUniform("view", view, false);
                                shader.setUniform("mode", 5);
                        }
                        else{
                                shader.setUniform("mode", 3);
                        }
                }

                shader.setUniform("red", uniformRed);
                shader.setUniform("colorMode", 0);

        }

        public void render(Shader shader, Camera camera, boolean debug) {
                prepare(shader, camera, debug);

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

                uniformRed += colorOffset * Window.deltaTime;
                if (uniformRed > 1.0f) {
                        colorOffset = -0.5f;
                } else if (uniformRed < 0.0f) {
                        colorOffset = 0.5f;
                }
        }

        public void setIndices(int[] indexData) {
                this.indices = (IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip();
                indexCount = indexData.length;
        }

        public void setVertices(float[] vertexData) {
                this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
                vertexCount = vertexData.length / 3;
        }

        public void setColors(float[] colorData) {
                this.colors = (FloatBuffer) MemoryUtil.memAllocFloat(colorData.length).put(colorData).flip();
                colorCount = colorData.length / 3;
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

        public boolean isIndexed() {
                return indexed;
        }

        public Vao getVao() {
                return vao;
        }
}
