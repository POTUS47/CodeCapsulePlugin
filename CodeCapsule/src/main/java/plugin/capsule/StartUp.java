package plugin.capsule;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class StartUp {
    // 单例实例
    //Version1
    private static StartUp instance;
    // 静态变量保存项目根路径
    private static String projectRootPath;
    public static Timer timer;

    // 私有构造函数，防止外部实例化
    private StartUp() {
        // 在构造函数中初始化计时器
        timer = new Timer();

        // 创建并注册 FileChangeListener
        FileChangeListener fileChangeListener = new FileChangeListener();
        VirtualFileManager.getInstance().addVirtualFileListener(fileChangeListener);
        System.out.println("FileChangeListener 已注册.");
    }

    // 获取单例实例
    public static StartUp getInstance() {
        if (instance == null) {
            instance = new StartUp();
            // 静态初始化操作
            initialize();
        }
        return instance;
    }
    public static void clearInstance(){
        if (instance != null) {
            // 停止计时器并释放资源
            timer.shutdownTimer();
            timer = null;

            // 如果注册了监听器，也可以在这里移除
            VirtualFileManager.getInstance().removeVirtualFileListener(new FileChangeListener());

            // 清除单例实例
            instance = null;
            System.out.println("StartUp 实例已清除.");
        }
    }
    // 静态初始化方法
    private static void initialize() {
        // 获取当前打开的项目路径
        projectRootPath = getCurrentProjectPath();
        if (projectRootPath != null) {
            System.out.println("项目根目录: " + projectRootPath);
        }

        // 创建VersionHistory文件夹
        Path versionHistoryDir = Paths.get(projectRootPath, "VersionHistory");
        try {
            Files.createDirectories(versionHistoryDir); // 创建目录，如果不存在
            System.out.println("VersionHistory 目录创建成功: " + versionHistoryDir);
        } catch (IOException ex) {
            System.err.println("无法创建 VersionHistory 目录: " + ex.getMessage());
        }
    }

    // 静态方法获取项目根路径
    public static Path getProjectRootPath() {
        if (projectRootPath != null) {
            return Path.of(projectRootPath);  // 将 String 转换为 Path
        }
        return null;
    }

    public static Path getVersionHistoryPath() {
        return Paths.get(projectRootPath, "VersionHistory");
    }

    private static String getCurrentProjectPath() {
        // 获取当前打开的项目
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        if (projects.length > 0) {
            Project currentProject = projects[0]; // 取得第一个打开的项目
            String projectBasePath = currentProject.getBasePath();  // 获取项目的路径字符串
            if (projectBasePath != null) {
                return projectBasePath;  // 返回项目路径
            }
        }
        return null;
    }
}
