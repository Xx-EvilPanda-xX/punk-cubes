package cubes;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class ColorRendererMulti extends ColorRenderer {
        private ArrayList<Vector3f> positions;
        private ArrayList<Float> scales;
        private ArrayList<Float> rots;
        private float rotation;
        private int itr;

        public ColorRendererMulti(float[] vertexData, float[] colorData, float[] normals, int[] indexData, ArrayList<Vector3f> positions, ArrayList<Float> scales, ArrayList<Float> rots) {
                super(vertexData, colorData, normals, indexData);
                this.positions = positions;
                this.scales = scales;
                this.rots = rots;
                if (positions.size() != rots.size() || positions.size() != scales.size() || scales.size() != rots.size()) {
                        throw new IllegalStateException("Mismatched position, rotation, and scaling arrays!");
                }
        }

        public ColorRendererMulti(float[] vertexData, float[] colorData, float[] normals, ArrayList<Vector3f> cubePositions, ArrayList<Float> cubeScales, ArrayList<Float> cubeRots) {
                super(vertexData, colorData, normals);
                this.positions = cubePositions;
                this.scales = cubeScales;
                this.rots = cubeRots;
                if (positions.size() != rots.size() || positions.size() != scales.size() || scales.size() != rots.size()) {
                        throw new IllegalStateException("Mismatched position, rotation, and scaling arrays!");
                }
        }

        @Override
        public void prepare(boolean debug) {
                if (!isCreated()) throw new IllegalStateException("Attempted to call render pass without initializing renderer");

                if (positions.size() != rots.size() || positions.size() != scales.size() || scales.size() != rots.size()) {
                        throw new IllegalStateException("Mismatched position, rotation, and scaling arrays!");
                }

                if (debug) System.out.println("yaw: " + getCamera().yaw + "\npitch: " + getCamera().pitch);

                Matrix4f model = new Matrix4f().translate(positions.get(itr)).scale(scales.get(itr), scales.get(itr), scales.get(itr)).rotate(rotation * rots.get(itr), 0.0f, 1.0f, 0.0f).rotate(rotation * rots.get(itr), 1.0f, 0.0f, 0.0f);

                Matrix4f proj = getCamera().getProjectionMatrix();

                Matrix4f view = getCamera().getViewMatrix();

                if (debug) {
                        System.out.println(model.toString());
                        getShader().setUniform("model", model, true);
                        System.out.println(proj.toString());
                        getShader().setUniform("projection", proj, true);
                        System.out.println(view.toString());
                        getShader().setUniform("view", view, true);
                } else {
                        getShader().setUniform("model", model, false);
                        getShader().setUniform("projection", proj, false);
                        getShader().setUniform("view", view, false);
                }

                getShader().setUniform("lightPos", Window.currentLightPos);
                getShader().setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
                if (!getCamera().getThirdPerson()) {
                        getShader().setUniform("viewPos", getCamera().playerPos);
                } else {
                        getShader().setUniform("viewPos", getCamera().playerPos.sub(getCamera().front.mul(getCamera().zoom / 10, new Vector3f()), new Vector3f()));
                }
                getShader().setUniform("mode", 1);

        }

        @Override
        public void render(boolean debug) {
                for (int i = 0; i < positions.size(); i++) {
                        itr = i;
                        prepare(debug);

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
