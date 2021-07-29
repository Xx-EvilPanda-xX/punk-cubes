package cubes;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Window implements Runnable {
        public static final float RECHARGE_TIME = 0.25f;
        public static float deltaTime = 0.0f;
        public static Vector3f currentLightPos = new Vector3f(0.0f, 0.0f, 1.0f);
        public static GLFWVidMode vidmode;
        public Camera camera;
        public Input input;
        public EventHandler eventHandler;

        public boolean renderQuads = false, showCoords = false, placingBlocks = false;
        public long window;
        public boolean fullscreen;
        public float windowWidth = Float.parseFloat(Configs.options.get("width"));
        public float windowHeight = Float.parseFloat(Configs.options.get("height"));

        private float lastFrame = 0.0f;
        private int frames;
        private static long time;

        private Thread pog;
        private Shader shader;
        private final String title = "Cubes!!!";
        private float[] coolDownPool = new float[32];

        public ColorQuadRenderer quads[];

        private TextureRenderer skyBox;
        private TextureRenderer player;
        private TextureRendererMulti blocks;
        private TextureRenderer light;
        private TextureRendererMulti planets;
        private TextureRendererMulti asteroids;
        private TextureRenderer bike;
        private TextureRenderer robot;

        public ArrayList<Vector3f> planetPositions = new ArrayList<>();
        public ArrayList<Float> planetScales = new ArrayList<>();
        public ArrayList<Vector3f> planetRots = new ArrayList<>();
        public ArrayList<Vector3f> asteroidPositions = new ArrayList<>();
        public ArrayList<Float> asteroidScales = new ArrayList<>();
        public ArrayList<Vector3f> asteroidRots = new ArrayList<>();
        public ArrayList<Vector3f> blockPositions = new ArrayList<>();
        public ArrayList<Float> blockScales = new ArrayList<>();
        public ArrayList<Vector3f> blockRots = new ArrayList<>();

        public void start() {
                pog = new Thread(this, "fortnite;");
                pog.start();
        }

        public void run() {
                Matrix4f proj = new Matrix4f().perspective((float) Math.toRadians(45), 1080.0f / 720.0f, 0.1f, 100.0f);
                Vector4f vec = new Vector4f(5.0f, 3.0f, 200.0f, 1.0f).mul(proj);
                System.out.println(proj.toString());
                System.out.println(vec.x + ", " + vec.y + ", " + vec.z + ", " + vec.w);

                System.out.println("Hello LWJGL " + Version.getVersion() + "!");

                init();
                create();
                while (!GLFW.glfwWindowShouldClose(window)) {
                        loop(Boolean.parseBoolean(Configs.options.get("render_solar_entities")), renderQuads, Boolean.parseBoolean(Configs.options.get("debug")));
                }

                Callbacks.glfwFreeCallbacks(window);
                GLFW.glfwDestroyWindow(window);
                GLFW.glfwTerminate();
                GLFW.glfwSetErrorCallback(null).free();
        }

        private void init() {
                if (!GLFW.glfwInit()) {
                        throw new IllegalStateException("Unable to initialize GLFW");
                }
                GLFWErrorCallback.createPrint(System.err).set();
                vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

                GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
                GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

                fullscreen = Boolean.parseBoolean(Configs.options.get("fullscreen"));
                if (fullscreen) {
                        window = GLFW.glfwCreateWindow(vidmode.width(), vidmode.height(), title, GLFW.glfwGetPrimaryMonitor(), MemoryUtil.NULL);
                } else {
                        window = GLFW.glfwCreateWindow(Integer.parseInt(Configs.options.get("width")), Integer.parseInt(Configs.options.get("height")), title, MemoryUtil.NULL, MemoryUtil.NULL);
                }

                if (window == MemoryUtil.NULL) {
                        throw new RuntimeException("Failed to create the GLFW window");
                }
                System.out.println("Window memory address: " + window);

                try (MemoryStack stack = MemoryStack.stackPush()) {
                        IntBuffer pWidth = stack.mallocInt(1);
                        IntBuffer pHeight = stack.mallocInt(1);
                        if (!Boolean.parseBoolean(Configs.options.get("fullscreen"))) {
                                GLFW.glfwGetWindowSize(window, pWidth, pHeight);
                                GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
                                GLFW.glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }

                GLFW.glfwMakeContextCurrent(window);
                GL.createCapabilities();
                GLFW.glfwShowWindow(window);
        }

        private void create() {
                for (int ptr = 0; ptr < Integer.parseInt(Configs.options.get("planet_count")); ptr++) {
                        planetPositions.add(genRandVec());
                        planetScales.add(genRandFloat() * 5);
                        planetRots.add(genRandVec());
                }

                for (int ptr = 0; ptr < Integer.parseInt(Configs.options.get("asteroid_count")); ptr++) {
                        asteroidPositions.add(genRandVec());
                        asteroidScales.add(genRandFloat());
                        asteroidRots.add(genRandVec());
                }

                GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
                camera = new Camera(this, new Vector3f(0.0f, 0.0f, -5.0f), 0.0f, 0.0f);
                input = new Input(camera, window);
                eventHandler = new EventHandler(camera, this);

                time = System.currentTimeMillis();

                GLFW.glfwSetWindowSizeCallback(window, (windowPog, width, height) -> {
                        windowWidth = width;
                        windowHeight = height;
                });

                shader = new Shader("shaders/basicVert.glsl", "shaders/basicFrag.glsl");
                shader.create();

                TextureQuadRenderer loadingScreen = new TextureQuadRenderer(new TexturedMesh(Geometry.QUAD_VERTICES, Geometry.QUAD_TEX_COORDS, Geometry.QUAD_NORMALS, new int[]{0, 1, 2, 0, 2, 3}, "textures/loading_screen.jpg"), 2.0f, 0.0f, 0.0f, 0.0f);
                loadingScreen.USE_PROJ_VIEW_MAT = false;
                loadingScreen.create(shader, camera);

                loadingScreen.render(false);

                GLFW.glfwSwapBuffers(window);

                quads = new ColorQuadRenderer[]{new ColorQuadRenderer(new ColoredMesh(Geometry.QUAD_VERTICES, Geometry.QUAD_COLORS, Geometry.QUAD_NORMALS, new int[]{0, 1, 2, 0, 2, 3}), 0.5f, 0.7f, 0.5f, 0.0f),
                        new ColorQuadRenderer(new ColoredMesh(Geometry.QUAD_VERTICES, Geometry.QUAD_COLORS, Geometry.QUAD_NORMALS, new int[]{0, 1, 2, 0, 2, 3}), 0.7f, 0.5f, -0.5f, 0.0f),
                        new ColorQuadRenderer(new ColoredMesh(Geometry.QUAD_VERTICES, Geometry.QUAD_COLORS, Geometry.QUAD_NORMALS, new int[]{0, 1, 2, 0, 2, 3}), 0.05f, 1.0f, 0.0f, 0.0f),
                };

                skyBox = new TextureRenderer(new TexturedMesh(Geometry.CUBE_VERTICES, Geometry.CUBE_TEX_COORDS, Geometry.CUBE_NORMALS, "textures/skybox.png"));
                player = new TextureRenderer("models/iron_man/IronMan/IronMan.obj", new String[]{}, false);
                planets = new TextureRendererMulti("models/island/island.obj", new String[]{"textures/old/wood.png"}, planetPositions, planetScales, planetRots, false);
                asteroids = new TextureRendererMulti("models/backpack/backpack.obj", new String[]{"models/backpack/diffuse.jpg"}, asteroidPositions, asteroidScales, asteroidRots, true);
                light = new TextureRenderer("models/donut/Donut.obj", new String[]{"models/donut/Tekstur_donat.png"}, false);
                blocks = new TextureRendererMulti("models/bu/Bu.obj", new String[]{"models/bu/bu.jpg"}, blockPositions, blockScales, blockRots, false);
                bike = new TextureRenderer("models/motorcycle/motorcycle.obj", new String[]{"models/motorcycle/motorcycle_tex.jpg"}, true);
                robot = new TextureRenderer("models/robot/robot.obj", new String[]{}, false);

                for (ColorQuadRenderer r : quads) {
                        r.create(shader, camera);
                }
                planets.create(shader, camera);
                blocks.create(shader, camera);
                skyBox.create(shader, camera);
                asteroids.create(shader, camera);
                player.create(shader, camera);
                light.create(shader, camera);
                bike.create(shader, camera);
                robot.create(shader, camera);

                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
                camera.setThirdPerson(true);

                System.out.println(GL11.glGetString(GL11.GL_VERSION));
                GLFW.glfwSetCursorPos(window, 0.0f, 0.0f);
        }

        private void updateFPS() {
                frames++;
                if (System.currentTimeMillis() > (time + 1000)) {
                        GLFW.glfwSetWindowTitle(window, title + " | FPS: " + frames);
                        time = System.currentTimeMillis();
                        frames = 0;
                }
        }

        private void updateSolarEntityRotation() {
                for (int i = 0; i < asteroidRots.size(); i++) {
                        asteroidRots.set(i, asteroidRots.get(i).add(deltaTime, deltaTime, deltaTime));
                }
        }

        private Vector3f genRandVec() {
                //idk what im doing, this is a really odd way to generate random nums
                float randX;
                float randY;
                float randZ;

                float skyboxScale = Float.parseFloat(Configs.options.get("skybox_scale"));

                randX = ((float) Math.random() * 10000.0f) - 5000.0f;
                while (randX > ((skyboxScale - 2.0f) / 2.0f) || randX < -((skyboxScale - 2.0f) / 2.0f)) {
                        randX = ((float) Math.random() * 10000.0f) - 5000.0f;
                }

                randY = ((float) Math.random() * 10000.0f) - 5000.0f;
                while (randY > ((skyboxScale - 2.0f) / 2.0f) || randY < -((skyboxScale - 2.0f) / 2.0f)) {
                        randY = ((float) Math.random() * 10000.0f) - 5000.0f;
                }

                randZ = ((float) Math.random() * 10000.0f) - 5000.0f;
                while (randZ > ((skyboxScale - 2.0f) / 2.0f) || randZ < -((skyboxScale - 2.0f) / 2.0f)) {
                        randZ = ((float) Math.random() * 10000.0f) - 5000.0f;
                }

                return new Vector3f(randX, randY, randZ);
        }

        private float genRandFloat() {
                float rand = ((float) Math.random() * 10.0f) - 5.0f;

                while (rand > 1.0f || rand < -1.0f) {
                        rand = ((float) Math.random() * 10.0f) - 5.0f;
                }

                return rand;
        }


        private void loop(boolean renderCubes, boolean renderQuads, boolean debug) {
                if (eventHandler.isFocused()) {
                        GLFW.glfwSetCursorPos(window, 0.0f, 0.0f);
                }
                float currentFrame = (float) GLFW.glfwGetTime();
                deltaTime = currentFrame - lastFrame;
                lastFrame = currentFrame;

                eventHandler.processInput();
                updateSolarEntityRotation();

                GL11.glViewport(0, 0, fullscreen ? vidmode.width() : (int) windowWidth, fullscreen ? vidmode.height() : (int) windowHeight);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

                float skyboxScale = Float.parseFloat(Configs.options.get("skybox_scale"));

                robot.setTrans(new Vector3f(-10.0f, skyboxScale / 2, 0.0f)).setScale(10.0f).setRotation(new Vector3f(0.0f, 0.0f,0.0f)).render(debug);
                bike.setTrans(new Vector3f(10.0f, skyboxScale / 2, 0.0f)).setScale(10.0f).setRotation(new Vector3f(0.0f, 0.0f,0.0f)).render(debug);
                skyBox.setTrans(new Vector3f(0.0f, 0.0f, 0.0f)).setScale(skyboxScale).setRotation(new Vector3f(0.0f, 0.0f, 0.0f)).render(debug);
                light.setTrans(currentLightPos).setScale(10.0f).setRotation(new Vector3f(0.0f, 0.0f, 0.0f)).render(debug);
                if (renderCubes) {
                        planets.render(debug);
                        asteroids.render(debug);
                }

                if (blockPositions.size() > 0 && !camera.isThirdPerson() && placingBlocks) {
                        Vector3f posTemp = new Vector3f(blockPositions.get(blockPositions.size() - 1));
                        float scaleTemp = blockScales.get(blockScales.size() - 1);
                        Vector3f rotTemp = blockRots.get(blockRots.size() - 1);

                        blockPositions.remove(blockPositions.size() - 1);
                        blockScales.remove(blockScales.size() - 1);
                        blockRots.remove(blockRots.size() - 1);

                        blocks.render(debug);

                        blockPositions.add(posTemp);
                        blockScales.add(scaleTemp);
                        blockRots.add(rotTemp);
                } else {
                        blocks.render(debug);
                }

                if (camera.isThirdPerson()) {
                        player.setTrans(new Vector3f(camera.playerPos.x, camera.playerPos.y - 1.0f, camera.playerPos.z)).setScale(0.01f).setRotation(new Vector3f(0.0f, camera.getThirdPersonRotation() + (float) Math.toRadians(90.0f), 0.0f)).render(debug);
                }

                if (renderQuads) {
                        for (ColorQuadRenderer r : quads) {
                                r.render(debug);
                        }
                }

                if (showCoords) {
                        if (coolDownPool[0] <= 0.0f) {
                                System.out.println("x: " + camera.playerPos.x + ", y: " + camera.playerPos.y + ", z: " + camera.playerPos.z);
                                coolDownPool[0] = RECHARGE_TIME;
                        }
                }

                if (placingBlocks && coolDownPool[1] <= 0.0f) {
                        blockPositions.add(new Vector3f(camera.playerPos));
                        blockScales.add(Float.parseFloat(Configs.options.get("block_scale")));
                        blockRots.add(new Vector3f(Float.parseFloat(Configs.options.get("block_rotation.x")), Float.parseFloat(Configs.options.get("block_rotation.y")), Float.parseFloat(Configs.options.get("block_rotation.z"))));
                        coolDownPool[1] = Float.parseFloat(Configs.options.get("block_placement_rate"));
                }

                GLFW.glfwSwapBuffers(window);
                GLFW.glfwPollEvents();
                updateFPS();

                for (int i = 0; i < coolDownPool.length; i++) {
                        coolDownPool[i] -= Window.deltaTime;
                        if (coolDownPool[i] < 0.0f) coolDownPool[i] = 0.0f;
                }
        }

        public static void main(String[] args) {
                new Window().start();
        }
}
