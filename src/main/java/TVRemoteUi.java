import command.ChannelDownCommand;
import command.ChannelUpCommand;
import command.GetChannelsCommand;
import command.GetCurrentChannelCommand;
import command.OffCommand;
import command.OnCommand;
import command.SetChannelCommand;
import command.VersionCommand;
import java.util.Scanner;

/**
 * Responsible for handling the user interface and user interaction.
 * <p>User should be able to "control" the remote from this UI.</p>
 * <p>Is a command-line based UI.</p>
 */
public class TVRemoteUi {
  private TVRemote remote;

  /**
   * Constructor for TVRemoteUi.
   *
   * @param remote the remote to control.
   */
  public TVRemoteUi(TVRemote remote) {
    this.remote = remote;
  }

  /**
   * Run the TVRemoteUi.
   */
  public void run() {
    System.out.println("Welcome to the TV remote!");
    System.out.println(
        "Commands: on, off, version, getchannels, channelup, channeldown, getcurrentchannel, setchannel, exit");
    boolean running = true;
    Scanner scanner = new Scanner(System.in);
    while (running) {
      System.out.print("Enter command: ");
      String command = scanner.nextLine().toLowerCase();
      switch (command) {
        case "on":
          this.remote.sendAndReceive(new OnCommand());
          break;
        case "off":
          this.remote.sendAndReceive(new OffCommand());
          break;
        case "version":
          this.remote.sendAndReceive(new VersionCommand());
          break;
        case "getchannels":
          this.remote.sendAndReceive(new GetChannelsCommand());
          break;
        case "getcurrentchannel":
          this.remote.sendAndReceive(new GetCurrentChannelCommand());
          break;
        case "channelup":
          this.remote.sendAndReceive(new ChannelUpCommand());
          break;
        case "channeldown":
          this.remote.sendAndReceive(new ChannelDownCommand());
          break;
        case "setchannel":
          Scanner channelScanner = new Scanner(System.in);
          System.out.print("Enter channel number: ");
          int channel = channelScanner.nextInt();
          this.remote.sendAndReceive(new SetChannelCommand(channel));
          break;
        case "exit":
          running = false;
          break;
        default:
          System.out.println("Unknown command: " + command);
      }
    }
  }
}
