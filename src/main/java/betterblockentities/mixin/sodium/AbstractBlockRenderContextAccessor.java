package betterblockentities.mixin.sodium;

/* sodium */
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;

/* fabric */
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

/* minecraft */
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/* java/misc */
import org.jetbrains.annotations.Nullable;

@Mixin(AbstractBlockRenderContext.class)
public interface AbstractBlockRenderContextAccessor
{
    @Accessor("level")
    BlockRenderView getLevel();

    @Accessor("random")
    Random getRandom();

    @Accessor("defaultRenderType")
    BlockRenderLayer getDefaultRenderType();

    @Accessor("defaultRenderType")
    void setDefaultRenderType(@Nullable BlockRenderLayer layer);

    @Accessor("state")
    BlockState getState();
    @Accessor("state")
    void setState(BlockState state);

    @Accessor("pos")
    BlockPos getPos();
    @Accessor("pos")
    void setPos(BlockPos pos);

    @Accessor("allowDowngrade")
    boolean getAllowDowngrade();
    @Accessor("allowDowngrade")
    void setAllowDowngrade(boolean allow);

    @Accessor("slice")
    LevelSlice getSlice();
    @Accessor("slice")
    void setSlice(LevelSlice slice);

    @Invoker("getEmitter")
    QuadEmitter getEmitterInvoke();

    @Invoker("prepareAoInfo")
    void prepareAoInfoInvoke(boolean modelAo);

    @Invoker("prepareCulling")
    void prepareCullingInvoke(boolean enableCulling);

    @Invoker("isFaceCulled")
    boolean isFaceCulledInvoke(@Nullable Direction face);
}