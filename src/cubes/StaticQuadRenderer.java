package cubes;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL15;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class StaticQuadRenderer {
    private static final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    private FloatBuffer vertices, textureCoords, normals;
    private IntBuffer indices;
    private String texturePath;
    private boolean indexed;
    private float rotation;
    private int vao, vbo, ebo, tbo, nbo, texture;
    public int indexCount, vertexCount, texCoordCount, normalCount, attribCount, numCubes;
    private float[] rotSpeeds;
    private Vector3f[] cubePositions;

    public StaticQuadRenderer (float[] vertexData, float[] texCoords, float[] normals, int[] indexData, Vector3f[] cubePositions, float[] rotSpeeds, String texturePath){
        this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
        this.textureCoords = (FloatBuffer) MemoryUtil.memAllocFloat(texCoords.length).put(texCoords).flip();
        this.normals = (FloatBuffer) MemoryUtil.memAllocFloat((normals.length)).put(normals).flip();
        this.indices = (IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip();
        indexCount = indexData.length;
        vertexCount = vertexData.length / 3;
        texCoordCount = texCoords.length / 3;
        normalCount = normals.length / 3;
        attribCount = 0;
        this.cubePositions = cubePositions;
        this.rotSpeeds = rotSpeeds;
        numCubes = cubePositions.length;
        this.texturePath = texturePath;
        if (cubePositions.length != rotSpeeds.length){
            throw new IllegalStateException();
        }
    }

    public StaticQuadRenderer (float[] vertexData, float[] texCoords, float[] normals, Vector3f[] cubePositions, float[] rotSpeeds, String texturePath){
        this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
        this.textureCoords = (FloatBuffer) MemoryUtil.memAllocFloat(texCoords.length).put(texCoords).flip();
        this.normals = (FloatBuffer) MemoryUtil.memAllocFloat((normals.length)).put(normals).flip();
        this.indices = null;
        vertexCount = vertexData.length / 3;
        texCoordCount = texCoords.length / 3;
        normalCount = normals.length / 3;
        indexCount = 0;
        attribCount = 0;
        this.cubePositions = cubePositions;
        this.rotSpeeds = rotSpeeds;
        numCubes = cubePositions.length;
        this.texturePath = texturePath;
        if (cubePositions.length != rotSpeeds.length){
            throw new IllegalStateException();
        }
    }

    public void create(boolean indexed){
        this.indexed = indexed;
        if (indexed) {
            vao = GL30.glGenVertexArrays();
            vbo = storeBuffer(vao, 0, 3, vertices);
            tbo = storeBuffer(vao, 1, 2, textureCoords);
            nbo = storeBuffer(vao, 2, 3, normals);
            texture = storeJarTexture(texturePath);

            ebo = GL15.glGenBuffers();

            GL30.glBindVertexArray(vao);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
        else{
            vao = GL30.glGenVertexArrays();
            vbo = storeBuffer(vao, 0, 3, vertices);
            tbo = storeBuffer(vao, 1, 2, textureCoords);
            nbo = storeBuffer(vao, 2, 3, normals);
            texture = storeJarTexture(texturePath);
            ebo = 0;
        }
    }

    public void render(Shader shader, Camera camera, Vector3f trans, boolean debug){
        if (debug) System.out.println("yaw: " + camera.yaw + "\npitch: " + camera.pitch);

        if (trans == null) {
            for (int i = 0; i < numCubes; i++) {

                Matrix4f model = new Matrix4f().translate(cubePositions[i]).scale(0.5f, 0.5f, 0.5f).rotate(rotation * rotSpeeds[i], 0.0f, 1.0f, 0.0f).rotate(rotation * rotSpeeds[i], 1.0f, 0.0f, 0.0f);

                Matrix4f proj = camera.getProjectionMatrix();

                Matrix4f view = camera.getViewMatrix();

                GL20.glActiveTexture(GL20.GL_TEXTURE0);
                GL20.glBindTexture(GL20.GL_TEXTURE_2D, texture);
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
                shader.setUniform("lightPos", new Vector3f(0.0f, 0.0f, 0.0f));
                shader.setUniform("viewPos", camera.pos);

                if (indexed) {
                    GL30.glBindVertexArray(this.vao);
                    enableAttribs();
                    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
                    GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT, 0);
                    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
                    disableAttribs();
                    GL30.glBindVertexArray(0);
                } else {
                    GL30.glBindVertexArray(vao);
                    enableAttribs();
                    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
                    disableAttribs();
                    GL30.glBindVertexArray(0);
                }
            }
        }
        else {
            Matrix4f model = new Matrix4f().translate(trans).scale(10.0f, 10.0f, 10.0f);

            Matrix4f proj = camera.getProjectionMatrix();

            Matrix4f view = camera.getViewMatrix();


            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            GL20.glBindTexture(GL20.GL_TEXTURE_2D, texture);
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
            shader.setUniform("lightPos", new Vector3f(0.0f, 0.0f, 0.0f));
            shader.setUniform("viewPos", camera.pos);

            if (indexed) {
                GL30.glBindVertexArray(this.vao);
                enableAttribs();
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
                GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT, 0);
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
                disableAttribs();
                GL30.glBindVertexArray(0);
            } else {
                GL30.glBindVertexArray(vao);
                enableAttribs();
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
                disableAttribs();
                GL30.glBindVertexArray(0);
            }
        }
        rotation += (1.0f * Window.deltaTime);
    }

    public int storeBuffer(int vao, int index, int size, FloatBuffer data){
        GL30.glBindVertexArray(vao);
        int buf = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buf);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        attribCount++;
        return buf;
    }

    private int storeJarTexture(String path){
        STBImage.stbi_set_flip_vertically_on_load(true);

        try {
            int width, height;
            IntBuffer x = MemoryUtil.memAllocInt(8);
            IntBuffer y = MemoryUtil.memAllocInt(8);
            IntBuffer nrChannels = MemoryUtil.memAllocInt(256);
            InputStream in = classloader.getResourceAsStream(path);
            int bytes = in.available();
            System.out.println(bytes);
            byte[] data = new byte[bytes];
            in.read(data);
            System.out.println("Texture located!");
            ByteBuffer imgData = (ByteBuffer) MemoryUtil.memAlloc(bytes).put(data).flip();
            int texture = GL20.glGenTextures();
            GL20.glBindTexture(GL20.GL_TEXTURE_2D, texture);
            ByteBuffer finalData = STBImage.stbi_load_from_memory(imgData, x, y, nrChannels, 0);
            if (finalData == null){
                System.out.println("FAILED TO LOAD TEXTURE FROM JAR RESOURCES WHILE LOADING. PROGRAM WILL EXIT");
            }
            width = x.get();
            height = y.get();
            GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGB, width, height, 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, finalData);
            GL30.glGenerateMipmap(GL20.GL_TEXTURE_2D);
            GL20.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL20.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            GL20.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL20.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
            STBImage.stbi_image_free(finalData);
            System.out.println("Texture loaded!");
            return texture;
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("FAILED TO LOAD TEXTURE FROM JAR RESOURCES WHEN ACCESSING FILE. PROGRAM WILL EXIT");
            System.exit(-1);
            return 0;
        }
    }

    private void enableAttribs(){
        for (int i = 0; i < attribCount; i++){
            GL20.glEnableVertexAttribArray(i);
        }
    }

    private void disableAttribs(){
        for (int i = 0; i < attribCount; i++){
            GL20.glDisableVertexAttribArray(i);
        }
    }

    public void setIndices(int[] indexData){
        this.indices = (IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip();
        indexCount = indexData.length;
    }

    public void setVertices(float[] vertexData){
        this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
        vertexCount = vertexData.length / 3;
    }

    public void setTextureCoords(float[] textureCoords){
        this.textureCoords = (FloatBuffer) MemoryUtil.memAllocFloat(textureCoords.length).put(textureCoords).flip();
        texCoordCount = textureCoords.length / 3;
    }

    public IntBuffer getIndices(){
        return indices;
    }

    public FloatBuffer getVertices(){
        return vertices;
    }

    public FloatBuffer getColors(){
        return textureCoords;
    }
}
