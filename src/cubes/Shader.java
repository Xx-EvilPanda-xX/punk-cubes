package cubes;

import org.lwjgl.opengl.*;


import java.io.*;
import java.util.Arrays;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Shader {
        private String vertPath;
        private String fragPath;
        private String vertex;
        private String fragment;
        private int vertShaderID;
        private int fragShaderID;
        private int programID;

        public Shader(String vertPath, String fragPath) {
                this.vertex = loadShaderFile(vertPath);
                this.fragment = loadShaderFile(fragPath);
                this.vertPath = vertPath;
                this.fragPath = fragPath;
        }

        private static String loadShaderFile(String path) {
                try {
                        StringBuilder builder = new StringBuilder();
                        BufferedReader reader = new BufferedReader(new FileReader("resources/" + path));
                        String read = "";

                        while ((read = reader.readLine()) != null) {
                                builder.append(read + "\n");
                        }

                        System.out.println("Shader loaded from direct path!");
                        return builder.toString();
                } catch (IOException ex) {
                        System.out.println("Failed to load shader at all possible locations. PROGRAM WILL EXIT");
                        System.exit(-1);
                        return null;
                }
        }

        public boolean create() {
                programID = GL20.glCreateProgram();
                vertShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

                GL20.glShaderSource(vertShaderID, vertex);
                GL20.glCompileShader(vertShaderID);

                if (GL20.glGetShaderi(vertShaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                        System.out.println("ERROR: syntax error in vertex shader source: " + GL20.glGetShaderInfoLog(vertShaderID));
                        return false;
                }

                fragShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

                GL20.glShaderSource(fragShaderID, fragment);
                GL20.glCompileShader(fragShaderID);

                if (GL20.glGetShaderi(fragShaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                        System.out.println("ERROR: syntax error in fragment shader source: " + GL20.glGetShaderInfoLog(fragShaderID));
                        return false;
                }

                GL20.glAttachShader(programID, vertShaderID);
                GL20.glAttachShader(programID, fragShaderID);
                GL20.glLinkProgram(programID);

                if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                        System.out.println("ERROR: error when linking shader program: " + GL20.glGetProgramInfoLog(programID));
                        return false;
                }

                GL20.glValidateProgram(programID);

                if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
                        System.out.println("ERROR: error when validating shader program: " + GL20.glGetProgramInfoLog(programID));
                        return false;
                }

                GL20.glDeleteShader(vertShaderID);
                GL20.glDeleteShader(fragShaderID);

                return true;
        }

        public int getUniformLocation(String name) {
                return GL20.glGetUniformLocation(programID, name);
        }

        public void setUniform(String name, float value) {
                GL20.glUniform1f(getUniformLocation(name), value);
        }

        public void setUniform(String name, int value) {
                GL20.glUniform1i(getUniformLocation(name), value);
        }

        public void setUniform(String name, boolean value) {
                GL20.glUniform1i(getUniformLocation(name), value ? 1 : 0);
        }

        public void setUniform(String name, Vector2f value) {
                GL20.glUniform2f(getUniformLocation(name), value.x, value.y);
        }

        public void setUniform(String name, Vector3f value) {
                GL20.glUniform3f(getUniformLocation(name), value.x, value.y, value.z);
        }

        public void setUniform(String name, Matrix4f value, boolean debug) {
                float[] matrix = new float[16];
                for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {
                                matrix[(i * 4) + j] = value.get(j, i);
                        }
                }

                GL20.glUniformMatrix4fv(getUniformLocation(name), true, matrix);
                if (debug) {
                        System.out.println(Arrays.toString(matrix));
                }
        }

        public void bind() {
                GL20.glUseProgram(programID);
        }

        public void unbind() {
                GL20.glUseProgram(0);
        }

        public int getProgramID() {
                return programID;
        }
}
		
		
		
		
		
