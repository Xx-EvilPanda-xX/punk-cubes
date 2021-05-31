package cubes;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.joml.Matrix4f;

public class ColorQuadRenderer extends ColorRenderer {
        public boolean USE_PROJ_VIEW_MAT = true;

        public final float SCALE;
        public final float SPEED;
        public final float YPOS;

        private float trans, transOffset, rotation;

        public ColorQuadRenderer(float[] vertexData, float[] colorData, float[] normalData, int[] indexData, float scale, float speed, float ypos, float rotation) {
                super(vertexData, colorData, normalData, indexData);
                SCALE = scale;
                SPEED = speed;
                YPOS = ypos;

                transOffset = SPEED;
                trans = 0.0f;
                this.rotation = rotation;
        }

        public ColorQuadRenderer(float[] vertexData, float[] colorData, float[] normalData, float scale, float speed, float ypos) {
                super(vertexData, colorData, normalData);
                SCALE = scale;
                SPEED = speed;
                YPOS = ypos;

                indexCount = 0;
                transOffset = SPEED;
                trans = 0.0f;
        }

        @Override
        public void prepare(boolean debug) {
                if (!isCreated()) throw new IllegalStateException("Attempted to call render pass without initializing renderer");
                if (debug) System.out.println("yaw: " + getCamera().yaw + "\npitch: " + getCamera().pitch);

                this.trans += transOffset * Window.deltaTime;
                if (this.trans > (1 - (SCALE / 2))) {
                        transOffset = -SPEED;
                        this.trans = (1 - (SCALE / 2));
                }
                if (this.trans < -(1 - (SCALE / 2))) {
                        transOffset = SPEED;
                        this.trans = -(1 - (SCALE / 2));
                }

                Matrix4f model = new Matrix4f().translate(this.trans, YPOS, 0.0f).scale(SCALE, SCALE, SCALE).rotate(rotation * this.trans, 0.0f, 0.0f, 1.0f);

                Matrix4f proj = getCamera().getProjectionMatrix();

                Matrix4f view = getCamera().getViewMatrix();


                if (debug) {
                        System.out.println(model.toString());
                        getShader().setUniform("model", model, true);
                        if (USE_PROJ_VIEW_MAT) {
                                System.out.println(proj.toString());
                                getShader().setUniform("projection", proj, true);
                                System.out.println(view.toString());
                                getShader().setUniform("view", view, true);

                        }
                } else {
                        getShader().setUniform("model", model, false);
                        if (USE_PROJ_VIEW_MAT) {
                                getShader().setUniform("projection", proj, false);
                                getShader().setUniform("view", view, false);
                        }
                }
                getShader().setUniform("lightPos", Window.currentLightPos);
                getShader().setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
                if (!getCamera().getThirdPerson()) {
                        getShader().setUniform("viewPos", getCamera().playerPos);
                } else {
                        getShader().setUniform("viewPos", getCamera().playerPos.sub(getCamera().front.mul(getCamera().zoom / 10, new Vector3f()), new Vector3f()));
                }
                getShader().setUniform("mode", USE_PROJ_VIEW_MAT ? 1 : 3);
        }

        @Override
        public void render(boolean debug) {
                getShader().bind();

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
}
