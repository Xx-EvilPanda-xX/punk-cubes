package cubes;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class ColoredMesh {
        private ArrayList<FloatBuffer> vertices = new ArrayList<>(), colors = new ArrayList<>(), normals = new ArrayList<>();
        private ArrayList<IntBuffer> indices = new ArrayList<>();
        private boolean indexed;
        private ArrayList<Integer> indexCounts = new ArrayList<>(), vertexCounts = new ArrayList<>();

        private Vao[] vao;
        private int[] vbo, tbo, cbo, nbo, uao;

        public ColoredMesh(float[] vertexData, float[] colorData, float[] normalData, int[] indexData) {
                vertices.add((FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip());
                colors.add((FloatBuffer) MemoryUtil.memAllocFloat(colorData.length).put(colorData).flip());
                normals.add((FloatBuffer) MemoryUtil.memAllocFloat(normalData.length).put(normalData).flip());
                indices.add((IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip());
                vertexCounts.add(vertexData.length / 3);
                indexCounts.add(indexData.length);
                indexed = true;
                vbo = new int[1];
                tbo = new int[1];
                cbo = new int[1];
                nbo = new int[1];
                uao = new int[1];
                vao = new Vao[1];
                for (int i = 0; i < vao.length; i++){
                        vao[i] = new Vao();
                }
        }

        public ColoredMesh(float[] vertexData, float[] colorData, float[] normalData) {
                vertices.add((FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip());
                colors.add((FloatBuffer) MemoryUtil.memAllocFloat(colorData.length).put(colorData).flip());
                normals.add((FloatBuffer) MemoryUtil.memAllocFloat(normalData.length).put(normalData).flip());
                indices.add(null);
                vertexCounts.add(vertexData.length / 3);
                indexCounts.add(0);
                indexed = false;
                vbo = new int[1];
                tbo = new int[1];
                cbo = new int[1];
                nbo = new int[1];
                uao = new int[1];
                vao = new Vao[1];
                for (int i = 0; i < vao.length; i++){
                        vao[i] = new Vao();
                }
        }

        public ArrayList<FloatBuffer> getVertices() {
                return vertices;
        }

        public ArrayList<FloatBuffer> getColors() {
                return colors;
        }

        public ArrayList<FloatBuffer> getNormals() {
                return normals;
        }

        public ArrayList<IntBuffer> getIndices() {
                return indices;
        }

        public boolean isIndexed() {
                return indexed;
        }

        public ArrayList<Integer> getIndexCounts() {
                return indexCounts;
        }

        public ArrayList<Integer> getVertexCounts() {
                return vertexCounts;
        }

        public Vao[] getVao() {
                return vao;
        }

        public int[] getVbo() {
                return vbo;
        }

        public int[] getTbo() {
                return tbo;
        }

        public int[] getCbo() {
                return cbo;
        }

        public int[] getNbo() {
                return nbo;
        }

        public int[] getUao() {
                return uao;
        }

        public void setVao(Vao vao, int index) {
                this.vao[index] = vao;
        }

        public void setVbo(int vbo, int index) {
                this.vbo[index] = vbo;
        }

        public void setTbo(int tbo, int index) {
                this.tbo[index] = tbo;
        }

        public void setCbo(int cbo, int index) {
                this.cbo[index] = cbo;
        }

        public void setNbo(int nbo, int index) {
                this.nbo[index] = nbo;
        }

        public void setUao(int uao, int index) {
                this.uao[index] = uao;
        }
}
