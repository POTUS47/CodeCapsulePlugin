//package testgroup.ply.demo;
//
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.actionSystem.CommonDataKeys;
//import com.intellij.openapi.fileEditor.FileDocumentManager;
//import com.intellij.openapi.fileEditor.FileEditor;
//import com.intellij.openapi.fileEditor.FileEditorManager;
//import com.intellij.openapi.vfs.VirtualFile;
//import com.intellij.openapi.wm.ToolWindow;
//import com.intellij.openapi.editor.Document;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.editor.event.DocumentListener;
//import com.intellij.openapi.ui.Messages;
//import com.intellij.ui.components.JBList;
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.util.ArrayList;
//import java.nio.file.Paths;
//import java.util.List;
//
//public class SaveDocs extends AnAction {
//    private List<String> versionHistory = new ArrayList<>();
//
//    @Override
//    public void actionPerformed(AnActionEvent e) {
//        // 获取当前文档
//        Editor editor = e.getData(CommonDataKeys.EDITOR);
//        if (editor == null) {
//            Messages.showErrorDialog("没有打开的编辑器", "错误");
//            return;
//        }
//        Document document = editor.getDocument();
//
//        // 记录当前版本
//        versionHistory.add(document.getText());
//
//        // 添加文档监听器
//        document.addDocumentListener(new DocumentListener() {
//            @Override
//            public void documentChanged(com.intellij.openapi.editor.event.DocumentEvent event) {
//                // 当文档更改时记录版本
//                versionHistory.add(document.getText());
//            }
//        });
//
//        // 保存历史版本
//        String content = document.getText();
//        String projectPath = e.getProject().getBasePath();
//        String customPath = Paths.get(projectPath, "history_file.txt").toString();
//        try {
//            Files.createDirectories(Paths.get(customPath).getParent());
//            Files.write(Paths.get(customPath), content.getBytes(StandardCharsets.UTF_8));
//            Messages.showInfoMessage("文件已保存到" + customPath, "成功");
//        } catch (IOException e1) {
//            Messages.showErrorDialog("保存文件失败", "错误");
//        }
//
//
//        // 显示历史版本
//        showVersionHistory();
//    }
//
//    private void showVersionHistory() {
//        JList<String> versionList = new JBList<>(versionHistory.toArray(new String[0]));
//        versionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//
////        versionList.addListSelectionListener(e -> {
////            if (!e.getValueIsAdjusting()) {
////                String selectedVersion = versionList.getSelectedValue();
////                // 恢复选定版本
////                restoreVersion(selectedVersion);
////            }
////        });
//
////        JPanel panel = new JPanel();
////        panel.setLayout(new BorderLayout()); // 设置布局为 BorderLayout
////        panel.add(new JScrollPane(versionList), BorderLayout.CENTER);
////        Messages.showDialog(panel, "选择版本", "版本历史", new String[]{"确定"}, 0, null);
//
////        // 创建自定义对话框
////        JPanel panel = new JPanel(new BorderLayout());
////        panel.add(new JScrollPane(versionList), BorderLayout.CENTER);
////        Messages.showDialog(panel, "选择版本", "版本历史", new String[]{"确定"}, 0, null);
//
//    }
//
//    private void restoreVersion(String version) {
//        // 这里添加逻辑来恢复文档内容
//        // 例如，使用Document的setText()方法
//    }
//}

package plugin.capsule;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContentImpl;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;

public class SaveDocs extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取项目
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        // 定义两个字符串
        String code1 = "nihao1\nnihao3\nnihao3";
        String code2 = "nihao1\nnihao3\nnihao3\nnihao2\nnihao3\nnihao3";

        // 获取两个 DocumentContent
        DiffContent content1 = createContentFromString(code1);
        DiffContent content2 = createContentFromString(code2);

        // 创建并显示差异请求
        SimpleDiffRequest request = new SimpleDiffRequest("String Comparison", content1, content2, "Code 1", "Code 2");
        DiffManager.getInstance().showDiff(project, request);
    }

    // 将字符串转换为 DiffContent
    private DiffContent createContentFromString(String content) {
        Document document = EditorFactory.getInstance().createDocument(content);
        return new DocumentContentImpl(document);
    }
}
