# Item Economy

So I added an item-based economy option. You can now use iron ingots or whatever item you want instead of needing Fundamentals.

## What it does

You get two economy modes now:
- Fundamentals - the existing plugin economy
- Item-based - use any vanilla item as money

Just pick one in the config. If you choose items, players pay with actual items from their inventory. No economy plugin needed.

## Config

```yaml
settings:
  economy:
    type: "item"
    item-currency:
      material: "IRON_INGOT"
```

Change the material to whatever. Gold ingots, diamonds, dirt blocks, I don't care. It'll work.

## How it works

Made an ItemEconomy class that does basic inventory stuff - check if player has X items, remove X items, give X items. Pretty simple.

Then went through and updated everything that touches economy:
- Creating villages
- Deposit/withdraw 
- Claiming chunks
- Getting refunds

They all check what economy type you're using and handle it accordingly. If the item config is broken it falls back to Fundamentals mode.

Items drop on the ground if your inventory is full. Handles stack sizes correctly. The usual stuff. Not sure if I handled offline edgecase; ie admin deleting a village...

## Files changed

New: `ItemEconomy.java`

Modified: JVillage.java, JVillageSettings.java, and the deposit/withdraw/create/delete commands.

## Why though

Honestly got tired of needing external economy plugins for what's basically just "do you have 10 iron ingots". Some servers just want simple item-based costs without all the economy plugin overhead. (also im a beta elietist)

If you're using Fundamentals already, nothing changes. But now you have the option to go standalone if you want.

## Testing

Tried both modes, seems to work. Created villages, deposited stuff, withdrew stuff, claimed chunks, deleted villages for refunds. Put invalid material names in the config to test the fallback. All good.

---

Should be fine. Open an issue if something's broken.
