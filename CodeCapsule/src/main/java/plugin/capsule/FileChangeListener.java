package plugin.capsule;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

//此类用于监听文件的创建，删除和内容的修改
public class FileChangeListener implements VirtualFileListener {

    // 使用 List 来存储改变的文件路径
    private static final List<Path> changedFilePath = new ArrayList<>();

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        // 文件内容变更后的逻辑
        dealChange(event);
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        // 文件删除后的逻辑
        dealChange(event);
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        // 文件创建后的逻辑
        dealChange(event);
        System.out.println("文件创建");
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        if (VirtualFile.PROP_NAME.equals(event.getPropertyName())) {
            String oldName = (String) event.getOldValue();
            String newName = (String) event.getNewValue();
            System.out.println("文件名从 " + oldName + " 改变为 " + newName);
            dealRename(oldName,newName);
        }
    }


    private void dealChange(@NotNull VirtualFileEvent event) {
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

        // 获取变更文件路径
        VirtualFile file = event.getFile();
        Path filePath = Paths.get(file.getPath());

        // 判断该文件是否在 src 文件夹中
        if (filePath.startsWith(srcFolderPath)) {
            System.out.println("srcFolderPath " + srcFolderPath);
            System.out.println("filePath " + filePath);
            // 获取从 src 内开始的相对路径
            Path relativePath = Paths.get(srcFolderPath.toString()).relativize(filePath);

            // 防止重复
            if (!changedFilePath.contains(relativePath)) {
                changedFilePath.add(relativePath);
                System.out.println("文件记录: " + relativePath);
            }
            StartUp.timer.resetTimer0();
        } else {
            System.out.println("不在 src 文件夹中，忽略: " + filePath);
        }

    }

    private void dealRename(String oldName, String newName) {
        //获取四个路径
        Path projectPath=StartUp.getProjectRootPath();
        Path srcFolderPath=Paths.get(projectPath.toString(), "src");
        Path oldFilePath=Paths.get(srcFolderPath.toString(), oldName);
        Path newFilePath=Paths.get(srcFolderPath.toString(), newName);

        // 判断该文件是否在 src 文件夹中
        if (oldFilePath.startsWith(srcFolderPath)&&newFilePath.startsWith(srcFolderPath)) {

            // 获取从 src 内开始的相对路径
            Path relativePath1 = Paths.get(srcFolderPath.toString()).relativize(oldFilePath);
            Path relativePath2 = Paths.get(srcFolderPath.toString()).relativize(newFilePath);

            // 防止重复
            if (!changedFilePath.contains(relativePath1)) {
                changedFilePath.add(relativePath1);
                System.out.println("文件记录: " + relativePath1);
            }
            if (!changedFilePath.contains(relativePath2)) {
                changedFilePath.add(relativePath2);
                System.out.println("文件记录: " + relativePath2);
            }
            StartUp.timer.resetTimer0();
        } else {
            System.out.println("不在 src 文件夹中，忽略: " + oldFilePath);
            System.out.println("不在 src 文件夹中，忽略: " + newFilePath);
        }

    }
    // 获取变更的文件路径
    public static List<Path> getChangedFilePath() {
        return new ArrayList<>(changedFilePath); // 返回已记录路径的副本
    }

    public static void clearChangedFilePath(){
        changedFilePath.clear();
    }
}
