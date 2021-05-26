package cubes;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class TextureQuadRenderer extends TextureRenderer {
        public boolean USE_PROJ_VIEW_MAT = true;

        public final float SCALE;
        public final float LIMIT;
        public final float SPEED;
        public final float YPOS;

        private float trans, transOffset, rotation;

        public TextureQuadRenderer(float[] vertexData, float[] texCoordData, float[] normalData, int[] indexData, float scale, float speed, float ypos, float rotation, String texturePath) {
                super(vertexData, texCoordData, normalData, indexData, texturePath);
                SCALE = scale;
                LIMIT = 1 - (scale / 2);
                SPEED = speed;
                YPOS = ypos;

                transOffset = SPEED;
                trans = 0.0f;
                this.rotation = rotation;
        }

        public TextureQuadRenderer(float[] vertexData, float[] texCoordData, float[] normalData, float scale, float speed, float ypos, String texturePath) {
                super(vertexData, texCoordData, normalData, texturePath);
                SCALE = scale;
                LIMIT = 1 - (scale / 2);
                SPEED = speed;
                YPOS = ypos;

                indexCount = 0;
                transOffset = SPEED;
                trans = 0.0f;
        }

        public void prepareQuad(Shader shader, Camera camera, boolean debug) {
                if (debug) System.out.println("yaw: " + camera.yaw + "\npitch: " + camera.pitch);

                this.trans += transOffset * Window.deltaTime;
                if (this.trans > LIMIT) {
                        transOffset = -SPEED;
                        this.trans = LIMIT;
                }
                if (this.trans < -LIMIT) {
                        transOffset = SPEED;
                        this.trans = -LIMIT;
                }

                Matrix4f model;

                if (USE_PROJ_VIEW_MAT) {
                        model = new Matrix4f().translate(this.trans, YPOS, -3.0f).scale(SCALE, SCALE, SCALE).rotate(rotation * this.trans, 0.0f, 0.0f, 1.0f);
                } else {
                        model = new Matrix4f().translate(this.trans, YPOS, 0.0f).scale(SCALE, SCALE, SCALE).rotate(rotation * this.trans, 0.0f, 0.0f, 1.0f);
                }

                Matrix4f proj = camera.getProjectionMatrix();

                Matrix4f view = camera.getViewMatrix();

                shader.setUniform("lightPos", Window.currentLightPos);
                shader.setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
                if (debug) {
                        System.out.println(model.toString());
                        shader.setUniform("model", model, true);
                        if (USE_PROJ_VIEW_MAT) {
                                System.out.println(proj.toString());
                                shader.setUniform("projection", proj, true);
                                System.out.println(view.toString());
                                shader.setUniform("view", view, true);
                                shader.setUniform("mode", 0);
                        } else {
                                shader.setUniform("mode", 2);
                        }
                } else {
                        shader.setUniform("model", model, false);
                        if (USE_PROJ_VIEW_MAT) {
                                shader.setUniform("projection", proj, false);
                                shader.setUniform("view", view, false);
                                shader.setUniform("mode", 0);
                        } else {
                                shader.setUniform("mode", 2);
                        }
                }
        }

        public void renderQuad(Shader shader, Camera camera, boolean debug) {
                prepareQuad(shader, camera, debug);

                getTexture().bind();
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

        public void prepare(Shader shader, Camera camera, Vector3f trans, float scale, float rotate, boolean debug) {
                throw new UnsupportedOperationException("No calling the parent class method from the child class!");
        }

        @Override
        public void render(Shader shader, Camera camera, Vector3f trans, float scale, float rotate, boolean debug) {
                throw new UnsupportedOperationException("No calling the parent class method from the child class!");
        }
}
