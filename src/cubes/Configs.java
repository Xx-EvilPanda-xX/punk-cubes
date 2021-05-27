package cubes;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.io.BufferedReader;
import java.io.FileReader;

public class Configs {
        public static boolean FULLSCREEN = false;
        public static boolean DEBUG = false;
        public static boolean RENDER_CUBES = true;

        public static int CUBE_COUNT = 4096;
        public static float SKYBOX_SCALE = 100.0f;

        public static int WIDTH = 1080;
        public static int HEIGHT = 720;

        public static Vector3f BLOCK_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);
        public static float BLOCK_PLACEMENT_RATE = 0.1f;
        public static float BLOCK_ROTATION = 0.0f;

        static {
                try {
                        StringBuilder builder = new StringBuilder();
                        BufferedReader reader = new BufferedReader(new FileReader("configs.txt"));
                        String read;
                        while ((read = reader.readLine()) != null) {
                                builder.append(read + "\n");
                        }

                        read = builder.toString();
                        String[] configLines = read.split("\n");
                        String[] configs = new String[configLines.length];
                        for (int i = 0; i < configLines.length; i++) {
                                configs[i] = configLines[i].split(" = ")[1];
                        }

                        DEBUG = Boolean.parseBoolean(configs[1]);
                        RENDER_CUBES = Boolean.parseBoolean(configs[2]);
                        BLOCK_PLACEMENT_RATE = Float.parseFloat(configs[7]);
                        BLOCK_COLOR.x = Float.parseFloat(configs[8].split(", ")[0]);
                        BLOCK_COLOR.y = Float.parseFloat(configs[8].split(", ")[1]);
                        BLOCK_COLOR.z = Float.parseFloat(configs[8].split(", ")[2]);
                        BLOCK_ROTATION = Float.parseFloat(configs[9]);

                        if ((FULLSCREEN = Boolean.parseBoolean(configs[0]))) {
                                GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
                                WIDTH = vidmode.width();
                                HEIGHT = vidmode.height();
                        } else {
                                WIDTH = Integer.parseInt(configs[3]);
                                HEIGHT = Integer.parseInt(configs[4]);
                        }

                        if ((CUBE_COUNT = Integer.parseInt(configs[5])) > 10000) {
                                System.out.println("Too many cubes! Cube count will be set to 4096");
                                CUBE_COUNT = 10000;
                        }

                        if ((SKYBOX_SCALE = Float.parseFloat(configs[6])) > 250 || (SKYBOX_SCALE = Float.parseFloat(configs[6])) < 5) {
                                System.out.println("Invalid skybox size! Skybox size will be size to 100.0");
                                SKYBOX_SCALE = 100.0f;
                        }

                        System.out.println("Custom config file successfully loaded!");

                } catch (Exception e) {
                        System.out.println("Config file not found or corrupted. Configs will be set to default values");
                        e.printStackTrace();
                }
        }
}
