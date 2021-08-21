package cubes;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL42;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.HashMap;

public class TextRenderer {
        private String text;
        private float xPos;
        private float yPos;
        private float fontSize;
        private float charSpacing;

        private FloatBuffer vertices, texCoords;
        private int vertexCount;
        private static HashMap<Character, Texture> chars = new HashMap<>();

        private Shader shader;
        private boolean created = false;

        private Vao vao = new Vao();
        private int vbo, tbo;

        private int charItr;

        public TextRenderer(String text, float xPos, float yPos, float fontSize, float charSpacing) {
                this.text = text;
                this.xPos = xPos;
                this.yPos = yPos;
                this.fontSize = fontSize;
                this.charSpacing = charSpacing;
        }

        public void create(Shader shader) {
                this.shader = shader;

                vertices = (FloatBuffer) MemoryUtil.memAllocFloat(12).put(0.0f).put(0.0f).put(0.25f).put(0.0f).put(0.0f).put(-0.5f).put(0.25f).put(-0.5f).put(0.25f).put(0.0f).put(0.0f).put(-0.5f).flip();
                texCoords = (FloatBuffer) MemoryUtil.memAllocFloat(12).put(0.0f).put(1.0f).put(1.0f).put(1.0f).put(0.0f).put(0.0f).put(1.0f).put(0.0f).put(1.0f).put(1.0f).put(0.0f).put(0.0f).flip();
                vertexCount = vertices.capacity();

                vbo = vao.storeBuffer(0, 2, vertices);
                tbo = vao.storeBuffer(1, 2, texCoords);

                created = true;
        }

        public void prepare() {
                if (!created)
                        throw new IllegalStateException("Attempted to call render pass without initializing renderer");

                Matrix4f model = new Matrix4f().translate(xPos + (charItr * (fontSize * charSpacing)), yPos, 0.0f).scale(fontSize);

                shader.setUniform("model", model);
        }

        public void render() {
                if (!EventHandler.rasterizerFill) {
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                }
                shader.bind();

                for (int j = 0; j < text.length(); j++) {
                        charItr = j;
                        if (text.charAt(charItr) != ' ') {
                                prepare();

                                chars.get(text.charAt(charItr)).bind();

                                vao.bind();
                                vao.enableAttribs();
                                GL42.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
                                vao.disableAttribs();
                                vao.unbind();
                        }
                }

                if (!EventHandler.rasterizerFill){
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                }
        }

        public void updateText(String text, float xPos, float yPos){
                this.text = text;
                this.xPos = xPos;
                this.yPos = yPos;
        }

        static {
                for (int i = 0; i < 128; i++){
                        StringBuilder charPath = new StringBuilder();
                        charPath.append("textures/fonts/basicFont/char_").append(i).append(".png");
                        if (new File("resources/" + charPath.toString()).exists()) {
                                chars.put((char) i, new Texture(charPath.toString()));
                        }
                }
        }
}
