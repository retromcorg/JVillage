# BlueMap Integration

So I added BlueMap support. Your villages now show up on the web map as colored regions. Pretty neat.

## What it does

Villages automatically render on BlueMap with their own colors. Chunks that touch each other get merged into one shape instead of looking like a bunch of squares, which looks way better. Click on a village and you get a popup with the info.

If you don't have BlueMap installed, nothing breaks. It just doesn't do anything. No extra dependencies either - used reflection to avoid that mess; prob not the right way to do it, but im new to java so take that elitists!!

## Config

```yaml
settings:
  bluemap:
    enabled: true
    marker-label: "Village Claims"
```

Set it to false if you don't want it.

## How it works

The fun part was figuring out how to merge adjacent chunks into clean shapes. Wrote a flood-fill thing to group chunks together, then trace the edges to get the outline. Each village gets a color based on its UUID so it's always the same.

Updates happen automatically when you claim/unclaim chunks. Batched them so it doesn't spam the map renderer. (distrubing results when not doing this LOL)

Used reflection to load BlueMap at runtime so this compiles on Java 8 without needing BlueMap as a dependency. Works or doesn't work depending on whether you have it installed.

## What changed

Added two new files:
- `BlueMapIntegration.java` - does the map stuff
- `BlueMapUpdateListener.java` - listens for claim changes

Hooked it into the main plugin loader and the claim/unclaim commands. Added BlueMap as a soft dependency in plugin.yml.

## Tested

Works with BlueMap, works without BlueMap. Villages show up, colors are consistent, chunks merge properly, markers update when you do stuff. Tested all the basic scenarios and it seems solid, granted I tested the stuff on my own fork of JVillage.

## Why

Players can now see where villages are on the web map. Useful for finding unclaimed land or just seeing how your territory looks. Server admins seem to like having this kind of visualization.

## Maybe later

Could add per-village color customization or visibility toggles. Custom icons would be cool too. But that's probably overkill for now.

---

That's it. Should work fine. Let me know if something breaks.
