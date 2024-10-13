package plugin.ui;
import plugin.capsule.VersionManage;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.IOException;

public class DirTree implements ToolWindowFactory, DumbAware {
    private DirTreeWindow dirTreeWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        dirTreeWindow = new DirTreeWindow(project);  // 仅传入 Project 实例
        try {
            setDirectoryPath("Version2");  // 设置默认的目录路径
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 将 dirTreeWindow 作为内容添加到工具窗口中
        toolWindow.getContentManager().addContent(
                toolWindow.getContentManager().getFactory().createContent(dirTreeWindow, "文件树", false)
        );

        // 示例: 通过按钮点击切换到“bin”目录
        JButton switchToBinButton = new JButton("切换到 bin 目录");
        switchToBinButton.addActionListener(e -> {
            try {
                setDirectoryPath("Version1");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });  // 切换到“bin”目录
        dirTreeWindow.add(switchToBinButton, BorderLayout.SOUTH);  // 将按钮添加到窗口底部
    }

    public void setDirectoryPath(String relativePath) throws IOException {
        if (dirTreeWindow != null) {
            dirTreeWindow.loadDirectory(relativePath);  // 调用 loadDirectory 加载目录
        }
    }
}

// 自定义面板类，用于展示项目的目录树
class DirTreeWindow extends JPanel {
    private final Project project;
    private JTree tree;

    public DirTreeWindow(Project project) {
        // 设置面板布局为边界布局
        super(new BorderLayout());
        // 初始化项目
        this.project = project;
    }

    // 加载指定目录下的文件树，relativePath 为相对于根目录的路径
    public void loadDirectory(String Version) throws IOException {
        //加载版本文件到Temp/src里
//        VersionManage.CheckOneVersion(Version);

        // 清空当前面板中的所有组件
        this.removeAll();

        //添加个Label
        JLabel label=new JLabel(Version);
        label.setBorder(new EmptyBorder(5, 10, 5, 10));
        this.add(label, BorderLayout.NORTH);

        // 获取项目根目录的 VirtualFile 对象
        VirtualFile projectBaseDir = project.getBaseDir();

        if (projectBaseDir != null) {
            // 在 projectBaseDir 中检索相对路径，获取目标目录
            VirtualFile dir = projectBaseDir.findFileByRelativePath("VersionHistory/Temp/src");
            if (dir != null) {
                // 创建根节点
                DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(dir.getName());
                createTreeNodes(rootNode, dir); // 生成树节点

                // 创建树模型并绑定到 JTree
                tree = new JTree(new DefaultTreeModel(rootNode));
                tree.setRootVisible(true); // 显示根节点

                // 添加选择监听器以处理文件打开事件
                tree.addTreeSelectionListener(event -> {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (selectedNode != null) {
                        // 获取选中节点的文件名
                        String selectedFileName = (String) selectedNode.getUserObject();
                        // 根据文件名查找对应的 VirtualFile
                        VirtualFile selectedFile = findFileInProject(selectedFileName, dir);
                        if (selectedFile != null) {
                            openFileInEditor(selectedFile);
                        }
                    }
                });

                // 可以滚动的面板，包含 JTree
                JBScrollPane scrollPane = new JBScrollPane(tree);
                this.add(scrollPane, BorderLayout.CENTER);
            }
        }

        // 重新验证组件
        this.revalidate();
        // 重绘面板
        this.repaint();
    }

    // 递归方法生成树节点
    private void createTreeNodes(DefaultMutableTreeNode node, VirtualFile file) {
        if (file.isDirectory()) { // 如果是目录
            // 遍历当前目录下的所有子文件和子目录
            for (VirtualFile child : file.getChildren()) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child.getName()); // 创建子节点
                node.add(childNode);
                createTreeNodes(childNode, child);
            }
        }
    }

    // 根据文件名查找对应的 VirtualFile
    private VirtualFile findFileInProject(String fileName, VirtualFile currentDir) {
        // 遍历当前目录的子文件
        for (VirtualFile file : currentDir.getChildren()) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    // 在编辑器中打开文件
    private void openFileInEditor(VirtualFile file) {
        if (file != null) {
            // 使用 IntelliJ API 打开文件
            com.intellij.openapi.fileEditor.FileEditorManager.getInstance(project).openFile(file, true);
        }
    }
}
