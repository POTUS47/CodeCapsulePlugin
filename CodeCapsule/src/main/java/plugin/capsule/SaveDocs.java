package plugin.capsule;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class SaveDocs extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        boolean isRevertVersion=false;
        VersionManage.saveMannually(isRevertVersion,null);
    }
}

