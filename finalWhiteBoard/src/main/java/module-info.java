module com.example.finalwhiteboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;

    opens com.example.finalwhiteboard to javafx.fxml;
    exports com.example.finalwhiteboard;
}