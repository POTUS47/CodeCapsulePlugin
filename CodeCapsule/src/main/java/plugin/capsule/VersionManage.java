package plugin.capsule;
import com.github.weisj.jsvg.S;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class VersionManage {
    // 获取某个版本的名称
    public static String getVersionName(String VersionName){
        // 获取VersionName 对应的文件夹
        File targetVersionDir = findVersionFolder(VersionName);
        // 查找 version_info.txt 文件
        File versionInfoFile = new File(targetVersionDir, "version_info.txt");
        if (!versionInfoFile.exists()) {
            System.out.println("未找到 version_info.txt 文件: " + versionInfoFile.getAbsolutePath());
            return null;
        }
        return getLineFromTxt(versionInfoFile,1);//获取txt第一行内容
    }

    // 获取某个版本的描述
    public static String getVersionDescription(String VersionName){
        // 获取VersionName 对应的文件夹
        File targetVersionDir = findVersionFolder(VersionName);
        // 查找 version_info.txt 文件
        File versionInfoFile = new File(targetVersionDir, "version_info.txt");
        if (!versionInfoFile.exists()) {
            System.out.println("未找到 version_info.txt 文件: " + versionInfoFile.getAbsolutePath());
            return null;
        }
        return getLineFromTxt(versionInfoFile,2);//获取txt第一行内容
    }

    // 为某版本重命名
    public static void renameVersion(String VersionName, String newVersionName) {
        // 获取VersionName 对应的文件夹
        File targetVersionDir = findVersionFolder(VersionName);
        // 查找 version_info.txt 文件
        File versionInfoFile = new File(targetVersionDir, "version_info.txt");
        if (!versionInfoFile.exists()) {
            System.out.println("未找到 version_info.txt 文件: " + versionInfoFile.getAbsolutePath());
            return;
        }
        modifyLineInTxt(versionInfoFile,1,newVersionName);//把txt第一行修改成新名字
    }

    // 为某版本修改版本描述
    public static void reDescribeVersion(String VersionName, String newDescription) {
        // 获取VersionName 对应的文件夹
        File targetVersionDir = findVersionFolder(VersionName);
        // 查找 version_info.txt 文件
        File versionInfoFile = new File(targetVersionDir, "version_info.txt");
        if (!versionInfoFile.exists()) {
            System.out.println("未找到 version_info.txt 文件: " + versionInfoFile.getAbsolutePath());
            return;
        }
        modifyLineInTxt(versionInfoFile,2,newDescription);//把txt第一行修改成新名字
    }

    // 辅助函数：用于寻找特定Version所在的文件夹（例如：传入“Version1”，返回Version1的文件夹）
    private static File findVersionFolder(String VersionName){
        // 获取版本历史的根目录路径
        String versionFolderPath = StartUp.getVersionHistoryPath().toString();
        File versionDir = new File(versionFolderPath);
        if (!versionDir.exists() || !versionDir.isDirectory()) {
            System.out.println("VersionHistory文件夹不存在: " + versionFolderPath);
            return null;
        }
        // 查找名为 VersionName 的子文件夹
        File targetVersionDir = new File(versionDir, VersionName);
        if (!targetVersionDir.exists() || !targetVersionDir.isDirectory()) {
            System.out.println("未找到版本文件夹: " + targetVersionDir.getAbsolutePath());
            return null;
        }
        return targetVersionDir;
    }

    // 辅助函数：修改txt的指定行内容
    public static void modifyLineInTxt(File file, int lineNumber, String newContent) {
        // 使用临时文件存储修改后的内容
        File tempFile = new File(file.getAbsolutePath() + ".tmp");
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            int currentLine = 1;
            // 逐行读取文件内容
            while ((line = reader.readLine()) != null) {
                if (currentLine == lineNumber) {
                    // 替换指定行的内容
                    writer.write(newContent);
                } else {
                    // 复制原来的内容
                    writer.write(line);
                }
                writer.newLine();
                currentLine++;
            }
        } catch (IOException e) {
            System.out.println("修改文件时出错: " + e.getMessage());
            return;
        }
        // 替换原来的文件
        if (!file.delete() || !tempFile.renameTo(file)) {
            System.out.println("重命名文件时出错");
        }
    }

    // 辅助函数：获取txt的指定行内容
    public static String getLineFromTxt(File file, int lineNumber) {
        if (lineNumber <= 0) {
            throw new IllegalArgumentException("行号必须大于0");
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int currentLine = 1;
            // 逐行读取文件内容
            while ((line = reader.readLine()) != null) {
                if (currentLine == lineNumber) {
                    return line; // 返回指定行的内容
                }
                currentLine++;
            }
        } catch (IOException e) {
            System.out.println("读取文件时出错: " + e.getMessage());
        }
        // 如果行号超出范围，则返回null
        return null;
    }

    // 核心函数：把某个版本的全部文件全部整理到TEMP文件夹
    public static void GetVersionAllFiles(String VersionName) throws IOException {
        // 根据给定路径查找版本文件夹
        File versionFolder=findVersionFolder(VersionName);
        File jsonFile = new File(versionFolder, "Structure.json");//指向想读取的json文件
        ProjectStructure versionStructure = new ProjectStructure();//创建一个空白的ProjectStructure 对象
        CheckVersionSave.JsonConvertToProjectStructure(jsonFile,versionStructure);//调用函数构建 ProjectStructure 对象
        // 在VersionHistory下创建名为Temp的文件夹
        File tempDir = new File(versionFolder.getParentFile(), "Temp"); // 在上一级目录创建Temp文件夹
        if (!tempDir.exists()) {
            if( tempDir.mkdir()) {
                System.out.println("Temp文件夹已创建: " + tempDir.getAbsolutePath());
            } else {
                throw new IOException("创建Temp文件夹失败: " + tempDir.getAbsolutePath());
            }
        }
        else{
            clearDirectory(tempDir);//清空原版TEMP文件夹里的内容
        }
        //在TEMP中重建整个项目
        rebuildProjectStructure(versionStructure, tempDir);
    }

    // 重建项目文件结构
    public static void rebuildProjectStructure(ProjectStructure versionStructure, File tempDir) throws IOException {
        // 遍历项目结构中的文件
        for (Map.Entry<String, FileNode> entry : versionStructure.getFiles().entrySet()) {
            String fileName = entry.getKey();
            FileNode node = entry.getValue();
            // 调用递归方法重建结构
            rebuildNodeStructure(node, fileName, tempDir);
        }
    }

    // 递归方法，重建每个节点的结构
    private static void rebuildNodeStructure(FileNode node, String currentName, File currentDir) throws IOException {
        // 根据节点类型进行处理
        if ("directory".equals(node.getType())) {
            // 创建目录
            File newDir = new File(currentDir, currentName);
            if (!newDir.exists()) {
                boolean created = newDir.mkdir();
                if (created) {
                    System.out.println("创建目录: " + newDir.getAbsolutePath());
                } else {
                    System.out.println("创建目录失败: " + newDir.getAbsolutePath());
                }
            }

            // 遍历子节点
            for (Map.Entry<String, FileNode> childEntry : node.getChildren().entrySet()) {
                String childName = childEntry.getKey();
                FileNode childNode = childEntry.getValue();
                // 递归调用以处理子节点
                rebuildNodeStructure(childNode, childName, newDir);
            }
        } else if ("file".equals(node.getType())) {
            // 文件节点
            int lastModifiedVersion = node.getLastModifiedVersion();
            // 复制文件到目标目录
            copyFileToTemp(currentName, lastModifiedVersion, currentDir);
        }
    }

    // 辅助函数：复制指定文件到指定目录
    public static void copyFileToTemp(String fileName, int versionNum, File targetDir) throws IOException {
        // 拼接版本名称
        String VersionName = "Version" + versionNum;
        // 获取特定文件夹
        File versionFolder = findVersionFolder(VersionName);
        if (versionFolder == null || !versionFolder.exists()) {
            throw new IOException("未找到版本文件夹: " + VersionName);
        }
        // 在版本文件夹中查找文件
        File targetFile = new File(versionFolder, fileName);
        if (!targetFile.exists()) {
            throw new IOException("未找到文件: " + targetFile.getAbsolutePath());
        }
        // 拷贝文件到目标目录(注意注意)
        File copiedFile = new File(targetDir, targetFile.getName());
        Files.copy(targetFile.toPath(), copiedFile.toPath());
        System.out.println("成功将文件拷贝到Temp文件夹: " + copiedFile.getAbsolutePath());
    }

    // 辅助函数：清空TEMP内容
    private static void clearDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                Files.delete(file.toPath());// 直接使用 Files.delete 方法删除文件或目录
            }
        }
    }

}
