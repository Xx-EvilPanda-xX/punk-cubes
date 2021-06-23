package cubes;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL42;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class TextureRendererMulti extends TextureRenderer {
        private ArrayList<Vector3f> positions;
        private ArrayList<Float> scales;
        private ArrayList<Vector3f> rots;
        private int itr;

        public TextureRendererMulti(TexturedMesh mesh, ArrayList<Vector3f> positions, ArrayList<Float> scales, ArrayList<Vector3f> rots) {
                super(mesh);
                this.positions = positions;
                this.scales = scales;
                this.rots = rots;
                if (positions.size() != rots.size() || positions.size() != scales.size() || scales.size() != rots.size()) {
                        throw new IllegalStateException("Mismatched position, rotation, and scaling array sizes!");
                }
        }

        @Override
        public void prepare(boolean debug) {
                if (!isCreated())
                        throw new IllegalStateException("Attempted to call render pass without initializing renderer");

                if (positions.size() != rots.size() || positions.size() != scales.size() || scales.size() != rots.size()) {
                        throw new IllegalStateException("Mismatched position, rotation, and scaling array sizes!");
                }

                if (debug) System.out.println("yaw: " + getCamera().getYaw() + "\npitch: " + getCamera().getPitch());

                Matrix4f model = new Matrix4f().translate(positions.get(itr)).scale(scales.get(itr), scales.get(itr), scales.get(itr)).rotate(rots.get(itr).x, 1.0f, 0.0f, 0.0f).rotate(rots.get(itr).y, 0.0f, 1.0f, 0.0f).rotate(rots.get(itr).z, 0.0f, 0.0f, 1.0f);

                Matrix4f proj = getCamera().getProjectionMatrix();

                Matrix4f view = getCamera().getViewMatrix();

                if (debug) {
                        System.out.println(model.toString());
                        float[] matrix = new float[16];
                        for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 4; j++) {
                                        matrix[(i * 4) + j] = model.get(i, j);
                                }
                        }
                        GL30.glBindVertexArray(getMesh().getVao().getHandle());
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getMesh().getUao());

                        FloatBuffer buf = (FloatBuffer) MemoryUtil.memAllocFloat(16).put(matrix).flip();
                        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 64 * itr, buf);
                        MemoryUtil.memFree(buf);

                        GL30.glEnableVertexAttribArray(4);
                        GL30.glEnableVertexAttribArray(5);
                        GL30.glEnableVertexAttribArray(6);
                        GL30.glEnableVertexAttribArray(7);

                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                        GL30.glBindVertexArray(0);

                        System.out.println(proj.toString());
                        getShader().setUniform("projection", proj, true);
                        System.out.println(view.toString());
                        getShader().setUniform("view", view, true);
                } else {
                        float[] matrix = new float[16];
                        for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 4; j++) {
                                        matrix[(i * 4) + j] = model.get(i, j);
                                }
                        }
                        GL30.glBindVertexArray(getMesh().getVao().getHandle());
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getMesh().getUao());

                        FloatBuffer buf = (FloatBuffer) MemoryUtil.memAllocFloat(16).put(matrix).flip();
                        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 64 * itr, buf);
                        MemoryUtil.memFree(buf);

                        GL30.glEnableVertexAttribArray(4);
                        GL30.glEnableVertexAttribArray(5);
                        GL30.glEnableVertexAttribArray(6);
                        GL30.glEnableVertexAttribArray(7);

                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                        GL30.glBindVertexArray(0);

                        getShader().setUniform("projection", proj, false);
                        getShader().setUniform("view", view, false);
                }

                getShader().setUniform("lightPos", Window.currentLightPos);
                getShader().setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
                if (!getCamera().isThirdPerson()) {
                        getShader().setUniform("viewPos", getCamera().playerPos);
                } else {
                        getShader().setUniform("viewPos", getCamera().playerPos.sub(getCamera().getFront().mul(getCamera().getZoom() / 10, new Vector3f()), new Vector3f()));
                }
                getShader().setUniform("mode", 0);
        }

        @Override
        public void render(boolean debug) {
                getShader().bind();
                getMesh().getTexture().bind();

                for (int i = 0; i < positions.size(); i++) {
                        itr = i;
                        if (itr > MAX_INSTANCES) {
                                throw new UnsupportedOperationException("Tried to render more than allowed instances");
                        }
                        prepare(debug);
                }

                Vao vao = getMesh().getVao();
                if (getMesh().isIndexed()) {
                        vao.bind();
                        vao.enableAttribs();
                        vao.bindIndices();
                        GL42.glDrawElementsInstanced(GL11.GL_TRIANGLES, getMesh().getIndexCount(), GL11.GL_UNSIGNED_INT, 0, positions.size());
                        vao.unbindIndices();
                        vao.disableAttribs();
                        vao.unbind();
                } else {
                        vao.bind();
                        vao.enableAttribs();
                        GL42.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, getMesh().getVertexCount(), positions.size());
                        vao.disableAttribs();
                        vao.unbind();
                }
                GL30.glDisableVertexAttribArray(4);
                GL30.glDisableVertexAttribArray(5);
                GL30.glDisableVertexAttribArray(6);
                GL30.glDisableVertexAttribArray(7);
        }
}
