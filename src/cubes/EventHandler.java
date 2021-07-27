package cubes;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class EventHandler {
        private Camera camera;
        private Window window;
        private long windowAddress;

        private final float MOMENTUM_TAPER_LIMIT = Float.parseFloat(Configs.options.get("momentum_taper_limit"));
        private final float MOMENTUM_TAPER_RATE = Float.parseFloat(Configs.options.get("momentum_taper_rate"));
        private float[] playerVelocities = new float[6];
        private float[] taperNegations = new float[6];
        private float[] coolDownPool = new float[32];
        private boolean focused = true;
        private boolean rasterizerFill = true;

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
                        if (Input.isKeyDown(GLFW.GLFW_KEY_W)) {
                                playerVelocities[0] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                if (playerVelocities[0] > (camera.isSprinting() ? MOMENTUM_TAPER_LIMIT * 2 : MOMENTUM_TAPER_LIMIT)){
                                        playerVelocities[0] = (camera.isSprinting() ? MOMENTUM_TAPER_LIMIT * 2 : MOMENTUM_TAPER_LIMIT);
                                }
                                taperNegations[0] = 2.5f;
                        } else{
                                playerVelocities[0] -= (MOMENTUM_TAPER_RATE * Window.deltaTime) / taperNegations[0];
                                if (playerVelocities[0] < 0.0f){
                                        playerVelocities[0] = 0.0f;
                                }
                                taperNegations[0] += 2.0f * Window.deltaTime;
                                if (taperNegations[0] > 5.0f){
                                        taperNegations[0] = 5.0f;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_S)) {
                                playerVelocities[1] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                if (playerVelocities[1] > MOMENTUM_TAPER_LIMIT){
                                        playerVelocities[1] = MOMENTUM_TAPER_LIMIT;
                                }
                                taperNegations[1] = 2.5f;
                        } else{
                                playerVelocities[1] -= (MOMENTUM_TAPER_RATE * Window.deltaTime) / taperNegations[1];
                                if (playerVelocities[1] < 0.0f){
                                        playerVelocities[1] = 0.0f;
                                }
                                taperNegations[1] += 2.0f * Window.deltaTime;
                                if (taperNegations[1] > 5.0f){
                                        taperNegations[1] = 5.0f;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_A)) {
                                playerVelocities[2] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                if (playerVelocities[2] > MOMENTUM_TAPER_LIMIT){
                                        playerVelocities[2] = MOMENTUM_TAPER_LIMIT;
                                }
                                taperNegations[2] = 2.5f;
                        } else{
                                playerVelocities[2] -= (MOMENTUM_TAPER_RATE * Window.deltaTime) / taperNegations[2];
                                if (playerVelocities[2] < 0.0f){
                                        playerVelocities[2] = 0.0f;
                                }
                                taperNegations[2] += 2.0f * Window.deltaTime;
                                if (taperNegations[2] > 5.0f){
                                        taperNegations[2] = 5.0f;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_D)) {
                                playerVelocities[3] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                if (playerVelocities[3] > MOMENTUM_TAPER_LIMIT){
                                        playerVelocities[3] = MOMENTUM_TAPER_LIMIT;
                                }
                                taperNegations[3] = 2.5f;
                        } else{
                                playerVelocities[3] -= (MOMENTUM_TAPER_RATE * Window.deltaTime) / taperNegations[3];
                                if (playerVelocities[3] < 0.0f){
                                        playerVelocities[3] = 0.0f;
                                }
                                taperNegations[3] += 2.0f * Window.deltaTime;
                                if (taperNegations[3] > 5.0f){
                                        taperNegations[3] = 5.0f;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
                                playerVelocities[4] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                if (playerVelocities[4] > MOMENTUM_TAPER_LIMIT){
                                        playerVelocities[4] = MOMENTUM_TAPER_LIMIT;
                                }
                                taperNegations[4] = 2.5f;
                        } else{
                                playerVelocities[4] -= (MOMENTUM_TAPER_RATE * Window.deltaTime) / taperNegations[4];
                                if (playerVelocities[4] < 0.0f){
                                        playerVelocities[4] = 0.0f;
                                }
                                taperNegations[4] += 2.0f * Window.deltaTime;
                                if (taperNegations[4] > 5.0f){
                                        taperNegations[4] = 5.0f;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                                playerVelocities[5] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                if (playerVelocities[5] > MOMENTUM_TAPER_LIMIT){
                                        playerVelocities[5] = MOMENTUM_TAPER_LIMIT;
                                }
                                taperNegations[5] = 2.5f;
                        } else{
                                playerVelocities[5] -= (MOMENTUM_TAPER_RATE * Window.deltaTime) / taperNegations[5];
                                if (playerVelocities[5] < 0.0f){
                                        playerVelocities[5] = 0.0f;
                                }
                                taperNegations[5] += 2.0f * Window.deltaTime;
                                if (taperNegations[5] > 5.0f){
                                        taperNegations[5] = 5.0f;
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
                        if (Input.isKeyDown(GLFW.GLFW_KEY_Y)){
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
                        if (Input.isKeyDown(GLFW.GLFW_KEY_C)) {
                                if (coolDownPool[1] <= 0.0f) {
                                        window.renderQuads = !window.renderQuads;
                                        coolDownPool[1] = Window.RECHARGE_TIME;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_M)) {
                                if (coolDownPool[2] <= 0.0f) {
                                        for (int i = 0; i < window.quads.length; i++) {
                                                window.quads[i].USE_PROJ_VIEW_MAT = !window.quads[i].USE_PROJ_VIEW_MAT;
                                        }
                                        coolDownPool[2] = Window.RECHARGE_TIME;
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
                        if (Input.isKeyDown(GLFW.GLFW_KEY_T)) {
                                if (coolDownPool[6] <= 0.0f) {
                                        window.showCoords = !window.showCoords;
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
