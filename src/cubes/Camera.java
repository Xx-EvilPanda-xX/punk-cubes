package cubes;

import org.joml.Vector3f;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class Camera{
    public Vector3f pos;
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private Vector3f worldUp;

    private Vector3f keyboardRight;
    private Vector3f keyboardFront;
    private Vector3f keyboardWorldUp;

    private final float WIDTH = (float) Window.WIDTH;
    private final float HIEGHT = (float) Window.HEIGHT;

    public float yaw;
    public float pitch;
	
    private final float MOVEMENT_SPEED = 1.0f;
    private final float MOUSE_SENSITIVITY = 0.075f;
    private float zoom = 45;
	
    public Camera(Vector3f pos, float yaw, float pitch){
	    this.front = pos.add(0.0f, 0.0f, 1.0f, new Vector3f());
	    this.pos = pos;
	    this.worldUp = new Vector3f(0.0f, 1.0f, 0.0f);
	    this.yaw = yaw;
	    this.pitch = pitch;
	    this.right = front.cross(this.worldUp, new Vector3f()).normalize();
	    this.up = right.cross(this.front, new Vector3f()).normalize();

	    this.keyboardWorldUp = new Vector3f(0.0f, 1.0f, 0.0f);
	    this.keyboardFront = pos.add(0.0f, 0.0f, 1.0f, new Vector3f());
	    this.keyboardRight = keyboardFront.cross(this.worldUp, new Vector3f()).normalize();

	    updateCameraVectors();
	    updateKeyboardVectors();
    }
	
    public Matrix4f getViewMatrix(){
        return new Matrix4f().lookAt(pos, pos.add(front, new Vector3f()), up);
    }

    public Matrix4f getProjectionMatrix(){
        return new Matrix4f().perspective(zoom, WIDTH/HIEGHT, 0.1f, 100.0f);
    }
	
    public void processMouseMovement(float xoffset, float yoffset, boolean constrainPitch){
	    xoffset *= MOUSE_SENSITIVITY;
	    yoffset *= MOUSE_SENSITIVITY;

	    yaw += xoffset;
	    pitch += yoffset;

	    if (constrainPitch){
	        if (pitch > 89.0f){
	            pitch = 89.0f;
	        }
	        if (pitch < -89.0f){
		        pitch = -89.0f;
	        }
	    }
	    if (yaw > 360.0f){
	        yaw = 0.0f;
        }
	    if (yaw < 0.0f){
	        yaw = 360.0f;
        }
	    updateCameraVectors();
	    updateKeyboardVectors();
    }
    
    public void processKeyboard(int direction, float deltaTime) {
        float velocity = MOVEMENT_SPEED * deltaTime;

        if (direction == 0){
            pos.add(keyboardFront.x * velocity, 0.0f, keyboardFront.z * velocity);
            if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)){
                pos.add(keyboardFront.x * (velocity * 2), 0.0f, keyboardFront.z * (velocity * 2));
            }
        }
        if (direction == 1){
            pos.sub(keyboardFront.x * velocity, 0.0f, keyboardFront.z * velocity);
            if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)){
                pos.sub(keyboardFront.x * (velocity * 2), 0.0f, keyboardFront.z * (velocity * 2));
            }
        }
        if (direction == 2){
            pos.sub(keyboardRight.x * velocity, 0.0f, keyboardRight.z * velocity);
            if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)){
                pos.sub(keyboardRight.x * (velocity * 2), 0.0f, keyboardRight.z * (velocity * 2));
            }
        }
        if (direction == 3){
            pos.add(keyboardRight.x * velocity, 0.0f, keyboardRight.z * velocity);
            if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)){
                pos.add(keyboardRight.x * (velocity * 2), 0.0f, keyboardRight.z * (velocity * 2));
            }
        }
        if (direction == 4){
            pos.add(keyboardWorldUp.x * velocity, keyboardWorldUp.y * velocity, keyboardWorldUp.z * velocity);
        }
        if (direction == 5){
            pos.sub(keyboardWorldUp.x * velocity, keyboardWorldUp.y * velocity, keyboardWorldUp.z * velocity);
        }
        if (direction == 6){
            pos.set(new Vector3f(0.0f, 0.0f,0.0f));
        }
    }

    public void processMouseScroll(float yoffset){
	    zoom -= yoffset / 100;
		
	    if (zoom < 44.8f){
	        zoom = 44.8f;
        }
        if (zoom > 45.2f){
            zoom = 45.2f;
        }
    }
    
    private void updateCameraVectors(){
	    Vector3f front = new Vector3f();
        front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        this.front = front.normalize();
        this.right = front.cross(this.worldUp, new Vector3f()).normalize();
        this.up = right.cross(this.front, new Vector3f()).normalize();
    }

    private void updateKeyboardVectors(){
        Vector3f front = new Vector3f();
        front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(0));
        front.y = (float) Math.sin(Math.toRadians(0));
        front.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(0));
        this.keyboardFront = front.normalize();
        this.keyboardRight = front.cross(this.keyboardWorldUp, new Vector3f()).normalize();
    }
}
