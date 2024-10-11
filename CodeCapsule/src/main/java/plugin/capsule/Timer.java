package plugin.capsule;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.nio.file.Path;

import java.util.List;

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

            // 当计时器达到15秒时，执行回调函数
            if (currentTime == 15) {

                onTimeReached15();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void onTimeReached15() {

        Path baseDir=StartUp.getVersionHistoryPath();
        List<Path>paths=FileChangeListener.getChangedFilePath();
        System.out.println("开始检查是否需要保存");

        //检测变量格式
        //实际输出为：
        //C:\Users\10510\IdeaProjects\ untitled2\src\nihao.java
        //C:\Users\10510\IdeaProjects\ untitled2\src\hao.java
        //C:\Users\10510\IdeaProjects\ untitled2\src\huai.java

        paths.forEach(path -> System.out.println(path.toString()));
//        CheckVersionSave.checkVersionSave(paths, baseDir.toString());
    }

    public void resetTimer() {
        currentTime = 0; // 重置时间为16秒
        // 如果需要重新开始计时器，可以在这里调用startTimer()
    }

}