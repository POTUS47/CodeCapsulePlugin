package plugin.capsule;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

public class FileChangeListener implements VirtualFileListener {

    // 使用 List 来存储改变的文件路径
    private static final List<Path> changedFilePath = new ArrayList<>();
//    private Path targetPath;

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        // 文件内容变更后的逻辑
        dealChange(event);
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {

//        targetPath = StartUp.getProjectRootPath();
//        if (targetPath == null) {
//            System.err.println("项目路径未初始化."); // 如果项目路径未初始化，直接返回
//            return;
//        }

        // 文件删除后的逻辑
        dealChange(event);
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        // 文件创建后的逻辑
        dealChange(event);
    }

    private void dealChange(@NotNull VirtualFileEvent event){
        // 获取项目
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        if (projects.length == 0) {
            System.err.println("没有打开的项目.");
            return;
        }
        // 取得第一个打开的项目
        Project currentProject = projects[0];
        String projectPath = currentProject.getBasePath();
        if (projectPath == null) {
            System.err.println("项目路径未初始化.");
            return;
        }
        // 构造 src 文件夹的目标路径
        Path srcFolderPath = Paths.get(projectPath, "src");
        System.out.println(srcFolderPath);
        // 获取变更文件路径
        VirtualFile file = event.getFile();
        Path filePath = Paths.get(file.getPath());

//        Path srcFolderPath = targetPath.resolve("src");

        // 判断该文件是否在 src 文件夹中
        if (srcFolderPath!=null){
            if (filePath.startsWith(srcFolderPath)) {
                //防止重复
                if (!changedFilePath.contains(filePath)) {
                    changedFilePath.add(filePath);
                    System.out.println("文件记录: " + filePath);
                }
            } else {
                System.out.println("不在 src 文件夹中，忽略: " + filePath);
            }
        }
        StartUp.timer.resetTimer();
    }
    // 获取变更的文件路径
    public static List<Path> getChangedFilePath() {
        return new ArrayList<>(changedFilePath); // 返回已记录路径的副本
    }
}
