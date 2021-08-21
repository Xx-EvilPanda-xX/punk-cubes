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

        private Vao[] vaos;
        private int[] vbos, tbos, cbos, nbos, uaos;

        public ColoredMesh(float[] vertexData, float[] colorData, float[] normalData, int[] indexData) {
                vertices.add((FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip());
                colors.add((FloatBuffer) MemoryUtil.memAllocFloat(colorData.length).put(colorData).flip());
                normals.add((FloatBuffer) MemoryUtil.memAllocFloat(normalData.length).put(normalData).flip());
                indices.add((IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip());
                vertexCounts.add(vertexData.length / 3);
                indexCounts.add(indexData.length);
                indexed = true;
                vbos = new int[1];
                tbos = new int[1];
                cbos = new int[1];
                nbos = new int[1];
                uaos = new int[1];
                vaos = new Vao[1];
                for (int i = 0; i < vaos.length; i++){
                        vaos[i] = new Vao();
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
                vbos = new int[1];
                tbos = new int[1];
                cbos = new int[1];
                nbos = new int[1];
                uaos = new int[1];
                vaos = new Vao[1];
                for (int i = 0; i < vaos.length; i++){
                        vaos[i] = new Vao();
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

        public Vao[] getVaos() {
                return vaos;
        }

        public int[] getVbos() {
                return vbos;
        }

        public int[] getTbos() {
                return tbos;
        }

        public int[] getCbos() {
                return cbos;
        }

        public int[] getNbos() {
                return nbos;
        }

        public int[] getUaos() {
                return uaos;
        }

        public void setVaos(Vao vao, int index) {
                this.vaos[index] = vao;
        }

        public void setVbos(int vbo, int index) {
                this.vbos[index] = vbo;
        }

        public void setTbos(int tbo, int index) {
                this.tbos[index] = tbo;
        }

        public void setCbos(int cbo, int index) {
                this.cbos[index] = cbo;
        }

        public void setNbos(int nbo, int index) {
                this.nbos[index] = nbo;
        }

        public void setUaos(int uao, int index) {
                this.uaos[index] = uao;
        }
}
