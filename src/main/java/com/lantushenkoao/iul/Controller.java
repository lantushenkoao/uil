package com.lantushenkoao.iul;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Controller {

    public static final String OUTPUT_FILE_NAME = "out.docx";

    @FXML
    private Label lbMessage;

    @FXML
    public void onSelectDirectoryClick(ActionEvent event){
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Выберите папку с файлами");

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        File directory = fileChooser.showDialog(stage);
        MessageHandler handler = new MessageHandler(lbMessage);
        try {
            List<FileDescriptor> fds = new FileDescriptorLoader().load(directory.getPath());

            if(fds.size() < 1){
                handler.handleMessage("В выбранном каталоге нет файлов");
                return;
            }
            WordProcessor processor = new WordProcessor(handler, fds, OUTPUT_FILE_NAME);
            processor.writeFilesToTable();
        }catch (Exception e){
            handler.handleError(e);
        }
    }
}
