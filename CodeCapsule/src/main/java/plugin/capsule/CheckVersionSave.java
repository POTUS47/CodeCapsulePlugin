package plugin.capsule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

//使用说明：15s到达时，仅需调用此类的构造函数：checkVersionSave(List<Path> paths, String baseDir)
public class CheckVersionSave {

    private static final String HASH_ALGORITHM = "SHA-256";
    private  File currentVersionDir ;
    // 主方法：接收路径列表并检查是否需要保存为新版本(需修改：V1的txt创建，json识别失败)
    //baseDir是VersionHistory的Path（包括VersionHistory）
    public boolean checkVersionSave(List<Path> paths, String baseDir) throws IOException, NoSuchAlgorithmException {
        System.out.println("Checking version save file");
        // 获取上一个版本的文件夹
        File lastVersionDir = getLastVersionDirectory(baseDir);
        if (lastVersionDir == null) {
            System.out.println("无第一个版本，开始生成第一个版本！");/////////////////////////////
            // 创建第一个版本的文件夹
            File version1Dir = new File(baseDir, "Version1");//使用父目录和子路径名创建一个 File 对象,即在VersionHistory下创建Version1
            version1Dir.mkdirs();//递归地创建所有必要的目录
            // 获取项目的根目录
            File projectRootDir = new File(baseDir).getParentFile();  //项目根目录在 baseDir 的上级
            System.out.println("获取到项目的根目录："+projectRootDir.getAbsolutePath());/////////////////////////////
            // 将项目文件拷贝到 Version1 文件夹
            copyDirectory(projectRootDir, version1Dir);
            // 生成项目结构并计算哈希值
            ProjectStructure initialStructure = new ProjectStructure(1);
            // 递归生成项目结构，从项目根目录下的内容开始
            generateProjectStructure(projectRootDir, initialStructure.getFiles(), 1);
            // 保存当前版本结构为 JSON 文件
            saveCurrentVersionStructure(initialStructure, baseDir);
            //创建描述文件
            File descriptionFile = new File(version1Dir, "version_info.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(descriptionFile))) {
                writer.write("Version1");  // 写版本名称
                writer.newLine();
                writer.write("无描述");  // 写描述
                writer.newLine();
                writer.write("创建时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));  // 写创建时间
            }
            System.out.println("Version1 created and project files copied successfully.");
            return true; // 初始版本已创建
        }
        System.out.println("已经有版本一，开始读取上个版本的json！");/////////////////////////////
        // 读取上一个版本的JSON文件
        File jsonFile = new File(lastVersionDir, "Structure.json");
        ProjectStructure previousVersionStructure = new ProjectStructure();
        // 手动解析 rootNode 构建 ProjectStructure 对象
        JsonConvertToProjectStructure(jsonFile,previousVersionStructure);
        System.out.println("Parsed ProjectStructure: " + previousVersionStructure);
        // 创建当前版本的目录结构
        ProjectStructure currentVersionStructure = new ProjectStructure();
        System.out.println("已经新建版本的目录结构！");/////////////////////////////
        // 先拷贝上个版本的对象到当前版本对象中
        copyPreviousVersionStructure(previousVersionStructure, currentVersionStructure);
        System.out.println("已经拷贝了旧版本的目录结构！");/////////////////////////////
        System.out.println("当前版本的号为："+currentVersionStructure.getVersion());/////
        // 在VersionHistory下创建相应版本的文件夹
        String newVersionDirName = "Version" + currentVersionStructure.getVersion();
        System.out.println(newVersionDirName+"文件夹创建成功！");/////////////////////////////
        File changesDir = new File(baseDir, newVersionDirName);
        if (!changesDir.exists()) {
            changesDir.mkdir(); // 创建目录
            this.currentVersionDir = changesDir;
        }
        // 遍历文件路径列表，检查哈希值
        boolean hasChanges = false;
        for (Path filePath : paths) {
            System.out.println("正在检测："+filePath);///////////////////////////
            Path projectRootPath = StartUp.getProjectRootPath();
            if (projectRootPath == null) {
                throw new IllegalStateException("项目根路径未设置");
            }
            Path absolutePath = projectRootPath.resolve(filePath).normalize();// 将相对路径转换为绝对路径
            System.out.println("转换成的绝对路径："+absolutePath);///////////////////////////
            File file = absolutePath.toFile();// 将 Path 转为 File

            hasChanges |= checkAndCompareFile(file, filePath,currentVersionStructure.getFiles(), currentVersionStructure.getVersion());
            System.out.println("检测完毕："+filePath+"当前结果为"+hasChanges);///////////////////////////
        }
        // 如果有变化，保存当前版本的结构
        if (hasChanges) {
            System.out.println("检测到文件有变化！开始保存当前版本的结构！");///////////////////////////
            saveCurrentVersionStructure(currentVersionStructure, baseDir);
            System.out.println("成功保存当前版本的结构！");///////////////////////////
            // 创建描述文件
            File descriptionFile = new File(changesDir, "version_info.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(descriptionFile))) {
                writer.write(newVersionDirName);  // 写版本名称
                writer.newLine();
                writer.write("无描述");  // 写描述
                writer.newLine();
                writer.write("创建时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));  // 写创建时间
                System.out.println("成功创建txt描述文件！！");///////////////////////////
            }
        }else {
            // 若没有变化，删除创建的 文件夹
            changesDir.delete(); // 直接删除空文件夹
            System.out.println("当前版本与旧版本相同！！");///////////////////////////
        }

        return hasChanges;
    }

    // 解析json文件的接口:需要传入json文件，一个空白的ProjectStructure（调用无参的构造函数创建一个即可）
    public static void JsonConvertToProjectStructure(File jsonFile,ProjectStructure Structure)throws IOException{
        // 读取上一个版本的JSON文件
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonFile);
        // 手动解析 rootNode 构建 ProjectStructure 对象
        Structure.setVersion(rootNode.get("version").asInt());
        Map<String, FileNode> fileMap = new HashMap<>();
        JsonNode filesNode = rootNode.get("files");
        if (filesNode != null && filesNode.isObject()) {
            filesNode.fields().forEachRemaining(entry -> {
                String fileName = entry.getKey();
                JsonNode fileNode = entry.getValue();
                FileNode node = parseFileNode(fileNode); // 实现一个递归解析方法
                fileMap.put(fileName, node);
            });
        }
        Structure.setFiles(fileMap);
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
                if (dirName.startsWith("Version")) {
                    int version = Integer.parseInt(dirName.replace("Version", ""));
                    if (version > maxVersion) {
                        maxVersion = version;
                        lastVersionDir = dir;
                    }
                }
            }
        }
        return lastVersionDir;
    }

    //核心函数：递归检查文件或目录，比较哈希值并更新当前结构
    private boolean checkAndCompareFile(File file,Path filePath,Map<String, FileNode> currentFiles, int currentVersion) throws NoSuchAlgorithmException, IOException {
        boolean hasChanges = false;
        if (file.isDirectory()) {
            if ("VersionHistory".equals(file.getName())) {
                return false; // 直接返回，跳过当前文件夹的处理
            }
            System.out.println("要检查的是目录！");///////////////////////////
            hasChanges=true;//有目录必定变
            // 获取上一个版本中的目录节点
            if( !findOrCreateCurrentDir(file, currentFiles)){
                throw new IOException("Failed to create or find directory: " + file.getAbsolutePath());
            }
        } else {
            System.out.println("要检查的是文件！");///////////////////////////
            hasChanges=findFileNodeInNestedDirs(file,filePath, currentFiles,currentVersion);
            System.out.println("文件检查完毕！！");///////////////////////////
        }
        return hasChanges;
    }

    // 辅助函数：查找当前版本的目录节点（查找到就删除，查找不到就创建）
    private boolean findOrCreateCurrentDir(File dir, Map<String, FileNode> currentFiles) {
        String[] pathParts = dir.getPath().split(Pattern.quote(File.separator));  // 使用 Pattern.quote 来处理分隔符
        Map<String, FileNode> currentLevel = currentFiles;
        FileNode currentNode = null;
        boolean isExist = true;
        FileNode parentNode = null; // 用于追踪父节点
        for (String part : pathParts) {
            parentNode = currentNode;  // 更新到当前的父节点
            currentNode = currentLevel.get(part);
            if (currentNode == null || !"directory".equals(currentNode.getType())) {
                currentNode = new FileNode("directory");  // 如果没有该目录或类型不正确，则创建新的目录节点
                currentLevel.put(part, currentNode);
                isExist = false;  // 表示本次操作是新建了该目录
            }
            currentLevel = currentNode.getChildren();  // 进入下一级目录
        }

        if (isExist && parentNode != null) {
            // 如果该目录存在，说明本次操作是删除了该目录。需要删除该目录以及其所有子目录和文件
            removeDirectory(currentNode, parentNode.getChildren(), dir.getName());
        }
        return true;
    }

    // 辅助函数：递归删除指定目录节点及其子节点
    private void removeDirectory(FileNode currentNode, Map<String, FileNode> parentFiles, String dirName) {
        if (currentNode.getChildren() != null && !currentNode.getChildren().isEmpty()) {
            // 递归删除所有子节点
            for (String childName : new HashMap<>(currentNode.getChildren()).keySet()) {
                removeDirectory(currentNode.getChildren().get(childName), currentNode.getChildren(), childName);
            }
        }
        // 从父节点的children中删除当前目录
        parentFiles.remove(dirName);
    }

    // 辅助函数：在嵌套的目录中查找文件节点（并根据情况修改目录结构和保存文件）
    private boolean findFileNodeInNestedDirs(File file, Path filePath,Map<String, FileNode> currentFiles, int currentVersion) throws IOException, NoSuchAlgorithmException {
        // 将 filePath 转换为字符串，并根据系统的文件分隔符进行切割
        String[] pathParts = filePath.toString().split(Pattern.quote(File.separator));
        System.out.println("已经切割path！");///////////////////////////
        Map<String, FileNode> currentLevel = currentFiles;
        System.out.println("已经把项目结构的MAP放入currentLevel！");///////////////////////////
        boolean isChanged = false;
        for (int i = 0; i < pathParts.length - 1; i++) {  // 不包括最后一个部分（文件本身）
            System.out.println("进入第"+i+"次循环，检查"+pathParts[i]);///////////
            FileNode node = currentLevel.get(pathParts[i]);
            if (node == null || !"directory".equals(node.getType())) {
                throw new IOException("Failed to find directory: " + file.getAbsolutePath()); // 如果找不到目录节点，或者类型不匹配
            }
            currentLevel = node.getChildren();  // 进入下一级目录
            System.out.println("成功进入下一级目录！");///////////
        }
        String fileName = pathParts[pathParts.length - 1];
        System.out.println("要查找的文件名："+fileName);///////////??????????????????????
        FileNode fileNode = currentLevel.get(fileName);
        System.out.println("在目录结构中根据文件名查找的结果：");///////////??????????????????????
        System.out.println(fileNode==null);///////////??????????????????????
        // 判断文件是否存在
        if (!file.exists()) {
            System.out.println("文件实际不存在，说明本次删除了此文件！");///////////
            // 文件不存在，说明被删除，从当前层的Map中移除该文件节点
            if (fileNode != null) {
                currentLevel.remove(fileName);  // 文件删除时从结构中移除
            }
            return true;
        }
        // 计算文件哈希值
        System.out.println("当前文件实际存在！正在计算当前文件的哈希值！");///////////
        String currentHash = calculateFileHash(file.toPath());
        if (fileNode == null||!"file".equals(fileNode.getType())) {
            System.out.println("属于情况1：文件新增！");///////////
            // 文件新增情况
            FileNode newFileNode = new FileNode("file", currentHash, currentVersion);  // 创建新的文件节点
            currentLevel.put(fileName, newFileNode);  // 插入到当前层级的Map中
            System.out.println("已经插入！"+currentLevel.get(fileName));///////////
            isChanged = true;
            //保存文件：
            saveFileToVersion(file,this.currentVersionDir);
            System.out.println("成功保存此文件！");/////
        } else if (!fileNode.getHash().equals(currentHash)) {
            // 文件存在且哈希值不同，说明文件内容被修改
            System.out.println("属于情况2：文件内容被修改了！");/////
            fileNode.setHash(currentHash);  // 更新哈希值
            fileNode.setLastModifiedVersion(currentVersion);  // 更新最后修改版本号
            System.out.println("该文件最后更新的版本号改为："+fileNode.getLastModifiedVersion());/////
            isChanged = true;
            System.out.println("成功修改此fileNode相关信息！");/////
            //保存文件：
            saveFileToVersion(file,this.currentVersionDir);
            System.out.println("成功保存此文件！");/////
        }
        return isChanged;
    }

    // 计算文件的SHA-256哈希值
    private String calculateFileHash(Path filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] fileBytes = Files.readAllBytes(filePath); // 使用 Path 直接读取文件
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
        System.out.println("previousVersion:"+previousVersion.getVersion());/////
        System.out.println("currentVersion:"+currentVersion.getVersion());/////
    }

    // 仅用于 Version1：递归生成项目目录结构
    private void generateProjectStructure(File directory, Map<String, FileNode> currentFiles, int currentVersion) throws IOException, NoSuchAlgorithmException {
        // 检查是否是目录
        if (directory.isDirectory()) {
            // 遍历当前目录中的所有文件和子目录
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    // 创建子目录的 FileNode
                    FileNode dirNode = new FileNode("directory");
                    currentFiles.put(file.getName(), dirNode); // 将子目录添加到当前级别的映射中
                    // 递归处理子目录
                    generateProjectStructure(file, dirNode.getChildren(), currentVersion);
                } else {
                    // 处理文件
                    Path filePath = file.toPath();
                    String fileHash = calculateFileHash(filePath); // 计算文件的哈希值
                    // 创建文件的 FileNode
                    FileNode fileNode = new FileNode("file", fileHash, currentVersion);
                    currentFiles.put(file.getName(), fileNode); // 将文件添加到当前级别的映射中
                }
            }
        }
    }

    // 保存当前版本的目录结构
    private void saveCurrentVersionStructure(ProjectStructure currentStructure, String baseDir) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();//ObjectMapper 是 Jackson 库中的一个类，用于将 Java 对象转换为 JSON 格式
        // 首先，创建Version专属的文件夹
        String newVersionDirName = "Version" + currentStructure.getVersion();
        File newVersionDir = new File(baseDir, newVersionDirName);
        if (!newVersionDir.exists()) {
            newVersionDir.mkdir();//只有第一个版本会进入此分支
        }
        // 保存新的JSON文件
        File jsonFile = new File(newVersionDir, "Structure.json");//创建一个表示 JSON 文件的 File 对象
        objectMapper.writeValue(jsonFile, currentStructure);//转换为 JSON 格式，并将结果写入 jsonFile
    }

    //递归解析每个 FileNode，同时可以限制递归的深度
    private static FileNode parseFileNode(JsonNode fileNode) {
        String type = fileNode.get("type").asText();
        String hash = fileNode.has("hash") ? fileNode.get("hash").asText() : null;
        int lastModifiedVersion = fileNode.get("lastModifiedVersion").asInt();

        FileNode node = new FileNode(type, hash, lastModifiedVersion);

        if ("directory".equals(type) && fileNode.has("children")) {
            Map<String, FileNode> childrenMap = new HashMap<>();
            JsonNode childrenNode = fileNode.get("children");
            if (childrenNode.isObject()) {
                childrenNode.fields().forEachRemaining(entry -> {
                    String childName = entry.getKey();
                    JsonNode childNode = entry.getValue();
                    childrenMap.put(childName, parseFileNode(childNode));
                });
            }
            node.setChildren(childrenMap);
        }

        return node;
    }

    //需改成ply接口：仅用于创建Version1:把指定目录下的文件，全部拷贝到目标目录中（排除VersionHistory）
    public void copyDirectory(File sourceDir, File targetDir) throws IOException {
        if (!targetDir.exists()) {
            targetDir.mkdirs(); // 创建目标文件夹
        }
        // 遍历源目录的所有文件和文件夹
        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if ("VersionHistory".equals(file.getName())) {
                    continue;                // 排除名为 "VersionHistory" 的文件夹
                }
                File targetFile = new File(targetDir, file.getName());
                if (file.isDirectory()) {
                    // 如果是目录，递归调用复制
                    copyDirectory(file, targetFile);
                } else {
                    // 如果是文件，直接复制
                    //CompressDocs.CompressDocs(file.toPath().toString(),targetFile.getName());
                    Files.copy(file.toPath(), targetFile.toPath());
                }
            }
        }
    }

    //需改成ply接口：
    private void saveFileToVersion(File file, File versionDir) throws IOException {
        File targetFile = new File(versionDir, file.getName());

        if (file.isDirectory()) {
            // 如果是目录，创建目录
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
        } else {
            // 如果是文件，复制文件到目标目录
            Files.copy(file.toPath(), targetFile.toPath());
        }
    }
}


