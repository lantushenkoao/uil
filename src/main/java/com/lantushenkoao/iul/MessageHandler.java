package com.lantushenkoao.iul;

import javafx.scene.control.Label;

public class MessageHandler {
    private Label label;
    public MessageHandler(Label label){
        this.label = label;
    }

    public void handleError(Exception e){
        e.printStackTrace();
        label.setText(e.getMessage());
    }

    public void handleMessage(String message){
        label.setText(label.getText() + "\n" + message);
    }
}
