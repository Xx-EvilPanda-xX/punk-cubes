package cubes;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL15;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class StaticQuadRenderer {
    private static final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    private FloatBuffer vertices, textureCoords, colors, normals;
    private IntBuffer indices;
    private String texturePath;
    private boolean indexed;
    private boolean hasColors;
    private float rotation;
    private int vao, vbo, ebo, tbo, cbo, nbo, texture;
    public int indexCount, vertexCount, texCoordCount, colorCount, normalCount, attribCount = 0, numCubes;
    private float[] rotSpeeds;
    private Vector3f[] cubePositions;

    public StaticQuadRenderer (float[] vertexData, float[] texCoordsOrColors, float[] normals, int[] indexData, Vector3f[] cubePositions, float[] rotSpeeds, String texturePath, boolean hasColors){
        this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
        if (hasColors) {
            this.colors = (FloatBuffer) MemoryUtil.memAllocFloat(texCoordsOrColors.length).put(texCoordsOrColors).flip();
            colorCount = texCoordsOrColors.length/ 3;
        }
        else{
            this.textureCoords = (FloatBuffer) MemoryUtil.memAllocFloat(texCoordsOrColors.length).put(texCoordsOrColors).flip();
            texCoordCount = texCoordsOrColors.length / 2;
        }
        this.normals = (FloatBuffer) MemoryUtil.memAllocFloat(normals.length).put(normals).flip();
        this.indices = (IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip();
        indexCount = indexData.length;
        vertexCount = vertexData.length / 3;
        normalCount = normals.length / 3;
        this.cubePositions = cubePositions;
        this.rotSpeeds = rotSpeeds;
        if (cubePositions == null || rotSpeeds == null){
            numCubes = 0;
        }
        else{
            numCubes = cubePositions.length;
            if (cubePositions.length != rotSpeeds.length){
                throw new IllegalStateException();
            }
        }
        this.texturePath = texturePath;
        this.hasColors = hasColors;
        indexed = true;
    }

    public StaticQuadRenderer (float[] vertexData, float[] texCoordsOrColors, float[] normals, Vector3f[] cubePositions, float[] rotSpeeds, String texturePath, boolean hasColors){
        this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
        if (hasColors) {
            this.colors = (FloatBuffer) MemoryUtil.memAllocFloat(texCoordsOrColors.length).put(texCoordsOrColors).flip();
            colorCount = texCoordsOrColors.length/ 3;
        }
        else{
            this.textureCoords = (FloatBuffer) MemoryUtil.memAllocFloat(texCoordsOrColors.length).put(texCoordsOrColors).flip();
            texCoordCount = texCoordsOrColors.length / 2;
        }
        this.normals = (FloatBuffer) MemoryUtil.memAllocFloat(normals.length).put(normals).flip();
        vertexCount = vertexData.length / 3;
        normalCount = normals.length / 3;
        this.cubePositions = cubePositions;
        this.rotSpeeds = rotSpeeds;
        if (cubePositions == null || rotSpeeds == null){
            numCubes = 0;
        }
        else{
            numCubes = cubePositions.length;
            if (cubePositions.length != rotSpeeds.length){
                throw new IllegalStateException();
            }
        }
        this.texturePath = texturePath;
        this.hasColors = hasColors;
        indexed = false;
    }

    public void create(Shader shader){
        if (indexed) {
            vao = GL30.glGenVertexArrays();
            vbo = storeBuffer(vao, 0, 3, vertices);
            if (!hasColors) {
                tbo = storeBuffer(vao, 1, 2, textureCoords);
                texture = storeJarTexture(texturePath);
                shader.setUniform("hasColors", false);

                //init color buffer as a default value to avoid opengl shader errors even though its not being used
                cbo = storeBuffer(vao, 2, 3, (FloatBuffer) MemoryUtil.memAllocFloat(3).put(new float[] {0.0f, 0.0f, 0.0f}).flip());
            }
            if (hasColors){
                cbo = storeBuffer(vao, 2, 3, colors);
                shader.setUniform("hasColors", true);

                //init texture coordinate buffer as a default value to avoid opengl shader errors even though its not being used
                tbo = storeBuffer(vao, 1, 2, (FloatBuffer) MemoryUtil.memAllocFloat(2).put(new float[] {0.0f, 0.0f}).flip());
            }
            nbo = storeBuffer(vao, 3, 3, normals);


            ebo = GL15.glGenBuffers();

            GL30.glBindVertexArray(vao);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
        else{
            vao = GL30.glGenVertexArrays();
            vbo = storeBuffer(vao, 0, 3, vertices);
            if (!hasColors) {
                tbo = storeBuffer(vao, 1, 2, textureCoords);
                texture = storeJarTexture(texturePath);
                shader.setUniform("hasColors", false);

                //init color buffer as a default value to avoid opengl shader errors even though its not being used
                cbo = storeBuffer(vao, 2, 3, (FloatBuffer) MemoryUtil.memAllocFloat(3).put(new float[] {0.0f, 0.0f, 0.0f}).flip());
            }
            if (hasColors){
                cbo = storeBuffer(vao, 2, 3, colors);
                shader.setUniform("hasColors", true);

                //init texture coordinate buffer as a default value to avoid opengl shader errors even though its not being used
                tbo = storeBuffer(vao, 1, 2, (FloatBuffer) MemoryUtil.memAllocFloat(2).put(new float[] {0.0f, 0.0f}).flip());
            }
            nbo = storeBuffer(vao, 3, 3, normals);

            ebo = 0;
        }
    }

    public void render(Shader shader, Camera camera, Vector3f trans, float scale, float rotate, boolean debug){
        if (debug) System.out.println("yaw: " + camera.yaw + "\npitch: " + camera.pitch);

        if (trans == null) {
            for (int i = 0; i < numCubes; i++) {

                Matrix4f model = new Matrix4f().translate(cubePositions[i]).scale(0.5f, 0.5f, 0.5f).rotate(rotation * rotSpeeds[i], 0.0f, 1.0f, 0.0f).rotate(rotation * rotSpeeds[i], 1.0f, 0.0f, 0.0f);

                Matrix4f proj = camera.getProjectionMatrix();

                Matrix4f view = camera.getViewMatrix();

                if (!hasColors) {
                    GL20.glActiveTexture(GL20.GL_TEXTURE0);
                    GL20.glBindTexture(GL20.GL_TEXTURE_2D, texture);
                }
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
                if (!camera.getThirdPerson()) {
                    shader.setUniform("viewPos", camera.pos);
                }
                else{
                    shader.setUniform("viewPos", camera.pos.sub(camera.front.mul(camera.zoom / 10, new Vector3f()), new Vector3f()));
                }
                shader.setUniform("hasColors", hasColors);

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

            Matrix4f model = new Matrix4f().translate(trans).scale(scale, scale, scale).rotate(rotate, 0.0f, 1.0f, 0.0f);

            Matrix4f proj = camera.getProjectionMatrix();

            Matrix4f view = camera.getViewMatrix();

            if (!hasColors) {
                GL20.glActiveTexture(GL20.GL_TEXTURE0);
                GL20.glBindTexture(GL20.GL_TEXTURE_2D, texture);
            }
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
            if (!camera.getThirdPerson()) {
                shader.setUniform("viewPos", camera.pos);
            }
            else{
                shader.setUniform("viewPos", camera.pos.sub(camera.front.mul(camera.zoom / 10, new Vector3f()), new Vector3f()));
            }
            shader.setUniform("hasColors", hasColors);

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

    private int storeBuffer(int vao, int index, int size, FloatBuffer data){
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
                System.out.println(STBImage.stbi_failure_reason());
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
            System.out.println(STBImage.stbi_failure_reason());
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

    public void setNormals(float[] normals){
        this.normals = (FloatBuffer) MemoryUtil.memAllocFloat(normals.length).put(normals).flip();
        normalCount = normals.length / 3;
    }

    public IntBuffer getIndices(){
        return indices;
    }

    public FloatBuffer getVertices(){
        return vertices;
    }

    public FloatBuffer getTextureCoords(){
        return textureCoords;
    }

    public FloatBuffer getNormals(){
        return normals;
    }
}
