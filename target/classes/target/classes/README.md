# Classes
## SmartTV
- This will be a TCP server
- Turned off by default. It can *ONLY* accept turnOn() command when turned off
- Can be turned on
- Can be turned off
- Can report whether on or off (use a method which returns a boolean whether on or off)
- When turned on:
  - Get number of available channels
  - Get the currently active channel. Channel 1 is default
  - Set channel to desired number c. Handle exception if c is out of range

## TVRemote
- This will be a TCP client
- With buttons:
  - Can turn TV on and off
  - Can switch one channel up
  - Can switch one channel down
- With a display:
  - Show current channel of which the TV is connected to

## TVRemoteHandler
- This handles the TCP connection between TVRemote and SmartTV

## Command
- Abstract class for all commands sent to the socket
- Also created a VersionCommand which extends Command and is a command for user to see version