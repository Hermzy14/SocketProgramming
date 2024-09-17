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

## TVRemoteUi
- This will be a console UI for TVRemote
- User can "press buttons" (write inputs) to interact with TVRemote

## Command
- Abstract class for all commands sent to the socket

### TurnOnCommand
- Command to turn on the TV
- It extends Command
- It can *ONLY* be sent when the TV is off

### TurnOffCommand
- Command to turn off the TV
- It extends Command
- It can *ONLY* be sent when the TV is on

### SetChannelCommand
- Command to switch channel
- It extends Command
- It can be sent when the TV is on
- It can be sent with a parameter to switch to a specific channel

### GetCurrentChannelCommand
- Command to get the current channel
- It extends Command
- It can be sent when the TV is on

### GetChannelsCommand
- Command to get the number of available channels
- It extends Command
- It can be sent when the TV is on

### ChannelUpCommand
- Command to switch channel up
- It extends Command
- It can be sent when the TV is on

### ChannelDownCommand
- Command to switch channel down
- It extends Command
- It can be sent when the TV is on

### VersionCommand
- Command to get the version of the TV
- It extends Command
- It can be sent when the TV is on