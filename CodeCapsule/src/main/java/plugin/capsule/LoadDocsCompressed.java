package plugin.capsule;
//解压并反序列化

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

public class LoadDocsCompressed {

    public static SnapShot loadSnapshot(String filePath) throws IOException, ClassNotFoundException {
        Path binaryFilePath = Paths.get(filePath);

        // 使用 GZIPInputStream 进行解压并反序列化
        try (FileInputStream fis = new FileInputStream(binaryFilePath.toFile());
             GZIPInputStream gzipIn = new GZIPInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(gzipIn)) {

            return (SnapShot) ois.readObject();
        }
    }

    public static void loadSnapshotsFromFolder(String folderPath) throws IOException, ClassNotFoundException {
        Path dirPath = Paths.get(folderPath);
        // 调用递归方法处理所有.gz文件
        processDirectory(dirPath);
    }

    private static void processDirectory(Path dir) throws IOException, ClassNotFoundException {
        // 遍历当前目录中的所有文件和文件夹
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    // 如果是文件夹，递归调用
                    processDirectory(path);
                } else if (path.toString().endsWith(".gz")) {
                    // 处理.gz文件
                    SnapShot snapshot = loadSnapshot(path.toString());

                    // 将解压后的内容保存为 Java 文件
                    String javaFileName = path.getFileName().toString().replace(".gz", "");
                    Path javaFilePath = path.getParent().resolve(javaFileName); // 保存到同一文件夹

                    // 这里假设 SnapShot 有一个方法可以返回 Java 文件的内容
                    try (BufferedWriter writer = Files.newBufferedWriter(javaFilePath)) {
                        writer.write(snapshot.getContent()); // 假设 SnapShot 有 getContent() 方法返回内容
                    }

                    // 删除压缩文件
                    Files.delete(path);
                }
            }
        }
    }

}
