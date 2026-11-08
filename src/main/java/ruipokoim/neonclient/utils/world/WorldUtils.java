package ruipokoim.neonclient.utils.world;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import ruipokoim.neonclient.mixin.ChunkAccessor;
import ruipokoim.neonclient.mixin.ClientChunkManagerAccessor;
import ruipokoim.neonclient.mixin.ClientChunkMapAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static ruipokoim.neonclient.NeonMain.mc;

public class WorldUtils {
    private static int i = 0;
    public static List<WorldChunk> getLoadedChunks() {
        List<WorldChunk> loadedChunks = new ArrayList<>();
        if (mc.world != null && mc.world.getChunkManager() != null) {
            ClientChunkManagerAccessor managerAccessor = (ClientChunkManagerAccessor) mc.world.getChunkManager();
            Object chunkMap = managerAccessor.neon$getChunks();
            ClientChunkMapAccessor mapAccessor = (ClientChunkMapAccessor) chunkMap;
            AtomicReferenceArray<WorldChunk> chunksArray = mapAccessor.neon$getChunks();
            for (int i = 0; i < chunksArray.length(); i++) {
                WorldChunk chunk = chunksArray.get(i);
                if (chunk != null && isInRadius(chunk)) {
                    loadedChunks.add(chunk);
                }
            }
        }
        return loadedChunks;
    }
    public static List<BlockEntity> getLoadedBlockEntities() {
        List<BlockEntity> loadedBlockEntities = new ArrayList<>();
        if (mc.world != null && mc.world.getChunkManager() != null) {
            for (WorldChunk chunk : getLoadedChunks()) {
                Map<BlockPos, BlockEntity> entities = ((ChunkAccessor) chunk).getBlockEntities();
                if (entities != null) {
                    loadedBlockEntities.addAll(entities.values());
                }
            }
        }
        return loadedBlockEntities;
    }
    private static boolean isInRadius(Chunk chunk) {
        int x = chunk.getPos().x;
        int z = chunk.getPos().z;
        return mc.world.getChunkManager().isChunkLoaded(x + 1, z) && mc.world.getChunkManager().isChunkLoaded(x - 1, z) && mc.world.getChunkManager().isChunkLoaded(x, z + 1) && mc.world.getChunkManager().isChunkLoaded(x, z - 1);
    }
}