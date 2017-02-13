package cm.aptoide.pt.shareapps.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AptoideSocket {

  protected final ExecutorService executorService;
  protected final int bufferSize;

  public AptoideSocket() {
    this(Executors.newCachedThreadPool(), 8192);
  }

  public AptoideSocket(ExecutorService executorService, int bufferSize) {
    this.executorService = executorService;
    this.bufferSize = bufferSize;
  }

  public AptoideSocket(int bufferSize) {
    this(Executors.newCachedThreadPool(), bufferSize);
  }

  public AptoideSocket(ExecutorService executorService) {
    this(executorService, 8192);
  }

  public AptoideSocket startAsync() {
    executorService.execute(this::start);
    return this;
  }

  public abstract AptoideSocket start();

  public void shutdownExecutorService() {
    executorService.shutdown();
  }

  public void forceShutdownExecutorService() {
    executorService.shutdownNow();
  }

  protected void copy(InputStream in, OutputStream out) throws IOException {
    copy(in, out, Long.MAX_VALUE);
  }

  protected void copy(InputStream in, OutputStream out, long len) throws IOException {
    byte[] buf = new byte[bufferSize];

    int totalBytesRead = 0;
    int bytesRead;
    while ((totalBytesRead) != len
        && (bytesRead = in.read(buf, 0, (int) Math.min(buf.length, len - totalBytesRead))) != -1) {
      out.write(buf, 0, bytesRead);

      totalBytesRead += bytesRead;
    }
  }
}