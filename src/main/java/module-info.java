module dev.nelmin.java {
    requires javafx.controls;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.jetbrains.annotations;
    requires com.google.gson;
    requires java.logging;
    requires static lombok;
    requires com.sun.jna.platform;
    requires com.sun.jna;

    exports dev.nelmin.java;
    exports dev.nelmin.java.configuration;
    exports dev.nelmin.java.application;
}
