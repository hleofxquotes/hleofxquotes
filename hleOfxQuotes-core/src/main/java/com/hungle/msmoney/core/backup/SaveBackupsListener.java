package com.hungle.msmoney.core.backup;

import java.io.File;

public interface SaveBackupsListener {

    void notifyStartBackup();

    void notifyStartCopyFile(File file, File dir, int size);

    void notifyCopyFile(File fromFile, File toFile, String password);

    void notifyDoneCopyFile(File file, File dir, int size, boolean copied);

    void notifyDoneBackup();

}
