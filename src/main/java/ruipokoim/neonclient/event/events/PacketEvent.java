package ruipokoim.neonclient.event.events;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import ruipokoim.neonclient.event.Cancellable;

public class PacketEvent {
    public static class Receive extends Cancellable {
        public Packet<?> packet;
        public ClientConnection connection;

        public Receive(Packet<?> packet, ClientConnection connection) {
            this.setCancelled(false);
            this.packet = packet;
            this.connection = connection;
        }
    }
    public static class Send extends Cancellable {
        public Packet<?> packet;
        public ClientConnection connection;

        public Send(Packet<?> packet, ClientConnection connection) {
            this.setCancelled(false);
            this.packet = packet;
            this.connection = connection;
        }
        public void sendSilently(Packet<?> packet) {
            connection.send(packet, null, true);
        }
    }
    public static class Sent {
        public Packet<?> packet;
        public ClientConnection connection;

        public Sent(Packet<?> packet, ClientConnection connection) {
            this.packet = packet;
            this.connection = connection;
        }
        public void sendSilently(Packet<?> packet) {
            connection.send(packet, null, true);
        }
    }
}
