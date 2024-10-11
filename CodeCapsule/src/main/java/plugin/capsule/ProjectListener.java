

////////////////暂时不可以
package plugin.capsule;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
//监听项目的打开和关闭
public class ProjectListener implements com.intellij.openapi.project.ProjectManagerListener{

    //项目打开时初始化
//    public void projectOpened(@NotNull Project pro) {
//        System.out.println("projectOpened");
//    }



    //项目关闭时的操作
    public void projectClosed(@NotNull Project pro) {
        //应该补充一下版本保存？
        System.out.println("projectClosed");
    }
}
