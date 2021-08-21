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
import org.lwjgl.opengl.GL30;
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

        public boolean showDebug = false, placingBlocks = false;
        public long window;
        public boolean fullscreen;
        public float windowWidth = Float.parseFloat(Configs.options.get("width"));
        public float windowHeight = Float.parseFloat(Configs.options.get("height"));

        private float lastFrame = 0.0f;
        private int frames;
        private static long time;

        private Thread pog;
        private Shader shader;
        private Shader textShader;
        private final String title = "Cubes!!!";
        private float[] coolDownPool = new float[32];

        private TextRenderer fpsCounter;
        private TextRenderer coords;
        private TextureRenderer skyBox;
        private TextureRenderer player;
        private TextureRendererMulti billys;
        private TextureRenderer donut;
        private TextureRendererMulti islands;
        private TextureRendererMulti backpacks;
        private TextureRenderer bike;
        private TextureRenderer robot;
        private TextureRenderer bu;

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
                        loop(Boolean.parseBoolean(Configs.options.get("render_solar_entities")));
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
                        planetScales.add(genRandFloat());
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

                boolean useLinuxShaders = Boolean.parseBoolean(Configs.options.get("use_linux_shaders"));
                shader = new Shader(useLinuxShaders ? "shaders/es-shaders/mainVert.glsl" : "shaders/mainVert.glsl", useLinuxShaders ? "shaders/es-shaders/mainFrag.glsl" : "shaders/mainFrag.glsl");
                shader.create();

                textShader = new Shader(useLinuxShaders ? "shaders/es-shaders/textVert.glsl" : "shaders/textVert.glsl", useLinuxShaders ? "shaders/es-shaders/textFrag.glsl" : "shaders/textFrag.glsl");
                textShader.create();

                GL11.glClearColor(0.0f, 0.5f, 0.5f, 1.0f);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

                TextRenderer loadingText = new TextRenderer("loading assets", -0.45f, 0.1f,0.4f, 0.15f);
                loadingText.create(textShader);

                loadingText.render();

                GLFW.glfwSwapBuffers(window);

                //try {
                //        Thread.sleep(10000);
                //} catch (InterruptedException e){
                //        e.printStackTrace();
                //}

                coords = new TextRenderer("x = " + camera.playerPos.x + "y = " + camera.playerPos.y + "z = " + camera.playerPos.z, -1.0f, 0.9f, 0.2f, 0.075f);
                fpsCounter = new TextRenderer("", -1.0f, 1.0f, 0.2f, 0.075f);
                skyBox = new TextureRenderer("models/cube.obj", new String[]{"textures/skybox.png"}, true);
                player = new TextureRenderer("models/iron_man/IronMan/IronMan.obj", new String[]{}, false);
                islands = new TextureRendererMulti("models/island/island.obj", new String[]{"textures/old/wood.png"}, planetPositions, planetScales, planetRots, false);
                backpacks = new TextureRendererMulti("models/backpack/backpack.obj", new String[]{"models/backpack/diffuse.jpg"}, asteroidPositions, asteroidScales, asteroidRots, true);
                donut = new TextureRenderer("models/donut/Donut.obj", new String[]{"models/donut/Tekstur_donat.png"}, false);
                billys = new TextureRendererMulti("models/bu/bu_lowpoly.obj", new String[]{"models/bu/bu.jpg"}, blockPositions, blockScales, blockRots, false);
                bike = new TextureRenderer("models/motorcycle/motorcycle.obj", new String[]{"models/motorcycle/motorcycle_tex.jpg"}, true);
                robot = new TextureRenderer("models/robot/robot.obj", new String[]{}, false);
                bu = new TextureRenderer("models/bu/Bu.obj", new String[]{"models/bu/bu.jpg"}, false);

                coords.create(textShader);
                fpsCounter.create(textShader);
                islands.create(shader, camera);
                billys.create(shader, camera);
                skyBox.create(shader, camera);
                backpacks.create(shader, camera);
                player.create(shader, camera);
                donut.create(shader, camera);
                bike.create(shader, camera);
                robot.create(shader, camera);
                bu.create(shader, camera);

                GL11.glEnable(GL11.GL_DEPTH_TEST);
                if (Boolean.parseBoolean(Configs.options.get("use_gamma_correction"))) {
                        GL11.glEnable(GL30.GL_FRAMEBUFFER_SRGB);
                }
                GL11.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
                camera.setThirdPerson(true);

                System.out.println(GL11.glGetString(GL11.GL_VERSION));
                GLFW.glfwSetCursorPos(window, 0.0f, 0.0f);
        }

        private void updateFPS() {
                frames++;
                if (System.currentTimeMillis() > (time + 1000)) {
                        fpsCounter.updateText("FPS = " + frames, -1.0f, 1.0f, 0.2f, 0.075f);
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


        private void loop(boolean renderCubes) {
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

                bu.setTrans(new Vector3f(0.0f, -100.0f, 0.0f)).setScale(222.2f).setRotation(new Vector3f(0.0f, 0.0f, (float) Math.toRadians(-180.0f))).render();
                robot.setTrans(new Vector3f(-10.0f, skyboxScale / 2, 0.0f)).setScale(10.0f).setRotation(new Vector3f(0.0f, 0.0f,0.0f)).render();
                bike.setTrans(new Vector3f(10.0f, skyboxScale / 2, 0.0f)).setScale(10.0f).setRotation(new Vector3f(0.0f, 0.0f,0.0f)).render();
                skyBox.setTrans(new Vector3f(0.0f, 0.0f, 0.0f)).setScale(skyboxScale).setRotation(new Vector3f(0.0f, 0.0f, 0.0f)).render();
                donut.setTrans(currentLightPos).setScale(10.0f).setRotation(new Vector3f(0.0f, 0.0f, 0.0f)).render();
                if (renderCubes) {
                        islands.render();
                        backpacks.render();
                }

                if (blockPositions.size() > 0 && !camera.isThirdPerson() && placingBlocks) {
                        Vector3f posTemp = new Vector3f(blockPositions.get(blockPositions.size() - 1));
                        float scaleTemp = blockScales.get(blockScales.size() - 1);
                        Vector3f rotTemp = new Vector3f(blockRots.get(blockRots.size() - 1));

                        blockPositions.remove(blockPositions.size() - 1);
                        blockScales.remove(blockScales.size() - 1);
                        blockRots.remove(blockRots.size() - 1);

                        billys.render();

                        blockPositions.add(posTemp);
                        blockScales.add(scaleTemp);
                        blockRots.add(rotTemp);
                } else {
                        billys.render();
                }

                if (camera.isThirdPerson()) {
                        player.setTrans(new Vector3f(camera.playerPos.x, camera.playerPos.y - 1.0f, camera.playerPos.z)).setScale(0.01f).setRotation(new Vector3f(0.0f, camera.getThirdPersonRotation() + (float) Math.toRadians(90.0f), 0.0f)).render();
                }

                if (showDebug) {
                        textRenderPass();
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

                if (coolDownPool[2] <= 0.0f) {
                        coords.updateText("x = " + camera.playerPos.x + ", y = " + camera.playerPos.y + ", z = " + camera.playerPos.z, -1.0f, 0.9f, 0.2f, 0.075f);
                        coolDownPool[2] = 0.1f;
                }

                for (int i = 0; i < coolDownPool.length; i++) {
                        coolDownPool[i] -= Window.deltaTime;
                        if (coolDownPool[i] < 0.0f) coolDownPool[i] = 0.0f;
                }
        }

        private void textRenderPass(){
                fpsCounter.render();
                coords.render();
        }

        public static void main(String[] args) {
                new Window().start();
        }
}
