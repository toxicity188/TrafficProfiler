package kr.toxicity.traffic.api;

import org.jetbrains.annotations.NotNull;

public sealed interface ReloadState {

    OnReload ON_RELOAD = new OnReload();

    final class OnReload implements ReloadState {
        private OnReload() {

        }
    }

    record Success(long time) implements ReloadState {}
    record Failure(@NotNull Throwable reason) implements ReloadState {}
}
