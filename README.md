Takes a snapshot of all online player's stats/attributes & randomly applies them to other players. Effectively swapping them completely.
The plugin is highly customizable with every feature being togglable both ingame and within 'config.yml'

-----------------------------------------------

**Swappable Attributes:**
- Health
- Hunger & Saturation
- Air Bubbles & Fire Ticks
- Location & Velocity
- Inventory
- Potion Effects
- Vehicles
- Ender Pearl Owners

-----------------------------------------------

**Known Issues**
- When swapping, entities sometimes don't update on the client side when there is a mob as a passanger in vehicles.
- Location swapping when two people are in a boat is currently bugged. It will not teleport anyone in that boat.

-----------------------------------------------

**Commands**

The main command which controls everything is '/playerswap' or '/ps'.
| Arguments | Description |
| -------- |-------------| 
| toggle   | Toggles swapping on/off continuously.|
| min [value] | Sets the minimum amount of time between swaps.|
| max [value] | Sets the maximum amount of time between swaps.|
| settings [setting] [true/false] |Toggles the specified setting.|
| sound [effect] [volume] [pitch] | Changes the volume played when swapping.|
| info | Displays all values from config.yml |
| reload | Reloads all values from config.yml |
| players | [DEBUG] Allows you to view the amount of players ready to swap. This is used to ensure the amount of people online & people ready to be swapped are synced.|
| chunks | [DEBUG] Displays how many chunks are loaded by the plugin at one time. This should always be zero after a swap.|
| run | [DEBUG] Initiates a single swap |
| cancel | Cancels & turns off any initiated swap.

-----------------------------------------------

**Basic Demonstration:**
https://www.youtube.com/watch?v=sLKzmdYzKnI

-----------------------------------------------

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
-------------------------------------------

**WARNING:** This is a survival/pvp plugin meant for small scale servers with friends. It is not intended for any large or commercial servers. (Use at your own risk)

The idea for plugin came from this Youtube short by ThirtyVirus: https://www.youtube.com/shorts/ODs75LhTxLs
