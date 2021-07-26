package cubes;

public class Geometry {
        private static final float r = Float.parseFloat(Configs.options.get("block_color.r"));
        private static final float g = Float.parseFloat(Configs.options.get("block_color.g"));
        private static final float b = Float.parseFloat(Configs.options.get("block_color.b"));

        public static final float[] QUAD_VERTICES = new float[]{
                -0.5f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f, 0.5f, 0.0f
        };

        public static final float[] QUAD_COLORS = new float[]{
                1.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 0.0f
        };

        public static final float[] QUAD_NORMALS = new float[]{
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f
        };

        public static final float[] QUAD_TEX_COORDS = new float[]{
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };

        public static final float[] CUBE_VERTICES = new float[]{
                -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f
        };

        public static final float[] CUBE_TEX_COORDS = new float[]{
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f
        };

        public static final float[] CUBE_NORMALS = new float[]{
                0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
        };

        public static final float[] CUBE_COLORS = new float[]{
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
        };

        public static final float[] BLOCK_COLORS = new float[]{
                r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b,
                r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b,
                r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b,
                r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b,
                r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b,
                r, g, b, r, g, b, r, g, b, r, g, b, r, g, b, r, g, b
        };


        public static final float[] SPACESHIP_VERTICES = new float[]{
                0.0f, -0.25f, 0.0f, 0.0f, 0.0f, -0.75f, 0.5f, 0.0f, 0.0f,
                0.0f, -0.25f, 0.0f, 0.0f, 0.0f, -0.75f, -0.5f, 0.0f, 0.0f,
                0.0f, -0.25f, 0.0f, 0.0f, 0.0f, 0.75f, 0.5f, 0.0f, 0.0f,
                0.0f, -0.25f, 0.0f, 0.0f, 0.0f, 0.75f, -0.5f, 0.0f, 0.0f,
                0.0f, 0.25f, 0.0f, 0.0f, 0.0f, -0.75f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.25f, 0.0f, 0.0f, 0.0f, -0.75f, -0.5f, 0.0f, 0.0f,
                0.0f, 0.25f, 0.0f, 0.0f, 0.0f, 0.75f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.25f, 0.0f, 0.0f, 0.0f, 0.75f, -0.5f, 0.0f, 0.0f
        };

        public static final float[] SPACESHIP_COLORS = new float[]{
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.929f, 0.517f, 0.407f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.929f, 0.517f, 0.407f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.929f, 0.517f, 0.407f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.929f, 0.517f, 0.407f,
                0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f, 0.709f, 0.219f, 0.09f
        };

        public static final float[] SPACESHIP_NORMALS = new float[]{
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f
        };

        public static final float[] PYRAMID_VERTICES = new float[]{
                0.0f, 0.5f, 0.0f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f,
                0.0f, 0.5f, 0.0f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,
                0.0f, 0.5f, 0.0f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f,
                0.0f, 0.5f, 0.0f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f,
        };

        public static final float[] PYRAMID_TEX_COORDS = new float[]{
                0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f,
        };

        public static final float[] PYRAMID_NORMALS = new float[]{
                1.0f, 0.0137073518f, 0.0f, 1.0f, 0.0137073518f, 0.0f, 1.0f, 0.0137073518f, 0.0f,
                0.0f, 0.0137073518f, -1.0f, 0.0f, 0.0137073518f, -1.0f, 0.0f, 0.0137073518f, -1.0f,
                -1.0f, 0.0137073518f, 0.0f, -1.0f, 0.0137073518f, 0.0f, -1.0f, 0.0137073518f, 0.0f,
                0.0f, 0.0137073518f, 1.0f, 0.0f, 0.0137073518f, 1.0f, 0.0f, 0.0137073518f, 1.0f,
                0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
        };
}
