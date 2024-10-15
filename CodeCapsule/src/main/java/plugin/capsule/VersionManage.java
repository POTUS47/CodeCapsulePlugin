package plugin.capsule;
import com.github.weisj.jsvg.S;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import plugin.ui.MessageShow;
import plugin.ui.VersionHistoryUI;

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
    // 对外接口：手动保存当前版本
    public static void saveMannually(boolean isRevertVersion,String revertVersionNum){
        StartUp.timer.resetTimer100();//手动调整计时器，防止重复保存
        List<Path> changedFile=FileChangeListener.getChangedFilePath();//调用接口查看当前有修改的文件列表
        if (changedFile.isEmpty()) {
            System.out.println("列表为空，没有更改的文件。");
            MessageShow.showNotification("操作失败", "没有要保存的内容！");
        } else {
            try {
                CheckVersionSave check=new CheckVersionSave();
                boolean hasChanged=check.checkVersionSave(changedFile, StartUp.getVersionHistoryPath().toString());
                if(hasChanged){
                    String versionDIr= StartUp.getVersionHistoryPath().toString();//获取VersionHistory文件夹地址
                    File lastVersionDir=CheckVersionSave.getLastVersionDirectory(versionDIr);//调用函数，获取最新版本的文件夹
                    if(!isRevertVersion){
                        String VersionTitle= MessageShow.showInputDialog("版本名称","请输入版本名称",lastVersionDir.getName());
                        String VersionDes=MessageShow.showInputDialog("版本描述","请输入版本描述","无描述");
                        VersionManage.renameVersion(lastVersionDir.getName(),VersionTitle);
                        VersionManage.reDescribeVersion(lastVersionDir.getName(),VersionDes);
                    }
                    else{
                        VersionManage.reDescribeVersion(lastVersionDir.getName(),"回退版本：回退到"+VersionManage.getVersionName(revertVersionNum));
                        MessageShow.showNotification("版本回退","成功回退到"+VersionManage.getVersionName(revertVersionNum));
                    }
                    System.out.println("列表中有更改的文件。");
                    MessageShow.showNotification("操作成功", "版本已成功保存！");
                }
                else {
                    System.out.println("列表中没有更改的文件。");
                    MessageShow.showNotification("操作失败", "没有要保存的内容！");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    //对外接口：回退到某版本
    public static void RevertToOneVersion(String VersionName) throws IOException, ClassNotFoundException {
        // 根据给定路径查找版本文件夹
        File versionFolder = findVersionFolder(VersionName);
        Path projectDir=StartUp.getProjectRootPath();
        Path srcPath = projectDir.resolve("src");
        // 在VersionHistory下创建名为Temp的文件夹
        GetVersionAllFiles(VersionName, srcPath.toFile());
        //把src下的文件解压缩，反序列化
        LoadDocsCompressed.loadSnapshotsFromFolder(srcPath.toString());
        saveMannually(true,VersionName);//接着手动保存一下

    }







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
            System.out.println(TargetDir.toPath()+"不存在！");
            // 如果目录不存在，尝试创建
            try {
                Files.createDirectories(TargetDir.toPath());
                System.out.println("文件夹已创建: " + TargetDir.getAbsolutePath());
            } catch (IOException e) {
                throw new IOException("创建文件夹失败: " + TargetDir.getAbsolutePath(), e);
            }
        } else {
            // 如果目录已经存在
            System.out.println(TargetDir.toPath()+"开始清理！");
            clearDirectory(TargetDir);//清空原文件夹里的内容
            System.out.println("原文件夹的内容已清空: " + TargetDir.getAbsolutePath());
        }
        //在目标文件夹中重建项目的src文件
        System.out.println("准备在目标文件夹中重建项目的src文件");/////
        rebuildProjectStructure(versionStructure, TargetDir);
    }

    // 查看某个版本代码：把某个版本的全部文件整理到TEMP文件夹中
    public static void CheckOneVersion(String VersionName) throws IOException, ClassNotFoundException {
        // 根据给定路径查找版本文件夹
        File versionFolder = findVersionFolder(VersionName);
        // 在VersionHistory下创建名为Temp的文件夹
        File tempDir = null; // 在上一级目录创建Temp文件夹
        if (versionFolder != null) {
            tempDir = new File(versionFolder.getParentFile(), "Temp");
        }
        File srcDir = new File(tempDir, "src");
        GetVersionAllFiles(VersionName, srcDir);
        System.out.println("GetVersionAllFiles执行完毕！");
        //把src下的文件解压缩，反序列化
        LoadDocsCompressed.loadSnapshotsFromFolder(srcDir.getPath());
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
            copyFileToTemp(currentName+".gz", lastModifiedVersion, currentDir,targetRootDir);
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
                if (file.isDirectory()) {
                    // 递归删除子目录内容
                    clearDirectory(file);
                }
                // 删除文件或空目录
                Files.delete(file.toPath());
            }
        }
    }


}
