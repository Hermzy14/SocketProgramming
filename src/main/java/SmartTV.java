import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A TCP server.
 * <p>Turned off by default, and can <b>ONLY</b> be turned on when off.</p>
 * <p>Can report whether on or off.</p>
 * <p>When turned on it should be able to send the number of available channels,
 * get the currently active channel (channel 1 is default), and set channel to desired number c.</p>
 *
 */
public class SmartTV {
  public static final int TCP_PORT = 1238; // in future maybe user should be able to choose between different TVs and therefore choose different TCP ports??
  private boolean running;
  private boolean on;
  private ServerSocket serverSocket;

  /**
   * Main method for SmartTV. Starts the TV when called by TVRemote.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    SmartTV tv = new SmartTV();
    tv.run();
  }

  /**
   * Run loop for SmartTV.
   */
  private void run() {
    if (openListeningSocket()) {
      this.running = true;
      while (this.running) {
        Socket client = acceptNextClient();
        TVRemoteHandler controllerHandler = new TVRemoteHandler(this, client);
        controllerHandler.run();
      }
    }
    System.out.println("TV turning off...");
  }

  /**
   * Open a listening TCP socket.
   *
   * @return {@code true} on success, {@code false} on error.
   */
  private boolean openListeningSocket() {
    boolean success = false;
    try {
      this.serverSocket = new ServerSocket(this.TCP_PORT);
      success = true;
    } catch (IOException e) {
      System.err.println("Could not open a listening socket on port " + TCP_PORT
          + ", reason: " + e.getMessage());
    }
    return success;
  }

  /**
   * Accepts the next client and returns the socket.
   *
   * @return the socket of the client.
   */
  private Socket acceptNextClient() {
    Socket clientSocket = null;
    try {
      clientSocket = this.serverSocket.accept();
    } catch (IOException e) {
      System.err.println("Could not accept the next client: " + e.getMessage());
    }
    return clientSocket;
  }

  /**
   * Returns whether TV is on or not.
   *
   * @return {@code true} if TV is on, {@code false} if TV is off.
   */
  public boolean isOn() {
    return this.on;
  }

  /**
   * Turns on the TV.
   */
  public void setOn() {
    this.on = true;
  }

  /**
   * Turns off the TV.
   */
  public void setOff() {
    this.on = false;
    this.running = false; // turn off the TV completely? TODO: check if this is the desired behavior
  }
}
