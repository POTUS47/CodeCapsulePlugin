package plugin.capsule;
import com.github.weisj.jsvg.S;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import plugin.ui.MessageShow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class SaveDocs extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        boolean isRevertVersion=false;
        VersionManage.saveMannually(isRevertVersion,null);
    }
}

