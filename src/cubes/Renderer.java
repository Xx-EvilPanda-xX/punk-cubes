package cubes;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;

public class Renderer {
    public static boolean USE_PROJ_VIEW_MAT = true;
    
    public final float SCALE;
    public final float LIMIT;
    public final float SPEED;
    public final float YPOS;

    private FloatBuffer vertices, colors;
    private IntBuffer indices;
    private boolean indexed;
    private float uniformRed, colorOffset, trans, transOffset, rotation;
    private int vao, vbo, ebo, cbo;
    public int vertexCount, indexCount, colorCount;

    public Renderer (float[] vertexData, float[] colorData, int[] indexData, float scale, float speed, float ypos, float rotation){
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
    }

    public Renderer (float[] vertexData, float[] colorData, float scale, float speed, float ypos){
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
    }

    public void create(boolean indexed){
        this.indexed = indexed;
        if (indexed) {
            vao = GL30.glGenVertexArrays();
            vbo = storeBuffer(vao, 0, 3, vertices);
            cbo = storeBuffer(vao, 1, 3, colors);
            ebo = GL15.glGenBuffers();

            GL30.glBindVertexArray(vao);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
        else{
            vao = GL30.glGenVertexArrays();
            vbo = storeBuffer(vao, 0, 3, vertices);
            cbo = storeBuffer(vao, 1, 3, colors);
            ebo = 0;
        }
    }

    public void render(Shader shader, Camera camera, boolean debug){
        if (debug) System.out.println("yaw: " + camera.yaw + "\npitch: " + camera.pitch);
        
        trans += transOffset * Window.deltaTime;
        if (trans > LIMIT){
            transOffset = -SPEED;
            trans = LIMIT;
        }
        if (trans < -LIMIT){
            transOffset = SPEED;
            trans = -LIMIT;
        }

        Matrix4f model;

        if (USE_PROJ_VIEW_MAT) {
            model = new Matrix4f().translate(trans, YPOS, -3.0f).scale(SCALE, SCALE, SCALE).rotate(rotation * trans, 0.0f, 0.0f, 1.0f);
        }
        else{
            model = new Matrix4f().translate(trans, YPOS, 0.0f).scale(SCALE, SCALE, SCALE).rotate(rotation * trans, 0.0f, 0.0f, 1.0f);
        }
        
        Matrix4f proj = camera.getProjectionMatrix();

        Matrix4f view = camera.getViewMatrix();
        
        if (debug) {
            System.out.println(model.toString());
            shader.setUniform("model", model, true);
            if (USE_PROJ_VIEW_MAT){
                System.out.println(proj.toString());
                shader.setUniform("projection", proj, true);
                System.out.println(view.toString());
                shader.setUniform("view", view, true);
            }
        }
        else{
            shader.setUniform("model", model, false);
            if (USE_PROJ_VIEW_MAT){
                shader.setUniform("projection", proj, false);
                shader.setUniform("view", view, false);
            }
        }

        shader.setUniform("red", uniformRed);

        if (indexed) {
            GL30.glBindVertexArray(this.vao);
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT, 0);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
        }
        else {
            GL30.glBindVertexArray(vao);
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
        }
        uniformRed += colorOffset * Window.deltaTime;
        if (uniformRed > 1.0f){
            colorOffset = -0.5f;
        }
        else if (uniformRed < 0.0f){
            colorOffset = 0.5f;
        }
    }

    public int storeBuffer(int vao, int index, int size, FloatBuffer data){
        GL30.glBindVertexArray(vao);
        int buf = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buf);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return buf;
    }
    
    public void setIndices(int[] indexData){
        this.indices = (IntBuffer) MemoryUtil.memAllocInt(indexData.length).put(indexData).flip();
        indexCount = indexData.length;
    }
    
    public void setVertices(float[] vertexData){
        this.vertices = (FloatBuffer) MemoryUtil.memAllocFloat(vertexData.length).put(vertexData).flip();
        vertexCount = vertexData.length / 3;
    }

    public void setColors(float[] colorData){
        this.colors = (FloatBuffer) MemoryUtil.memAllocFloat(colorData.length).put(colorData).flip();
        colorCount = colorData.length / 3;
    }
    
    public IntBuffer getIndices(){
        return indices;
    }
    
    public FloatBuffer getVertices(){
        return vertices;
    }

    public FloatBuffer getColors(){
        return colors;
    }
}
