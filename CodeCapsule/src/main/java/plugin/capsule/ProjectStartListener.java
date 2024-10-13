package plugin.capsule;

import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.project.Project;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

//该类用于在项目启动时实例化启动类StartUp
//区分项目启动和IntelliJ IDEA启动
public class ProjectStartListener  implements ProjectActivity {

    private final StartUp startUp;

    // 构造函数，在项目启动时实例化 StartUp 单例
    public ProjectStartListener() {
        // 通过单例模式获取 StartUp 的唯一实例
        this.startUp = StartUp.getInstance();
    }

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        // 在协程中执行的代码
        System.out.println("协程项目启动: " + project.getName());

        // 测试使用
        try {
            VersionManage.revertVersion("Version2");
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return Unit.INSTANCE;  // 返回 Kotlin 的 Unit 实例
    }
}