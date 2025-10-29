package betterblockentities.util;

/* sodium */
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;

/* fabric */
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform;

/* minecraft */
import net.minecraft.client.texture.Sprite;

public class ModelTransform
{
    /* rotates base MutableQuadView from passed degrees and not radians */
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

    /* swaps the sprite of the quad while preserving UV mapping proportions */
    public static QuadTransform swapSprite(Sprite newSprite) {
        return quad -> {
            if (!(quad instanceof MutableQuadViewImpl mQuad)) return true;
            Sprite oldSprite = mQuad.cachedSprite();
            if (oldSprite == null) return true;

            for (int i = 0; i < 4; i++) {
                float uNorm = (mQuad.u(i) - oldSprite.getMinU()) / (oldSprite.getMaxU() - oldSprite.getMinU());
                float vNorm = (mQuad.v(i) - oldSprite.getMinV()) / (oldSprite.getMaxV() - oldSprite.getMinV());
                mQuad.uv(i,
                        newSprite.getMinU() + uNorm * (newSprite.getMaxU() - newSprite.getMinU()),
                        newSprite.getMinV() + vNorm * (newSprite.getMaxV() - newSprite.getMinV())
                );
            }
            mQuad.cachedSprite(newSprite);
            return true;
        };
    }

    private ModelTransform() {
        throw new IllegalStateException("Instancing of this class is not allowed!");
    }
}
