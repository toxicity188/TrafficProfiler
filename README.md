# TrafficProfiler

### Simple traffic profiler
This plugin generates traffic summary in your plugin folder per regular time.

- Profiles in/out traffic.
- Profiles in/out buffer per each packet.
- Profiles in/out packet per player.

```
[TrafficProfiler] Summary saved at plugins\TrafficProfiler\summary\summary-per-time.json
```

### API
```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependenceis {
    compileOnly("com.github.toxicity188:TrafficProfiler:VERSION")
}
```

### Supported version
- Bukkit 1.21-1.21.4

### Format
```json
{
  "total_time": "300002.0ms",
  "average_traffic": {
    "in": "0kbps",
    "out": "362kbps"
  },
  "global": {
    "client_bound": {
      "minecraft_level_chunk_with_light": {
        "value": 107004,
        "percentage": "98.513%"
      },
      "minecraft_entity_position_sync": {
        "value": 365,
        "percentage": "0.336%"
      },
      "minecraft_move_entity_pos_rot": {
        "value": 343,
        "percentage": "0.316%"
      },
      "...": {}
    },
    "server_bound": {
      "minecraft_move_player_pos_rot": {
        "value": 90,
        "percentage": "63.217%"
      },
      "minecraft_move_player_pos": {
        "value": 43,
        "percentage": "30.278%"
      },
      "minecraft_client_tick_end": {
        "value": 5,
        "percentage": "3.95%"
      },
      "...": {}
    }
  },
  "per_player": {
    "toxicity_210": {
      "time": 290480,
      "client_bound": {
        "minecraft_level_chunk_with_light": {
          "value": 107004,
          "percentage": "98.513%"
        },
        "minecraft_entity_position_sync": {
          "value": 365,
          "percentage": "0.336%"
        },
        "minecraft_move_entity_pos_rot": {
          "value": 343,
          "percentage": "0.316%"
        },
        "...": {}
      },
      "server_bound": {
        "minecraft_move_player_pos_rot": {
          "value": 90,
          "percentage": "63.217%"
        },
        "minecraft_move_player_pos": {
          "value": 43,
          "percentage": "30.278%"
        },
        "minecraft_client_tick_end": {
          "value": 5,
          "percentage": "3.95%"
        },
        "...": {}
      }
    }
  }
}
```