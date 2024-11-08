package plugin.capsule;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.nio.file.Path;
import java.util.List;

//只有StartUp中实例化了Timer
public class Timer {

    private final ScheduledExecutorService scheduler;
    private int currentTime = 16; // 从16秒开始计时

    public Timer() {
        // 初始化定时器
        scheduler = Executors.newSingleThreadScheduledExecutor();
        startTimer();
    }

    //开始计时
    private void startTimer() {
        scheduler.scheduleAtFixedRate(() -> {
            currentTime++;
            System.out.println("当前时间：" + currentTime + "秒");

            // 当计时器达到10秒时，执行回调函数
            if (currentTime == 10) {////////////////
                System.out.println("到达10s");
                try {
                    onTimeReached15();
                } catch (IOException | NoSuchAlgorithmException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    //对外暴露，当项目关闭或IDEA关闭时可手动触发15s的效果
    public void onTimeReached15() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        Path baseDir=StartUp.getVersionHistoryPath();
        List<Path>paths=FileChangeListener.getChangedFilePath();
        System.out.println("baseDir"+baseDir);
        paths.forEach(path -> System.out.println(path.toString()));
        System.out.println("开始检查是否需要保存");

        //检测变量格式
        //实际输出为：
        //C:\Users\10510\IdeaProjects\ untitled2\src\nihao.java
        //C:\Users\10510\IdeaProjects\ untitled2\src\hao.java
        //C:\Users\10510\IdeaProjects\ untitled2\src\huai.java

        //下面两行用来生成版本
        CheckVersionSave check=new CheckVersionSave();
        check.checkVersionSave(paths, baseDir.toString());

    }

    public void resetTimer0() {
        currentTime = 0; // 重置时间为16秒

    }

    public void resetTimer100() {
        currentTime = 100; // 重置时间为100秒
    }

    // 停止并清除定时器
    public void shutdownTimer() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow(); // 立即停止所有正在执行的任务
            System.out.println("计时器已停止");
        }
    }
}