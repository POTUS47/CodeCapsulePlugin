package plugin.capsule;
//import plugin.ui.VersionHistoryUI;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContentImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.diff.DiffManager;
import com.intellij.openapi.project.Project;
import com.intellij.diff.requests.SimpleDiffRequest;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;



public class SaveDocs extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject(); // 获取当前项目
        if (project == null) {
            Messages.showErrorDialog("没有打开的项目", "错误");
            return;
        }
//        VersionHistoryUI dialog = new VersionHistoryUI(project);
//        dialog.show(); // 显示对话框
//        Editor editor = e.getData(CommonDataKeys.EDITOR);
//        if (editor == null) {
//            Messages.showErrorDialog("没有打开的编辑器", "错误");
//            return;
//        }
//        //待修改--------------------------
//        String targetDirectory1 = e.getProject().getBasePath();  // 使用项目的根目录
//        String realPath=targetDirectory1+"/src/main/java/plugin/capsule/SnapShot.java";
//        //------------------------------
//        CompressDocs.CompressDocs(realPath,"Version1");



//
//        try {
//            // 反序列化并解压缩获取 Snapshot
//            String targetDirectory = e.getProject().getBasePath();  // 使用项目根目录
//            Path binaryFilePath = Paths.get(targetDirectory, file.getName() + "_snapshot.gz");
//            SnapShot a = LoadDocsCompressed.loadSnapshot(binaryFilePath.toString());
//
//            // 将解压缩后的文件内容转换为 String
//            String snapshotContent = a.getContent();
//            String code2 = "nihao1\nnihao3\nnihao3\nnihao2\nnihao3\nnihao3";123
//            //String currentContent = editor.getDocument().getText();
//
//            DiffContent content1 = createContentFromString(snapshotContent);
//            DiffContent content2 = createContentFromString(code2);888
//
//            // 获取项目
//        Project project = e.getProject();1
//        if (project == null) {
//            return;
//        }
//            // 创建并显示差异请求
//        SimpleDiffRequest request = new SimpleDiffRequest("String Comparison", content1, content2, "snap", "Code 2");
//        DiffManager.getInstance().showDiff(project, request);
//
//
//
//        } catch (IOException | ClassNotFoundException ex) {
//            Messages.showErrorDialog("加载文件快照时发生错误: " + ex.getMessage(), "错误");
//        }


    }

    // 将字符串转换为 DiffContent
    private DiffContent createContentFromString(String content) {
        Document document = EditorFactory.getInstance().createDocument(content);
        return new DocumentContentImpl(document);
    }
}
