package kr.toxicity.traffic.api.nms;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

@Getter
public class ProtocolResult {
    private final Map<PacketBound, TrafficResult> result;
    private final long timeMills;

    public ProtocolResult(long time) {
        this(new EnumMap<>(PacketBound.class), time);
    }
    public ProtocolResult(@NotNull Map<PacketBound, TrafficResult> resultMap, long timeMills) {
        this.result = resultMap;
        this.timeMills = timeMills;
    }

    public void put(@NotNull PacketBound bound, @NotNull TrafficResult value) {
        result.put(bound, value);
    }
}
