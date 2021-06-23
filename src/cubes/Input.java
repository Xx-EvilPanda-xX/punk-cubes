package cubes;

import org.lwjgl.glfw.GLFW;

public class Input {
        private static long windowPog;

        public Input(Camera camera, long windowPog) {
                this.windowPog = windowPog;

                GLFW.glfwSetCursorPosCallback(windowPog, (window, xpos, ypos) -> {
                        float xoffset = (float) xpos;
                        float yoffset;
                        if (!camera.isThirdPerson()) {
                                yoffset = (float) -ypos;
                        } else {
                                yoffset = (float) ypos;
                        }

                        camera.processMouseMovement(xoffset, yoffset, true);
                });

                GLFW.glfwSetScrollCallback(windowPog, (window, offsetx, offsety) -> {
                        if (!camera.isOptifineZoom()) {
                                camera.processMouseScroll((float) offsety);
                        }
                });
        }

        public static boolean isKeyDown(int key) {
                return GLFW.glfwGetKey(windowPog, key) == GLFW.GLFW_PRESS;
        }

        public static boolean isButtonDown(int button) {
                return GLFW.glfwGetMouseButton(windowPog, button) == GLFW.GLFW_PRESS;
        }
}
