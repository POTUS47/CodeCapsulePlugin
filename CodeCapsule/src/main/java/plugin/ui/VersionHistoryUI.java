package plugin.ui;
import plugin.capsule.StartUp;
import plugin.capsule.StartUp.*;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;
import plugin.capsule.VersionManage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public final class VersionHistoryUI implements ToolWindowFactory {
    private Project project;
    private DirTree dirTree;
    private JButton backButton;
    private JButton revertButton;
    private JLabel versionId;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        dirTree= new DirTree(project);
        versionId=new JLabel("Version ID");
        // 添加返回按钮
        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 切换回版本面板
                JPanel versionPanel = getVersionInfo(toolWindow);
                toolWindow.getComponent().removeAll();
                toolWindow.getComponent().add(versionPanel, BorderLayout.CENTER);
                toolWindow.getComponent().revalidate();
                toolWindow.getComponent().repaint();
            }
        });
        revertButton = new JButton("Revert");
        revertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 切换到空面板
                try {
                    VersionManage.RevertToOneVersion(revertButton.getName());
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        // 显示版本列表的面板
        JPanel versionPanel = getVersionInfo(toolWindow);

        // 创建工具栏，并在上方添加刷新按钮
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);  // 工具栏不可拖动

        // 创建刷新按钮
        JButton refreshButton = new JButton("Refresh");
        toolBar.add(refreshButton);  // 将刷新按钮添加到工具栏

        // 添加刷新按钮的点击事件
        refreshButton.addActionListener(e -> {
            // 刷新操作，比如重新加载版本列表
            JPanel updatedVersionPanel = getVersionInfo(toolWindow);
            toolWindow.getComponent().remove(versionPanel);  // 移除旧版本面板
            toolWindow.getComponent().add(updatedVersionPanel, BorderLayout.CENTER);  // 添加更新后的版本面板
            toolWindow.getComponent().revalidate();
            toolWindow.getComponent().repaint();
        });

        // 将工具栏添加到工具窗口的顶部
        toolWindow.getComponent().setLayout(new BorderLayout());
        //toolWindow.getComponent().add(toolBar, BorderLayout.NORTH);  // 将工具栏放在顶部
        toolWindow.getComponent().add(versionPanel, BorderLayout.CENTER);  // 将版本面板放在中央

    }

    public JPanel getVersionInfo(ToolWindow toolWindow){
        JPanel versionPanel = new JPanel();
        // 获取版本目录路径

        File versionHistoryDir = new File(project.getBasePath() + "/VersionHistory");

        // 检查版本历史目录是否存在
        if (versionHistoryDir.exists() && versionHistoryDir.isDirectory()) {
            File[] versionDirs = versionHistoryDir.listFiles(File::isDirectory);

            if (versionDirs != null) {//如果VersionHistory里有版本文件夹
                for (File versionDir : versionDirs) {
                    File versionInfoFile = new File(versionDir, "version_info.txt");

                    if (versionInfoFile.exists()) {
                        try {
                            // 读取 version_info.txt 文件
                            BufferedReader reader = new BufferedReader(new FileReader(versionInfoFile));
                            String versionName = reader.readLine(); // 第一行：版本名称
                            String versionDescription = reader.readLine(); // 第二行：版本描述
                            String versionTime = reader.readLine(); // 第三行：记录时间
                            reader.close();

                            // 创建按钮，显示版本信息
                            String Id= versionDir.getName();
                            JButton versionButton = createVersionButton(Id,toolWindow,versionName, versionDescription, versionTime);
                            versionPanel.add(versionButton);
                            versionPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 添加间距


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return versionPanel;
    }

    // 创建包含版本信息的按钮
    private JButton createVersionButton(String buttonId,ToolWindow toolWindow,String versionName, String versionDescription, String versionTime) {
        // 组合版本信息文本
        String buttonText = "<html><b>Version:</b> " + versionName +
                "<br><b>Description:</b> " + versionDescription +
                "<br><b>Time:</b> " + versionTime + "</html>";

        // 创建按钮
        JButton versionButton = new JButton(buttonText);
        versionButton.setName(buttonId);
        versionButton.setHorizontalAlignment(SwingConstants.LEFT); // 设置文本左对齐
        // 设置按钮的背景颜色、前景色和字体
        versionButton.setBackground(Color.GRAY);  // 背景色
        versionButton.setForeground(Color.cyan);  // 前景色
        versionButton.setFont(new Font("Monaco", Font.PLAIN, 14));  // 字体
        versionButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));  // 设置按钮的边框

        // 设置按钮的悬停效果
        versionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                versionButton.setBackground(Color.LIGHT_GRAY);  // 鼠标悬停时背景色变浅
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                versionButton.setBackground(Color.GRAY);  // 鼠标移开时恢复背景色
            }
        });

        // 设置按钮点击事件
        versionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 切换到空面板
                try {
                    switchToEmptyPanel(versionButton.getName(),toolWindow);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        return versionButton;
    }


    // 切换到空面板，并添加返回按钮
    private void switchToEmptyPanel(String Id,ToolWindow toolWindow) throws IOException, ClassNotFoundException {
        VersionManage.CheckOneVersion(Id);
        versionId.setText(Id);
        revertButton.setName(Id);
        // 将返回按钮放在左上角
        JPanel topPanel = new JPanel(new BorderLayout()); // 使用 BorderLayout
        topPanel.setBorder(new EmptyBorder(5, 15, 5, 10));
        // 创建并添加 versionId 到左侧
        topPanel.add(versionId, BorderLayout.CENTER); // 将 versionId 放在左边
        // 创建并添加 backButton 到右侧
        topPanel.add(backButton, BorderLayout.WEST); // 将 backButton 放在右边
        topPanel.add(revertButton, BorderLayout.EAST);

        dirTree.loadDirectory("Version2");
        dirTree.add(topPanel, BorderLayout.NORTH);


        // 替换工具窗口的内容为空面板
       toolWindow.getComponent().removeAll();
        toolWindow.getComponent().add(dirTree, BorderLayout.CENTER);
//        toolWindow.getComponent().revalidate();
//        toolWindow.getComponent().repaint();

    }
}