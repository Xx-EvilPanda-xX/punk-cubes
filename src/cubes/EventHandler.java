package cubes;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class EventHandler {
        private Camera camera;
        private Window window;
        private long windowAddress;

        public static boolean rasterizerFill = true;

        private final float SPRINT_MULTIPLIER = 2.0f;
        private final float TAPER_NEGATION_LIMIT = 5.0f;
        private final float TAPER_NEGATION_RATE = 2.5f;
        private final float MOMENTUM_TAPER_LIMIT = Float.parseFloat(Configs.options.get("momentum_taper_limit"));
        private final float MOMENTUM_TAPER_RATE = Float.parseFloat(Configs.options.get("momentum_taper_rate"));
        private float[] playerVelocities = new float[6];
        private float[] taperNegations = new float[6];
        private float[] coolDownPool = new float[32];
        private boolean focused = true;

        public EventHandler(Camera camera, Window window){
                this.camera = camera;
                this.window = window;
                this.windowAddress = window.window;
        }

        public void processInput() {
                if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
                        GLFW.glfwSetWindowShouldClose(windowAddress, true);
                }

                if (focused) {
                        if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) && Input.isKeyDown(GLFW.GLFW_KEY_W)) {
                                camera.setSprinting(true);
                                camera.setSprintFov(camera.getSprintFov() + Window.deltaTime);
                                if (camera.getSprintFov() > 0.125f) {
                                        camera.setSprintFov(0.125f);
                                }
                        } else {
                                camera.setSprintFov(camera.getSprintFov() - Window.deltaTime);
                                if (camera.getSprintFov() < 0.0f) {
                                        camera.setSprintFov(0.0f);
                                        camera.setSprinting(false);
                                }
                        }

                        if (Input.isKeyDown(GLFW.GLFW_KEY_P)) {
                                if (coolDownPool[0] <= 0.0f) {
                                        GLFW.glfwSetCursorPosCallback(windowAddress, (windowPog, xpos, ypos) -> {
                                        });
                                        GLFW.glfwSetScrollCallback(windowAddress, (windowPog, offsetx, offsety) -> {
                                        });
                                        focused = !focused;
                                        GLFW.glfwSetInputMode(windowAddress, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
                                        coolDownPool[0] = Window.RECHARGE_TIME;
                                        return;
                                }
                        }

                        for (int i = 0; i < 6; i++){
                                boolean key;
                                boolean sprintable = false;

                                switch (i){
                                        case 0:
                                                key = Input.isKeyDown(GLFW.GLFW_KEY_W);
                                                sprintable = true;
                                                break;
                                        case 1:
                                                key = Input.isKeyDown(GLFW.GLFW_KEY_S);
                                                break;
                                        case 2:
                                                key = Input.isKeyDown(GLFW.GLFW_KEY_A);
                                                break;
                                        case 3:
                                                key = Input.isKeyDown(GLFW.GLFW_KEY_D);
                                                break;
                                        case 4:
                                                key = Input.isKeyDown(GLFW.GLFW_KEY_SPACE);
                                                break;
                                        case 5:
                                                key = Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT);
                                                break;
                                        default:
                                                key = false;
                                                break;
                                }

                                if (key) {
                                        playerVelocities[i] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                        if (playerVelocities[i] > (camera.isSprinting() && sprintable ? MOMENTUM_TAPER_LIMIT * SPRINT_MULTIPLIER : MOMENTUM_TAPER_LIMIT)){
                                                playerVelocities[i] = (camera.isSprinting() && sprintable ? MOMENTUM_TAPER_LIMIT * SPRINT_MULTIPLIER : MOMENTUM_TAPER_LIMIT);
                                        }
                                        taperNegations[i] = camera.isSprinting() ? MOMENTUM_TAPER_LIMIT : MOMENTUM_TAPER_LIMIT * SPRINT_MULTIPLIER;
                                } else {
                                        playerVelocities[i] -= (MOMENTUM_TAPER_RATE * MOMENTUM_TAPER_LIMIT * Window.deltaTime) / taperNegations[i];
                                        if (playerVelocities[i] < 0.0f){
                                                playerVelocities[i] = 0.0f;
                                        }
                                        taperNegations[i] += TAPER_NEGATION_RATE * Window.deltaTime;
                                        if (taperNegations[i] > TAPER_NEGATION_LIMIT + (MOMENTUM_TAPER_LIMIT * SPRINT_MULTIPLIER)){
                                                taperNegations[i] = TAPER_NEGATION_LIMIT + (MOMENTUM_TAPER_LIMIT * SPRINT_MULTIPLIER);
                                        }
                                }
                        }

                        for (int i = 0; i < taperNegations.length; i++) {
                                if (taperNegations[i] < 0.0f) {
                                        taperNegations[i] = 0.0f;
                                }
                        }

                        camera.processKeyboard(Direction.FORWARD, playerVelocities[0]);
                        camera.processKeyboard(Direction.BACK, playerVelocities[1]);
                        camera.processKeyboard(Direction.LEFT, playerVelocities[2]);
                        camera.processKeyboard(Direction.RIGHT, playerVelocities[3]);
                        camera.processKeyboard(Direction.UP, playerVelocities[4]);
                        camera.processKeyboard(Direction.DOWN, playerVelocities[5]);

                        if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)) {
                                camera.setOptifineZoom(true);
                        } else {
                                camera.setOptifineZoom(false);
                        }

                        if (Input.isKeyDown(GLFW.GLFW_KEY_C)){
                                if (coolDownPool[9] <= 0.0f) {
                                        if (rasterizerFill){
                                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                                                rasterizerFill = !rasterizerFill;
                                        } else {
                                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                                                rasterizerFill = !rasterizerFill;
                                        }
                                        coolDownPool[9] = Window.RECHARGE_TIME;
                                }
                        }

                        if (Input.isKeyDown(GLFW.GLFW_KEY_F)) {
                                if (coolDownPool[3] <= 0.0f) {
                                        window.placingBlocks = !window.placingBlocks;
                                        coolDownPool[3] = Window.RECHARGE_TIME;
                                }
                        }

                        if (Input.isKeyDown(GLFW.GLFW_KEY_G)) {
                                if (coolDownPool[4] <= 0.0f) {
                                        window.blockPositions.clear();
                                        window.blockScales.clear();
                                        window.blockRots.clear();
                                        coolDownPool[4] = Window.RECHARGE_TIME;
                                }
                        }

                        if (Input.isKeyDown(GLFW.GLFW_KEY_R)) {
                                Window.currentLightPos = new Vector3f(camera.playerPos);
                        }

                        if (Input.isKeyDown(GLFW.GLFW_KEY_V)) {
                                if (coolDownPool[5] <= 0.0f) {
                                        camera.setThirdPerson(!camera.isThirdPerson());
                                        coolDownPool[5] = Window.RECHARGE_TIME;
                                }
                        }

                        if (Input.isKeyDown(GLFW.GLFW_KEY_F3)) {
                                if (coolDownPool[6] <= 0.0f) {
                                        window.showDebug = !window.showDebug;
                                        coolDownPool[6] = Window.RECHARGE_TIME;
                                }
                        }
                } else {
                        if (Input.isKeyDown(GLFW.GLFW_KEY_P)) {
                                if (coolDownPool[0] <= 0.0f) {
                                        GLFW.glfwSetCursorPosCallback(windowAddress, (windowPog, xpos, ypos) -> {
                                                float xoffset = (float) xpos;
                                                float yoffset = !camera.isThirdPerson() ? (float) -ypos : (float) ypos;

                                                camera.processMouseMovement(xoffset, yoffset, true);
                                        });

                                        GLFW.glfwSetScrollCallback(windowAddress, (windowPog, offsetx, offsety) -> {
                                                if (!camera.isOptifineZoom()) {
                                                        camera.processMouseScroll((float) offsety);
                                                }
                                        });

                                        focused = !focused;
                                        GLFW.glfwSetInputMode(windowAddress, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
                                        coolDownPool[0] = Window.RECHARGE_TIME;
                                }
                        }
                }

                for (int i = 0; i < coolDownPool.length; i++) {
                        coolDownPool[i] -= Window.deltaTime;
                        if (coolDownPool[i] < 0.0f) coolDownPool[i] = 0.0f;
                }
        }

        public boolean isFocused(){
                return focused;
        }
}
