package cubes;

import org.lwjgl.glfw.GLFW;

public class Input {
        private static double mouseX, mouseY;
        private static double scrollX, scrollY;
        private static long windowPog;

        public Input(Camera camera, long windowPog) {
                this.windowPog = windowPog;

                GLFW.glfwSetCursorPosCallback(windowPog, (window, xpos, ypos) -> {
                        mouseX += xpos;
                        mouseY += ypos;

                        float xoffset = (float) xpos;
                        float yoffset;
                        if (!camera.getThirdPerson()) {
                                yoffset = (float) -ypos;
                        } else {
                                yoffset = (float) ypos;
                        }

                        camera.processMouseMovement(xoffset, yoffset, true);
                });

                GLFW.glfwSetScrollCallback(windowPog, (window, offsetx, offsety) -> {
                        scrollX += offsetx;
                        scrollY += offsety;
                        camera.processMouseScroll((float) offsety);
                });
        }

        public static boolean isKeyDown(int key) {
                return GLFW.glfwGetKey(windowPog, key) == GLFW.GLFW_PRESS;
        }

        public static boolean isButtonDown(int button) {
                return GLFW.glfwGetMouseButton(windowPog, button) == GLFW.GLFW_PRESS;
        }

        public static double getMouseX() {
                return mouseX;
        }

        public static double getMouseY() {
                return mouseY;
        }

        public static double getScrollX() {
                return scrollX;
        }

        public static double getScrollY() {
                return scrollY;
        }
}
