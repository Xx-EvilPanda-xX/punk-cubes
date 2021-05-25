package cubes;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class TextureRenderer implements Renderer{
        private FloatBuffer vertices, textureCoords, normals;
        private IntBuffer indices;
        private String texturePath;
        private boolean indexed;
        private Texture texture;
        private Vao vao;
        private int vbo, tbo, nbo, cbo;
        public int indexCount, vertexCount, texCoordCount, normalCount;

        public TextureRenderer(float[] vertexData, float[] texCoords, float[] normals, int[] indexData, String texturePath) {
                this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
                this.textureCoords = (FloatBuffer) MemoryUtil.memAllocFloat(texCoords.length).put(texCoords).flip();
                texCoordCount = texCoords.length / 2;
                this.normals = (FloatBuffer) MemoryUtil.memAllocFloat(normals.length).put(normals).flip();
                this.indices = (IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip();
                indexCount = indexData.length;
                vertexCount = vertexData.length / 3;
                normalCount = normals.length / 3;
                this.texturePath = texturePath;
                indexed = true;
        }

        public TextureRenderer(float[] vertexData, float[] texCoords, float[] normals, String texturePath) {
                this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
                this.textureCoords = (FloatBuffer) MemoryUtil.memAllocFloat(texCoords.length).put(texCoords).flip();
                texCoordCount = texCoords.length / 2;
                this.normals = (FloatBuffer) MemoryUtil.memAllocFloat(normals.length).put(normals).flip();
                this.indices = null;
                indexCount = 0;
                vertexCount = vertexData.length / 3;
                normalCount = normals.length / 3;
                this.texturePath = texturePath;
                indexed = false;
        }

        public void create() {
                if (indexed) {
                        vao = new Vao();
                        vbo = vao.storeBuffer(0, 3, vertices);
                        tbo = vao.storeBuffer(1, 2, textureCoords);

                        //init color buffer as a default value to avoid opengl shader errors even though its not being used
                        cbo = vao.storeBuffer(2, 3, (FloatBuffer) MemoryUtil.memAllocFloat(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip());
                        nbo = vao.storeBuffer(3, 3, normals);

                        texture = new Texture(texturePath);
                        texture.storeDirectTexture();

                        vao.storeIndices(indices);
                } else {
                        vao = new Vao();
                        vbo = vao.storeBuffer(0, 3, vertices);
                        tbo = vao.storeBuffer(1, 2, textureCoords);

                        //init color buffer as a default value to avoid opengl shader errors even though its not being used
                        cbo = vao.storeBuffer(2, 3, (FloatBuffer) MemoryUtil.memAllocFloat(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip());
                        nbo = vao.storeBuffer(3, 3, normals);

                        texture = new Texture(texturePath);
                        texture.storeDirectTexture();
                }
        }

        public void prepare(Shader shader, Camera camera, Vector3f trans, float scale, float rotate, boolean debug){
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

                shader.setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
                if (!camera.getThirdPerson()) {
                        shader.setUniform("viewPos", camera.playerPos);
                } else {
                        shader.setUniform("viewPos", camera.playerPos.sub(camera.front.mul(camera.zoom / 10, new Vector3f()), new Vector3f()));
                }
                shader.setUniform("mode", 0);
                shader.setUniform("numLights", 1);
                shader.setUniform("colorMode", -1);
        }

        public void render(Shader shader, Camera camera, Vector3f trans, float scale, float rotate, boolean debug) {
                prepare(shader, camera, trans, scale, rotate, debug);

                texture.bind();
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

        public void setIndices(int[] indexData) {
                this.indices = (IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip();
                indexCount = indexData.length;
        }

        public void setVertices(float[] vertexData) {
                this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
                vertexCount = vertexData.length / 3;
        }

        public void setTextureCoords(float[] textureCoords) {
                this.textureCoords = (FloatBuffer) MemoryUtil.memAllocFloat(textureCoords.length).put(textureCoords).flip();
                texCoordCount = textureCoords.length / 3;
        }

        public void setNormals(float[] normals) {
                this.normals = (FloatBuffer) MemoryUtil.memAllocFloat(normals.length).put(normals).flip();
                normalCount = normals.length / 3;
        }

        public IntBuffer getIndices() {
                return indices;
        }

        public FloatBuffer getVertices() {
                return vertices;
        }

        public FloatBuffer getTextureCoords() {
                return textureCoords;
        }

        public FloatBuffer getNormals() {
                return normals;
        }

        public Vao getVao() {
                return vao;
        }

        public Texture getTexture() {
                return texture;
        }

        public boolean isIndexed() {
                return indexed;
        }
}
