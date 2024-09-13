import command.Command;
import command.OffCommand;
import command.OnCommand;
import command.VersionCommand;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This is the TCP client for the SmartTV.
 * <p>Using different buttons, the user should be able to turn TV on and off, and switch one
 * channel up and down.</p>
 * <p>With a display the user should be able to see the current channel the TV is
 * connected to.</p>
 */
public class TVRemote {
  private static final String SERVER_HOST = "localhost";
      // usikker på hva denne egt skal være så har den bare som localhost enn så lenge
  private Socket socket;
  private BufferedReader reader;
  private PrintWriter writer;
  private ObjectOutputStream objectWriter;

  /**
   * Runs the controller/remote client.
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    TVRemote remote = new TVRemote();
    remote.run();
  }

  /**
   * Runs the tv remote. User can turn TV on and off, and switch one channel up and down through
   * a command-line based UI.
   */
  private void run() {
    if (connect()) {
      TVRemoteUi ui = new TVRemoteUi(this);
      ui.run();
    }
    System.out.println("Exiting...");
  }

  /**
   * Establish a connection to a TCP server (SmartTV).
   *
   * @return {@code ture} on success, {@code false} on error.
   */
  private boolean connect() {
    boolean result = false;
    try {
      this.socket = new Socket(SERVER_HOST, SmartTV.TCP_PORT);
      this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      this.writer = new PrintWriter(this.socket.getOutputStream(), true);
      this.objectWriter = new ObjectOutputStream((this.socket.getOutputStream()));
      System.out.println("Connection established!");
      result = true;
    } catch (IOException e) {
      System.err.println("Error on connection: " + e.getMessage());
    }
    return result;
  }

  /**
   * Sends and receives command.
   *
   * @param command command received.
   */
  public void sendAndReceive(Command command) {
    if (sendToTv(command)) {
      String response = receiveOneLineFromTv();
      if (response != null) {
        System.out.println("TV's response: " + response);
      }
    }
  }

  /**
   * Sends a message to the SmartTV (TCP server).
   * <p>We assume here that connection is already established.</p>
   *
   * @param message the message to send.
   * @return {@code true} when the message is successfully sent, {@code false} on error.
   */
  private boolean sendToTv(Command message) {
    boolean success = false;
    try {
      this.objectWriter.writeObject(message);
      success = true;
    } catch (Exception e) {
      System.err.println("Error while sending the message: " + e.getMessage());
    }
    return success;
  }

  /**
   * Receives one line of text from the SmartTV.
   *
   * @return the received line or {@code null} on error
   */
  private String receiveOneLineFromTv() {
    String response = null;
    try {
      response = this.reader.readLine();
    } catch (IOException e) {
      System.err.println("Error while receiving data from the SmartTV: " + e.getMessage());
    }
    return response;
  }
}
