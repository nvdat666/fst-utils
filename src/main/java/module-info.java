module com.example.fstutils {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
//    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires json.simple;
    requires poi.ooxml;
    requires poi;
    requires org.json;
    requires org.fxmisc.richtext;

    opens com.example.fstutils to javafx.fxml;
    exports com.example.fstutils;
}