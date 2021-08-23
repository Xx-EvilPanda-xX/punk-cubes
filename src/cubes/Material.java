package cubes;

import org.joml.Vector3f;

public class Material {
        public Vector3f Ka;
        public Vector3f Kd;
        public Vector3f Ks;
        public float specular;

        public Material() {
        }

        public Material(Vector3f Ka, Vector3f Kd, Vector3f Ks, float specular) {
                this.Ka = Ka;
                this.Kd = Kd;
                this.Ks = Ks;
                this.specular = specular;
        }
}
