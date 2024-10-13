package plugin.ui;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.impl.ContentManagerImpl;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import com.intellij.openapi.project.Project;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;

public class VersionHistoryUI extends DialogWrapper {

    private JTree fileTree;  // 用于显示文件目录树
    private Project project; // 保存项目对象
    private JPanel diffPanel; // 用于显示Diff内容

    public VersionHistoryUI(Project project) {
        super(true); // 设置为可模态
        this.project = project; // 保存项目对象
        setTitle("文件目录和版本对比");
        init(); // 初始化对话框
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建Diff展示面板
        diffPanel = new JPanel(new BorderLayout());
        mainPanel.add(diffPanel, BorderLayout.CENTER);

        // 创建并填充文件目录树
        //fileTree = createFileTree();
        JBScrollPane scrollPane = new JBScrollPane(fileTree);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        String content1 = "loadContentForVersion1(selectedNode)"; // 从版本1加载内容
        String content2 = "loadContentForVersion2(selectedNode)"; // 从版本2加载内容
        showDiff(content1, content2); // 显示差异

        // 这里可以添加其他组件，例如版本对比的区域

        return mainPanel;
    }

    // 在 SaveFilesDialog 类中继续添加


    private void showDiff(String content1, String content2) {
        // 创建 DiffContent 和 SimpleDiffRequest
        DiffContentFactory contentFactory = DiffContentFactory.getInstance();
        DiffContent originalContent = contentFactory.create(project, content1);
        DiffContent currentContent = contentFactory.create(project, content2);

        SimpleDiffRequest diffRequest = new SimpleDiffRequest("版本对比", originalContent, currentContent, "旧版本", "新版本");
    }



    @Override
    protected void doOKAction() {
        // 点击确定按钮的操作
        super.doOKAction();
    }
}
