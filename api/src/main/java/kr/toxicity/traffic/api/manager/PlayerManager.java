package kr.toxicity.traffic.api.manager;

import kr.toxicity.traffic.api.nms.PacketProfiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public interface PlayerManager extends TrafficManager {
    @Nullable PacketProfiler player(@NotNull UUID uuid);
    void stopProfiling();
    void startProfiling();
    @NotNull File generateProfileResult();
}
