package cubes;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Vao {
        private ArrayList<Integer> attribs = new ArrayList<>();
        private int handle = GL30.glGenVertexArrays();
        private int ebo = GL15.glGenBuffers();

        public int storeBuffer(int index, int size, FloatBuffer data) {
                GL30.glBindVertexArray(handle);
                int buf = GL15.glGenBuffers();
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buf);
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
                GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                attribs.add(index);
                return buf;
        }

        public void storeIndices(IntBuffer data) {
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
                GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        public void enableAttribs() {
                for (int i = 0; i < attribs.size(); i++) {
                        GL20.glEnableVertexAttribArray(attribs.get(i));
                }
        }

        public void disableAttribs() {
                for (int i = 0; i < attribs.size(); i++) {
                        GL20.glDisableVertexAttribArray(attribs.get(i));
                }
        }

        public void bind() {
                GL30.glBindVertexArray(handle);
        }

        public void unbind() {
                GL30.glBindVertexArray(0);
        }

        public void bindIndices() {
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        }

        public void unbindIndices() {
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        public ArrayList<Integer> getAttribs() {
                return attribs;
        }

        public int getHandle() {
                return handle;
        }
}
