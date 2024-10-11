package plugin.capsule;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckVersionSave {

    private static final String HASH_ALGORITHM = "SHA-256";

    // 主方法：接收路径列表并检查是否需要保存为新版本
    //baseDir是VersionHistory的Path
    public boolean checkVersionSave(List<String> paths, String baseDir) throws IOException, NoSuchAlgorithmException {
        // 获取上一个版本的文件夹
        File lastVersionDir = getLastVersionDirectory(baseDir);
        if (lastVersionDir == null) {
            // 创建第一个版本的文件夹
            File version1Dir = new File(baseDir, "Version1");
            version1Dir.mkdirs();
            // 获取项目的根目录
            File projectRootDir = new File(baseDir).getParentFile();  // 假设项目根目录在 baseDir 的上一级
            // 将项目文件拷贝到 Version1 文件夹
            copyDirectory(projectRootDir, version1Dir);
            // 生成项目结构并计算哈希值
            ProjectStructure initialStructure = new ProjectStructure(1);
            generateProjectStructure(projectRootDir, initialStructure, 1); // 生成项目结构
            // 保存当前版本结构为 JSON 文件
            saveCurrentVersionStructure(initialStructure, baseDir);
            System.out.println("Version1 created and project files copied successfully.");
            return true; // 初始版本已创建
        }


        // 读取上一个版本的JSON文件
        File jsonFile = new File(lastVersionDir, "S.json");
        ObjectMapper objectMapper = new ObjectMapper();
        ProjectStructure previousVersionStructure = objectMapper.readValue(jsonFile, ProjectStructure.class);

        // 创建当前版本的目录结构
        ProjectStructure currentVersionStructure = new ProjectStructure(1);
        currentVersionStructure.setVersion(previousVersionStructure.getVersion() + 1);

        // 先拷贝上个版本的对象到当前版本对象中
        copyPreviousVersionStructure(previousVersionStructure, currentVersionStructure);

        // 遍历文件路径列表，检查哈希值
        boolean hasChanges = false;
        for (String filePath : paths) {
            File file = new File(filePath);
            hasChanges |= checkAndCompareFile(file, previousVersionStructure.getFiles(), currentVersionStructure.getFiles(), currentVersionStructure.getVersion());
        }

        // 如果有变化，保存当前版本的结构
        if (hasChanges) {
            saveCurrentVersionStructure(currentVersionStructure, baseDir);
        }

        return hasChanges;
    }

    // 获取最新的版本目录
    private File getLastVersionDirectory(String baseDir) {
        File baseDirectory = new File(baseDir);
        File[] versionDirs = baseDirectory.listFiles(File::isDirectory);
        File lastVersionDir = null;
        int maxVersion = 0;

        if (versionDirs != null) {
            for (File dir : versionDirs) {
                String dirName = dir.getName();
                if (dirName.startsWith("VERSION")) {
                    int version = Integer.parseInt(dirName.replace("VERSION", ""));
                    if (version > maxVersion) {
                        maxVersion = version;
                        lastVersionDir = dir;
                    }
                }
            }
        }
        return lastVersionDir;
    }

    // 递归检查文件或目录，比较哈希值并更新当前结构
    private boolean checkAndCompareFile(File file, Map<String, FileNode> previousFiles, Map<String, FileNode> currentFiles, int currentVersion) throws NoSuchAlgorithmException, IOException {
        String fileName = file.getName();
        boolean hasChanges = false;

        if (file.isDirectory()) {
            FileNode previousDir = previousFiles.get(fileName);
            FileNode currentDir = new FileNode("directory");
            currentFiles.put(fileName, currentDir);

            if (previousDir != null && "directory".equals(previousDir.getType())) {
                for (File subFile : file.listFiles()) {
                    hasChanges |= checkAndCompareFile(subFile, previousDir.getChildren(), currentDir.getChildren(), currentVersion);
                }
            } else {
                hasChanges = true;
                for (File subFile : file.listFiles()) {
                    hasChanges |= checkAndCompareFile(subFile, new HashMap<>(), currentDir.getChildren(), currentVersion);
                }
            }
        } else {
            String currentHash = calculateFileHash(file.getPath());
            FileNode previousFile = previousFiles.get(fileName);
            if (previousFile != null && "file".equals(previousFile.getType()) && previousFile.getHash().equals(currentHash)) {
                currentFiles.put(fileName, new FileNode("file", currentHash, previousFile.getLastModifiedVersion()));
            } else {
                currentFiles.put(fileName, new FileNode("file", currentHash, currentVersion));
                hasChanges = true;
            }
        }

        return hasChanges;
    }

    // 计算文件的SHA-256哈希值
    private String calculateFileHash(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        byte[] hashBytes = digest.digest(fileBytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // 拷贝上一个版本的目录结构
    private void copyPreviousVersionStructure(ProjectStructure previousVersion, ProjectStructure currentVersion) {
        currentVersion.setFiles(new HashMap<>(previousVersion.getFiles()));
        currentVersion.setVersion(previousVersion.getVersion() + 1);
    }

    // 递归生成项目目录结构
    private void generateProjectStructure(File file, ProjectStructure structure, int currentVersion) throws IOException, NoSuchAlgorithmException {
        if (file.isDirectory()) {
            FileNode dirNode = new FileNode("directory");
            structure.getFiles().put(file.getName(), dirNode);

            for (File subFile : file.listFiles()) {
                generateProjectStructure(subFile, structure, currentVersion);
            }
        } else {
            String fileHash = calculateFileHash(file.getPath());
            structure.getFiles().put(file.getName(), new FileNode("file", fileHash, currentVersion));
        }
    }

    // 保存当前版本的目录结构
    private void saveCurrentVersionStructure(ProjectStructure currentStructure, String baseDir) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String newVersionDirName = "VERSION" + currentStructure.getVersion();
        File newVersionDir = new File(baseDir, newVersionDirName);
        if (!newVersionDir.exists()) {
            newVersionDir.mkdir();
        }

        // 保存新的JSON文件
        File jsonFile = new File(newVersionDir, "S.json");
        objectMapper.writeValue(jsonFile, currentStructure);
    }
}


