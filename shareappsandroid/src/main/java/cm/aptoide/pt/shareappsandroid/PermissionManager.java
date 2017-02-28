package cm.aptoide.pt.shareappsandroid;

/**
 * Created by filipe on 23-02-2017.
 */

public interface PermissionManager {

  boolean checkPermissions();

  void requestPermissions();

  void registerListener(PermissionListener listener);

  void removeListener();
}