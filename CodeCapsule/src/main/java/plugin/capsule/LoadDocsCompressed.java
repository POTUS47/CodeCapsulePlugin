package plugin.capsule;
//解压并反序列化

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
}
