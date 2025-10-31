package betterblockentities.util;

public interface BlockEntityExt {
    boolean getJustReceivedUpdate();
    void setJustReceivedUpdate(boolean value);

    void setRemoveChunkVariant(boolean value);
    boolean getRemoveChunkVariant();
}
