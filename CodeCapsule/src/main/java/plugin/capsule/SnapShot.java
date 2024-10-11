package plugin.capsule;
//快照

import java.io.Serializable;
import java.util.Date;

public class SnapShot implements Serializable {
    private String fileName;
    private String content;
    private Date timestamp;

    public SnapShot(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
        this.timestamp = new Date();  // 记录保存时间
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
