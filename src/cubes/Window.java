package cubes;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import java.nio.IntBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Window implements Runnable{
    public static final int CUBE_COUNT = 4096;
    public static final float SKYBOX_SCALE = 100.0f;

    public static int WIDTH = 1080;
    public static int HEIGHT = 720;

    public static float deltaTime = 0.0f;
    private float viewModeCooldown = 0.0f, renderQuadsCooldown, useProjMatCooldown, lastFrame = 0.0f;
    private final String title = "Cubes!!!";
    public Camera camera;
    public Input input;
    private int frames;
    private static long time;
    private Thread pog;
    private long window;
    private Renderer quads[];
    private StaticQuadRenderer cube;
    private StaticQuadRenderer skyBox;
    private StaticQuadRenderer player;
    private Shader bouncyShader, bouncyShaderProj;
    private Shader staticQuadShader;
    private boolean renderCubes = true, renderQuads = false;
    public final Vector3f[] cubePositions = new Vector3f[CUBE_COUNT];
    public final float[] cubeRots = new float[CUBE_COUNT];

    public void start(){
        pog = new Thread(this, "fortnite;");
        pog.start();
    }

    public void run() {
        Matrix4f proj = new Matrix4f().perspective((float) Math.toRadians(45), 1080.0f/720.0f, 0.1f, 100.0f);
        System.out.println(proj.toString());
        
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        create();
        while (!GLFW.glfwWindowShouldClose(window)) {
            loop(renderCubes, renderQuads, false);
        }
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);

        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, title, MemoryUtil.NULL, MemoryUtil.NULL);
        
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GLFW.glfwShowWindow(window);
    }

    private void create() {
        for (int ptr = 0; ptr < CUBE_COUNT; ptr++){
            cubePositions[ptr] = genRandVec();
            cubeRots[ptr] = genRandFloat();
        }

        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        camera = new Camera(new Vector3f(0.0f, 0.0f, -5.0f), 81.7f, 7.3f);
        input = new Input(camera, window);

        time = System.currentTimeMillis();


        bouncyShaderProj = new Shader("shaders/BouncyQuadVert.glsl", "shaders/BouncyQuadFrag.glsl");
        bouncyShaderProj.create();

        bouncyShader = new Shader("shaders/BouncyQuadVertNOPROJ.glsl", "shaders/BouncyQuadFragNOPROJ.glsl");
        bouncyShader.create();

        staticQuadShader = new Shader("shaders/basicVert.glsl", "shaders/basicFrag.glsl");
        staticQuadShader.create();
        
        quads = new Renderer[]{new Renderer(new float[]{
                -0.5f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f, 0.5f, 0.5f
        },
                new float[]{
                        1.0f, 1.0f, 0.0f,
                        1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 1.0f,
                        1.0f, 0.0f, 0.0f,
                },
                new int[]{
                        0, 1, 2, 0, 2, 3
                }, 0.5f, 0.7f, 0.5f, 0),


                new Renderer(new float[]{
                        -0.5f, 0.5f, 0.0f,
                        -0.5f, -0.5f, 0.0f,
                        0.5f, -0.5f, 0.0f,
                        0.5f, 0.5f, 0.5f
                },
                        new float[]{
                                1.0f, 1.0f, 0.0f,
                                1.0f, 0.0f, 1.0f,
                                0.0f, 1.0f, 1.0f,
                                1.0f, 0.0f, 0.0f,
                        },
                        new int[]{
                                0, 1, 2, 0, 2, 3
                        }, 0.7f, 0.5f, -0.5f, 0),

                new Renderer(new float[]{
                        -0.5f, 0.5f, 0.0f,
                        -0.5f, -0.5f, 0.0f,
                        0.5f, -0.5f, 0.0f,
                        0.5f, 0.5f, 0.5f
                },
                        new float[]{
                                1.0f, 1.0f, 0.0f,
                                1.0f, 0.0f, 1.0f,
                                0.0f, 1.0f, 1.0f,
                                1.0f, 0.0f, 0.0f,
                        },
                        new int[]{
                                0, 1, 2, 0, 2, 3
                        }, 0.05f, 1.0f, 0.0f, 0),
        };

        skyBox = new StaticQuadRenderer(new float[]{
                -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f,  0.5f, -0.5f, 0.5f,  0.5f, -0.5f, -0.5f,  0.5f, -0.5f, -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f, 0.5f, -0.5f,  0.5f, 0.5f,  0.5f,  0.5f, 0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f, -0.5f,  0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f, 0.5f,  0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f,  0.5f, 0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f,  0.5f, 0.5f, -0.5f,  0.5f, -0.5f, -0.5f,  0.5f, -0.5f, -0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f, 0.5f,  0.5f, -0.5f, 0.5f,  0.5f,  0.5f, 0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f, -0.5f
        },
                new float[]{
                        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                        1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f
                },
                new float[]{
                        0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                        -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
                }, null, null, "textures/oak_planks.png", false);

        cube = new StaticQuadRenderer(new float[]{
                -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f,  0.5f, -0.5f, 0.5f,  0.5f, -0.5f, -0.5f,  0.5f, -0.5f, -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f, 0.5f, -0.5f,  0.5f, 0.5f,  0.5f,  0.5f, 0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f, -0.5f,  0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f, 0.5f,  0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f,  0.5f, 0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f,  0.5f, 0.5f, -0.5f,  0.5f, -0.5f, -0.5f,  0.5f, -0.5f, -0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f, 0.5f,  0.5f, -0.5f, 0.5f,  0.5f,  0.5f, 0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f, -0.5f
        },
                new float[]{
                        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                        1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f
                },
                new float[]{
                        0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                        -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
                }, cubePositions, cubeRots, "textures/diamond_block.png", false);

        player = new StaticQuadRenderer(new float[]{
                0.0f, -0.25f, 0.0f, 0.0f, 0.0f, -0.5f, 0.75f, 0.0f, 0.0f,
                0.0f, -0.25f, 0.0f, 0.0f, 0.0f, -0.5f, -0.75f, 0.0f, 0.0f,
                0.0f, -0.25f, 0.0f, 0.0f, 0.0f, 0.5f, 0.75f, 0.0f, 0.0f,
                0.0f, -0.25f, 0.0f, 0.0f, 0.0f, 0.5f, -0.75f, 0.0f, 0.0f,
                0.0f, 0.25f, 0.0f, 0.0f, 0.0f, -0.5f, 0.75f, 0.0f, 0.0f,
                0.0f, 0.25f, 0.0f, 0.0f, 0.0f, -0.5f, -0.75f, 0.0f, 0.0f,
                0.0f, 0.25f, 0.0f, 0.0f, 0.0f, 0.5f, 0.75f, 0.0f, 0.0f,
                0.0f, 0.25f, 0.0f, 0.0f, 0.0f, 0.5f, -0.75f, 0.0f, 0.0f
        }, new float[]{
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f
        }, new float[]{
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f
        }, null, null, null, true);

        for (Renderer r : quads) {
            r.create(true);
        }
        cube.create(staticQuadShader);
        skyBox.create(staticQuadShader);
        player.create(staticQuadShader);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        camera.setThirdPerson(false);

        System.out.println(GL11.glGetString(GL11.GL_VERSION));
        GLFW.glfwSetCursorPos(window, 0.0f, 0.0f);
    }
    
    private void updateFPS(){
        frames++;
        if (System.currentTimeMillis() > (time + 1000)){
            GLFW.glfwSetWindowTitle(window, title + " | FPS: " + frames);
            time = System.currentTimeMillis();
            frames = 0;
        }
    }

    private Vector3f genRandVec(){
        //idk what im doing, this is a really odd way to generate random nums
        float randX;
        float randY;
        float randZ;

        randX = ((float) Math.random() * 1000.0f) - 500.0f;
        while (randX > ((SKYBOX_SCALE - 2.5f) / 2.0f) || randX < -((SKYBOX_SCALE - 2.5f) / 2.0f)){
            randX = ((float) Math.random() * 1000.0f) - 500.0f;
        }

        randY = ((float) Math.random() * 1000.0f) - 500.0f;
        while (randY > ((SKYBOX_SCALE - 2.5f) / 2.0f) || randY < -((SKYBOX_SCALE - 2.5f) / 2.0f)){
            randY = ((float) Math.random() * 1000.0f) - 500.0f;
        }

        randZ = ((float) Math.random() * 1000.0f) - 500.0f;
        while (randZ > ((SKYBOX_SCALE - 2.5f) / 2.0f) || randZ < -((SKYBOX_SCALE - 2.5f) / 2.0f)){
            randZ = ((float) Math.random() * 1000.0f) - 500.0f;
        }

        return new Vector3f(randX, randY, randZ);
    }

    private float genRandFloat(){
        float rand = ((float) Math.random() * 10.0f) - 5.0f;

        while (rand > 1.0f || rand < -1.0f){
            rand = ((float) Math.random() * 10.0f) - 5.0f;
        }

        return rand;
    }
        

    private void loop(boolean renderCubes, boolean renderQuads, boolean debug) {
        GLFW.glfwSetCursorPos(window, 0.0f, 0.0f);

        float currentFrame = (float) GLFW.glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;

        processInput(this.window);
        GL11.glViewport(0, 0, WIDTH, HEIGHT);

        GL11.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        if (renderCubes){
            staticQuadShader.bind();
            cube.render(staticQuadShader, camera, null, 0.0f, 0.0f, debug);
            skyBox.render(staticQuadShader ,camera, new Vector3f(0.0f, 0.0f, 0.0f), SKYBOX_SCALE, 0.0f, debug);
            if (camera.getThirdPerson()) {
                player.render(staticQuadShader, camera, camera.pos, 1.0f, camera.rotation, debug);
            }
            staticQuadShader.unbind();
        }
        if (renderQuads){
            if (Renderer.USE_PROJ_VIEW_MAT) {
                bouncyShaderProj.bind();
            }
            else {
                bouncyShader.bind();
            }
            for (Renderer r : quads){
                if (Renderer.USE_PROJ_VIEW_MAT) {
                    r.render(bouncyShaderProj, camera, debug);
                }
                else {
                    r.render(bouncyShader, camera, debug);
                }
            }
            if (Renderer.USE_PROJ_VIEW_MAT) {
                bouncyShaderProj.unbind();
            }
            else {
                bouncyShader.unbind();
            }
        }
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
        updateFPS();
    }
    
    private void processInput(long window){
        if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
            GLFW.glfwSetWindowShouldClose(window, true);
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_W)){
            camera.processKeyboard(0, deltaTime);
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_S)){
            camera.processKeyboard(1, deltaTime);
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_A)){
            camera.processKeyboard(2, deltaTime);
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_D)){
            camera.processKeyboard(3, deltaTime);
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)){
            camera.processKeyboard(4, deltaTime);
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)){
            camera.processKeyboard(5, deltaTime);
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_C)){
            if (renderQuadsCooldown <= 0.0f) {
                renderQuads = !renderQuads;
                renderQuadsCooldown = 0.25f;
            }
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_M)){
            if (useProjMatCooldown <= 0.0f) {
                Renderer.USE_PROJ_VIEW_MAT = !Renderer.USE_PROJ_VIEW_MAT;
                useProjMatCooldown = 0.25f;
            }
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_F)){
            camera.processKeyboard(6, deltaTime);
            staticQuadShader.setUniform("lightPos", new Vector3f(0.0f, 0.0f, 0.0f));
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_V)){
            if (viewModeCooldown <= 0.0f) {
                camera.setThirdPerson(!camera.getThirdPerson());
                viewModeCooldown = 0.25f;
            }
        }
        viewModeCooldown -= deltaTime;
        renderQuadsCooldown -= deltaTime;
        useProjMatCooldown -= deltaTime;
    }

    public static void main(String[] args) {
        new Window().start();
    }
}
