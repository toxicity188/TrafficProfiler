package kr.toxicity.traffic.api.manager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

public interface ConfigManager {
    boolean debug();
    long summaryTime();
    @NotNull ByteUnit byteUnit();

    @Getter
    @RequiredArgsConstructor
    enum ByteUnit {
        B("bps"),
        KB("kbps"),
        MB("mbps"),
        GB("gbps")
        ;
        private final long multiplier = 1L << (ordinal() * 10);
        private final String unit;
    }
}
