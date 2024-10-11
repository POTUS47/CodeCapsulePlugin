package plugin.capsule;

import java.util.HashMap;
import java.util.Map;

// 项目结构类，表示项目的根目录结构
public class ProjectStructure {
    private int version;
    private Map<String, FileNode> files;
    //构造函数
    public ProjectStructure(int version) {
        this.version = version;
        this.files = new HashMap<>();
    }
    //构造函数
    public ProjectStructure() {
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<String, FileNode> getFiles() {
        return files;
    }

    public void setFiles(Map<String, FileNode> files) {
        this.files = files;
    }
}

// 文件或目录节点类
class FileNode {
    private String type;  // "file" or "directory"
    private String hash;  // 文件的哈希值
    private int lastModifiedVersion;  // 文件的最后修改版本号
    private Map<String, FileNode> children;  // 子目录或文件

    // 构造函数：目录
    public FileNode(String type) {
        this.type = type;
        this.children = new HashMap<>();
    }

    // 构造函数：文件
    public FileNode(String type, String hash, int lastModifiedVersion) {
        this.type = type;
        this.hash = hash;
        this.lastModifiedVersion = lastModifiedVersion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getLastModifiedVersion() {
        return lastModifiedVersion;
    }

    public void setLastModifiedVersion(int lastModifiedVersion) {
        this.lastModifiedVersion = lastModifiedVersion;
    }

    public Map<String, FileNode> getChildren() {
        return children;
    }

    public void setChildren(Map<String, FileNode> children) {
        this.children = children;
    }
}
