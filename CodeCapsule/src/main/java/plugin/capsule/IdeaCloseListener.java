package plugin.capsule;

import com.intellij.ide.AppLifecycleListener;

//用于监听整个IntelliJ IDEA的关闭
public class IdeaCloseListener implements AppLifecycleListener {
    @Override
    public void appClosing() {
        System.out.println("IDE 正在关闭");
    }
}
