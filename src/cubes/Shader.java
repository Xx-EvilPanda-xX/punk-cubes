package cubes;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

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
                try {
                        this.vertex = loadShaderFile(vertPath);
                        this.fragment = loadShaderFile(fragPath);
                        this.vertPath = vertPath;
                        this.fragPath = fragPath;
                } catch (ClassNotFoundException e) {
                        System.out.println("Incorrect class name literal");
                }
        }

        private static String loadShaderFile(String path) throws ClassNotFoundException {
                try {
                        System.out.println("Attempting to load shader from jar resources at: " + path);
                        Class cl = Class.forName("cubes.Shader");
                        ClassLoader loader = cl.getClassLoader();
                        InputStream is = loader.getResourceAsStream(path);
                        if (is == null) {
                                System.out.println("input stream is null, could not find resource");
                        }
                        String shader = (new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))).lines().collect(Collectors.joining("\n"));
                        System.out.println("Shader loaded from jar resources!");
                        return shader;
                } catch (NullPointerException e) {
                        System.out.println("File not being run from executable jar. Attempting to load shader from direct path: resources/" + path);

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
                matrix[0] = value.get(0, 0);
                matrix[1] = value.get(1, 0);
                matrix[2] = value.get(2, 0);
                matrix[3] = value.get(3, 0);
                matrix[4] = value.get(0, 1);
                matrix[5] = value.get(1, 1);
                matrix[6] = value.get(2, 1);
                matrix[7] = value.get(3, 1);
                matrix[8] = value.get(0, 2);
                matrix[9] = value.get(1, 2);
                matrix[10] = value.get(2, 2);
                matrix[11] = value.get(3, 2);
                matrix[12] = value.get(0, 3);
                matrix[13] = value.get(1, 3);
                matrix[14] = value.get(2, 3);
                matrix[15] = value.get(3, 3);
                if (debug) {
                        System.out.println(Arrays.toString(matrix));
                        GL20.glUniformMatrix4fv(getUniformLocation(name), true, matrix);
                } else {
                        GL20.glUniformMatrix4fv(getUniformLocation(name), true, matrix);
                }
        }

        public void bind() {
                GL20.glUseProgram(programID);
        }

        public void unbind() {
                GL20.glUseProgram(0);
        }

        public int getVertShaderID() {
                return vertShaderID;
        }

        public int getFragShaderID() {
                return fragShaderID;
        }

        public int getProgramID() {
                return programID;
        }
}
		
		
		
		
		
