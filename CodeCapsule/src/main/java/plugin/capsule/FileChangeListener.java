package plugin.capsule;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileChangeListener implements VirtualFileListener {

    // 使用 List 来存储改变的文件路径
    private static final List<Path> changedFilePath = new ArrayList<>();

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        // 文件内容变更后的逻辑
        VirtualFile file = event.getFile();
        Path filePath = Paths.get(file.getPath());
        if (!changedFilePath.contains(filePath)) {
            changedFilePath.add(filePath);
            System.out.println("文件变更: " + filePath);
        }
        StartUp.timer.resetTimer();
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        // 文件删除后的逻辑
        VirtualFile file = event.getFile();
        Path filePath = Paths.get(file.getPath());
        if (!changedFilePath.contains(filePath)) {
            changedFilePath.add(filePath);
            System.out.println("文件删除: " + filePath);
        }
        StartUp.timer.resetTimer();
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        // 文件创建后的逻辑
        VirtualFile file = event.getFile();
        Path filePath = Paths.get(file.getPath());
        if (!changedFilePath.contains(filePath)) {
            changedFilePath.add(filePath);
            System.out.println("文件创建: " + filePath);
        }
        StartUp.timer.resetTimer();
    }

    // 获取变更的文件路径
    public static List<Path> getChangedFilePath() {
        return new ArrayList<>(changedFilePath); // 返回已记录路径的副本
    }
}
