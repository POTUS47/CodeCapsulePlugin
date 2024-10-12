package plugin.capsule;
import com.github.weisj.jsvg.S;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class VersionManage {
    //用于寻找特定Version所在的文件夹（例如：传入“Version1”，返回Version1的文件夹）
    private static File FindVersionFolder(String VersionName){
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

    //为某版本重命名
    public static void RenameVersion(String VersionName, String newVersionName) {
        // 获取VersionName 对应的文件夹
        File targetVersionDir = FindVersionFolder(VersionName);
        // 查找 version_info.txt 文件
        File versionInfoFile = new File(targetVersionDir, "version_info.txt");
        if (!versionInfoFile.exists()) {
            System.out.println("未找到 version_info.txt 文件: " + versionInfoFile.getAbsolutePath());
            return;
        }
        modifyLineInTxt(versionInfoFile,1,newVersionName);//把txt第一行修改成新名字
    }

    //为某版本修改版本描述
    public static void ReDescribeVersion(String VersionName, String newDescription) {
        // 获取VersionName 对应的文件夹
        File targetVersionDir = FindVersionFolder(VersionName);
        // 查找 version_info.txt 文件
        File versionInfoFile = new File(targetVersionDir, "version_info.txt");
        if (!versionInfoFile.exists()) {
            System.out.println("未找到 version_info.txt 文件: " + versionInfoFile.getAbsolutePath());
            return;
        }
        modifyLineInTxt(versionInfoFile,2,newDescription);//把txt第一行修改成新名字
    }

    // 修改文本文件的指定行内容
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

    //把某个版本的全部文件全部整理到TEMP文件夹
    public static void GetVersionAllFiles(String VersionName) throws IOException {
        // 根据给定路径查找版本文件夹
        File versionFolder=FindVersionFolder(VersionName);
        File jsonFile = new File(versionFolder, "Structure.json");//指向想读取的json文件
        ProjectStructure VersionStructure = new ProjectStructure();//创建一个空白的ProjectStructure 对象
        CheckVersionSave.JsonConvertToProjectStructure(jsonFile,VersionStructure);//调用函数构建 ProjectStructure 对象
        //不完整 还在补充
    }
}
