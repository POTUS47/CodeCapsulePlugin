package plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class VersionHistoryUI implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // 显示版本列表的面板
        JPanel versionPanel = createVersionPanel(toolWindow);

        // 将版本面板设置为工具窗口的内容
        toolWindow.getComponent().setLayout(new BorderLayout());
        toolWindow.getComponent().add(versionPanel, BorderLayout.CENTER);
    }

    // 创建显示版本按钮的面板
    private JPanel createVersionPanel(ToolWindow toolWindow) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        String[] versions = {"Version 1", "Version 2", "Version 3"};

        for (String version : versions) {
            JButton versionButton = new JButton(version);
            versionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 切换到空面板
                    switchToEmptyPanel(toolWindow);
                }
            });
            buttonPanel.add(versionButton);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 5))); // 添加间距
        }

        // 用于滚动的面板
        return buttonPanel;
    }

    // 切换到空面板，并添加返回按钮
    private void switchToEmptyPanel(ToolWindow toolWindow) {
        JPanel emptyPanel = new JPanel(new BorderLayout());

        // 添加返回按钮
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 切换回版本面板
                JPanel versionPanel = createVersionPanel(toolWindow);
                toolWindow.getComponent().removeAll();
                toolWindow.getComponent().add(versionPanel, BorderLayout.CENTER);
                toolWindow.getComponent().revalidate();
                toolWindow.getComponent().repaint();
            }
        });

        // 将返回按钮放在左上角
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        emptyPanel.add(topPanel, BorderLayout.NORTH);

        // 替换工具窗口的内容为空面板
        toolWindow.getComponent().removeAll();
        toolWindow.getComponent().add(emptyPanel, BorderLayout.CENTER);
        toolWindow.getComponent().revalidate();
        toolWindow.getComponent().repaint();
    }
}

//
//import com.intellij.openapi.project.DumbAware;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.wm.ToolWindow;
//import com.intellij.openapi.wm.ToolWindowFactory;
//import com.intellij.ui.content.Content;
//import com.intellij.ui.content.ContentFactory;
//import org.apache.commons.lang3.StringUtils;
//import org.jetbrains.annotations.NotNull;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.Calendar;
//import java.util.Objects;
//
//final class VersionHistoryUI implements ToolWindowFactory, DumbAware {
//    @Override
//    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
//        // 创建一个面板来放置按钮
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // 竖直排列按钮
//
//        // 示例版本列表
//        String[] versions = {"Version 1", "Version 2", "Version 3", "Version 4", "Version 5", "Version 6"};
//
//        // 为每个版本创建一个按钮
//        for (String version : versions) {
//            JButton versionButton = new JButton(version);
//            versionButton.addActionListener(e -> toolWindow.hide(null)); // 添加事件监听器
//            buttonPanel.add(versionButton);
//            buttonPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 添加间距
//        }
//
//        // 将按钮面板放入滚动面板中
//        JScrollPane scrollPane = new JScrollPane(buttonPanel);
//        scrollPane.setPreferredSize(new Dimension(200, 300)); // 设置滚动面板大小
//
//        // 将滚动面板添加到工具窗口
//        toolWindow.getComponent().setLayout(new BorderLayout());
//        toolWindow.getComponent().add(scrollPane, BorderLayout.CENTER);
//    }
//    // 切换到空面板，并添加返回按钮
//    private void switchToEmptyPanel(ToolWindow toolWindow) {
//        JPanel emptyPanel = new JPanel(new BorderLayout());
//
//        // 添加返回按钮
//        JButton backButton = new JButton("Back");
//        backButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // 切换回版本面板
//                JPanel versionPanel = createVersionPanel(toolWindow);
//                toolWindow.getComponent().removeAll();
//                toolWindow.getComponent().add(versionPanel, BorderLayout.CENTER);
//                toolWindow.getComponent().revalidate();
//                toolWindow.getComponent().repaint();
//            }
//        });
//
//        // 将返回按钮放在左上角
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        topPanel.add(backButton);
//        emptyPanel.add(topPanel, BorderLayout.NORTH);
//
//        // 替换工具窗口的内容为空面板
//        toolWindow.getComponent().removeAll();
//        toolWindow.getComponent().add(emptyPanel, BorderLayout.CENTER);
//        toolWindow.getComponent().revalidate();
//        toolWindow.getComponent().repaint();
//    }
//
//    // 按钮点击事件处理器
////    private class VersionButtonListener implements ActionListener {
////        private final String version;
////
////        public VersionButtonListener(String version) {
////            this.version = version;
////        }
////
////        @Override
////        public void actionPerformed(ActionEvent e) {
////            // 弹出对话框显示选中的版本信息
////            JOptionPane.showMessageDialog(null, "Selected version: " + version, "Version Info", JOptionPane.INFORMATION_MESSAGE);
////        }
////    }
//
////    @Override
////    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
////        // 创建版本列表
////        DefaultListModel<String> listModel = new DefaultListModel<>();
////        listModel.addElement("Version 1");
////        listModel.addElement("Version 2");
////        listModel.addElement("Version 3");
////
////        JList<String> versionList = new JList<>(listModel);
////        versionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
////
////        // 添加鼠标点击事件处理
//////        versionList.addMouseListener(new MouseAdapter() {
//////            @Override
//////            public void mouseClicked(MouseEvent e) {
//////                if (e.getClickCount() == 2) { // 双击选择版本
//////                    String selectedVersion = versionList.getSelectedValue();
//////                    if (selectedVersion != null) {
//////                        showVersionDialog(selectedVersion);
//////                    }
//////                }
//////            }
//////        });
////
////        // 设置布局并添加到工具窗口
////        JPanel panel = new JPanel(new BorderLayout());
////        panel.add(new JScrollPane(versionList), BorderLayout.CENTER);
////        toolWindow.getComponent().setLayout(new BorderLayout());
////        toolWindow.getComponent().add(panel, BorderLayout.CENTER);
////    }
//
//    private void showVersionDialog(String version) {
//        // 创建一个对话框显示版本信息
//        JOptionPane.showMessageDialog(null, "Selected version: " + version, "Version Info", JOptionPane.INFORMATION_MESSAGE);
//    }
////        CalendarToolWindowContent toolWindowContent = new CalendarToolWindowContent(toolWindow);
////        Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
////        toolWindow.getContentManager().addContent(content);
////    }
//
//    private static class CalendarToolWindowContent {
//
//        private static final String CALENDAR_ICON_PATH = "/toolWindow/Calendar-icon.png";
//        private static final String TIME_ZONE_ICON_PATH = "/toolWindow/Time-zone-icon.png";
//        private static final String TIME_ICON_PATH = "/toolWindow/Time-icon.png";
//
//        private final JPanel contentPanel = new JPanel();
//        private final JLabel currentDate = new JLabel();
//        private final JLabel timeZone = new JLabel();
//        private final JLabel currentTime = new JLabel();
//
//        public CalendarToolWindowContent(ToolWindow toolWindow) {
//            contentPanel.setLayout(new BorderLayout(0, 20));
//            contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
//            contentPanel.add(createCalendarPanel(), BorderLayout.PAGE_START);
//            contentPanel.add(createControlsPanel(toolWindow), BorderLayout.CENTER);
//            updateCurrentDateTime();
//        }
//
//        @NotNull
//        private JPanel createCalendarPanel() {
//            JPanel calendarPanel = new JPanel();
//            setIconLabel(currentDate, CALENDAR_ICON_PATH);
//            setIconLabel(timeZone, TIME_ZONE_ICON_PATH);
//            setIconLabel(currentTime, TIME_ICON_PATH);
//            calendarPanel.add(currentDate);
//            calendarPanel.add(timeZone);
//            calendarPanel.add(currentTime);
//            return calendarPanel;
//        }
//
//        private void setIconLabel(JLabel label, String imagePath) {
//            label.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePath))));
//        }
//
//        @NotNull
//        private JPanel createControlsPanel(ToolWindow toolWindow) {
//            JPanel controlsPanel = new JPanel();
//            JButton refreshDateAndTimeButton = new JButton("Refresh");
//            refreshDateAndTimeButton.addActionListener(e -> updateCurrentDateTime());
//            controlsPanel.add(refreshDateAndTimeButton);
//            JButton hideToolWindowButton = new JButton("Hide");
//            hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
//            controlsPanel.add(hideToolWindowButton);
//            return controlsPanel;
//        }
//
//        private void updateCurrentDateTime() {
//            Calendar calendar = Calendar.getInstance();
//            currentDate.setText(getCurrentDate(calendar));
//            timeZone.setText(getTimeZone(calendar));
//            currentTime.setText(getCurrentTime(calendar));
//        }
//
//        private String getCurrentDate(Calendar calendar) {
//            return calendar.get(Calendar.DAY_OF_MONTH) + "/"
//                    + (calendar.get(Calendar.MONTH) + 1) + "/"
//                    + calendar.get(Calendar.YEAR);
//        }
//
//        private String getTimeZone(Calendar calendar) {
//            long gmtOffset = calendar.get(Calendar.ZONE_OFFSET); // offset from GMT in milliseconds
//            String gmtOffsetString = String.valueOf(gmtOffset / 3600000);
//            return (gmtOffset > 0) ? "GMT + " + gmtOffsetString : "GMT - " + gmtOffsetString;
//        }
//
//        private String getCurrentTime(Calendar calendar) {
//            return getFormattedValue(calendar, Calendar.HOUR_OF_DAY) + ":" + getFormattedValue(calendar, Calendar.MINUTE);
//        }
//
//        private String getFormattedValue(Calendar calendar, int calendarField) {
//            int value = calendar.get(calendarField);
//            return StringUtils.leftPad(Integer.toString(value), 2, "0");
//        }
//
//        public JPanel getContentPanel() {
//            return contentPanel;
//        }
//
//    }
//
//}
////import com.intellij.diff.DiffContentFactory;
////import com.intellij.diff.contents.DiffContent;
////import com.intellij.diff.DiffContentFactory;
////import com.intellij.diff.requests.SimpleDiffRequest;
////import com.intellij.openapi.project.Project;
////import com.intellij.openapi.ui.DialogWrapper;
////import com.intellij.ui.components.JBScrollPane;
////import com.intellij.ui.content.ContentManager;
////import com.intellij.ui.content.impl.ContentManagerImpl;
////
////import javax.swing.*;
////import javax.swing.tree.DefaultMutableTreeNode;
////import java.awt.*;
////import com.intellij.openapi.project.Project;
////import com.intellij.diff.requests.SimpleDiffRequest;
////import com.intellij.diff.DiffContentFactory;
////import com.intellij.diff.DiffRequestFactory;
////import com.intellij.diff.requests.SimpleDiffRequest;
////import com.intellij.openapi.project.Project;
////import com.intellij.openapi.ui.DialogWrapper;
////import com.intellij.ui.components.JBScrollPane;
////
////import javax.swing.*;
////import javax.swing.tree.DefaultMutableTreeNode;
////import java.awt.*;
////
////
////import java.io.IOException;
////import java.nio.file.Path;
////import java.nio.file.Paths;
////import java.io.FileOutputStream;
////import java.io.ObjectOutputStream;
////import java.util.zip.GZIPOutputStream;
////
////import com.intellij.openapi.ui.DialogWrapper;
////import com.intellij.ui.components.JBScrollPane;
////
////import javax.swing.*;
////import java.awt.*;
////
////public class VersionHistoryUI extends DialogWrapper {
////
////    private JTree fileTree;  // 用于显示文件目录树
////    private Project project; // 保存项目对象
////    private JPanel diffPanel; // 用于显示Diff内容
////
////    public VersionHistoryUI(Project project) {
////        super(true); // 设置为可模态
////        this.project = project; // 保存项目对象
////        setTitle("文件目录和版本对比");
////        init(); // 初始化对话框
////    }
////
////    @Override
////    protected JComponent createCenterPanel() {
////        JPanel mainPanel = new JPanel(new BorderLayout());
////
////        // 创建Diff展示面板
////        diffPanel = new JPanel(new BorderLayout());
////        mainPanel.add(diffPanel, BorderLayout.CENTER);
////
////        // 创建并填充文件目录树
////        //fileTree = createFileTree();
////        JBScrollPane scrollPane = new JBScrollPane(fileTree);
////        mainPanel.add(scrollPane, BorderLayout.CENTER);
////
////        String content1 = "loadContentForVersion1(selectedNode)"; // 从版本1加载内容
////        String content2 = "loadContentForVersion2(selectedNode)"; // 从版本2加载内容
////        showDiff(content1, content2); // 显示差异
////
////        // 这里可以添加其他组件，例如版本对比的区域
////
////        return mainPanel;
////    }
////
////    // 在 SaveFilesDialog 类中继续添加
////
////
////    private void showDiff(String content1, String content2) {
////        // 创建 DiffContent 和 SimpleDiffRequest
////        DiffContentFactory contentFactory = DiffContentFactory.getInstance();
////        DiffContent originalContent = contentFactory.create(project, content1);
////        DiffContent currentContent = contentFactory.create(project, content2);
////
////        SimpleDiffRequest diffRequest = new SimpleDiffRequest("版本对比", originalContent, currentContent, "旧版本", "新版本");
////    }
////
////
////
////    @Override
////    protected void doOKAction() {
////        // 点击确定按钮的操作
////        super.doOKAction();
////    }
////}
