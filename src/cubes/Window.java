package cubes;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import java.nio.*;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Window implements Runnable{
    public static int WIDTH = 1080;
    public static int HEIGHT = 720;

    public static float deltaTime = 0.0f;
    private float lastFrame = 0.0f;
    private final String title = "Cubes!!!";
    public Camera camera;
    public Input input;
    private int frames;
    private static long time;
    public Thread pog;
    private long window;
    private Renderer quads[];
    private StaticQuadRenderer cube;
    private StaticQuadRenderer skyBox;
    private Shader bouncyShader;
    private Shader staticQuadShader;

    public final Vector3f[] cubePositions = {
                                            new Vector3f(2.1f, 0.9f, 3.0f),
                                            new Vector3f(3.6f, 2.7f, 0.1f),
                                            new Vector3f(0.6f, 2.2f, -2.0f),
                                            new Vector3f(1.0f, 1.0f, -1.0f),
                                            new Vector3f(-1.5f, 2.0f, -0.1f),
                                            new Vector3f(0.0f, 2.0f, 0.0f),
                                            };

    public final float[] cubeRots = {-0.4f, 1.0f, 1.3f, -1.2f, -0.1f, 0.0f};

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
        loop(true, false, false);

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
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        camera = new Camera(new Vector3f(0.0f, 0.0f, -5.0f), 81.7f, 7.3f);
        input = new Input(camera, window);

        time = System.currentTimeMillis();
        
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

        for (Renderer r : quads) {
            r.create(true);
        }

        if (Renderer.USE_PROJ_VIEW_MAT){
            bouncyShader = new Shader("shaders/BouncyQuadVert.glsl", "shaders/BouncyQuadFrag.glsl");
            bouncyShader.create();
        }
        else{
            bouncyShader = new Shader("shaders/BouncyQuadVertNOPROJ.glsl", "shaders/BouncyQuadFragNOPROJ.glsl");
            bouncyShader.create();
        }


        skyBox = new StaticQuadRenderer(new float[]{
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,

                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,

                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,

                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,

                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f, -0.5f,

                -0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f

        },
                new float[]{
                        0.0f, 0.0f,
                        1.0f, 0.0f,
                        1.0f, 1.0f,
                        1.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 0.0f,

                        0.0f, 0.0f,
                        1.0f, 0.0f,
                        1.0f, 1.0f,
                        1.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 0.0f,

                        1.0f, 0.0f,
                        1.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 0.0f,

                        1.0f, 0.0f,
                        1.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 0.0f,

                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                        1.0f, 0.0f,
                        0.0f, 0.0f,
                        0.0f, 1.0f,

                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                        1.0f, 0.0f,
                        0.0f, 0.0f,
                        0.0f, 1.0f

                },
                new float[]{
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,

                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f


                }, cubePositions, cubeRots, "textures/oak_planks.png");

        skyBox.create(false);

        cube = new StaticQuadRenderer(new float[]{
                        -0.5f, -0.5f, -0.5f,
                         0.5f, -0.5f, -0.5f,
                         0.5f,  0.5f, -0.5f,
                         0.5f,  0.5f, -0.5f,
                        -0.5f,  0.5f, -0.5f,
                        -0.5f, -0.5f, -0.5f,

                        -0.5f, -0.5f,  0.5f,
                         0.5f, -0.5f,  0.5f,
                         0.5f,  0.5f,  0.5f,
                         0.5f,  0.5f,  0.5f,
                        -0.5f,  0.5f,  0.5f,
                        -0.5f, -0.5f,  0.5f,

                        -0.5f,  0.5f,  0.5f,
                        -0.5f,  0.5f, -0.5f,
                        -0.5f, -0.5f, -0.5f,
                        -0.5f, -0.5f, -0.5f,
                        -0.5f, -0.5f,  0.5f,
                        -0.5f,  0.5f,  0.5f,

                         0.5f,  0.5f,  0.5f,
                         0.5f,  0.5f, -0.5f,
                         0.5f, -0.5f, -0.5f,
                         0.5f, -0.5f, -0.5f,
                         0.5f, -0.5f,  0.5f,
                         0.5f,  0.5f,  0.5f,

                        -0.5f, -0.5f, -0.5f,
                         0.5f, -0.5f, -0.5f,
                         0.5f, -0.5f,  0.5f,
                         0.5f, -0.5f,  0.5f,
                        -0.5f, -0.5f,  0.5f,
                        -0.5f, -0.5f, -0.5f,

                        -0.5f,  0.5f, -0.5f,
                         0.5f,  0.5f, -0.5f,
                         0.5f,  0.5f,  0.5f,
                         0.5f,  0.5f,  0.5f,
                        -0.5f,  0.5f,  0.5f,
                        -0.5f,  0.5f, -0.5f
                
                },
                new float[]{
                        0.0f, 0.0f,
                        1.0f, 0.0f,
                        1.0f, 1.0f,
                        1.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 0.0f,

                        0.0f, 0.0f,
                        1.0f, 0.0f,
                        1.0f, 1.0f,
                        1.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 0.0f,
                        
                        1.0f, 0.0f,
                        1.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 0.0f,

                        1.0f, 0.0f,
                        1.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 0.0f,
                        
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                        1.0f, 0.0f,
                        0.0f, 0.0f,
                        0.0f, 1.0f,

                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                        1.0f, 0.0f,
                        0.0f, 0.0f,
                        0.0f, 1.0f
                        
                },
                new float[]{
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,

                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f


                }, cubePositions, cubeRots, "textures/diamond_block.png");

        cube.create(false);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        staticQuadShader = new Shader("shaders/basicVert.glsl", "shaders/basicFrag.glsl");
        staticQuadShader.create();

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
        

    private void loop(boolean renderCubes, boolean renderQuads, boolean debug) {
        int frames = 0;
        while ( !GLFW.glfwWindowShouldClose(window) ) {

            float currentFrame = (float) GLFW.glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            processInput(this.window);
            GL11.glViewport(0, 0, WIDTH, HEIGHT);
            //GL11.glClearColor(0.0f, 0.5f, 1.0f, 1.0f);
            if (frames < 50){
                GL11.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                if (renderCubes){
                    staticQuadShader.bind();
                    cube.render(staticQuadShader, camera, null, debug);
                    skyBox.render(staticQuadShader,camera, new Vector3f(0.0f, 0.0f, 0.0f), debug);
                    staticQuadShader.unbind();
                }
                if (renderQuads){
                    bouncyShader.bind();
                    for (Renderer r : quads){
                        r.render(bouncyShader, camera, debug);
                    }
                    bouncyShader.unbind();
                }
                GLFW.glfwSwapBuffers(window);
                GLFW.glfwPollEvents();
            }
            else if (frames < 100){
                GL11.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                if (renderCubes){
                    staticQuadShader.bind();
                    cube.render(staticQuadShader, camera, null, debug);
                    skyBox.render(staticQuadShader,camera, new Vector3f(0.0f, 0.0f, 0.0f), debug);
                    staticQuadShader.unbind();
                }
                if (renderQuads){
                    bouncyShader.bind();
                    for (Renderer r : quads){
                        r.render(bouncyShader, camera, debug);
                    }
                    bouncyShader.unbind();
                }
                GLFW.glfwSwapBuffers(window);
                GLFW.glfwPollEvents();
            }
            else if (frames < 150) {
                GL11.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                if (renderCubes){
                    staticQuadShader.bind();
                    cube.render(staticQuadShader, camera, null, debug);
                    skyBox.render(staticQuadShader,camera, new Vector3f(0.0f, 0.0f, 0.0f), debug);
                    staticQuadShader.unbind();
                }
                if (renderQuads){
                    bouncyShader.bind();
                    for (Renderer r : quads){
                        r.render(bouncyShader, camera, debug);
                    }
                    bouncyShader.unbind();
                }
                GLFW.glfwSwapBuffers(window);
                GLFW.glfwPollEvents();
            }
            else{
                frames = 0;
            }
            frames++;
            updateFPS();
        }
        staticQuadShader.unbind();
        bouncyShader.unbind();
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
        if (Input.isKeyDown(GLFW.GLFW_KEY_F)){
            camera.processKeyboard(6, deltaTime);
            staticQuadShader.setUniform("lightPos", new Vector3f(0.0f, 0.0f, 0.0f));
        }
    }

    public static void main(String[] args) {
        new Window().start();
    }
}
