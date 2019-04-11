package dbs;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.util.Random;

public class Utils {

  private static String bytesToHex(byte[] hash) {
    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < hash.length; i++) {
      String hex = Integer.toHexString(0xff & hash[i]);
      if (hex.length() == 1) hexString.append('0');
      hexString.append(hex);
    }
    return hexString.toString();
  }

  public static String hash(@NotNull File file, long peerId) throws Exception {
    String filePath = file.getPath();
    long lastModified = file.lastModified();
    String bitString = filePath + lastModified;
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] encodedHash = digest.digest(bitString.getBytes());
    return bytesToHex(encodedHash);
  }

  public static int numberOfChunks(long filesize) {
    return (int) ((filesize + Protocol.chunkSize - 1) / Protocol.chunkSize);
  }

  public static Registry registry() {
    Registry registry;

    try {
      // Try to get the already open registry.
      registry = LocateRegistry.getRegistry();
    } catch (RemoteException e1) {
      try {
        // Race: There is no open registry. Create one.
        // TODO: where to find selected port?
        registry = LocateRegistry.createRegistry(Protocol.registryPort);
      } catch (RemoteException e2) {
        // Lost race: someone created a registry in the meanwhile.
        try {
          registry = LocateRegistry.getRegistry();
        } catch (RemoteException e3) {
          // Something very bad happened.
          throw new Error(e1); // throw the first error
        }
      }
    }

    return registry;
  }

  public static int getRandom(int min, int max) {
    Random rand = new Random(); // TODO: make static
    return rand.nextInt(max - min + 1) + min;
  }
}