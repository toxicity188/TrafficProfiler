package kr.toxicity.traffic.api.nms;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PacketProfiler extends AutoCloseable {
    @NotNull Player player();
    void start();
    void stop();
    void clear();
    @NotNull ProtocolResult summary();
}
