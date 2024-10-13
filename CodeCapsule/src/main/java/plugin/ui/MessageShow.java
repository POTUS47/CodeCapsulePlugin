package plugin.ui;

import com.intellij.notification.Notifications;
import com.intellij.openapi.ui.Messages;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;

public class MessageShow {
    // 需要获取回答的提示窗
    public String showInputDialog(String messageTitle,String question,String defaultAnswer) {
        String message = question; // 提示信息
        String title = messageTitle; // 弹窗标题
        String defaultValue = defaultAnswer; // 输入框中的默认值，可以为空字符串
        // 显示输入对话框并获取用户输入
        String userInput = Messages.showInputDialog(message, title, Messages.getQuestionIcon(), defaultValue, null);
        // 检查用户是否输入了内容
        if (userInput != null) {
            // 用户输入了内容
            System.out.println("用户输入：" + userInput);
            // 可以在这里进行后续的逻辑处理
        } else {
            // 用户点击了取消或关闭了对话框
            System.out.println("用户取消了输入");
        }
        return userInput;
    }
    //提示窗(需要用户点击关闭)
    public void showMessage(String messageTitle,String messageContent) {
        String message = messageContent; // 提示内容
        String title = messageTitle; // 弹窗标题
        // 显示提示框
        Messages.showMessageDialog(message, title, Messages.getInformationIcon());
    }
    //提示窗(不需要用户点击关闭)
    public void showNotification(String messageTitle, String messageContent) {
        // 创建通知对象
        Notification notification = new Notification(
                "Custom Notification Group", // 通知组的ID（可以自定义）
                messageTitle,                // 通知标题
                messageContent,              // 通知内容
                NotificationType.INFORMATION // 通知类型（信息、警告或错误）
        );
        // 显示通知
        Notifications.Bus.notify(notification);
    }

}
