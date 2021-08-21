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
        private int meshItr;
        private int instances;

        public TextureRendererMulti(TexturedMesh mesh, ArrayList<Vector3f> positions, ArrayList<Float> scales, ArrayList<Vector3f> rots) {
                super(mesh);
                this.positions = positions;
                this.scales = scales;
                this.rots = rots;
                if (positions.size() != rots.size() || positions.size() != scales.size() || scales.size() != rots.size()) {
                        throw new IllegalStateException("Mismatched position, rotation, and scaling array sizes!");
                }
        }

        public TextureRendererMulti(String modelpath, String[] texturePaths, ArrayList<Vector3f> positions, ArrayList<Float> scales, ArrayList<Vector3f> rots, boolean useFullTexture){
                super(modelpath, texturePaths, useFullTexture);
                this.positions = positions;
                this.scales = scales;
                this.rots = rots;
                if (positions.size() != rots.size() || positions.size() != scales.size() || scales.size() != rots.size()) {
                        throw new IllegalStateException("Mismatched position, rotation, and scaling array sizes!");
                }
        }

        @Override
        public void prepare() {
                if (!isCreated())
                        throw new IllegalStateException("Attempted to call render pass without initializing renderer");

                if (positions.size() != rots.size() || positions.size() != scales.size() || scales.size() != rots.size()) {
                        throw new IllegalStateException("Mismatched position, rotation, and scaling array sizes!");
                }

                Matrix4f model = new Matrix4f().translate(positions.get(itr)).scale(scales.get(itr), scales.get(itr), scales.get(itr)).rotate(rots.get(itr).x, 1.0f, 0.0f, 0.0f).rotate(rots.get(itr).y, 0.0f, 1.0f, 0.0f).rotate(rots.get(itr).z, 0.0f, 0.0f, 1.0f);

                Matrix4f proj = itr == 0 ? getCamera().getProjectionMatrix() : null;

                Matrix4f view = itr == 0 ? getCamera().getViewMatrix() : null;

                float[] matrix = new float[16];
                for (int j = 0; j < 4; j++) {
                        for (int k = 0; k < 4; k++) {
                                matrix[(j * 4) + k] = model.get(j, k);
                        }
                }
                GL30.glBindVertexArray(getMesh().getVaos()[meshItr].getHandle());
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getMesh().getUaos()[meshItr]);

                FloatBuffer buf = (FloatBuffer) MemoryUtil.memAllocFloat(16).put(matrix).flip();
                GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 64 * itr, buf);
                MemoryUtil.memFree(buf);

                GL30.glEnableVertexAttribArray(3);
                GL30.glEnableVertexAttribArray(4);
                GL30.glEnableVertexAttribArray(5);
                GL30.glEnableVertexAttribArray(6);

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                GL30.glBindVertexArray(0);

                if (itr == 0) {
                        getShader().setUniform("projection", proj);
                        getShader().setUniform("view", view);

                        getShader().setUniform("lightPos", Window.currentLightPos);
                        getShader().setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
                        getShader().setUniform("viewPos", getCamera().isThirdPerson() ? getCamera().playerPos.sub(getCamera().getFront().mul(getCamera().getZoom() / 10, new Vector3f()), new Vector3f()) : getCamera().playerPos);
                        getShader().setUniform("mode", 0);

                        getShader().setUniform("material.Ka", getMesh().meshes.get(meshItr).material.Ka);
                        getShader().setUniform("material.Kd", getMesh().meshes.get(meshItr).material.Kd);
                        getShader().setUniform("material.Ks", getMesh().meshes.get(meshItr).material.Ks);
                        getShader().setUniform("material.spec", getMesh().meshes.get(meshItr).material.specular);
                }
        }

        @Override
        public void render() {
                getShader().bind();

                for (int i = 0; i < mesh.meshes.size(); i++) {
                        meshItr = i;

                        for (int j = 0; j < positions.size(); j++) {
                                itr = j;
                                if (itr > MAX_INSTANCES) {
                                        throw new UnsupportedOperationException("Tried to render more than allowed instances");
                                }
                                prepare();
                        }
                        instances = positions.size();

                        if (i > getMesh().getTextures().size() - 1) {
                                getShader().setUniform("useMaterialDiffuse", true);
                                if (getMesh().isForceTexture() && getMesh().getTextures().get(0) != null) {
                                        getMesh().getTextures().get(mesh.getTextures().size() - 1).bind();
                                        getShader().setUniform("useMaterialDiffuse", false);
                                }
                        } else {
                                getMesh().getTextures().get(i).bind();
                                getShader().setUniform("useMaterialDiffuse", false);
                        }

                        Vao vao = getMesh().getVaos()[i];

                        vao.bind();
                        vao.enableAttribs();
                        if (getMesh().isIndexed()) {
                                vao.bindIndices();
                                GL42.glDrawElementsInstanced(GL11.GL_TRIANGLES, getMesh().getIndexCounts().get(i), GL11.GL_UNSIGNED_INT, 0, instances);
                                vao.unbindIndices();
                        } else {
                                GL42.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, getMesh().getVertexCounts().get(i), instances);
                        }
                        vao.disableAttribs();
                        vao.unbind();

                        GL30.glDisableVertexAttribArray(3);
                        GL30.glDisableVertexAttribArray(4);
                        GL30.glDisableVertexAttribArray(5);
                        GL30.glDisableVertexAttribArray(6);
                }
        }
}
