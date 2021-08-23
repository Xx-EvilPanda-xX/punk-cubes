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

public class Window {
        public static final float RECHARGE_TIME = 0.25f;
        public static final float DEBUG_RECHARGE_TIME = 0.1f;
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

        private Shader shader;
        private Shader textShader;
        private final String title = "Cubes!!!";

        private TextRenderer text1;
        private TextRenderer text2;
        private TextRenderer text3;
        private TextRenderer text4;
        private TextRenderer fpsCounter;
        private TextRenderer coords;
        private TextRenderer firstPersonDirection;
        private TextRenderer thirdPersonDirection;
        private TextRenderer buCount;
        private TextureRenderer skyBox;
        private TextureRenderer player;
        private TextureRendererMulti billys;
        private TextureRenderer donut;
        private TextureRendererMulti islands;
        private TextureRendererMulti backpacks;
        private TextureRenderer bike;
        private TextureRenderer robot;
        private TextureRenderer bu;

        public ArrayList<Vector3f> islandPositions = new ArrayList<>();
        public ArrayList<Float> islandScales = new ArrayList<>();
        public ArrayList<Vector3f> islandRots = new ArrayList<>();
        public ArrayList<Vector3f> backpackPositions = new ArrayList<>();
        public ArrayList<Float> backpackScales = new ArrayList<>();
        public ArrayList<Vector3f> backpackRots = new ArrayList<>();
        public ArrayList<Vector3f> billyPositions = new ArrayList<>();
        public ArrayList<Float> billyScales = new ArrayList<>();
        public ArrayList<Vector3f> billyRots = new ArrayList<>();

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
                for (int ptr = 0; ptr < Integer.parseInt(Configs.options.get("island_count")); ptr++) {
                        islandPositions.add(genRandVec());
                        islandScales.add(genRandFloat());
                        islandRots.add(new Vector3f(0.0f, 0.0f, 0.0f));
                }

                for (int ptr = 0; ptr < Integer.parseInt(Configs.options.get("backpack_count")); ptr++) {
                        backpackPositions.add(genRandVec());
                        backpackScales.add(genRandFloat());
                        backpackRots.add(genRandVec());
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

                TextRenderer loadingText = new TextRenderer("LOADING ASSETS", -0.45f, 0.1f, 0.4f, 0.15f);
                loadingText.create(textShader);

                loadingText.render();

                GLFW.glfwSwapBuffers(window);

                text1 = new TextRenderer("#___#", 0.9f, -0.75f, 0.2f, 0.075f);
                text2 = new TextRenderer("(o~o)", 0.9f, -0.8f, 0.2f, 0.075f);
                text3 = new TextRenderer(" ) (", 0.9f, -0.85f, 0.2f, 0.075f);
                text4 = new TextRenderer("(___)", 0.9f, -0.9f, 0.2f, 0.075f);
                coords = new TextRenderer("XYZ: " + camera.playerPos.x + ", " + camera.playerPos.y + ", " + camera.playerPos.z, -1.0f, 0.9f, 0.2f, 0.075f);
                firstPersonDirection = new TextRenderer("yaw/pitch: " + camera.getYaw() + ", " + camera.getPitch(), -1.0f, 0.8f, 0.2f, 0.075f);
                thirdPersonDirection = new TextRenderer("third person rotation: " + camera.getKeyBoardYaw(), -1.0f, 0.8f, 0.2f, 0.075f);
                buCount = new TextRenderer("bu count: ", -1.0f, 0.7f, 0.2f, 0.075f);
                fpsCounter = new TextRenderer("", -1.0f, 1.0f, 0.2f, 0.075f);
                skyBox = new TextureRenderer("models/cube.obj", new String[]{"textures/skybox.png"}, true);
                player = new TextureRenderer("models/iron_man/IronMan/IronMan.obj", new String[]{}, false);
                islands = new TextureRendererMulti("models/island/island.obj", new String[]{"textures/old/wood.png"}, islandPositions, islandScales, islandRots, false);
                backpacks = new TextureRendererMulti("models/backpack/backpack.obj", new String[]{"models/backpack/diffuse.jpg"}, backpackPositions, backpackScales, backpackRots, true);
                donut = new TextureRenderer("models/donut/Donut.obj", new String[]{"models/donut/Tekstur_donat.png"}, false);
                billys = new TextureRendererMulti("models/bu/bu_lowpoly.obj", new String[]{"models/bu/bu.jpg"}, billyPositions, billyScales, billyRots, false);
                bike = new TextureRenderer("models/motorcycle/motorcycle.obj", new String[]{"models/motorcycle/motorcycle_tex.jpg"}, true);
                robot = new TextureRenderer("models/robot/robot.obj", new String[]{}, false);
                bu = new TextureRenderer("models/bu/Bu.obj", new String[]{"models/bu/bu.jpg"}, false);

                text1.create(textShader);
                text2.create(textShader);
                text3.create(textShader);
                text4.create(textShader);
                coords.create(textShader);
                firstPersonDirection.create(textShader);
                thirdPersonDirection.create(textShader);
                buCount.create(textShader);
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
                GL11.glClearColor(0.43f, 0.61f, 0.98f, 1.0f);
                camera.setThirdPerson(true);

                System.out.println(GL11.glGetString(GL11.GL_VERSION));
                GLFW.glfwSetCursorPos(window, 0.0f, 0.0f);
        }

        private void updateFPS() {
                frames++;
                if (System.currentTimeMillis() > (time + 1000)) {
                        fpsCounter.updateText("FPS: " + frames, -1.0f, 1.0f);
                        time = System.currentTimeMillis();
                        frames = 0;
                }
        }

        private void updateSolarEntityRotation() {
                for (int i = 0; i < backpackRots.size(); i++) {
                        backpackRots.set(i, backpackRots.get(i).add(deltaTime, deltaTime, deltaTime));
                }
        }

        public void updateDebug() {
                coords.updateText("XYZ: " + camera.playerPos.x + ", " + camera.playerPos.y + ", " + camera.playerPos.z, -1.0f, 0.9f);
                firstPersonDirection.updateText("yaw/pitch: " + camera.getYaw() + ", " + camera.getPitch(), -1.0f, 0.8f);
                thirdPersonDirection.updateText("third person rotation: " + camera.getKeyBoardYaw(), -1.0f, 0.8f);
                buCount.updateText("bu count: " + billys.getInstances(), -1.0f, 0.7f);
                eventHandler.coolDownPool[10] = DEBUG_RECHARGE_TIME;
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

                while (rand > 1.0f || rand < 0.0f) {
                        rand = ((float) Math.random() * 10.0f) - 5.0f;
                }

                return rand;
        }


        private void loop(boolean renderSolarEntites) {
                if (eventHandler.isFocused()) {
                        GLFW.glfwSetCursorPos(window, 0.0f, 0.0f);
                }
                float currentFrame = (float) GLFW.glfwGetTime();
                deltaTime = currentFrame - lastFrame;
                lastFrame = currentFrame;

                eventHandler.processInput();
                updateSolarEntityRotation();
                if (eventHandler.coolDownPool[10] <= 0.0f) {
                        updateDebug();
                }

                GL11.glViewport(0, 0, fullscreen ? vidmode.width() : (int) windowWidth, fullscreen ? vidmode.height() : (int) windowHeight);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

                float skyboxScale = Float.parseFloat(Configs.options.get("skybox_scale"));

                bu.setTrans(new Vector3f(0.0f, -100.0f, 0.0f)).setScale(222.2f).setRotation(new Vector3f(0.0f, 0.0f, (float) Math.toRadians(-180.0f))).render();
                robot.setTrans(new Vector3f(-10.0f, skyboxScale / 2, 0.0f)).setScale(10.0f).setRotation(new Vector3f(0.0f, 0.0f, 0.0f)).render();
                bike.setTrans(new Vector3f(10.0f, skyboxScale / 2, 0.0f)).setScale(10.0f).setRotation(new Vector3f(0.0f, 0.0f, 0.0f)).render();
                skyBox.setTrans(new Vector3f(0.0f, 0.0f, 0.0f)).setScale(skyboxScale).setRotation(new Vector3f(0.0f, 0.0f, 0.0f)).render();
                donut.setTrans(currentLightPos).setScale(10.0f).setRotation(new Vector3f(0.0f, 0.0f, 0.0f)).render();
                if (renderSolarEntites) {
                        islands.render();
                        backpacks.render();
                }

                if (billyPositions.size() > 0 && !camera.isThirdPerson() && placingBlocks) {
                        Vector3f posTemp = new Vector3f(billyPositions.get(billyPositions.size() - 1));
                        float scaleTemp = billyScales.get(billyScales.size() - 1);
                        Vector3f rotTemp = new Vector3f(billyRots.get(billyRots.size() - 1));

                        billyPositions.remove(billyPositions.size() - 1);
                        billyScales.remove(billyScales.size() - 1);
                        billyRots.remove(billyRots.size() - 1);

                        billys.render();

                        billyPositions.add(posTemp);
                        billyScales.add(scaleTemp);
                        billyRots.add(rotTemp);
                } else {
                        billys.render();
                }

                if (camera.isThirdPerson()) {
                        player.setTrans(new Vector3f(camera.playerPos.x, camera.playerPos.y - 1.0f, camera.playerPos.z)).setScale(0.01f).setRotation(new Vector3f(0.0f, camera.getThirdPersonRotation() + (float) Math.toRadians(90.0f), 0.0f)).render();
                }

                if (showDebug) {
                        textRenderPass();
                }

                if (placingBlocks && eventHandler.coolDownPool[11] <= 0.0f) {
                        billyPositions.add(new Vector3f(camera.playerPos));
                        billyScales.add(Float.parseFloat(Configs.options.get("bu_scale")));
                        billyRots.add(new Vector3f(Float.parseFloat(Configs.options.get("bu_rotation.x")), Float.parseFloat(Configs.options.get("bu_rotation.y")), Float.parseFloat(Configs.options.get("bu_rotation.z"))));
                        eventHandler.coolDownPool[11] = Float.parseFloat(Configs.options.get("bu_placement_rate"));
                }

                GLFW.glfwSwapBuffers(window);
                GLFW.glfwPollEvents();
                updateFPS();
        }

        private void textRenderPass() {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                fpsCounter.render();
                coords.render();
                if (camera.isThirdPerson()) {
                        thirdPersonDirection.render();
                } else {
                        firstPersonDirection.render();
                }
                buCount.render();
                text1.render();
                text2.render();
                text3.render();
                text4.render();
                GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        public static void main(String[] args) {
                Window pog = new Window();
                pog.run();
        }
}
