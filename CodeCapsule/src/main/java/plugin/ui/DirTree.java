package plugin.ui;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class DirTree implements ToolWindowFactory, DumbAware {
    private DirTreeWindow dirTreeWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        dirTreeWindow = new DirTreeWindow(project);
        setDirectoryPath("src");  // 设置默认的目录路径
        // 将dirTreeWindow作为内容添加到工具窗口中
        toolWindow.getContentManager().addContent(
                toolWindow.getContentManager().getFactory().createContent(dirTreeWindow, "文件树", false)
        );
    }
    public void setDirectoryPath(String relativePath) {
        if (dirTreeWindow != null) {
            dirTreeWindow.loadDirectory(relativePath);  // 调用loadDirectory加载目录
        }
    }
}

// 自定义面板类，用于展示项目的目录树
class DirTreeWindow extends JPanel {
    private final Project project;
    private JTree tree;

    public DirTreeWindow(Project project) {
        super(new BorderLayout());
        this.project = project;
    }

    // 加载指定目录下的文件树，relativePath为相对于根目录的路径
    public void loadDirectory(String relativePath) {
        // 清空当前面板中的所有组件
        this.removeAll();

        // 获取项目根目录的 VirtualFile 对象,Eg.file://C:/Users/10510/IdeaProjects/test
        VirtualFile projectBaseDir = project.getBaseDir();

        if (projectBaseDir != null) {
            // 在projectBaseDir中检索相对路径，获取目标目录
            VirtualFile dir = projectBaseDir.findFileByRelativePath(relativePath);
            if (dir != null) {
                DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(dir.getName());
                createTreeNodes(rootNode, dir);

                // 创建树模型并绑定到JTree
                tree = new JTree(new DefaultTreeModel(rootNode));
                tree.setRootVisible(true);

                // 滚动面板，包含JTree
                JBScrollPane scrollPane = new JBScrollPane(tree);
                this.add(scrollPane, BorderLayout.CENTER);
            }
        }

        this.revalidate();
        this.repaint();
    }

    //递归方法生成树节点
    //DefaultMutableTreeNode node表示需要添加子节点的父节点
    private void createTreeNodes(DefaultMutableTreeNode node, VirtualFile file) {
        //文件夹才需要添加子节点
        if (file.isDirectory()) {
            //file.getChildren() 返回当前目录下的所有子文件和子目录的 VirtualFile 对象列表
            for (VirtualFile child : file.getChildren()) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child.getName());
                node.add(childNode);
                createTreeNodes(childNode, child);
            }
        }
    }
}
