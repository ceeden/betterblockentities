package betterblockentities.helpers;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform;

public class ModelRotationTransform
{
    public static QuadTransform rotateY(float degrees) {
        float radians = (float) Math.toRadians(degrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        float centerX = 0.5f;
        float centerZ = 0.5f;
        return quad -> {
            for (int i = 0; i < 4; i++) {
                float x = quad.x(i) - centerX;
                float y = quad.y(i);
                float z = quad.z(i) - centerZ;
                float newX = x * cos - z * sin;
                float newZ = x * sin + z * cos;
                quad.pos(i, newX + centerX, y, newZ + centerZ);
            }
            return true;
        };
    }
    private ModelRotationTransform() {
        throw new IllegalStateException("Instancing of this class is not allowed!");
    }
}
