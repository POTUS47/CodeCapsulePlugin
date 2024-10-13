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
    //Version
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








////////////////////////////////////////////////////////////版本二：action版本
//package plugin.capsule;
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.vfs.VirtualFileManager;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.project.ProjectManager;
//
//public class StartUp extends AnAction {
//
//    // 静态变量保存项目根路径
//    private static String projectRootPath;
//    public static Timer timer;
//
////    public StartUp() {
////
////        // 在构造函数中初始化计时器
////        timer = new Timer();
////
////        // 创建并注册 FileChangeListener（暂时写在action里测试）
////        FileChangeListener fileChangeListener = new FileChangeListener();
////        VirtualFileManager.getInstance().addVirtualFileListener(fileChangeListener);
////        System.out.println("FileChangeListener 已注册.");
////    }
//
//    //静态初始化块 只会在类首次被加载时执行一次。
//    //这对于那些需要全局初始化的操作非常有用，比如设置全局监听器、初始化单例等。
//    //静态初始化块
//    //牛批
//    static {
//        // 在静态初始化块中初始化计时器
//        timer = new Timer();
//
//        // 创建并注册 FileChangeListener
//        FileChangeListener fileChangeListener = new FileChangeListener();
//        VirtualFileManager.getInstance().addVirtualFileListener(fileChangeListener);
//        System.out.println("FileChangeListener 已注册.");
//
////        // 获取项目根目录
////        projectRootPath=getProjectRootPath();
////        if (projectRootPath != null) {
////            System.out.println("项目根目录: " + projectRootPath);
////        }
////
////        // 创建VersionHistory文件夹
////        Path versionHistoryDir = Paths.get(projectRootPath, "VersionHistory");
////        try {
////            Files.createDirectories(versionHistoryDir); // 创建目录，如果不存在
////            System.out.println("VersionHistory 目录创建成功: " + versionHistoryDir);
////        } catch (IOException ex) {
////            System.err.println("无法创建 VersionHistory 目录: " + ex.getMessage());
////        }
//    }
//
//    @Override
//    public void actionPerformed(AnActionEvent e) {
//        // 获取项目根目录
//        Project project = e.getProject();
//        if (project != null) {
//            projectRootPath = project.getBasePath();
//        }
//        if (projectRootPath != null) {
//            System.out.println("项目根目录: " + projectRootPath);
//        }
//
//        // 创建VersionHistory文件夹
//        Path versionHistoryDir = Paths.get(projectRootPath, "VersionHistory");
//        try {
//            Files.createDirectories(versionHistoryDir); // 创建目录，如果不存在
//            System.out.println("VersionHistory 目录创建成功: " + versionHistoryDir);
//        } catch (IOException ex) {
//            System.err.println("无法创建 VersionHistory 目录: " + ex.getMessage());
//        }
//    }
//    // 静态方法获取项目根路径
//    public static Path getProjectRootPath() {
//        if (projectRootPath != null) {
//            return Path.of(projectRootPath);  // 将 String 转换为 Path
//        }
//        return null;
//    }
//    public static Path getVersionHistoryPath() {
//        return Paths.get(projectRootPath, "VersionHistory");
//    }
//    private static String getCurrentProjectPath() {
//        // 获取当前打开的项目
//        Project[] projects = ProjectManager.getInstance().getOpenProjects();
//        if (projects.length > 0) {
//            Project currentProject = projects[0]; // 取得第一个打开的项目
//            String projectBasePath = currentProject.getBasePath();  // 获取项目的路径字符串
//            if (projectBasePath != null) {
//                return projectBasePath;  // 将 String 转为 Path
//            }
//        }
//        return null;
//    }
//}

////////////////////失败的尝试
//package plugin.capsule;
//
//import com.intellij.openapi.application.ApplicationManager;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.startup.StartupActivity;
//import com.intellij.openapi.vfs.VirtualFileManager;
//import com.intellij.openapi.vfs.VirtualFileListener;
//import com.intellij.openapi.vfs.newvfs.BulkFileListener;
//import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
//import com.intellij.openapi.vfs.VirtualFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//public class StartUp implements StartupActivity {
//
//    private static String projectRootPath;
//    private FileChangeListener fileChangeListener;
//
//    @Override
//    public void runActivity(Project project) {
//        // 创建并注册 FileChangeListener
//        fileChangeListener = new FileChangeListener();
//        VirtualFileManager.getInstance().addVirtualFileListener(fileChangeListener, project);
//        System.out.println("FileChangeListener 已注册.");
//
//        // 获取项目根目录
//        projectRootPath = project.getBasePath();
//        if (projectRootPath != null) {
//            System.out.println("项目根目录: " + projectRootPath);
//        }
//
//        // 创建VersionHistory文件夹
//        createVersionHistoryDirectory(projectRootPath);
//    }
//
//    private void createVersionHistoryDirectory(String projectRootPath) {
//        if (projectRootPath == null) {
//            return;
//        }
//
//        Path versionHistoryDir = Paths.get(projectRootPath, "VersionHistory");
//        try {
//            Files.createDirectories(versionHistoryDir); // 创建目录，如果不存在
//            System.out.println("VersionHistory 目录创建成功: " + versionHistoryDir);
//        } catch (IOException ex) {
//            System.err.println("无法创建 VersionHistory 目录: " + ex.getMessage());
//        }
//    }
//
//    // 静态方法获取项目根路径
//    public static String getProjectRootPath() {
//        return projectRootPath;
//    }
//}