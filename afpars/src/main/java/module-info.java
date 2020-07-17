module afpars {
    requires org.bytedeco.javacpp;
    requires org.bytedeco.opencv;
    requires kotlin.stdlib;
    requires jfreesvg;
    requires java.desktop;

    requires javafx.controls;
    requires javafx.swing;
    requires javafx.graphics;
    requires javafx.fxml;

    opens ch.fhnw.afpars to javafx.fxml;
    opens ch.fhnw.afpars.ui.controller to javafx.fxml;

    exports ch.fhnw.afpars;
    exports ch.fhnw.afpars.ui.controller;
}