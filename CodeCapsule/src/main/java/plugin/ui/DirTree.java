package plugin.ui;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.contents.DiffContent;
import plugin.capsule.LoadDocsCompressed;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import com.intellij.ui.components.JBScrollPane;
import plugin.capsule.SnapShot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import com.intellij.diff.contents.DocumentContentImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.ui.Messages;
import com.intellij.diff.DiffManager;
import com.intellij.diff.requests.SimpleDiffRequest;



import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

//调用前需自行保证所需版本已经到Temp里了

//DirTree dirTree= new DirTree(project);
//dirTree.loadDirectory("Version2");
//
////切换方法
// dirTree.loadDirectory("Version1");

// 自定义面板类，用于展示项目的目录树
class DirTree extends JPanel {
    private  Project project;
    private JTree tree;

    public DirTree(Project project) {
        // 设置面板布局为边界布局
        super(new BorderLayout());
        // 初始化项目
        this.project = project;
    }

    // 加载指定目录下的文件树，relativePath 为相对于根目录的路径
    public void loadDirectory(String Version)  {

        //加载版本文件到Temp/src里
        //VersionManage.CheckOneVersion(Version);

        // 清空当前面板中的所有组件
        this.removeAll();
        // 重新验证组件
        this.revalidate();
        // 重绘面板
        this.repaint();

        //添加个Label
//        JLabel label=new JLabel(Version);
//        label.setBorder(new EmptyBorder(5, 10, 5, 10));
//        this.add(label, BorderLayout.NORTH);

        // 获取项目根目录的 VirtualFile 对象
        VirtualFile projectBaseDir = project.getBaseDir();

        if (projectBaseDir != null) {
            // 在 projectBaseDir 中检索相对路径，获取目标目录
            VirtualFile dir = projectBaseDir.findFileByRelativePath("VersionHistory/Temp/src");
            if (dir != null) {
                // 创建根节点
                DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(dir.getName());
                dir.refresh(false, true);
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
                        VirtualFile selectedFile = findFile(selectedFileName, dir);
                        if (selectedFile != null&&!selectedFile.isDirectory()) {
                            //openFileInEditor(selectedFile);
                            // 获取文件的相对路径，相对于 VersionHistory/Temp/src/
                            String relativePath = getRelativePath(selectedFile, dir);
                            System.out.println("Selected file relative path: " + relativePath);

                            diffShow(relativePath);
                        }
                    }
                });

                // 可以滚动的面板，包含 JTree
                JBScrollPane scrollPane = new JBScrollPane(tree);
                this.add(scrollPane, BorderLayout.CENTER);
            }
        }


    }

    //递归在文件夹下寻找文件
    private VirtualFile findFile(String fileName, VirtualFile dir) {
        for (VirtualFile child : dir.getChildren()) {
            if(fileName.equals(child.getName())) {
                return child;
            }else{
                VirtualFile result=findFile(fileName, child);
                if(result!=null) {
                    return result;
                }
            }
        }
        return null;
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

    // 在编辑器中打开文件
    private void openFileInEditor(VirtualFile file) {
        if (file != null) {
            // 使用 IntelliJ API 打开文件
            com.intellij.openapi.fileEditor.FileEditorManager.getInstance(project).openFile(file, true);
        }
    }

    //差异比较,传入比较版本相对于src的路径
    private void diffShow(String relativePath){
        String currentVersionFilePath=project.getBasePath()+"/src/"+relativePath;
        String otherVersionFilePath= project.getBasePath()+"/VersionHistory/Temp/src/"+relativePath;

        File currentVersionFile = new File(currentVersionFilePath);
        File otherVersionFile = new File(otherVersionFilePath);
        try {
            // 检查文件是否存在，并且是文件而非目录
            boolean currentVersionExists = currentVersionFile.exists() && currentVersionFile.isFile();
            boolean otherVersionExists = otherVersionFile.exists() && otherVersionFile.isFile();

            // 加载文件内容
            String snapshotContentA = "";
            String snapshotContentB = "";

            if (currentVersionExists) {
//                // 反序列化并解压缩获取 Snapshot
//                SnapShot a = LoadDocsCompressed.loadSnapshot(currentVersionFilePath);
//                snapshotContentA = a.getContent();
                snapshotContentA = Files.readString(Paths.get(currentVersionFilePath));
            }

            if (otherVersionExists) {
//                // 反序列化并解压缩获取 Snapshot
//                SnapShot b = LoadDocsCompressed.loadSnapshot(otherVersionFilePath);
//                snapshotContentB = b.getContent();
                snapshotContentB = Files.readString(Paths.get(otherVersionFilePath));
            }

            // 如果两方都不存在
            if (!currentVersionExists && !otherVersionExists) {
                return;
            }

            // 创建 DiffContent 对象
//            DiffContent content1 = createContentFromString(snapshotContentA);
//            DiffContent content2 = createContentFromString(snapshotContentB);
            DiffContent content1 = DiffContentFactory.getInstance().create(snapshotContentA);
            DiffContent content2 = DiffContentFactory.getInstance().create(snapshotContentB);
            // 创建并显示差异请求
            SimpleDiffRequest request = new SimpleDiffRequest("String Comparison", content1, content2, "Current Version", "Other Version");
            DiffManager.getInstance().showDiff(project, request);

        } catch (IOException ex) {
            Messages.showErrorDialog("加载文件时发生错误: " + ex.getMessage(), "错误");
        }
    }

    private DiffContent createContentFromString(String content) {
        Document document = EditorFactory.getInstance().createDocument(content);
        return new DocumentContentImpl(document);
//        return DiffContentFactory.getInstance().create(project, content);
    }
    // 根据文件获取其相对于特定目录（如 VersionHistory/Temp/src/）的路径
    private String getRelativePath(VirtualFile file, VirtualFile baseDir) {
        // 获取基准目录路径
        String baseDirPath = baseDir.getPath();
        // 获取文件的完整路径
        String filePath = file.getPath();

        // 判断文件路径是否以基准目录路径为前缀
        if (filePath.startsWith(baseDirPath)) {
            // 返回相对路径，去掉前缀部分并去除前导斜杠
            return filePath.substring(baseDirPath.length() + 1);
        }
        return file.getName(); // 如果无法计算相对路径，则返回文件名
    }

}
