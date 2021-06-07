package cubes;

import org.joml.Vector3f;
import org.joml.Matrix4f;

public class Camera {
        public Vector3f playerPos;

        private Vector3f front;
        private Vector3f up;
        private Vector3f right;
        private Vector3f worldUp;

        private boolean thirdPerson;
        private boolean optifineZoom;
        private boolean sprinting = false;

        private Vector3f keyboardRight;
        private Vector3f keyboardFront;
        private Vector3f keyboardWorldUp;

        private float thirdPersonRotation;
        private static final float optifineZoomFactor = 44.25f;
        private float sprintFov = 0.0f;

        private float width = (float) Configs.WIDTH;
        private float height = (float) Configs.HEIGHT;

        private float yaw;
        private float keyBoardYaw;
        private float pitch;
        private float zoom = 45.0f;

        private final float MOVEMENT_SPEED = 3.0f;
        private final float MOUSE_SENSITIVITY = 0.045f;

        public Camera(Vector3f pos, float yaw, float pitch) {
                this.front = pos.add(0.0f, 0.0f, 1.0f, new Vector3f());
                this.playerPos = pos;
                this.worldUp = new Vector3f(0.0f, 1.0f, 0.0f);
                this.yaw = yaw;
                this.keyBoardYaw = yaw;
                this.pitch = pitch;
                this.right = front.cross(this.worldUp, new Vector3f()).normalize();
                this.up = right.cross(this.front, new Vector3f()).normalize();
                this.thirdPersonRotation = (float) Math.toRadians(keyBoardYaw);

                this.keyboardWorldUp = new Vector3f(0.0f, 1.0f, 0.0f);
                this.keyboardFront = pos.add(0.0f, 0.0f, 1.0f, new Vector3f());
                this.keyboardRight = keyboardFront.cross(this.worldUp, new Vector3f()).normalize();

                updateAllVectors();
        }

        public Matrix4f getViewMatrix() {
                if (!thirdPerson) {
                        return new Matrix4f().lookAt(playerPos, playerPos.add(front, new Vector3f()), up);
                } else {
                        return new Matrix4f().lookAt(playerPos.sub(front.mul(zoom / 10, new Vector3f()), new Vector3f()), playerPos, up);
                }
        }

        public Matrix4f getProjectionMatrix() {
                width = (float) Configs.WIDTH;
                height = (float) Configs.HEIGHT;
                if (!optifineZoom) {
                        if (sprinting) {
                                return new Matrix4f().perspective(zoom + sprintFov, width / height, 0.1f, 1000.0f);
                        } else {
                                return new Matrix4f().perspective(zoom, width / height, 0.1f, 1000.0f);
                        }
                } else {
                        if (sprinting) {
                                return new Matrix4f().perspective(optifineZoomFactor + sprintFov / 2.0f, width / height, 0.1f, 1000.0f);
                        } else {
                                return new Matrix4f().perspective(optifineZoomFactor, width / height, 0.1f, 1000.0f);
                        }
                }
        }

        public void processMouseMovement(float xoffset, float yoffset, boolean constrainPitch) {
                xoffset *= MOUSE_SENSITIVITY;
                yoffset *= MOUSE_SENSITIVITY;

                if (!optifineZoom) {
                        yaw += xoffset;
                        pitch += yoffset;
                } else {
                        yaw += xoffset / 5;
                        pitch += yoffset / 5;
                }

                if (constrainPitch) {
                        if (pitch > 89.0f) {
                                pitch = 89.0f;
                        }
                        if (pitch < -89.0f) {
                                pitch = -89.0f;
                        }
                }
                if (yaw > 360.0f) {
                        yaw = 0.0f;
                }
                if (yaw < 0.0f) {
                        yaw = 360.0f;
                }
                updateAllVectors();
        }

        public void processKeyboard(int direction, float deltaTime) {
                float velocity = MOVEMENT_SPEED * deltaTime;

                if (direction == 0) {
                        playerPos.add(keyboardFront.x * velocity, 0.0f, keyboardFront.z * velocity);
                        if (sprinting) {
                                playerPos.add(keyboardFront.x * (velocity * 2), 0.0f, keyboardFront.z * (velocity * 2));
                        }
                }
                if (direction == 1) {
                        playerPos.sub(keyboardFront.x * velocity, 0.0f, keyboardFront.z * velocity);
                        if (sprinting) {
                                playerPos.sub(keyboardFront.x * (velocity * 2), 0.0f, keyboardFront.z * (velocity * 2));
                        }
                }
                if (direction == 2) {
                        if (!thirdPerson) {
                                playerPos.sub(keyboardRight.x * velocity, 0.0f, keyboardRight.z * velocity);
                                if (sprinting) {
                                        playerPos.sub(keyboardRight.x * (velocity * 2), 0.0f, keyboardRight.z * (velocity * 2));
                                }
                        } else {
                                keyBoardYaw -= velocity * 25.0f;
                                thirdPersonRotation = -((float) Math.toRadians(keyBoardYaw));
                        }
                }
                if (direction == 3) {
                        if (!thirdPerson) {
                                playerPos.add(keyboardRight.x * velocity, 0.0f, keyboardRight.z * velocity);
                                if (sprinting) {
                                        playerPos.add(keyboardRight.x * (velocity * 2), 0.0f, keyboardRight.z * (velocity * 2));
                                }
                        } else {
                                keyBoardYaw += velocity * 25.0f;
                                thirdPersonRotation = -((float) Math.toRadians(keyBoardYaw));
                        }
                }
                if (direction == 4) {
                        playerPos.add(keyboardWorldUp.x * velocity, keyboardWorldUp.y * velocity, keyboardWorldUp.z * velocity);
                }
                if (direction == 5) {
                        playerPos.sub(keyboardWorldUp.x * velocity, keyboardWorldUp.y * velocity, keyboardWorldUp.z * velocity);
                }
                if (keyBoardYaw > 360.0f) {
                        keyBoardYaw = 0.0f;
                }
                if (keyBoardYaw < 0.0f) {
                        keyBoardYaw = 360.0f;
                }
                updateAllVectors();
        }

        public void processMouseScroll(float yoffset) {
                zoom -= yoffset / 30;

                if (zoom < 44.5f) {
                        zoom = 44.5f;
                }
                if (zoom > 46.0f) {
                        zoom = 46.0f;
                }
        }

        private void updateCameraVectors() {
                Vector3f front = new Vector3f();
                front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
                front.y = (float) Math.sin(Math.toRadians(pitch));
                front.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
                this.front = front.normalize();
                this.right = front.cross(this.worldUp, new Vector3f()).normalize();
                this.up = right.cross(this.front, new Vector3f()).normalize();
        }

        private void updateKeyboardVectors() {
                if (thirdPerson) {
                        Vector3f front = new Vector3f();
                        front.x = (float) Math.cos(Math.toRadians(keyBoardYaw)) * (float) Math.cos(Math.toRadians(0));
                        front.y = (float) Math.sin(Math.toRadians(0));
                        front.z = (float) Math.sin(Math.toRadians(keyBoardYaw)) * (float) Math.cos(Math.toRadians(0));
                        this.keyboardFront = front.normalize();
                        this.keyboardRight = front.cross(this.keyboardWorldUp, new Vector3f()).normalize();
                } else {
                        Vector3f front = new Vector3f();
                        front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(0));
                        front.y = (float) Math.sin(Math.toRadians(0));
                        front.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(0));
                        this.keyboardFront = front.normalize();
                        this.keyboardRight = front.cross(this.keyboardWorldUp, new Vector3f()).normalize();
                }
        }

        public void updateAllVectors() {
                updateCameraVectors();
                updateKeyboardVectors();
        }

        public Vector3f getFront() {
                return front;
        }

        public Vector3f getUp() {
                return up;
        }

        public Vector3f getWorldUp() {
                return worldUp;
        }

        public Vector3f getRight() {
                return right;
        }

        public boolean isThirdPerson() {
                return thirdPerson;
        }

        public boolean isOptifineZoom() {
                return optifineZoom;
        }

        public boolean isSprinting() {
                return sprinting;
        }

        public float getThirdPersonRotation() {
                return thirdPersonRotation;
        }

        public float getSprintFov() {
                return sprintFov;
        }

        public float getYaw() {
                return yaw;
        }

        public float getKeyBoardYaw() {
                return keyBoardYaw;
        }

        public float getPitch() {
                return pitch;
        }

        public float getZoom() {
                return zoom;
        }

        public void setThirdPerson(boolean thirdPerson) {
                this.thirdPerson = thirdPerson;
                if (!thirdPerson) {
                        yaw = keyBoardYaw;
                        pitch = 0.0f;
                        updateCameraVectors();
                } else {
                        keyBoardYaw = yaw;
                        thirdPersonRotation = -((float) Math.toRadians(keyBoardYaw));
                        updateKeyboardVectors();
                }
        }

        public void setOptifineZoom(boolean optifineZoom) {
                this.optifineZoom = optifineZoom;
        }

        public void setSprinting(boolean sprinting) {
                this.sprinting = sprinting;
        }

        public void setThirdPersonRotation(float thirdPersonRotation) {
                this.thirdPersonRotation = thirdPersonRotation;
        }

        public void setSprintFov(float sprintFov) {
                this.sprintFov = sprintFov;
        }

        public void setYaw(float yaw) {
                this.yaw = yaw;
        }

        public void setKeyBoardYaw(float keyBoardYaw) {
                this.keyBoardYaw = keyBoardYaw;
        }

        public void setPitch(float pitch) {
                this.pitch = pitch;
        }

        public void setZoom(float zoom) {
                this.zoom = zoom;
        }
}
