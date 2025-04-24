module dev.nelmin.java.mtiv {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;

    opens dev.nelmin.java.mtiv to javafx.fxml;
    exports dev.nelmin.java.mtiv;
}