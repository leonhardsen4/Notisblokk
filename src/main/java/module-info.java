module com.leonhardsen.notisblokk {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.jetbrains.annotations;
    requires java.desktop;
    requires javafx.web;
    requires jdk.compiler;

    opens com.leonhardsen.notisblokk.view to javafx.fxml;
    exports com.leonhardsen.notisblokk.view;
    opens com.leonhardsen.notisblokk.controller to javafx.fxml;
    exports com.leonhardsen.notisblokk.controller;
    opens com.leonhardsen.notisblokk.model to javafx.fxml;
    exports com.leonhardsen.notisblokk.model;
    opens com.leonhardsen.notisblokk.dao to javafx.fxml;
    exports com.leonhardsen.notisblokk.dao;
    opens com.leonhardsen.notisblokk.utils to javafx.fxml;
    exports com.leonhardsen.notisblokk.utils;



}
