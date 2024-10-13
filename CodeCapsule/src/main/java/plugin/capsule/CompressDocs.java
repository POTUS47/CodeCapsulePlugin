package plugin.capsule;
//序列化并压缩文件

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;
import com.intellij.openapi.vfs.VirtualFileManager;
import java.nio.charset.StandardCharsets;




public class CompressDocs {

    public static void CompressDocs(String originalPath,Path versionPath)  {
        // 转换文件路径为 URL 格式
        String urlPath = VirtualFileManager.constructUrl("file", originalPath);

        // 获取 VirtualFileManager 实例
        VirtualFileManager fileManager = VirtualFileManager.getInstance();
        VirtualFile virtualFile = fileManager.findFileByUrl(urlPath);

        if (virtualFile == null) {
            throw new IllegalArgumentException("无法找到指定路径的文件: " + originalPath);
        }

        try {
            // 获取文件内容并转换为 String
            byte[] contentBytes = virtualFile.contentsToByteArray();
            String content = new String(contentBytes,StandardCharsets.UTF_8);

            // 创建 Snapshot 对象
            SnapShot snapshot = new SnapShot(virtualFile.getName(), content);

            // 将 Snapshot 序列化为压缩后的二进制文件
            try {
                // 定义保存的目录和文件名
                //String targetDirectory = e.getProject().getBasePath();  // 使用项目的根目录
                //String targetDirectory=StartUp.getProjectRootPath().toString();

                // 拼接路径
//                Path versionHistoryDir = Paths.get(targetDirectory, "VersionHistory", version);
//                    // 确保 "VersionHistory" 和 "version" 目录存在
//                Files.createDirectories(versionHistoryDir);

                    // 定义目标文件路径
                Path binaryFilePath = versionPath.resolve(virtualFile.getName() + ".gz");

                // 使用 GZIPOutputStream 进行压缩，并将 Snapshot 对象序列化
                try (FileOutputStream fos = new FileOutputStream(binaryFilePath.toFile());
                     GZIPOutputStream gzipOut = new GZIPOutputStream(fos);
                     ObjectOutputStream oos = new ObjectOutputStream(gzipOut)) {

                    oos.writeObject(snapshot);  // 序列化对象并写入
                    oos.flush();
                    System.out.println("chenggong");
                    //Messages.showInfoMessage("文件快照已成功保存为压缩的二进制文件！", "成功");
                }

            } catch (IOException ex) {
                Messages.showErrorDialog("保存文件快照时发生错误: " + ex.getMessage(), "错误");
            }
        } catch (IOException exc) {
            throw new RuntimeException("读取文件内容时发生错误: " + exc.getMessage(), exc);
        }
    }
}