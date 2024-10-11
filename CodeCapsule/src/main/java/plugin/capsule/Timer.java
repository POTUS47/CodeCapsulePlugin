package plugin.capsule;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

            // 当计时器达到30秒时，执行回调函数
            if (currentTime == 15) {
                onTimeReached15();
                resetTimer();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void onTimeReached15() {
        System.out.println("计时器达到了15秒！");

    }

    private void stopTimer() {
        scheduler.shutdown();
    }

    public void resetTimer() {
        currentTime = 0; // 重置时间为16秒
        // 如果需要重新开始计时器，可以在这里调用startTimer()
    }

}