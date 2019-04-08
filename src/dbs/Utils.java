package dbs;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Utils {
  private final static Logger LOGGER = Logger.getLogger(Utils.class.getName());

  public static String hash(@NotNull File file, long peerId) throws Exception {
    String filePath = file.getPath();
    long lastModified = file.lastModified();
    String bitString = filePath + lastModified;
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] encodedHash;
    try {
      encodedHash = digest.digest(bitString.getBytes());
    } catch (Exception e) {
      LOGGER.severe("Could not execute hash function using the bit string '" + bitString + "'\n");
      return null;
    }
    BigInteger hash = new BigInteger(1, encodedHash);

    String hashtext = hash.toString(16);

    while (hashtext.length() < 32) {
      hashtext = "0" + hashtext;
    }

    return hashtext;
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

  public static void waitRandom(int min, int max, TimeUnit timeUnit) throws InterruptedException {
    Random rand = new Random();
    int delay = rand.nextInt(max) + min;
    timeUnit.sleep(1);
  }
}