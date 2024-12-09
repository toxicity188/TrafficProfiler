package kr.toxicity.traffic.api;

import kr.toxicity.traffic.api.manager.CommandManager;
import kr.toxicity.traffic.api.manager.ConfigManager;
import kr.toxicity.traffic.api.manager.PlayerManager;
import kr.toxicity.traffic.api.nms.NMS;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class TrafficProfiler extends JavaPlugin {
    private static TrafficProfiler inst;

    @Override
    public final void onLoad() {
        if (inst != null) throw new RuntimeException();
        inst = this;
    }

    public static @NotNull TrafficProfiler inst() {
        return Objects.requireNonNull(inst);
    }

    public abstract @NotNull ReloadState reload();
    public abstract boolean onReload();

    public abstract @NotNull NMS nms();

    public abstract @NotNull ConfigManager configManager();
    public abstract @NotNull PlayerManager playerManager();
    public abstract @NotNull CommandManager commandManager();
}
