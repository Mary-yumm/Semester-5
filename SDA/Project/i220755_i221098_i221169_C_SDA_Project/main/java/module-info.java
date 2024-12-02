module org.example.sdaprojectvotix {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires java.sql;
    requires spring.web;
    requires reactor.core;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires mysql.connector.j;

    opens votix to javafx.fxml;
    exports votix;
    exports votix.controllers;
    opens votix.controllers to javafx.fxml;
    exports votix.controllers.PollingPC;
    opens votix.controllers.PollingPC to javafx.fxml;
    exports votix.controllers.AdminControllers;
    opens votix.controllers.AdminControllers to javafx.fxml;
    exports votix.models;
    opens votix.models to javafx.fxml;
    exports votix.services;
    opens votix.services to javafx.fxml;
    exports votix.controllers.PopUps;
    opens votix.controllers.PopUps to javafx.fxml;

}