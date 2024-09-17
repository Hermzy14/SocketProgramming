package command;

/**
 * Command to set the channel of the TV.
 */
public class SetChannelCommand extends Command{
    private int channel;

    /**
     * Constructor for SetChannelCommand.
     *
     * @param channel the channel to set.
     */
    public SetChannelCommand(int channel) {
        this.channel = channel;
    }

    /**
     * Returns the channel to set.
     *
     * @return the channel to set.
     */
    public int getChannel() {
        return this.channel;
    }
}
