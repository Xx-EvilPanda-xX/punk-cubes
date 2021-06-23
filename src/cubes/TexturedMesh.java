package cubes;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class TexturedMesh {
        private FloatBuffer vertices, textureCoords, normals;
        private IntBuffer indices;
        private String texturePath;
        private boolean indexed;
        private int indexCount, vertexCount;

        private Texture texture;

        private Vao vao = new Vao();
        private int vbo, tbo, cbo, nbo, uao;

        public TexturedMesh(float[] vertexData, float[] texCoords, float[] normalData, int[] indexData, String texturePath) {
                this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
                this.textureCoords = (FloatBuffer) MemoryUtil.memAllocFloat(texCoords.length).put(texCoords).flip();
                this.normals = (FloatBuffer) MemoryUtil.memAllocFloat(normalData.length).put(normalData).flip();
                this.indices = (IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip();
                vertexCount = vertexData.length / 3;
                indexCount = indexData.length;
                this.texturePath = texturePath;
                indexed = true;

                texture = new Texture(texturePath);
                texture.storeDirectTexture();
        }

        public TexturedMesh(float[] vertexData, float[] texCoords, float[] normalData, String texturePath) {
                this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
                this.textureCoords = (FloatBuffer) MemoryUtil.memAllocFloat(texCoords.length).put(texCoords).flip();
                this.normals = (FloatBuffer) MemoryUtil.memAllocFloat(normalData.length).put(normalData).flip();
                this.indices = null;
                vertexCount = vertexData.length / 3;
                indexCount = 0;
                this.texturePath = texturePath;
                indexed = false;

                texture = new Texture(texturePath);
                texture.storeDirectTexture();
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

        public IntBuffer getIndices() {
                return indices;
        }

        public String getTexturePath() {
                return texturePath;
        }

        public boolean isIndexed() {
                return indexed;
        }

        public int getIndexCount() {
                return indexCount;
        }

        public int getVertexCount() {
                return vertexCount;
        }

        public Vao getVao() {
                return vao;
        }

        public int getVbo() {
                return vbo;
        }

        public int getTbo() {
                return tbo;
        }

        public int getCbo() {
                return cbo;
        }

        public int getNbo() {
                return nbo;
        }

        public int getUao() {
                return uao;
        }

        public Texture getTexture() {
                return texture;
        }

        public void setVao(Vao vao) {
                this.vao = vao;
        }

        public void setVbo(int vbo) {
                this.vbo = vbo;
        }

        public void setTbo(int tbo) {
                this.tbo = tbo;
        }

        public void setCbo(int cbo) {
                this.cbo = cbo;
        }

        public void setNbo(int nbo) {
                this.nbo = nbo;
        }

        public void setUao(int uao) {
                this.uao = uao;
        }

        public void setTexture(Texture texture) {
                this.texture = texture;
        }
}
