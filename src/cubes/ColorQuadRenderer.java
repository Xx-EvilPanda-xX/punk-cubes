package cubes;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class ColorQuadRenderer extends ColorRenderer {
        public boolean USE_PROJ_VIEW_MAT = true;

        public final float SCALE;
        public final float SPEED;
        public final float YPOS;

        private float trans, transOffset, rotation;

        public ColorQuadRenderer(ColoredMesh mesh, float scale, float speed, float ypos, float rotation) {
                super(mesh);
                SCALE = scale;
                SPEED = speed;
                YPOS = ypos;

                transOffset = SPEED;
                trans = 0.0f;
                this.rotation = rotation;
        }

        @Override
        public void prepare(boolean debug) {
                if (!isCreated())
                        throw new IllegalStateException("Attempted to call render pass without initializing renderer");
                if (debug) System.out.println("yaw: " + getCamera().getYaw() + "\npitch: " + getCamera().getPitch());

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
                        float[] matrix = new float[16];
                        for (int i = 0; i < 4; i++){
                                for (int j = 0; j < 4; j++){
                                        matrix[(i * 4) + j] = model.get(i, j);
                                }
                        }
                        GL30.glBindVertexArray(getMesh().getVao().getHandle());
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getMesh().getUao());

                        FloatBuffer buf = (FloatBuffer) MemoryUtil.memAllocFloat(16).put(matrix).flip();
                        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buf);
                        MemoryUtil.memFree(buf);

                        GL30.glEnableVertexAttribArray(4);
                        GL30.glEnableVertexAttribArray(5);
                        GL30.glEnableVertexAttribArray(6);
                        GL30.glEnableVertexAttribArray(7);

                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                        GL30.glBindVertexArray(0);

                        if (USE_PROJ_VIEW_MAT) {
                                System.out.println(proj.toString());
                                getShader().setUniform("projection", proj, true);
                                System.out.println(view.toString());
                                getShader().setUniform("view", view, true);

                        }
                } else {
                        float[] matrix = new float[16];
                        for (int i = 0; i < 4; i++){
                                for (int j = 0; j < 4; j++){
                                        matrix[(i * 4) + j] = model.get(i, j);
                                }
                        }
                        GL30.glBindVertexArray(getMesh().getVao().getHandle());
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getMesh().getUao());

                        FloatBuffer buf = (FloatBuffer) MemoryUtil.memAllocFloat(16).put(matrix).flip();
                        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buf);
                        MemoryUtil.memFree(buf);

                        GL30.glEnableVertexAttribArray(4);
                        GL30.glEnableVertexAttribArray(5);
                        GL30.glEnableVertexAttribArray(6);
                        GL30.glEnableVertexAttribArray(7);

                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                        GL30.glBindVertexArray(0);

                        if (USE_PROJ_VIEW_MAT) {
                                getShader().setUniform("projection", proj, false);
                                getShader().setUniform("view", view, false);
                        }
                }
                getShader().setUniform("lightPos", Window.currentLightPos);
                getShader().setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
                if (!getCamera().isThirdPerson()) {
                        getShader().setUniform("viewPos", getCamera().playerPos);
                } else {
                        getShader().setUniform("viewPos", getCamera().playerPos.sub(getCamera().getFront().mul(getCamera().getZoom() / 10, new Vector3f()), new Vector3f()));
                }
                getShader().setUniform("mode", USE_PROJ_VIEW_MAT ? 1 : 3);
        }

        @Override
        public void render(boolean debug) {
                getShader().bind();

                prepare(debug);
                if (getMesh().isIndexed()) {
                        getMesh().getVao().bind();
                        getMesh().getVao().enableAttribs();
                        getMesh().getVao().bindIndices();
                        GL11.glDrawElements(GL11.GL_TRIANGLES, getMesh().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
                        getMesh().getVao().unbindIndices();
                        getMesh().getVao().disableAttribs();
                        getMesh().getVao().unbind();
                } else {
                        getMesh().getVao().bind();
                        getMesh().getVao().enableAttribs();
                        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, getMesh().getVertexCount());
                        getMesh().getVao().disableAttribs();
                        getMesh().getVao().unbind();
                }
                GL30.glDisableVertexAttribArray(4);
                GL30.glDisableVertexAttribArray(5);
                GL30.glDisableVertexAttribArray(6);
                GL30.glDisableVertexAttribArray(7);
        }
}
