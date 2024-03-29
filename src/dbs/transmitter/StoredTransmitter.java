package dbs.transmitter;

import dbs.ChunkKey;
import dbs.Peer;
import dbs.Protocol;
import dbs.Utils;
import dbs.files.FileInfoManager;
import dbs.message.Message;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class StoredTransmitter implements Runnable {

  private final ChunkKey key;
  private final Message message;
  private Future scheduled;

  StoredTransmitter(ChunkKey key) {
    this.key = key;
    this.message = Message.STORED(key.getFileId(), key.getChunkNo());

    int wait = Utils.getRandom(Protocol.minDelay, Protocol.maxDelay);
    this.scheduled = BackupHandler.getInstance().storedPool.schedule(this, wait,
        TimeUnit.MILLISECONDS);
  }

  @Override
  public void run() {
    if (FileInfoManager.getInstance().hasChunk(key.getFileId(), key.getChunkNo()))
      Peer.getInstance().send(message);
    BackupHandler.getInstance().storers.remove(key);
  }
}
