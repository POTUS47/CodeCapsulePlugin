package plugin.capsule;
import com.github.weisj.jsvg.S;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import plugin.ui.MessageShow;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class SaveDocs extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<Path> changedFile=FileChangeListener.getChangedFilePath();//调用接口查看当前有修改的文件列表
        if (changedFile.isEmpty()) {
            System.out.println("列表为空，没有更改的文件。");
        } else {

            CheckVersionSave check=new CheckVersionSave();
            try {
                boolean hasChanged=check.checkVersionSave(changedFile, StartUp.getVersionHistoryPath().toString());
                if(hasChanged){
                    String VersionTitle=MessageShow.showInputDialog("版本名称","请输入版本名称","NewVersion");
                    //问题：需要获取当前最新的版本号！！！！
                    String VersionDes=MessageShow.showInputDialog("版本描述","请输入版本描述","无描述");
                    // 接下来，需要根据最新版本号，获取当前版本号。使用相应接口修改txt的前两行
                    // 还需要对回退版本在描述里面标明？？？
                    System.out.println("列表中有更改的文件。");
                    MessageShow.showNotification("操作成功", "版本已成功保存！");
                }
                else {
                    System.out.println("列表中没有更改的文件。");
                    MessageShow.showNotification("操作失败", "没有要保存的内容！");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

