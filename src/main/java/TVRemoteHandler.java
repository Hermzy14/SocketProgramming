import command.ChannelDownCommand;
import command.ChannelUpCommand;
import command.Command;
import command.GetChannelsCommand;
import command.GetCurrentChannelCommand;
import command.OffCommand;
import command.OnCommand;
import command.SetChannelCommand;
import command.VersionCommand;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Handles one SmartTV-TCP client connection.
 */
public class TVRemoteHandler {
  private SmartTV tv;
  private Socket clientRemote;
  private ObjectInputStream objectReader;
  private PrintWriter socketWriter;

  public TVRemoteHandler(SmartTV tv, Socket clientRemote) {
    this.tv = tv;
    this.clientRemote = clientRemote;
    System.out.println("Remote connected from " + clientRemote.getRemoteSocketAddress()
        + ", port: " + clientRemote.getPort());
  }

  /**
   * Runs the TVRemoteHandler
   */
  public void run() {
    if (establishStreams()) {
      handleClientRequest();
      closeSocket();
    }

    System.out.println("Exiting the handler of the remote "
        + this.clientRemote.getRemoteSocketAddress());
  }

  /**
   * Handles the client's request.
   */
  private void handleClientRequest() {
    Command command;
    boolean shouldContinue;
    do {
      command = receiveClientCommand();
      shouldContinue = handleCommand(command);
    } while (shouldContinue);
  }

  /**
   * Handle one command from the client. If the command is unknown, it returns an error message.
   * If the TV is turned on, it will accept all commands. If the TV is turned off, it will only
   * accept the on command.
   *
   * @param command a command sent by the client.
   * @return {@code true} when the command is handled, {@code false} when an error
   * happened or the client must be shut down.
   */
  private boolean handleCommand(Command command) {
    boolean shouldContinue = true;
    System.out.println("Command from the client: " + command);
    String response = null;

    if (command == null) {
      shouldContinue = false;
    } else {
      response = switch (command) {
        case OnCommand onCommand when !this.tv.isOn() -> getOnResponse();
        case OffCommand offCommand when this.tv.isOn() -> getOffResponse();
        case VersionCommand versionCommand when this.tv.isOn() -> getVersionResponse();
        case GetChannelsCommand getChannelsCommand when this.tv.isOn() -> getGetChannelsResponse();
        case ChannelUpCommand channelUpCommand when this.tv.isOn() -> getChannelUpResponse();
        case ChannelDownCommand channelDownCommand when this.tv.isOn() -> getChannelDownResponse();
        case GetCurrentChannelCommand getCurrentChannelCommand when this.tv.isOn() ->
            getGetCurrentChannelResponse();
        case SetChannelCommand setChannelCommand when this.tv.isOn() ->
            getSetChannelResponse(setChannelCommand.getChannel());
        default -> handleUnknownCommand(command);
      };
    }

    if (response != null) {
      sendToRemote(response);
    }

    return shouldContinue;
  }

  /**
   * Turns the TV on if it is off. If it is already on, it returns a message saying so.
   *
   * @return the response of whether it is turning on or already is on to the client.
   */
  private String getOnResponse() {
    String response = "";
    if (this.tv.isOn()) {
      response = "TV is already on.";
    } else if (!this.tv.isOn()) {
      response = "TV is turning on!";
      this.tv.turnOn();
    }
    return response;
  }

  /**
   * Turns the TV off if it is on.
   *
   * @return a message to the client that the TV is turning off.
   */
  private String getOffResponse() {
    String response = "";
    if (this.tv.isOn()) {
      response = "TV is turning off!";
      this.tv.turnOff();
    }
    return response;
  }

  /**
   * Handles the version command.
   *
   * @return the version
   */
  private String getVersionResponse() {
    return "SmartTV_Version_0.1";
  }

  /**
   * Handles the get channels command.
   *
   * @return the available channels
   */
  private String getGetChannelsResponse() {
    String response = "Available channels: ";
    ArrayList<Integer> channels = this.tv.getAvailableChannels();
    Iterator<Integer> iterator = channels.iterator();

    while (iterator.hasNext()) {
      response += iterator.next();
      if (iterator.hasNext()) {
        response += ", ";
      }
    }

    return response;
  }

  /**
   * Handles the channel up command.
   *
   * @return the response of channel up.
   */
  private String getChannelUpResponse() {
    String response = "";
    try {
      this.tv.channelUp();
      response = "Channel up! Channel is now: " + this.tv.getCurrentChannel();
    } catch (IllegalArgumentException e) {
      response = "Error while channel up: " + e.getMessage();
    }
    return response;
  }

  /**
   * Handles the channel down command.
   *
   * @return the response of channel down.
   */
  private String getChannelDownResponse() {
    String response = "";
    try {
      this.tv.channelDown();
      response = "Channel down! Channel is now: " + this.tv.getCurrentChannel();
    } catch (IllegalArgumentException e) {
      response = "Error while channel down: " + e.getMessage();
    }
    return response;
  }

  /**
   * Handles the get current channel command.
   *
   * @return the current channel
   */
  private String getGetCurrentChannelResponse() {
    return "Current channel: " + this.tv.getCurrentChannel();
  }

  /**
   * Handles the set channel command.
   *
   * @return the response of setting the channel.
   */
  private String getSetChannelResponse(int channel) {
    String response = "";

    try {
      this.tv.setCurrentChannel(channel);
      response = "Channel set to " + channel;
    } catch (IllegalArgumentException e) {
      response = "Error while setting channel: " + e.getMessage();
    }

    return response;
  }

  /**
   * If an unknown command is received, this method is called.
   *
   * @return a message to the client that the command is unknown with some more information on what
   * could be wrong.
   */
  private String handleUnknownCommand(Command command) {
    String response;

    switch (command) {
      case OnCommand onCommand when this.tv.isOn() -> response = "TV is already on.";
      case OffCommand offCommand when !this.tv.isOn() ->
          response = "TV is off, write 'on' to turn on.";
      case VersionCommand versionCommand when !this.tv.isOn() ->
          response = "TV is off, write 'on' to turn on.";
      case GetChannelsCommand getChannelsCommand when !this.tv.isOn() ->
          response = "TV is off, write 'on' to turn on.";
      case GetCurrentChannelCommand getCurrentChannelCommand when !this.tv.isOn() ->
          response = "TV is off, write 'on' to turn on.";
      case SetChannelCommand setChannelCommand when !this.tv.isOn() ->
          response = "TV is off, write 'on' to turn on.";
      case ChannelUpCommand channelUpCommand when !this.tv.isOn() ->
          response = "TV is off, write 'on' to turn on.";
      case ChannelDownCommand channelDownCommand when !this.tv.isOn() ->
          response = "TV is off, write 'on' to turn on.";
      default -> response = "Unknown command: " + command;
    }

    return response;
  }

  /**
   * Establish the input and output of the socket.
   *
   * @return {@code true} if successful, and {@code false} if not.
   */
  private boolean establishStreams() {
    boolean success = false;
    try {
      this.objectReader = new ObjectInputStream(this.clientRemote.getInputStream());
      this.socketWriter = new PrintWriter(this.clientRemote.getOutputStream(), true);
      success = true;
    } catch (IOException e) {
      System.err.println("Error while processing the client (remote): " + e.getMessage());
    }
    return success;
  }

  /**
   * Receive one command (one line of text) from the client.
   *
   * @return the command, or {@code null} on socket error.
   */
  private Command receiveClientCommand() {
    Command command = null;
    try {
      command = (Command) this.objectReader.readObject();
    } catch (IOException e) {
      System.err.println("Error while receiving data from the client: " + e.getMessage());
    } catch (ClassNotFoundException e) {
      System.err.println("An object of invalid class received. " + e.getMessage());
    }
    return command;
  }

  /**
   * Sends message to the remote.
   *
   * @param message the message to send to tv remote.
   */
  private void sendToRemote(String message) {
    try {
      this.socketWriter.println(message);
    } catch (Exception e) {
      System.err.println("Error while sending a message to the client: " + e.getMessage());
    }
  }

  /**
   * Closes the socket.
   */
  private void closeSocket() {
    try {
      this.clientRemote.close();
    } catch (IOException e) {
      System.err.println("Error while closing socket for client "
          + this.clientRemote.getRemoteSocketAddress() + ", reason: " + e.getMessage());
    }
  }
}
