package dbs;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;

public class MulticastChannel {

  private final InetAddress address;
  private final int port;

  public MulticastChannel(@NotNull InetAddress address, int port) {
    this.address = address;
    this.port = port;
  }

  MulticastChannel(@NotNull String address, @NotNull String port) throws IOException {
    // parse address
    this.address = InetAddress.getByName(address);

    if (!this.address.isMulticastAddress()) {
      throw new IOException("Given address is not a multicast address: " + address);
    }

    // parse port
    try {
      this.port = Integer.parseInt(port);
      if (this.port < 0) throw new NumberFormatException();
    } catch (NumberFormatException e) {
      throw new NumberFormatException("Invalid port: " + port);
    }
  }

  InetAddress getAddress() {
    return address;
  }

  int getPort() {
    return port;
  }
}
