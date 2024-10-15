package plugin.capsule;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/////////////////////////////项目级别的项目关闭监听
public class ProjectCloseListener implements ProjectManagerListener {

    @Override
    public void projectClosing(@NotNull Project project) {
        System.out.println("项目关闭");

        //static 成员是类级别的属性或方法，它们在内存中只会存在一个副本，而不是每个实例都有一个独立的副本。
        //因此，当你通过类名（StartUp.timer）或实例（StartUp.getInstance().timer）来访问时，实际上访问
        //的都是同一个 static 成员。
        try {
            // 通过 timer 调用 onTimeReached15() 方法，判断需不需要保存最新版本
            StartUp.timer.onTimeReached15();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        StartUp.clearInstance();
    }
}

