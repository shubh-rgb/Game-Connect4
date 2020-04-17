package com.shubhankar.connectFour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
       FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();
        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu(){

        Menu fileMenu = new Menu("File");
        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> controller.resetGame());

        MenuItem resetGame = new MenuItem("ResetGame");
        resetGame.setOnAction(actionEvent -> controller.resetGame());
        SeparatorMenuItem separateItem = new SeparatorMenuItem();
        MenuItem quitGame = new MenuItem("Quit Game");
        quitGame.setOnAction(actionEvent -> {
            Platform.exit();
            System.exit(0);
        });

        fileMenu.getItems().addAll(newGame,resetGame,separateItem,quitGame);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutGame = new MenuItem("About Game");
        aboutGame.setOnAction(actionEvent -> alertAboutGame());
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Developer");
        aboutMe.setOnAction(actionEvent -> alertAboutMe());
        helpMenu.getItems().addAll(aboutGame,separatorMenuItem,aboutMe);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);

        return menuBar;
    }

    private void alertAboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Developer");
        alert.setHeaderText("Shubhankar Tripathi");
        alert.setContentText("I am just a beginner");
        alert.show();
    }

    private void alertAboutGame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four Game");
        alert.setHeaderText("How To Play?");
        alert.setContentText("Connect Four is a two-player connection game in which"+
                "the players first choose a color and then take turns dropping colored"+
                "discs from the top into a seven-column, six-row vertically suspended grid."+
                "The pieces fall straight down, occupying the next available space within the column."+
                "The objective of the game is to be the first to form a horizontal, vertical, or diagonal"+
                "line of four of one's own discs. Connect Four is a solved game. The first player can always"+
                        "win by playing the right moves.");
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
