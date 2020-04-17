package com.shubhankar.connectFour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    private static final int COLUMN = 7;
    private static final int ROWS = 6;
    private static final int CIRCLE_DIAMETERS = 70;
    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";

    private boolean isPlayerOneTurn = true;
    private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMN];

    @FXML
    public GridPane rootGridPane;
    @FXML
    public Pane insertedDiscPane;
    @FXML
    public Label playerNameLabel;
    @FXML
    public TextField playerOneTextField;
    @FXML
    public TextField playerTwoTextField;
    @FXML
    public Button setNameButton;

    private boolean isAllowedToInsert = true;
    private static String Player_One;
    private static String Player_two;

    public void createPlayground(){
        Shape rectangleWithHoles = createStructuralGrid();
        rootGridPane.add(rectangleWithHoles,0,1);

        List<Rectangle> rectangleList = createClickableColumn();
        for (Rectangle rectangle : rectangleList) {
            rootGridPane.add(rectangle,0,1);
        }

        setNameButton.setOnAction(event ->{
            Player_One =  playerOneTextField.getText() ;
            Player_two = playerTwoTextField.getText();
        });
    }

    private Shape createStructuralGrid(){
        Shape rectangleWithHoles = new Rectangle((COLUMN+1)*CIRCLE_DIAMETERS,(ROWS+1)*CIRCLE_DIAMETERS);

        for(int row=0;row<ROWS;row++){
            for(int col=0;col<COLUMN;col++){
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETERS / 2.0);
                circle.setCenterX(CIRCLE_DIAMETERS / 2.0);
                circle.setCenterY(CIRCLE_DIAMETERS / 2.0);
                circle.setTranslateX(col*(CIRCLE_DIAMETERS+5)+CIRCLE_DIAMETERS/4.0);
                circle.setTranslateY(row*(CIRCLE_DIAMETERS+5)+CIRCLE_DIAMETERS/4.0);
                circle.setSmooth(true);
                rectangleWithHoles = Shape.subtract(rectangleWithHoles,circle);
            }
        }
        rectangleWithHoles.setFill(Color.WHITE);
        return rectangleWithHoles;
    }

    private List<Rectangle> createClickableColumn(){

        List<Rectangle> rectangleList = new ArrayList<>();

        for(int col=0; col<COLUMN;col++) {
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETERS, (ROWS + 1) * CIRCLE_DIAMETERS);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col*(CIRCLE_DIAMETERS+5)+CIRCLE_DIAMETERS / 4.0);

            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

            final int column = col;
            rectangle.setOnMouseClicked(event -> {
                if(isAllowedToInsert)
                    isAllowedToInsert = false;
                insertDisc(new Disc(isPlayerOneTurn),column);});
            rectangleList.add(rectangle);
        }
        return rectangleList;
    }

    private void insertDisc(Disc disc , int column){
        int row = ROWS-1;
        while(row>=0){
            if(getDiscIfPresent(row,column)==null)
                break;
            row--;
        }
        if(row<0)
            return;

        insertedDiscArray[row][column] = disc;
        insertedDiscPane.getChildren().add(disc);

        disc.setTranslateX(column*(CIRCLE_DIAMETERS+5)+CIRCLE_DIAMETERS / 4.0);


        TranslateTransition trans = new TranslateTransition(Duration.seconds(0.4),disc);
        trans.setToY(row*(CIRCLE_DIAMETERS+5)+CIRCLE_DIAMETERS/4.0);

        int currentRow = row;
        trans.setOnFinished(event -> {
            isAllowedToInsert = true;
            if(gameEnded(currentRow,column)){
                gameOver(); }
            isPlayerOneTurn =!isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn? Player_One : Player_two);
        });
        trans.play();
    }

    private boolean gameEnded(int row, int column) {

        List<Point2D> verticalPoints = IntStream.rangeClosed(row-3,row+3)
                                        .mapToObj(r -> new Point2D(r,column))
                                        .collect(Collectors.toList());

        List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3,column+3)
                .mapToObj(col -> new Point2D(row,col))
                .collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row-3,column+3);

        List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6)
                .mapToObj(i-> startPoint1.add(i, -i))
                .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row-3,column-3);

        List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6)
                .mapToObj(i->startPoint2.add(i,i))
                .collect(Collectors.toList());

        return (checkCombination(verticalPoints) || checkCombination(horizontalPoints)
                || checkCombination(diagonal1Points) || checkCombination(diagonal2Points));
    }

    private boolean checkCombination(List<Point2D> points) {
        int chain =0;
        for (Point2D point:points) {
            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn){
                chain++;
                if (chain == 4) {
                    return true; }
                else chain = 0;}
        }
        return false;
    }

    private Disc getDiscIfPresent(int row, int column){
        if(row>=ROWS || row<0 || column>=COLUMN || column<0)
            return null;

        return insertedDiscArray[row][column];
    }

    private void gameOver(){
        String winner = isPlayerOneTurn ? Player_One : Player_two;
        System.out.println("Winner is: "+winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("Winner is "+winner);
        alert.setContentText("Want to play again?");

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(yesBtn,noBtn);

        Platform.runLater(() ->
        {
            Optional<ButtonType> btnClicked = alert.showAndWait();
            if(btnClicked.isPresent() && btnClicked.get() == yesBtn)
                resetGame();
            else {
                Platform.exit();
                System.exit(0); }
        });
    }

    public void resetGame() {
        insertedDiscPane.getChildren().clear();
        for (int row=0; row<insertedDiscArray.length;row++){
            for (int col = 0; col<insertedDiscArray[row].length;col++)
                insertedDiscArray[row][col] = null;
        }
        isPlayerOneTurn = true;
        playerNameLabel.setText(Player_One);
        createPlayground();
        playerOneTextField = null;
        playerTwoTextField = null;
        Player_One = null;
        Player_two = null;
    }

    private static class Disc extends Circle{

        private final boolean isPlayerOneMove;

        public Disc(boolean isPlayerOneMove){
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(CIRCLE_DIAMETERS/2.0);
            setFill(isPlayerOneMove? Color.valueOf(discColor1):Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETERS/2.0);
            setCenterY(CIRCLE_DIAMETERS/2.0);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
