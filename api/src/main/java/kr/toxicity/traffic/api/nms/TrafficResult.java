package kr.toxicity.traffic.api.nms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public interface TrafficResult {
    @NotNull TrafficResult plus(@NotNull TrafficResult other);
    @NotNull @Unmodifiable
    Map<String, Long> get();
}
