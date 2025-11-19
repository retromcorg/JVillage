# Item-Based Economy System

Added support for item-based currency so you don't need Fundamentals to run a working economy. Use iron ingots, gold, diamonds, whatever - just set it in the config.

## What it does

Gives you two economy options:
- **Fundamentals** (default) - Uses the Fundamentals plugin economy
- **Item** - Uses any vanilla Minecraft item as currency (no plugin required)

Players deposit/withdraw items from their inventory directly into the village bank. Creating villages, claiming chunks, setting warps - all costs are paid with the configured item.

## Config

```yaml
settings:
  economy:
    type: "item"  # or "fundamentals"
    item-currency:
      material: "IRON_INGOT"  # Any valid Material name
```

Use `IRON_INGOT`, `GOLD_INGOT`, `DIAMOND`, `EMERALD`, etc. Whatever makes sense for your server.

## How it works

Created an `ItemEconomy` class that handles inventory operations - checking if players have enough, taking items, giving items back. It's straightforward.

Updated all the economy checks:
- Village creation costs
- Deposit/withdraw commands
- Claiming chunk costs
- Delete refunds
- Everything that touches money now checks the economy type first

If you set it to "item" mode, it uses the item economy. If set to "fundamentals" or Fundamentals isn't installed, it falls back gracefully.

## What changed

**New files:**
- `ItemEconomy.java` - Handles item currency operations

**Updated files:**
- `JVillage.java` - Initialize economy system based on config
- `JVillageSettings.java` - Added economy config options
- `JDepositCommand.java` - Support both economy types
- `JWithdrawCommand.java` - Support both economy types  
- `JCreateCommand.java` - Support item costs for village creation
- `JDeleteCommand.java` - Support item refunds

## Why

Not everyone wants to run Fundamentals or deal with economy plugins. Sometimes you just want a simple "pay with iron" system. This gives server owners that choice without breaking existing setups.

If you're already using Fundamentals, nothing changes - it keeps working exactly the same way. But if you want to go standalone, now you can.

## Testing

Tested with both economy modes:
- Creating villages with item costs
- Depositing items into village bank
- Withdrawing items from village bank
- Claiming chunks (costs items from village bank)
- Deleting villages (refunds items)
- Config validation for invalid materials
- Fallback to Fundamentals when item config is wrong

Works with full inventories (drops items on ground), handles stack sizes properly, all the edge cases.

---

Figured this would be useful for servers that want a lightweight setup. Let me know if anything breaks.
