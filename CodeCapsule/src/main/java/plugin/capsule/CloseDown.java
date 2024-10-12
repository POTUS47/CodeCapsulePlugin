//package plugin.capsule;
//
//import com.intellij.openapi.application.ApplicationManager;
//import com.intellij.openapi.components.Service;
//import com.intellij.openapi.diagnostic.Logger;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.util.Disposable;
//import com.intellij.openapi.util.Disposer;
//
//import org.jetbrains.annotations.NotNull;
//
//public class CloseDown implements Disposable {
//
//    private static final Logger LOG = Logger.getInstance(CloseDown.class);
//
//    public CloseDown(@NotNull Project project) {
//        Disposer.register(project, this); // 将自己注册为可释放对象
//    }
//
//    @Override
//    public void dispose() {
//        // 在项目关闭时执行的操作
//        System.out.println("Project closed");
//    }
//}