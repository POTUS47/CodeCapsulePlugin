package plugin.capsule;
import com.github.weisj.jsvg.S;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class VersionManage {
    //为某版本重命名
    public static void RenameVersion(String VersionName, String newVersionName) {
        // 根据给定路径查找版本文件夹
        String versionFolderPath=StartUp.getVersionHistoryPath().toString();
        File versionDir = new File(versionFolderPath);
        if (!versionDir.exists() || !versionDir.isDirectory()) {
            System.out.println("版本文件夹不存在: " + versionFolderPath);
            return;
        }
        // 查找version_info.txt文件
        File versionInfoFile = new File(versionDir, "version_info.txt");
        if (!versionInfoFile.exists()) {
            System.out.println("未找到version_info.txt文件: " + versionInfoFile.getAbsolutePath());
            return;
        }
        // 读取并替换version_info.txt文件的第一行内容
        try {
            // 读取文件的内容
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(versionInfoFile))) {
                // 读取第一行并替换为新的版本名称
                content.append(newVersionName).append(System.lineSeparator());
                // 从第二行开始继续读取
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(System.lineSeparator());
                }
            }
            // 将新的内容写回version_info.txt文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(versionInfoFile))) {
                writer.write(content.toString());
            }
            System.out.println("版本名称已成功修改为: " + newVersionName);
        } catch (IOException e) {
            System.out.println("重命名版本时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
