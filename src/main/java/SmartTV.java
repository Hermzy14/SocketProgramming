import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * A TCP server.
 * <p>Turned off by default, and can <b>ONLY</b> be turned on when off.</p>
 * <p>Can report whether on or off.</p>
 * <p>When turned on it should be able to send the number of available channels,
 * get the currently active channel (channel 1 is default), and set channel to desired number c.</p>
 */
public class SmartTV {
  public static final int TCP_PORT = 1238;
  // in future maybe user should be able to choose between different TVs and therefore choose different TCP ports??
  private boolean running;
  private boolean on;
  private ServerSocket serverSocket;
  private int currentChannel;
  private int channels;

  /**
   * Main method for SmartTV. Starts the TV when called by TVRemote.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    SmartTV tv = new SmartTV();
    tv.setAvailableChannels(10);
    tv.setCurrentChannel(1);
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
        TVRemoteHandler remoteHandler = new TVRemoteHandler(this, client);
        remoteHandler.run();
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
   * Sets the number of available channels.
   *
   * @param availableChannels the number of available channels.
   */
  private void setAvailableChannels(int availableChannels) {
    this.channels = availableChannels;
  }

  /**
   * Returns the current active channel.
   *
   * @return the current active channel.
   */
  public int getCurrentChannel() {
    return this.currentChannel;
  }

  /**
   * Sets the channel to the desired number.
   *
   * @param channelNumber the desired channel number.
   */
  public void setCurrentChannel(int channelNumber) {
    if (channelNumber < 1) {
      throw new IllegalArgumentException("Channel number must be at least 1.");
    } else if (channelNumber > this.channels) {
      throw new IllegalArgumentException("Channel number must be at most " + this.channels + ".");
    } else {
      this.currentChannel = channelNumber;
    }
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
  public void turnOn() {
    this.on = true;
  }

  /**
   * Turns off the TV.
   */
  public void turnOff() {
    this.on = false;
    //this.running = false; // turn off the TV completely? TODO: check if this is the desired behavior
  }

  /**
   * Returns all available channels.
   *
   * @return all available channels.
   */
  public ArrayList<Integer> getAvailableChannels() {
    ArrayList<Integer> availableChannels = new ArrayList<>();
    for (int i = 1; i <= this.channels; i++) {
      availableChannels.add(i);
    }
    return availableChannels;
  }

  /**
   * Switches the channel up.
   */
  public void channelUp() {
    if (this.currentChannel < this.channels) {
      this.currentChannel++;
    } else {
      throw new IllegalArgumentException("Cannot switch channel up, already at highest channel.");
    }
  }

  /**
   * Switches the channel down.
   */
  public void channelDown() {
    if (this.currentChannel > 1) {
      this.currentChannel--;
    } else {
      throw new IllegalArgumentException("Cannot switch channel down, already at lowest channel.");
    }
  }
}
