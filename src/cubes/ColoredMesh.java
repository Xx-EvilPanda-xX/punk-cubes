package cubes;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ColoredMesh {
        private FloatBuffer vertices, colors, normals;
        private IntBuffer indices;
        private boolean indexed;
        private int indexCount, vertexCount;

        private Vao vao = new Vao();
        private int vbo, tbo, cbo, nbo, uao;

        public ColoredMesh(float[] vertexData, float[] colorData, float[] normalData, int[] indexData){
                this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
                this.colors = (FloatBuffer) MemoryUtil.memAllocFloat(colorData.length).put(colorData).flip();
                this.normals = (FloatBuffer) MemoryUtil.memAllocFloat(normalData.length).put(normalData).flip();
                this.indices = (IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip();
                vertexCount = vertexData.length / 3;
                indexCount = indexData.length;
                indexed = true;
        }

        public ColoredMesh(float[] vertexData, float[] colorData, float[] normalData){
                this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
                this.colors = (FloatBuffer) MemoryUtil.memAllocFloat(colorData.length).put(colorData).flip();
                this.normals = (FloatBuffer) MemoryUtil.memAllocFloat(normalData.length).put(normalData).flip();
                this.indices = null;
                vertexCount = vertexData.length / 3;
                indexCount = 0;
                indexed = false;
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

        public IntBuffer getIndices() {
                return indices;
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
}
