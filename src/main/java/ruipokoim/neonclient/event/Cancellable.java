package ruipokoim.neonclient.event;
public class Cancellable {
    private boolean cancelled = false;
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    public void cancel() { setCancelled(true); }
}