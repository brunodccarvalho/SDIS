package dbs.transmitter;

import dbs.Configuration;
import dbs.Peer;
import dbs.fileInfoManager.FileInfoManager;
import dbs.files.FileRequest;
import dbs.files.FilesManager;
import dbs.message.Message;

import java.io.File;
import java.util.logging.Level;

public class DeleteTransmitter implements Runnable {

  private String pathname;
  private String fileId;

  public DeleteTransmitter(String pathname) {
    this.pathname = pathname;
  }


  @Override
  public void run() {

    // check if the given pathname is valid
    File fileToDelete;
    FileRequest fileRequest = FilesManager.retrieveFileInfo(pathname, Peer.getInstance().getId());
    if(fileRequest == null) {
      Peer.log("Could not access the provided file", Level.SEVERE);
      return;
    }
    else {
      fileToDelete = fileRequest.getFile();
      this.fileId = fileRequest.getFileId();
    }

    // delete file
    if(!FileInfoManager.getInstance().deleteFile(fileToDelete, this.fileId)) {
      Peer.log("Could not perform the deletion of the file " + this.pathname, Level.SEVERE);
      return;
    }

    // send DELETE message
    Peer.getInstance().send(Message.DELETE(this.fileId, Configuration.version));
    Peer.log("sent message delete", Level.INFO);
  }
}
