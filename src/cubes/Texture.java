package cubes;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture {
        private String texturePath;
        private int handle;

        public Texture(String texturePath) {
                this.texturePath = texturePath;
        }

        public int storeDirectTexture() {
                STBImage.stbi_set_flip_vertically_on_load(true);

                try {
                        int width, height;
                        IntBuffer x = MemoryUtil.memAllocInt(8);
                        IntBuffer y = MemoryUtil.memAllocInt(8);
                        IntBuffer nrChannels = MemoryUtil.memAllocInt(256);
                        InputStream in = new FileInputStream("resources/" + texturePath);
                        int bytes = in.available();
                        System.out.println("texture size: " + bytes);
                        byte[] data = new byte[bytes];
                        in.read(data);
                        System.out.println("Texture located!");
                        ByteBuffer imgData = (ByteBuffer) MemoryUtil.memAlloc(bytes).put(data).flip();
                        int texture = GL20.glGenTextures();
                        GL20.glBindTexture(GL20.GL_TEXTURE_2D, texture);
                        ByteBuffer finalData = STBImage.stbi_load_from_memory(imgData, x, y, nrChannels, 0);
                        if (finalData == null) {
                                System.out.println("FAILED TO LOAD TEXTURE FROM DIRECT RESOURCES WHILE LOADING");
                                System.out.println(STBImage.stbi_failure_reason());
                        }
                        width = x.get();
                        height = y.get();
                        GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGB, width, height, 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, finalData);
                        GL30.glGenerateMipmap(GL20.GL_TEXTURE_2D);
                        GL20.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                        GL20.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                        GL20.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                        GL20.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
                        STBImage.stbi_image_free(finalData);
                        System.out.println("Texture loaded!");
                        handle = texture;
                        return texture;
                } catch (Exception e) {
                        System.out.println("FAILED TO LOAD TEXTURE FROM DIRECT RESOURCES WHEN ACCESSING FILE");
                        System.out.println(STBImage.stbi_failure_reason());
                        handle = 0;
                        return 0;
                }
        }

        public void bind() {
                GL20.glActiveTexture(GL20.GL_TEXTURE0);
                GL20.glBindTexture(GL20.GL_TEXTURE_2D, handle);
        }

        public String getTexturePath() {
                return texturePath;
        }

        public int getHandle() {
                return handle;
        }
}
