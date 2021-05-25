package cubes;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class ColorRendererMulti extends ColorRenderer{
        public ArrayList<Vector3f> cubePositions;
        public ArrayList<Float> rotSpeeds;
        private float rotation;

        public ColorRendererMulti(float[] vertexData, float[] colorData, float[] normals, int[] indexData, ArrayList<Vector3f> cubePositions, ArrayList<Float> cubeRots){
                super(vertexData, colorData, normals, indexData);
                this.cubePositions = cubePositions;
                this.rotSpeeds = cubeRots;
                if (cubePositions.size() != rotSpeeds.size()) {
                        throw new IllegalStateException();
                }
        }

        public ColorRendererMulti(float[] vertexData, float[] colorData, float[] normals, ArrayList<Vector3f> cubePositions, ArrayList<Float> cubeRots){
                super(vertexData, colorData, normals);
                this.cubePositions = cubePositions;
                this.rotSpeeds = cubeRots;
                if (cubePositions.size() != rotSpeeds.size()) {
                        throw new IllegalStateException();
                }
        }

        public void prepare(Shader shader, Camera camera, boolean debug, int i){
                if (debug) System.out.println("yaw: " + camera.yaw + "\npitch: " + camera.pitch);

                Matrix4f model = new Matrix4f().translate(cubePositions.get(i)).scale(0.5f, 0.5f, 0.5f).rotate(rotation * rotSpeeds.get(i), 0.0f, 1.0f, 0.0f).rotate(rotation * rotSpeeds.get(i), 1.0f, 0.0f, 0.0f);

                Matrix4f proj = camera.getProjectionMatrix();

                Matrix4f view = camera.getViewMatrix();

                if (debug) {
                        System.out.println(model.toString());
                        shader.setUniform("model", model, true);
                        System.out.println(proj.toString());
                        shader.setUniform("projection", proj, true);
                        System.out.println(view.toString());
                        shader.setUniform("view", view, true);
                } else {
                        shader.setUniform("model", model, false);
                        shader.setUniform("projection", proj, false);
                        shader.setUniform("view", view, false);
                }

                shader.setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
                if (!camera.getThirdPerson()) {
                        shader.setUniform("viewPos", camera.playerPos);
                } else {
                        shader.setUniform("viewPos", camera.playerPos.sub(camera.front.mul(camera.zoom / 10, new Vector3f()), new Vector3f()));
                }
                shader.setUniform("mode", 1);
                shader.setUniform("numLights", 1);
                shader.setUniform("colorMode", -1);

        }

        public void render(Shader shader, Camera camera, boolean debug) {
                for (int i = 0; i < cubePositions.size(); i++){
                        prepare(shader, camera, debug, i);

                        if (isIndexed()) {
                                getVao().bind();
                                getVao().enableAttribs();
                                getVao().bindIndices();
                                GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT, 0);
                                getVao().unbindIndices();
                                getVao().disableAttribs();
                                getVao().unbind();
                        } else {
                                getVao().bind();
                                getVao().enableAttribs();
                                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
                                getVao().disableAttribs();
                                getVao().unbind();
                        }
                }
                rotation += Window.deltaTime;
        }
}
