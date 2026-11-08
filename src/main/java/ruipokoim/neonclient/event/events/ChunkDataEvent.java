package ruipokoim.neonclient.event.events;

import net.minecraft.world.chunk.WorldChunk;
public record ChunkDataEvent(WorldChunk chunk) {}