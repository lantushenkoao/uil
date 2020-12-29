package com.lantushenkoao.iul;

import com.lantushenkoao.iul.processors.CombinedTemplateProcessor;
import com.lantushenkoao.iul.processors.SplitTemplateProcessor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Controller {

    public static final String OUTPUT_FILE_NAME = "out.docx";
    private TemplateFactory templateFactory;
    @FXML
    private Label lbMessage;

    @FXML
    private ChoiceBox cbTemplate;

    @FXML
    public void initialize(){
        this.templateFactory = new TemplateFactory();
        for(TemplateFactory.Template template: templateFactory.list()){
            cbTemplate.getItems().add(template.getDisplayName());
        }
        cbTemplate.setValue(TemplateFactory.TEMPLATE_COMBINED);
    }

    @FXML
    public void onSelectDirectoryClick(ActionEvent event){
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Выберите папку с файлами");

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        File directory = fileChooser.showDialog(stage);
        lbMessage.setText("");
        MessageHandler handler = new MessageHandler(lbMessage);
        try {
            List<FileDescriptor> fds = new FileDescriptorLoader(handler).load(directory.getPath());

            if(fds.size() < 1){
                handler.handleMessage("В выбранном каталоге нет файлов");
                return;
            }
            if(cbTemplate.getValue().equals(TemplateFactory.TEMPLATE_SPLIT)){
                SplitTemplateProcessor combinedTemplateProcessor = new SplitTemplateProcessor(handler, fds, OUTPUT_FILE_NAME);
                combinedTemplateProcessor.writeFilesToTable();
            } else if (cbTemplate.getValue().equals(TemplateFactory.TEMPLATE_COMBINED)){
                CombinedTemplateProcessor processor = new CombinedTemplateProcessor(handler, fds, OUTPUT_FILE_NAME);
                processor.writeFilesToTable();
            }

        }catch (Exception e){
            handler.handleError(e);
        }
    }
}
