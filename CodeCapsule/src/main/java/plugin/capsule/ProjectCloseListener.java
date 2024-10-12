package plugin.capsule;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

/////////////////////////////项目级别的项目关闭监听
public class ProjectCloseListener implements ProjectManagerListener {

    @Override
    public void projectClosing(@NotNull Project project) {
        System.out.println("项目关闭");
    }
}

