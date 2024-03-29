package dbs;

public class Configuration {

  // Which protocol version does this peer use?
  public static String version = "1.0";

  // Set this to true if the version is not 1.0
  public static boolean enhancedPeer = false;

  // Folder where each peer root directory is stored. Please use an absolute path.
  public static String allPeersRootDir = "/tmp/dbs";

  // Peer's root directory.
  public static String peerRootDirPrefix = "peer-";

  // Backup subdirectory, where chunks kept by this peer are kept.
  // Note: This might be inlined somewhere already
  public static String backupDir = "backup";

  // Backup entry prefix
  public static String entryPrefix = "file-";

  // Chunk file prefix.
  public static String chunkPrefix = "chunk-";

  // Restore subdirectory, where files restored are kept.
  public static String restoredDir = "restored";

  public static String filesinfoDir = "filesinfo";

  // My file info (desired replication degree, set of peers which have a backup of those
  // files' chunks, pathname and chunk count)
  public static String ownFilesinfo = "ownfiles-metadata";

  // Others' files info (desired replication degree and set of peers which have a
  // backup of those files)
  public static String otherFilesinfo = "otherfiles-metadata";

  // Multicaster's timeout for reading from multicast socket
  public static int multicastTimeout = 300; // milliseconds

  // Peer socket's timeout for waiting on new queue message
  public static int socketTimeout = 300; // milliseconds

  // Peer socket's message queue capacity, (in datagram packets)
  public static int socketQueueCapacity = 10000;

  // Thread pool sizes (core pool sizes)
  public static int peerThreadPoolSize = 8;
  public static int putchunkPoolSize = 8;
  public static int storedPoolSize = 20;
  public static int chunkPoolSize = 30;
  public static int getchunkPoolSize = 8;
  public static int restorerPoolSize = 5;
  public static int waiterPoolSize = 25;
  public static int removerPoolSize = 20;

  // Maximum number of allowed PUTCHUNKs for each chunk before the backup gives up
  public static int maxPutchunkAttempts = 5;

  // Maximum number of allowed GETCHUNKs for each chunk before the restore gives up
  public static int maxGetchunkAttempts = 5;

  // Maximum storage capacity for chunks.
  public static volatile long storageCapacityKB = 10000; // KB ?
}
