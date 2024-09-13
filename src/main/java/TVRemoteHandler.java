import command.Command;
import command.OffCommand;
import command.OnCommand;
import command.VersionCommand;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

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
   * Handle one command from the client.
   *
   * @param command a command sent by the client.
   * @return {@code true} when the command is handled, {@code false} when an error
   * happened or the client must be shut down.
   */
  private boolean handleCommand(Command command) {
    boolean shouldContinue = true;
    System.out.println("Command from the client: " + command);
    String response = null;

    if (command == null) { // TODO - gjøre om til switch-case??
      shouldContinue = false;
    } else {
      if (command instanceof VersionCommand) {
        response = getVersionResponse();
      } else if (command instanceof OnCommand) {
        response = getOnResponse();
      } else if (command instanceof OffCommand) {
        response = getOffResponse();
      } else { // TODO - implementer alle funksjoner
        response = "Unknown command";
      }
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
      this.tv.setOn();
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
      this.tv.setOff();
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
