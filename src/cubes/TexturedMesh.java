package cubes;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.assimp.*;

public class TexturedMesh {
        public ArrayList<Mesh> meshes = new ArrayList<>();

        private ArrayList<FloatBuffer> vertices = new ArrayList<>(), textureCoords = new ArrayList<>(), normals = new ArrayList<>();
        private ArrayList<IntBuffer> indices = new ArrayList<>();
        private String texturePath;
        private boolean indexed;
        private ArrayList<Integer> indexCounts = new ArrayList<>(), vertexCounts = new ArrayList<>();
        private String modelPath;

        private ArrayList<Texture> textures = new ArrayList<>();

        private Vao[] vaos;
        private int[] vbos, tbos, nbos, uaos;

        private PointerBuffer ptr;
        private boolean forceTexture;

        public TexturedMesh(String modelPath, String[] texturePaths, boolean forceTexture){
                loadFromObj(modelPath);
                this.forceTexture = forceTexture;
                System.out.println("Model loaded successfully at " + modelPath);

                for (int i = 0; i < texturePaths.length; i++) {
                        textures.add(new Texture(texturePaths[i]));
                }

                int totalVertexCount = 0;
                int totalIndexCount = 0;

                for (int i = 0; i < vertices.size(); i++){
                        vertexCounts.add(vertices.get(i).capacity() / 3);
                        totalVertexCount += vertices.get(i).capacity() / 3;
                }
                for (int i = 0; i < indices.size(); i++){
                        indexCounts.add(indices.get(i).capacity());
                        totalIndexCount += indices.get(i).capacity();
                }

                System.out.println("vertex count: " + totalVertexCount);
                System.out.println("index count: " + totalIndexCount);

                vbos = new int[meshes.size()];
                tbos = new int[meshes.size()];
                nbos = new int[meshes.size()];
                uaos = new int[meshes.size()];
                vaos = new Vao[meshes.size()];
                for (int i = 0; i < vaos.length; i++){
                        vaos[i] = new Vao();
                }

                indexed = true;
        }

        private void loadFromObj(String modelPath){
                AIScene scene = Assimp.aiImportFile("resources/" + modelPath, Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs);
                this.modelPath = modelPath;
                if (scene == null || (scene.mFlags() &  Assimp.AI_SCENE_FLAGS_INCOMPLETE)  != 0 || scene.mRootNode() == null){
                        throw new IllegalStateException("couldn't load model at: " + modelPath);
                }
                ptr = scene.mMeshes();
                processNode(scene.mRootNode(), scene);
                toBuffers();
        }

        private void processNode(AINode node, AIScene scene){
                for (int i = 0; i < node.mNumMeshes(); i++){
                        AIMesh mesh = AIMesh.create(ptr.get());
                        meshes.add(processMesh(mesh, scene));
                }

                PointerBuffer nodePtr = node.mChildren();
                for (int i = 0; i < node.mNumChildren(); i++){
                        AINode childNode = AINode.create(nodePtr.get());
                        processNode(childNode, scene);
                }
        }

        private Mesh processMesh(AIMesh mesh, AIScene scene){
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

                Material material = new Material();

                if (mesh.mMaterialIndex() > 0) {
                        PointerBuffer ptr = scene.mMaterials();
                        AIMaterial mat = AIMaterial.create(ptr.get(mesh.mMaterialIndex()));
                        PointerBuffer ptr1 = mat.mProperties();

                        for (int i = 0; i < mat.mNumProperties(); i++) {
                                AIMaterialProperty matProp = AIMaterialProperty.create(ptr1.get(i));
                                AIString key = matProp.mKey();
                                String property = key.dataString();

                                if (property.equals("$clr.ambient")) {
                                        ByteBuffer data = matProp.mData();
                                        Vector3f Ka = new Vector3f(data.getFloat(), data.getFloat(), data.getFloat());
                                        material.Ka = Ka;
                                }
                                if (property.equals("$clr.diffuse")) {
                                        ByteBuffer data = matProp.mData();
                                        Vector3f Kd = new Vector3f(data.getFloat(), data.getFloat(), data.getFloat());
                                        material.Kd = Kd;
                                }
                                if (property.equals("$clr.specular")) {
                                        ByteBuffer data = matProp.mData();
                                        Vector3f Ks = new Vector3f(data.getFloat(), data.getFloat(), data.getFloat());
                                        material.Ks = Ks;
                                }
                                if (property.equals("$mat.shininess")) {
                                        ByteBuffer data = matProp.mData();
                                        float specular = data.getFloat();
                                        material.specular = specular;
                                }
                        }
                }

                return new Mesh(vertices, indices, material);
        }



        private void toBuffers(){
                int[] verticesSizes = new int[meshes.size()];
                int[] indicesSizes = new int[meshes.size()];

                for (int i = 0; i < meshes.size(); i++){
                        verticesSizes[i] = meshes.get(i).vertices.size();
                }
                for (int i = 0; i < meshes.size(); i++){
                        indicesSizes[i] = meshes.get(i).indices.size();
                }

                ArrayList<FloatBuffer> allVertices = new ArrayList<>();
                ArrayList<FloatBuffer> allTexCoords = new ArrayList<>();
                ArrayList<FloatBuffer> allNormals = new ArrayList<>();
                ArrayList<IntBuffer> allIndices = new ArrayList<>();

                for (int i = 0; i < meshes.size(); i++){
                        FloatBuffer vertices = MemoryUtil.memAllocFloat(verticesSizes[i] * 3);
                        FloatBuffer textureCoords = MemoryUtil.memAllocFloat(verticesSizes[i] * 2);
                        FloatBuffer normals = MemoryUtil.memAllocFloat(verticesSizes[i] * 3);
                        IntBuffer indices = MemoryUtil.memAllocInt(indicesSizes[i]);

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
                                indices.put(meshes.get(i).indices.get(j));
                        }

                        allVertices.add((FloatBuffer) vertices.flip());
                        allTexCoords.add((FloatBuffer) textureCoords.flip());
                        allNormals.add((FloatBuffer) normals.flip());
                        allIndices.add((IntBuffer) indices.flip());
                }

                this.vertices = allVertices;
                this.normals = allNormals;
                this.textureCoords = allTexCoords;
                this.indices = allIndices;
        }


        public ArrayList<FloatBuffer> getVertices() {
                return vertices;
        }

        public ArrayList<FloatBuffer> getTextureCoords() {
                return textureCoords;
        }

        public ArrayList<FloatBuffer> getNormals() {
                return normals;
        }

        public ArrayList<IntBuffer> getIndices() {
                return indices;
        }

        public String getTexturePath() {
                return texturePath;
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

        public int[] getNbos() {
                return nbos;
        }

        public int[] getUaos() {
                return uaos;
        }

        public ArrayList<Texture> getTextures() {
                return textures;
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

        public void setNbos(int nbo, int index) {
                this.nbos[index] = nbo;
        }

        public void setUaos(int uao, int index) {
                this.uaos[index] = uao;
        }

        public void setTextures(ArrayList<Texture> textures) {
                this.textures = textures;
        }

        public boolean isForceTexture() {
                return forceTexture;
        }

        static class Mesh{
                ArrayList<Vertex> vertices;
                ArrayList<Integer> indices;
                Material material;

                public Mesh(ArrayList<Vertex> vertices, ArrayList<Integer> indices, Material material){
                        this.vertices = vertices;
                        this.indices = indices;
                        this.material = material;
                }
        }

        static class Vertex{
                Vector3f position;
                Vector3f normal;
                Vector2f texCoords;
        }
}
