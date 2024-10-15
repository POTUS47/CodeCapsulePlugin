package plugin.ui;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.ui.JBColor;
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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

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
                reload(toolWindow);
            }
        });
        revertButton = new JButton("Revert");
        revertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 切换到空面板
                try {
                    VersionManage.RevertToOneVersion(revertButton.getName());
                    VirtualFile a =getCurrentFile(project);
                    // 保存并刷新文档
                    refreshFileInEditor(a);

                    // 同步 PSI 文档
                    commitAndRefreshDocument(FileEditorManager.getInstance(project).getSelectedTextEditor());

                    // 刷新整个虚拟文件系统
                    refreshProjectFiles();
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        // 显示版本列表的面板
        JScrollPane versionPanel = getVersionInfo(toolWindow);

        // 创建工具栏，并在上方添加刷新按钮
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);  // 工具栏不可拖动

        // 创建刷新按钮
        JButton refreshButton = new JButton("Refresh");
        toolBar.add(refreshButton);  // 将刷新按钮添加到工具栏

        // 添加刷新按钮的点击事件
        refreshButton.addActionListener(e -> {
            // 刷新操作，比如重新加载版本列表
            JScrollPane updatedVersionPanel = getVersionInfo(toolWindow);
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

    public JScrollPane getVersionInfo(ToolWindow toolWindow) {
        JPanel versionPanel = new JPanel();
        versionPanel.setLayout(new BoxLayout(versionPanel, BoxLayout.Y_AXIS)); // 垂直布局

        // 获取版本目录路径
        File versionHistoryDir = new File(project.getBasePath() + "/VersionHistory");

        // 检查版本历史目录是否存在
        if (versionHistoryDir.exists() && versionHistoryDir.isDirectory()) {
            File[] versionDirs = versionHistoryDir.listFiles(file -> file.isDirectory() && !file.getName().equals("Temp"));

            if (versionDirs != null) {
                // 对版本目录根据时间进行排序（读取 version_info.txt 中的时间）
                Arrays.sort(versionDirs, (dir1, dir2) -> {
                    File versionInfoFile1 = new File(dir1, "version_info.txt");
                    File versionInfoFile2 = new File(dir2, "version_info.txt");

                    try {
                        String time1 = getVersionTime(versionInfoFile1);
                        String time2 = getVersionTime(versionInfoFile2);

                        // 按照时间排序，最新的在上面
                        return time2.compareTo(time1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return 0;
                });

                // 遍历排序后的版本目录
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
                            String Id = versionDir.getName();
                            JButton versionButton = createVersionButton(Id, toolWindow, versionName, versionDescription, versionTime);
                            versionPanel.add(versionButton);
//                            versionPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 添加间距

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        // 创建带有滚动条的面板
        JScrollPane scrollPane = new JScrollPane(versionPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(400, 550)); // 可根据需要调整大小

        return scrollPane;
    }

    // 获取版本时间的方法
    private String getVersionTime(File versionInfoFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(versionInfoFile));
        reader.readLine(); // 跳过版本名称
        reader.readLine(); // 跳过版本描述
        String versionTime = reader.readLine(); // 获取版本时间
        reader.close();
        return versionTime;
    }


//    public JPanel getVersionInfo(ToolWindow toolWindow){
//        JPanel versionPanel = new JPanel();
//        // 获取版本目录路径
//
//        File versionHistoryDir = new File(project.getBasePath() + "/VersionHistory");
//
//        // 检查版本历史目录是否存在
//        if (versionHistoryDir.exists() && versionHistoryDir.isDirectory()) {
//            File[] versionDirs = versionHistoryDir.listFiles(File::isDirectory);
//
//            if (versionDirs != null) {//如果VersionHistory里有版本文件夹
//                for (File versionDir : versionDirs) {
//                    File versionInfoFile = new File(versionDir, "version_info.txt");
//
//                    if (versionInfoFile.exists()) {
//                        try {
//                            // 读取 version_info.txt 文件
//                            BufferedReader reader = new BufferedReader(new FileReader(versionInfoFile));
//                            String versionName = reader.readLine(); // 第一行：版本名称
//                            String versionDescription = reader.readLine(); // 第二行：版本描述
//                            String versionTime = reader.readLine(); // 第三行：记录时间
//                            reader.close();
//
//                            // 创建按钮，显示版本信息
//                            String Id= versionDir.getName();
//                            JButton versionButton = createVersionButton(Id,toolWindow,versionName, versionDescription, versionTime);
//                            versionPanel.add(versionButton);
//                            versionPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 添加间距
//
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//        return versionPanel;
//    }

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
//        versionButton.setBackground(Color.GRAY);  // 背景色
//        versionButton.setForeground(Color.cyan);  // 前景色

        //设置按钮透明
        versionButton.setContentAreaFilled(false);
        versionButton.setFont(new Font("Monaco", Font.PLAIN, 14));  // 字体
        //versionButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, JBColor.GRAY));

        // 创建组合边框:底部边框+padding
        Border matteBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, JBColor.GRAY);
        Border paddingBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        versionButton.setBorder(BorderFactory.createCompoundBorder(matteBorder, paddingBorder));

        // 设置按钮的悬停效果
        versionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                versionButton.setContentAreaFilled(true);
                versionButton.setBackground(JBColor.LIGHT_GRAY);  // 鼠标悬停时背景色变浅
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                versionButton.setContentAreaFilled(false);
//                versionButton.setBackground(JBColor.GRAY);  // 鼠标移开时恢复背景色
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
        revertButton.setText("Revert to "+Id);
        // 将返回按钮放在左上角
        JPanel topPanel = new JPanel(new BorderLayout()); // 使用 BorderLayout
        topPanel.setBorder(new EmptyBorder(5, 15, 5, 10));
        // 创建并添加 versionId 到左侧
        //topPanel.add(versionId, BorderLayout.WEST); // 将 versionId 放在左边
        // 创建并添加 backButton 到右侧
        topPanel.add(backButton, BorderLayout.WEST); // 将 backButton 放在右边
        topPanel.add(revertButton, BorderLayout.EAST);

        dirTree.loadDirectory(Id);
        dirTree.add(topPanel, BorderLayout.NORTH);


        // 替换工具窗口的内容为空面板
       toolWindow.getComponent().removeAll();
        toolWindow.getComponent().add(dirTree, BorderLayout.CENTER);
//        toolWindow.getComponent().revalidate();
//        toolWindow.getComponent().repaint();

    }

    public VirtualFile getCurrentFile(Project project) {
        // 获取当前活动的文件
        VirtualFile file = FileEditorManager.getInstance(project).getSelectedFiles()[0];
        if (file != null) {
            System.out.println("当前打开的文件: " + file.getPath());
        } else {
            System.out.println("当前没有打开的文件");
        }
        return file;
    }

    public Editor getCurrentEditor(Project project) {
        // 获取当前活动的编辑器
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            System.out.println("当前打开的编辑器: " + editor.getDocument().getText());
        } else {
            System.out.println("当前没有打开的编辑器");
        }
        return editor;
    }

    public void refreshFileInEditor(VirtualFile file) {
        // 保存并刷新文件
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        fileDocumentManager.saveAllDocuments(); // 保存所有打开的文档

        // 刷新虚拟文件系统
        file.refresh(false, false);
    }
    public void refreshProjectFiles() {
        // 刷新整个虚拟文件系统
        VirtualFileManager.getInstance().syncRefresh();
    }
    public void commitAndRefreshDocument(Editor editor) {
        // 提交所有文档更改并刷新编辑器
        PsiDocumentManager.getInstance(editor.getProject()).commitAllDocuments();
    }
    public void reload(ToolWindow toolWindow) {
        // 切换回版本面板
        JScrollPane versionPanel = getVersionInfo(toolWindow);
        toolWindow.getComponent().removeAll();
        toolWindow.getComponent().add(versionPanel, BorderLayout.CENTER);
        toolWindow.getComponent().revalidate();
        toolWindow.getComponent().repaint();
    }
}