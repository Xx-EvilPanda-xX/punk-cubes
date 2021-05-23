package cubes;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class Vao {
        private int attribCount;
        private int handle = GL30.glGenVertexArrays();

        public int storeBuffer(int index, int size, FloatBuffer data) {
                GL30.glBindVertexArray(handle);
                int buf = GL15.glGenBuffers();
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buf);
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
                GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                attribCount++;
                return buf;
        }

        public int getAttribCount(){
                return attribCount;
        }

        public int getHandle(){
                return handle;
        }
}
