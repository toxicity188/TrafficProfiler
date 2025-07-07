package kr.toxicity.traffic.api.nms;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NMS {
    @NotNull PacketProfiler profiler(@NotNull Player player);
    void submitToEventLoop(@NotNull Player player, @NotNull Runnable runnable);
}
