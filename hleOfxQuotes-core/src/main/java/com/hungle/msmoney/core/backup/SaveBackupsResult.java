package com.hungle.msmoney.core.backup;

public class SaveBackupsResult {
    private long elapsed;
    
    private int count = 0;
    
    private int copiedCount = 0;

    @Override
    public String toString() {
        return "SaveBackupsResult [elapsed=" + elapsed + ", count=" + count + ", copiedCount=" + copiedCount + "]";
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long ended) {
        this.elapsed = ended;
    }

    public void incCopiedCount() {
        copiedCount++;

    }

    public int getCopiedCount() {
        return copiedCount;
    }

    public void setCopiedCount(int copiedCount) {
        this.copiedCount = copiedCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void intCount() {
        count++;
    }

}
