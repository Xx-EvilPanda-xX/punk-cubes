package cubes;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.io.BufferedReader;
import java.io.FileReader;

public class Configs {
        public static boolean FULLSCREEN = false;
        public static boolean DEBUG = false;
        public static boolean RENDER_SOLAR_ENTITIES = true;

        public static int PLANET_COUNT = 100;
        public static int ASTEROID_COUNT = 100;
        public static float SKYBOX_SCALE = 100.0f;

        public static int WIDTH = 1080;
        public static int HEIGHT = 720;

        public static Vector3f BLOCK_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);
        public static float BLOCK_PLACEMENT_RATE = 0.1f;
        public static Vector3f BLOCK_ROTATION = new Vector3f(0.0f, 0.0f, 0.0f);
        public static float BLOCK_SCALE = 1.0f;

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
                        RENDER_SOLAR_ENTITIES = Boolean.parseBoolean(configs[2]);
                        BLOCK_PLACEMENT_RATE = Float.parseFloat(configs[8]);

                        BLOCK_COLOR.x = Float.parseFloat(configs[9].split(", ")[0]);
                        BLOCK_COLOR.y = Float.parseFloat(configs[9].split(", ")[1]);
                        BLOCK_COLOR.z = Float.parseFloat(configs[9].split(", ")[2]);

                        BLOCK_ROTATION.x = Float.parseFloat(configs[10].split(", ")[0]);
                        BLOCK_ROTATION.y = Float.parseFloat(configs[10].split(", ")[1]);
                        BLOCK_ROTATION.z = Float.parseFloat(configs[10].split(", ")[2]);

                        BLOCK_SCALE = Float.parseFloat(configs[11]);

                        if ((FULLSCREEN = Boolean.parseBoolean(configs[0]))) {
                                GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
                                WIDTH = vidmode.width();
                                HEIGHT = vidmode.height();
                        } else {
                                WIDTH = Integer.parseInt(configs[3]);
                                HEIGHT = Integer.parseInt(configs[4]);
                        }

                        if ((PLANET_COUNT = Integer.parseInt(configs[5])) > 10000) {
                                System.out.println("Too many planets! Cube count will be set to 10000");
                                PLANET_COUNT = 10000;
                        }

                        if ((ASTEROID_COUNT = Integer.parseInt(configs[6])) > 10000) {
                                System.out.println("Too many asteroids! Cube count will be set to 10000");
                                ASTEROID_COUNT = 10000;
                        }

                        if ((SKYBOX_SCALE = Float.parseFloat(configs[7])) > 250 || (SKYBOX_SCALE = Float.parseFloat(configs[7])) < 5) {
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
