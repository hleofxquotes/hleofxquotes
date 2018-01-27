package com.hungle.msmoney.core.backup;

import java.io.File;

public interface SaveBackupsListener {

    void notifyCopyFile(File fromFile, File toFile, String password);

    void notifyStartBackup();

    void notifyStartCopyFile(File file, String dirName, int size);

    void notifyDoneCopyFile(boolean copied, File file, String dirName, int size);

    void notifyDoneBackup();

}
