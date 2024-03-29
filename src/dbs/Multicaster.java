package dbs;

import java.io.IOError;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

public final class Multicaster implements Runnable {

  public interface Processor {
    Runnable runnable(DatagramPacket packet);
  }

  private MulticastSocket socket;
  private final Processor processor;
  private boolean finished = false;
  private final MulticastChannel multicastChannel;

  /**
   * Die regularly by leaving the Multicast group and then closing the socket normally.
   * Idempotent operation.
   */
  private void die() {
    if (socket == null) return;
    finished = true;

    try {  // throws iff constructor throws, so this never throws.
      socket.leaveGroup(multicastChannel.getAddress());
      socket.close();
      socket = null;
    } catch (IOException e) {
      socket.close();
      socket = null;
    }
  }

  /**
   * Receive a packet from the multicast network.
   * If there is a reading timeout, it retries automatically until finished.
   *
   * @return The datagram packet read.
   */
  private DatagramPacket receive() {
    // TODO: Check if there is a problem here. This is a slight memory optimization.
    byte[] buffer = new byte[Protocol.maxPacketSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

    try {
      socket.receive(packet);
      return packet;
    } catch (SocketTimeoutException e) {
      return null;
    } catch (IOException e) {
      if (socket.isClosed()) {
        throw new IOError(e);
      }
      System.err.println(e.getMessage());
      e.printStackTrace(System.err);
      return null;
    }
  }

  /**
   * Create a Multicaster for a given multicast group and port, with a message
   * processor chosen by the controlling peer.
   *
   * @param multicastChannel The Protocol Channel this Multicaster polls
   * @param processor        The processor of this Multicaster
   * @throws IOException If there is a problem setting up the multicast network, e.g.
   *                     invalid multicast address, port or timeout.
   */
  public Multicaster(MulticastChannel multicastChannel,
                     Multicaster.Processor processor) throws IOException {
    this.multicastChannel = multicastChannel;
    this.processor = processor;
    try {
      this.socket = new MulticastSocket(multicastChannel.getPort());
      this.socket.joinGroup(multicastChannel.getAddress());
      this.socket.setSoTimeout(Configuration.multicastTimeout);
      this.socket.setTimeToLive(1);
    } catch (IOException e) {
      Peer.log("Could not create socket", e, Level.SEVERE);
      throw e;
    }
  }

  public final Processor processor() {
    return this.processor;
  }

  final void finish() {
    this.finished = true;
  }

  /**
   * Thread pool task. Receives packets from the multicast socket and forwards them to
   * threads in the peer's thread pool to parse and handle.
   * Does nothing if called once finished.
   */
  @Override
  public void run() {
    DatagramPacket packet;

    while (!finished) {
      packet = receive();
      if (packet == null) continue;

      Peer.getInstance().getPool().submit(processor.runnable(packet));
    }

    die();
  }
}