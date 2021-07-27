package cubes;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class EventHandler {
        Camera camera;
        Window window;
        long windowAddress;

        private final float MOMENTUM_TAPER_LIMIT = Float.parseFloat(Configs.options.get("momentum_taper_limit"));
        private final float MOMENTUM_TAPER_RATE = Float.parseFloat(Configs.options.get("momentum_taper_rate"));
        private float[] movementVelocities = new float[6];
        private float[] taperNegations = new float[6];
        private float[] coolDownPool = new float[32];
        private boolean focused = true;
        private boolean rasterizerFill = true;
        private static final float RECHARGE_TIME = 0.25f;

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
                                        coolDownPool[0] = RECHARGE_TIME;
                                        return;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_W)) {
                                movementVelocities[0] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                if (movementVelocities[0] > (camera.isSprinting() ? MOMENTUM_TAPER_LIMIT * 2 : MOMENTUM_TAPER_LIMIT)){
                                        movementVelocities[0] = (camera.isSprinting() ? MOMENTUM_TAPER_LIMIT * 2 : MOMENTUM_TAPER_LIMIT);
                                }
                                taperNegations[0] = 1.0f;
                        } else{
                                movementVelocities[0] -= (MOMENTUM_TAPER_RATE * Window.deltaTime) / taperNegations[0];
                                if (movementVelocities[0] < 0.0f){
                                        movementVelocities[0] = 0.0f;
                                }
                                taperNegations[0] += 2.0f * Window.deltaTime;
                                if (taperNegations[0] > 5.0f){
                                        taperNegations[0] = 4.0f;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_S)) {
                                movementVelocities[1] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                if (movementVelocities[1] > MOMENTUM_TAPER_LIMIT){
                                        movementVelocities[1] = MOMENTUM_TAPER_LIMIT;
                                }
                                taperNegations[1] = 1.0f;
                        } else{
                                movementVelocities[1] -= (MOMENTUM_TAPER_RATE * Window.deltaTime) / taperNegations[1];
                                if (movementVelocities[1] < 0.0f){
                                        movementVelocities[1] = 0.0f;
                                }
                                taperNegations[1] += 2.0f * Window.deltaTime;
                                if (taperNegations[1] > 5.0f){
                                        taperNegations[1] = 4.0f;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_A)) {
                                movementVelocities[2] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                if (movementVelocities[2] > MOMENTUM_TAPER_LIMIT){
                                        movementVelocities[2] = MOMENTUM_TAPER_LIMIT;
                                }
                                taperNegations[2] = 1.0f;
                        } else{
                                movementVelocities[2] -= (MOMENTUM_TAPER_RATE * Window.deltaTime) / taperNegations[2];
                                if (movementVelocities[2] < 0.0f){
                                        movementVelocities[2] = 0.0f;
                                }
                                taperNegations[2] += 2.0f * Window.deltaTime;
                                if (taperNegations[2] > 5.0f){
                                        taperNegations[2] = 4.0f;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_D)) {
                                movementVelocities[3] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                if (movementVelocities[3] > MOMENTUM_TAPER_LIMIT){
                                        movementVelocities[3] = MOMENTUM_TAPER_LIMIT;
                                }
                                taperNegations[3] = 1.0f;
                        } else{
                                movementVelocities[3] -= (MOMENTUM_TAPER_RATE * Window.deltaTime) / taperNegations[3];
                                if (movementVelocities[3] < 0.0f){
                                        movementVelocities[3] = 0.0f;
                                }
                                taperNegations[3] += 2.0f * Window.deltaTime;
                                if (taperNegations[3] > 5.0f){
                                        taperNegations[3] = 4.0f;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
                                movementVelocities[4] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                if (movementVelocities[4] > MOMENTUM_TAPER_LIMIT){
                                        movementVelocities[4] = MOMENTUM_TAPER_LIMIT;
                                }
                                taperNegations[4] = 1.0f;
                        } else{
                                movementVelocities[4] -= (MOMENTUM_TAPER_RATE * Window.deltaTime) / taperNegations[4];
                                if (movementVelocities[4] < 0.0f){
                                        movementVelocities[4] = 0.0f;
                                }
                                taperNegations[4] += 2.0f * Window.deltaTime;
                                if (taperNegations[4] > 5.0f){
                                        taperNegations[4] = 4.0f;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                                movementVelocities[5] += MOMENTUM_TAPER_RATE * Window.deltaTime;
                                if (movementVelocities[5] > MOMENTUM_TAPER_LIMIT){
                                        movementVelocities[5] = MOMENTUM_TAPER_LIMIT;
                                }
                                taperNegations[5] = 1.0f;
                        } else{
                                movementVelocities[5] -= (MOMENTUM_TAPER_RATE * Window.deltaTime) / taperNegations[5];
                                if (movementVelocities[5] < 0.0f){
                                        movementVelocities[5] = 0.0f;
                                }
                                taperNegations[5] += 2.0f * Window.deltaTime;
                                if (taperNegations[5] > 5.0f){
                                        taperNegations[5] = 4.0f;
                                }
                        }

                        for (int i = 0; i < taperNegations.length; i++) {
                                if (taperNegations[i] < 0.0f) {
                                        taperNegations[i] = 0.0f;
                                }
                        }

                        camera.processKeyboard(Direction.FORWARD, movementVelocities[0]);
                        camera.processKeyboard(Direction.BACK, movementVelocities[1]);
                        camera.processKeyboard(Direction.LEFT, movementVelocities[2]);
                        camera.processKeyboard(Direction.RIGHT, movementVelocities[3]);
                        camera.processKeyboard(Direction.UP, movementVelocities[4]);
                        camera.processKeyboard(Direction.DOWN, movementVelocities[5]);

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
                                        coolDownPool[9] = RECHARGE_TIME;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_C)) {
                                if (coolDownPool[1] <= 0.0f) {
                                        window.renderQuads = !window.renderQuads;
                                        coolDownPool[1] = RECHARGE_TIME;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_M)) {
                                if (coolDownPool[2] <= 0.0f) {
                                        for (int i = 0; i < window.quads.length; i++) {
                                                window.quads[i].USE_PROJ_VIEW_MAT = !window.quads[i].USE_PROJ_VIEW_MAT;
                                        }
                                        coolDownPool[2] = RECHARGE_TIME;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_F)) {
                                if (coolDownPool[3] <= 0.0f) {
                                        window.placingBlocks = !window.placingBlocks;
                                        coolDownPool[3] = RECHARGE_TIME;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_G)) {
                                if (coolDownPool[4] <= 0.0f) {
                                        window.blockPositions.clear();
                                        window.blockScales.clear();
                                        window.blockRots.clear();
                                        coolDownPool[4] = RECHARGE_TIME;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_R)) {
                                Window.currentLightPos = new Vector3f(camera.playerPos);
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_V)) {
                                if (coolDownPool[5] <= 0.0f) {
                                        camera.setThirdPerson(!camera.isThirdPerson());
                                        coolDownPool[5] = RECHARGE_TIME;
                                }
                        }
                        if (Input.isKeyDown(GLFW.GLFW_KEY_T)) {
                                if (coolDownPool[6] <= 0.0f) {
                                        window.showCoords = !window.showCoords;
                                        coolDownPool[6] = RECHARGE_TIME;
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
                                        coolDownPool[0] = RECHARGE_TIME;
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
