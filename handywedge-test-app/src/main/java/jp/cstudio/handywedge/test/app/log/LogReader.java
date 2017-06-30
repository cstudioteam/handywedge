package jp.cstudio.handywedge.test.app.log;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Set;

import javax.websocket.Session;

public abstract class LogReader implements Runnable {

  private int crunchifyRunEveryNSeconds = 1000;
  private long lastKnownPosition = 0;
  private boolean shouldIRun = true;
  private File crunchifyFile = null;


  public LogReader(String myFile, int myInterval) {
    crunchifyFile = new File(myFile);
    this.crunchifyRunEveryNSeconds = myInterval;
  }

  @Override
  public void run() {

    try {
      while (shouldIRun) {
        Thread.sleep(crunchifyRunEveryNSeconds);
        long fileLength = crunchifyFile.length();
        if (fileLength > lastKnownPosition) {

          // Reading and writing file
          RandomAccessFile readWriteFileAccess = new RandomAccessFile(crunchifyFile, "rw");
          readWriteFileAccess.seek(lastKnownPosition);
          String crunchifyLine = null;
          while ((crunchifyLine = readWriteFileAccess.readLine()) != null) {
            for (Session s : AppLogView.sessions) {
              // s.getAsyncRemote().sendText(crunchifyLine);
              s.getBasicRemote().sendText(crunchifyLine);

            }
          }
          lastKnownPosition = readWriteFileAccess.getFilePointer();
          readWriteFileAccess.close();
        }
      }
    } catch (Exception e) {
      shouldIRun = false;
    }
  }

  public abstract Set<Session> getSession();

}
