package plugin.capsule;
import com.github.weisj.jsvg.S;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.nio.file.Path;

import static com.jayway.jsonpath.Filter.filter;

public class VersionManage {
    // 获取某个版本的名称
    public static String getVersionName(String VersionName) {
        // 获取VersionName 对应的文件夹
        File targetVersionDir = findVersionFolder(VersionName);
        // 查找 version_info.txt 文件
        File versionInfoFile = new File(targetVersionDir, "version_info.txt");
        if (!versionInfoFile.exists()) {
            System.out.println("未找到 version_info.txt 文件: " + versionInfoFile.getAbsolutePath());
            return null;
        }
        return getLineFromTxt(versionInfoFile, 1);//获取txt第一行内容
    }

    // 获取某个版本的描述
    public static String getVersionDescription(String VersionName) {
        // 获取VersionName 对应的文件夹
        File targetVersionDir = findVersionFolder(VersionName);
        // 查找 version_info.txt 文件
        File versionInfoFile = new File(targetVersionDir, "version_info.txt");
        if (!versionInfoFile.exists()) {
            System.out.println("未找到 version_info.txt 文件: " + versionInfoFile.getAbsolutePath());
            return null;
        }
        return getLineFromTxt(versionInfoFile, 2);//获取txt第一行内容
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
        modifyLineInTxt(versionInfoFile, 1, newVersionName);//把txt第一行修改成新名字
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
        modifyLineInTxt(versionInfoFile, 2, newDescription);//把txt第一行修改成新名字
    }

    // 辅助函数：用于寻找特定Version所在的文件夹（例如：传入“Version1”，返回Version1的文件夹）
    private static File findVersionFolder(String VersionName) {
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
        System.out.println("成功找到"+VersionName+"的文件夹: " + targetVersionDir.getAbsolutePath());
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

    // 核心函数：把某个版本的全部文件全部整理到某个文件夹
    public static void GetVersionAllFiles(String VersionName, File TargetDir) throws IOException {
        // 根据给定路径查找版本文件夹
        File versionFolder = findVersionFolder(VersionName);
        File jsonFile = new File(versionFolder, "Structure.json");//指向想读取的json文件
        System.out.println("成功找到json文件");
        ProjectStructure versionStructure = new ProjectStructure();//创建一个空白的ProjectStructure 对象
        CheckVersionSave.JsonConvertToProjectStructure(jsonFile, versionStructure);//调用函数构建 ProjectStructure 对象
        System.out.println("成功构建了ProjectStructure对象");
        if (!TargetDir.exists()) {
            if (TargetDir.mkdir()) {
                System.out.println("文件夹不存在，文件夹已创建: " + TargetDir.getAbsolutePath());
            } else {
                throw new IOException("文件夹不存在，创建文件夹失败: " + TargetDir.getAbsolutePath());
            }
        } else {
            clearDirectory(TargetDir);//清空原文件夹里的内容
            System.out.println("原文件夹的内容已清空: " + TargetDir.getAbsolutePath());
        }
        //在目标文件夹中重建项目的src文件
        System.out.println("准备在目标文件夹中重建项目的src文件");/////
        rebuildProjectStructure(versionStructure, TargetDir);
    }

    // 查看某个版本代码：把某个版本的全部文件整理到TEMP文件夹中
    public static void CheckOneVersion(String VersionName) throws IOException {
        // 根据给定路径查找版本文件夹
        File versionFolder = findVersionFolder(VersionName);
        // 在VersionHistory下创建名为Temp的文件夹
        File tempDir = new File(versionFolder.getParentFile(), "Temp"); // 在上一级目录创建Temp文件夹
        GetVersionAllFiles(VersionName, tempDir);
    }

    // 重建项目文件结构
    public static void rebuildProjectStructure(ProjectStructure versionStructure, File targetDir) throws IOException {
        // 遍历项目结构中的文件
        for (Map.Entry<String, FileNode> entry : versionStructure.getFiles().entrySet()) {
            String fileName = entry.getKey();
            FileNode node = entry.getValue();
            // 调用递归方法重建结构
            rebuildNodeStructure(node, fileName, targetDir,targetDir);
        }
    }

    // 递归方法，重建每个节点的结构
    private static void rebuildNodeStructure(FileNode node, String currentName, File currentDir,File targetRootDir) throws IOException {
        // 根据节点类型进行处理
        if ("directory".equals(node.getType())) {
            // 创建目录
            File newDir = new File(currentDir, currentName);
            //File newVersion = new File(oldVersionDir, currentName);
            if (!newDir.exists()) {
                boolean created = newDir.mkdir();
                if (created) {
                    System.out.println("创建目录: " + newDir.getAbsolutePath());
                } else {
                    System.out.println("创建目录失败: " + newDir.getAbsolutePath());
                }
            }
            System.out.println("开始处理"+currentName+"目录下的子文件" );
            // 遍历子节点
            for (Map.Entry<String, FileNode> childEntry : node.getChildren().entrySet()) {
                String childName = childEntry.getKey();
                FileNode childNode = childEntry.getValue();
                // 递归调用以处理子节点
                rebuildNodeStructure(childNode, childName, newDir,targetRootDir);
            }
            System.out.println("结束处理"+currentName+"目录下的子文件" );
        } else if ("file".equals(node.getType())) {
            // 文件节点
            int lastModifiedVersion = node.getLastModifiedVersion();
            // 复制文件到目标目录
            copyFileToTemp(currentName, lastModifiedVersion, currentDir,targetRootDir);
        }
    }

    // 辅助函数：复制指定文件到指定目录
    public static void copyFileToTemp(String fileName, int versionNum, File targetDir,File targetRootDir) throws IOException {

        Path relativePath = targetRootDir.toPath().relativize(targetDir.toPath()); // 计算相对路径
        // 拼接版本名称
        String VersionName = "Version" + versionNum;
        // 获取特定文件夹
        File versionFolder = findVersionFolder(VersionName);
        // 拼接 relativePath
        Path newPath = versionFolder.toPath().resolve(relativePath);
        File oldVersionDir=newPath.toFile();
        if (versionFolder == null || !versionFolder.exists()) {
            throw new IOException("未找到版本文件夹: " + versionFolder.getAbsolutePath());
        }
        // 在版本文件夹中查找文件
        File targetFile = new File(oldVersionDir, fileName);
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

    //对外接口：回退到某个版本
    public static void revertVersion (String VersionName) throws IOException, NoSuchAlgorithmException {
        CheckOneVersion(VersionName);
/*        //实际应替换为：GetVersionAllFiles(VersionName,C:\Users\10510\IdeaProjects\trytry\src对应的file)
        //比如C:\Users\10510\IdeaProjects\trytry
        Path projectPath=StartUp.getProjectRootPath();
        Path versionHistoryPath = null;
        if (projectPath != null) {
            //比如C:\Users\10510\IdeaProjects\trytry\VersionHistory\Temp
            versionHistoryPath = projectPath.resolve("VersionHistory").resolve("Temp");
        }
        // 创建一个List来存储所有新创建或更新的路径,(文件监听捕捉不到）
        List<Path> paths = new ArrayList<>();

        // 递归遍历Temp目录，删除项目中对应的文件
        deleteDuplicateFiles(versionHistoryPath, projectPath);

        // 将Temp目录中的内容复制到项目根目录
        copyFiles(versionHistoryPath, projectPath,paths);
        System.out.println("版本回退完成.");

        //保存新版本
        paths.forEach(System.out::println);
        Path baseDir=StartUp.getVersionHistoryPath();

        //开始报错
        CheckVersionSave check=new CheckVersionSave();
        check.checkVersionSave(paths, baseDir.toString());*/
    }

    // 辅助函数：删除当前项目中的重复文件
/*
    private static void deleteDuplicateFiles(Path sourceDir, Path targetDir) throws IOException {
        if (Files.notExists(sourceDir)) {
            System.out.println("Temp 目录不存在.");
            return;
        }

        // 遍历Temp文件夹
        Files.walk(sourceDir)
                .filter(path -> !path.equals(sourceDir))  // 忽略掉sourceDir自身
                .forEach(sourcePath -> {
                    try {
                        // sourceDir,比如C:\Users\10510\IdeaProjects\trytry\VersionHistory\Temp
                        // 计算出相对于Temp目录的相对路径，比如/src(Temp内不包括VersionX)
                        Path relativePath = sourceDir.relativize(sourcePath);
                        // System.out.println("relativePath:"+relativePath);

                        // 合并路径，比如C:\Users\10510\IdeaProjects\trytry\src
                        Path targetPath = targetDir.resolve(relativePath);
                        // System.out.println("targetPath:"+targetPath);

                        // 如果targetPath存在且不是VersionHistory文件夹，删除它
                        if (Files.exists(targetPath) && !targetPath.startsWith(targetDir.resolve("VersionHistory"))) {
                            if (Files.isDirectory(targetPath)) {
                                Files.walk(targetPath)
                                        // 按照路径深度从深到浅进行排序，确保先删除文件和子目录，再删除父目录
                                        .sorted((path1, path2) -> Integer.compare(path2.getNameCount(), path1.getNameCount()))
                                        .forEach(path -> {
                                            try {
                                                Files.deleteIfExists(path);
                                            } catch (IOException e) {
                                                System.err.println("无法删除: " + path + " - " + e.getMessage());
                                            }
                                        });
                            } else {
                                Files.deleteIfExists(targetPath);
                            }
                            System.out.println("删除: " + targetPath);
                        }
                    } catch (IOException e) {
                        System.err.println("删除文件出错: " + sourcePath + " - " + e.getMessage());
                    }
        });
    }
*/

    // 复制Temp目录中的文件到项目根目录
/*    private static  void copyFiles(Path sourceDir, Path targetDir,List<Path> paths) throws IOException {

        Path projectPath = StartUp.getProjectRootPath();
        Path srcFolderPath = Paths.get(String.valueOf(projectPath), "src");
        Files.walk(sourceDir).forEach(sourcePath -> {
            try {
                // 计算相对路径以确定目标路径
                Path relativePath = sourceDir.relativize(sourcePath);
                Path targetPath = targetDir.resolve(relativePath);

                // 创建目标目录（如果是目录）
                if (Files.isDirectory(sourcePath)) {
                    if (Files.notExists(targetPath)) {
                        Files.createDirectories(targetPath);
                    }
                } else {
                    // 如果是文件则复制到目标位置
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
                System.out.println("复制: " + sourcePath + " 到 " + targetPath);
                Path path = Paths.get(srcFolderPath.toString()).relativize(targetPath);
                paths.add(path);

            } catch (IOException e) {
                System.err.println("复制文件出错: " + sourcePath + " - " + e.getMessage());
            }
        });
    }*/
}
