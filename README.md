**WARNING:** This is a survival/pvp plugin meant for small servers with friends. It is not intended for any commercial servers.

**Features**
Takes a snapshot of all online player's stats/attributes & randomly applies them to other players.
Attributes:
- Health
- Hunger & Saturation
- Air Bubbles & Fire Ticks
- Location & Velocity
- Inventory
- Potion Effects
- Vehicles
- Ender Pearl Owners

**Config.yml**
```
# The minimum & maximum amount of time a swap can happen.
# The delay is a randomly generated value between these two values. (in seconds)
# The minimum delay cannot be less than 1s.
min-delay: 10.0
max-delay: 60.0
#
# Swapped Attributes (Turning any value to false will disable that value from swapping)
# NOTES:
#     - For velocity & vehicle swapping to be enabled, location also has to be enabled
#     - Toggling vehicle to true will make players swap with their boats/minecarts/horses etc...
#     - Toggling ender-pearl to true will make ender pearl owners swap when thrown
swap:
  health: true
  hunger: true
  saturation: true
  air-bubbles: true
  fire-ticks: true
  location: true
  inventory: true
  potion-effects: true
  vehicle: true
  velocity: true
  ender-pearl: false
#
# The sound effect played when a player is swapped. (Players for every player if they are successfully swapped)
# Volume & Pitch range: (0 .0-1.0)
swap-sound:
  enabled: true
  name: ENTITY.EXPERIENCE_ORB.PICKUP
  volume: 0.5
  pitch: 0.5
#
# The message that's sent for every player that is swapped
# swap-format is replaced with %msg% that's defined in swap-message
# Only initialize one line for swap-format as '%msg%'
# Use '&' for color codes
# Chat Placeholders:
#   - '%original%' ~ The original player (Only works in swap-message)
#   - '%new%'      ~ The new player (Only works in swap-message)
#   - '&delay%'    ~ How long the swap took (Only works in swap-format)
#   - '%msg%'      ~ The individual swap message (Only works in swap-format)
swap-format: '&b%original% -> %new%'
swap-message:
  - '&e&l----------------------------'
  - '&aSwap took: %delay% seconds.'
  - '%msg%'
  - '&e&l----------------------------'

```

The idea for plugin came from this Youtube short by ThirtyVirus: https://www.youtube.com/shorts/ODs75LhTxLs
