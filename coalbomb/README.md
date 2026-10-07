# Coal Bomb (Minecraft 1.20.1, Forge 47.x)

Throwable coal bomb. It sticks where it hits, glows for ~55 s, marks the area with red
particles for 5 s, then a wave eats all blocks from the center outward (slow, then faster).

Craft: 8 coal around 1 TNT. Or: /give @s coalbomb:coal_bomb

Settings: config/coalbomb-common.toml (radius, timings, speed, vertical range).

## Build the .jar without installing anything
1. Create a repo on github.com, upload everything from this folder (keep the .github/workflows/build.yml path).
2. Open the Actions tab -> "Build mod" -> wait for the green check.
3. Open the run -> Artifacts -> coalbomb-jar -> unzip -> put coalbomb-1.0.0.jar in .minecraft/mods
