package cubes;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.assimp.*;

public class TexturedMesh {
        ArrayList<Mesh> meshes = new ArrayList<>();

        private FloatBuffer vertices, textureCoords, normals;
        private IntBuffer indices;
        private String texturePath;
        private boolean indexed;
        private int indexCount, vertexCount;

        private Texture texture;

        private Vao vao = new Vao();
        private int vbo, tbo, cbo, nbo, uao;

        PointerBuffer ptr;

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

        public TexturedMesh(String modelPath, String texturePath){
                loadFromObj(modelPath);
                System.out.println("Model loaded successfully at " + modelPath);

                texture = new Texture(texturePath);
                texture.storeDirectTexture();

                vertexCount = vertices.capacity() / 3;
                indexCount = indices.capacity();

                System.out.println("vertex count: " + vertexCount);
                System.out.println("index count: " + indexCount);

                indexed = true;
        }

        private void loadFromObj(String modelPath){
                AIScene scene = Assimp.aiImportFile("resources/" + modelPath, Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs);
                if (scene == null || (scene.mFlags() &  Assimp.AI_SCENE_FLAGS_INCOMPLETE)  != 0 || scene.mRootNode() == null){
                        throw new IllegalStateException("couldn't load model at: " + modelPath);
                }
                ptr = scene.mMeshes();
                processNode(scene.mRootNode());
                toBuffers();
        }

        private void processNode(AINode node){
                for (int i = 0; i < node.mNumMeshes(); i++){
                        AIMesh mesh = AIMesh.create(ptr.get());
                        meshes.add(processMesh(mesh));
                }

                PointerBuffer nodePtr = node.mChildren();
                for (int i = 0; i < node.mNumChildren(); i++){
                        AINode childNode = AINode.create(nodePtr.get());
                        processNode(childNode);
                }
        }

        private Mesh processMesh(AIMesh mesh){
                ArrayList<Vertex> vertices = new ArrayList<>();
                ArrayList<Integer> indices = new ArrayList<>();

                for (int i = 0; i < mesh.mNumVertices(); i++) {
                        Vertex vertex = new Vertex();
                        Vector3f vector = new Vector3f();

                        vector.x = mesh.mVertices().get(i).x();
                        vector.y = mesh.mVertices().get(i).y();
                        vector.z = mesh.mVertices().get(i).z();

                        vertex.position = new Vector3f(vector);

                        if (mesh.mNormals() != null) {
                                vector.x = mesh.mNormals().get(i).x();
                                vector.y = mesh.mNormals().get(i).y();
                                vector.z = mesh.mNormals().get(i).z();
                        } else{
                                vector.x = 0.0f;
                                vector.y = 0.0f;
                                vector.z = 0.0f;
                        }

                        vertex.normal = new Vector3f(vector);

                        if (mesh.mNumUVComponents().get() > 0) {
                                vector.x = mesh.mTextureCoords(0).get(i).x();
                                vector.y = mesh.mTextureCoords(0).get(i).y();
                        } else {
                                vector.x = 0.0f;
                                vector.y = 0.0f;
                        }

                        vertex.texCoords = new Vector2f(vector.x, vector.y);

                        vertices.add(vertex);
                }

                for (int i = 0; i < mesh.mNumFaces(); i++){
                        AIFace face = mesh.mFaces().get(i);
                        for (int j = 0; j < face.mNumIndices(); j++){
                                indices.add(face.mIndices().get(j));
                        }
                }

                return new Mesh(vertices, indices);
        }

        private void toBuffers(){
                int verticesSize = 0;
                int indicesSize = 0;
                int add = 0;

                for (int i = 0; i < meshes.size(); i++){
                        verticesSize += meshes.get(i).vertices.size();
                }
                for (int i = 0; i < meshes.size(); i++){
                        indicesSize += meshes.get(i).indices.size();
                }

                FloatBuffer vertices = MemoryUtil.memAllocFloat(verticesSize * 3);
                FloatBuffer textureCoords = MemoryUtil.memAllocFloat(verticesSize * 2);
                FloatBuffer normals = MemoryUtil.memAllocFloat(verticesSize * 3);
                IntBuffer indices = MemoryUtil.memAllocInt(indicesSize);

                for (int i = 0; i < meshes.size(); i++){
                        for (int j = 0; j < meshes.get(i).vertices.size(); j++){
                                vertices.put(meshes.get(i).vertices.get(j).position.x);
                                vertices.put(meshes.get(i).vertices.get(j).position.y);
                                vertices.put(meshes.get(i).vertices.get(j).position.z);

                                if (meshes.get(i).vertices.get(j).normal != null) {
                                        normals.put(meshes.get(i).vertices.get(j).normal.x);
                                        normals.put(meshes.get(i).vertices.get(j).normal.y);
                                        normals.put(meshes.get(i).vertices.get(j).normal.z);
                                } else {
                                        normals.put(0.0f).put(0.0f).put(0.0f);
                                }

                                if (meshes.get(i).vertices.get(j).texCoords != null) {
                                        textureCoords.put(meshes.get(i).vertices.get(j).texCoords.x);
                                        textureCoords.put(meshes.get(i).vertices.get(j).texCoords.y);
                                } else {
                                        textureCoords.put(0.0f).put(0.0f);
                                }
                        }

                        for (int j = 0; j < meshes.get(i).indices.size(); j++){
                                indices.put(meshes.get(i).indices.get(j) + add);
                        }

                        add += meshes.get(i).vertices.size();
                }

                vertices.flip();
                normals.flip();
                textureCoords.flip();
                indices.flip();

                this.vertices = vertices;
                this.normals = normals;
                this.textureCoords = textureCoords;
                this.indices = indices;
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

        static class Mesh{
                ArrayList<Vertex> vertices;
                ArrayList<Integer> indices;

                public Mesh(ArrayList<Vertex> vertices, ArrayList<Integer> indices){
                        this.vertices = vertices;
                        this.indices = indices;
                }
        }

        static class Vertex{
                Vector3f position;
                Vector3f normal;
                Vector2f texCoords;
        }
}
