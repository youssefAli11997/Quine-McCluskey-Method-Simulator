package quine.mccluskey.method.simulator;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainController {
    @FXML
    ListView<String> mintermsList = new ListView<>();
    @FXML
    ListView<String> dontCaresList = new ListView<>();
    @FXML
    TextField mintermsTextField = new TextField();
    @FXML
    TextField dontCaresTextField = new TextField();
    @FXML
    TextArea ap = new TextArea();
    
    Stage stage1;

    public void addSol(String s){
        ap.clear();
        ap.setText(s);
    }
    
    ArrayList<Integer> addedMinTerms = new ArrayList<Integer>();
    ArrayList<Integer> addedDontCares = new ArrayList<Integer>();
    
    MainClass obj = new MainClass();
    
    public void mintermsListAdd(){
        String minterm = mintermsTextField.getText();
        int value = Integer.parseInt(minterm);

        if(addedMinTerms.indexOf(value) > -1 || addedDontCares.indexOf(value) > -1){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Warning !");
            alert.setHeaderText("Duplicated Term");
            alert.setContentText("You have already added this term before.");
            alert.showAndWait();
        }
        else{
            mintermsList.getItems().add(minterm);
            addedMinTerms.add(value);
        }
        mintermsTextField.clear();
    }
     public void dontCaresListAdd(){
        String dontCare = dontCaresTextField.getText();
        int value = Integer.parseInt(dontCare);
        // check if already added
        if(addedDontCares.indexOf(value) > -1 || addedMinTerms.indexOf(value) > -1){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Warning !");
            alert.setHeaderText("Duplicated Term");
            alert.setContentText("You have already added this term before.");
            alert.showAndWait();
        }
        else{
            obj.addDontCare(value);
            dontCaresList.getItems().add(dontCare);
            addedDontCares.add(value);
        }
        dontCaresTextField.clear();
    }
    
    public void aboutButton(){
        Alert about = new Alert(AlertType.INFORMATION);
        about.setTitle("About the developers");
        about.setHeaderText("This project was developed by:");
        about.setContentText("- Mohamed Mashaal (First-year CSED Student)\n- Youssef Ali (First-year CSED Student)");

        about.showAndWait();
    }
    
    public void applyButton(){
        if(addedMinTerms.isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Warning !");
            alert.setHeaderText("No terms");
            alert.setContentText("You have not entered any terms");
            alert.showAndWait();
            return;
        }
        obj = new MainClass();
        for(int i = 0 ; i < addedMinTerms.size() ; i ++)
            obj.addMinterm(addedMinTerms.get(i));
        for(int i = 0 ; i < addedDontCares.size() ; i ++)
            obj.addDontCare(addedDontCares.get(i));
        obj.callQuine();
        obj.getPrimeImplicants();
        obj.createChart();
	obj.printIt();
        obj.printChart();
	obj.printPrime();
	obj.printSols();
    }
    
    public void answerButton() throws IOException{
       if(addedMinTerms.isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Warning !");
            alert.setHeaderText("No terms");
            alert.setContentText("You have not entered any terms");
            alert.showAndWait();
            return;
        }
        addSol(obj.printFormattedSols());
       
    }
    
    public void stepsButton(){
        if(addedMinTerms.isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Warning !");
            alert.setHeaderText("No terms");
            alert.setContentText("You have not entered any terms");
            alert.showAndWait();
            return;
        }
        addSol(obj.finalString());
    }
    
    
    public void clearButton(){
        obj = new MainClass();
        addedDontCares = new ArrayList<Integer>();
        addedMinTerms = new ArrayList<Integer>();
        mintermsList.getItems().clear();
        dontCaresList.getItems().clear();
        ap.clear();
    }
    
    
    public void exitButton(){
        System.exit(0);
    }
    public void readMinTerms() throws FileNotFoundException{
       
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Get the MinTerms File");
        
        File f1 = fileChooser.showOpenDialog(stage1);
        Scanner sc = new Scanner(f1);
        sc.useDelimiter(",[ ]*");
        int term;
        String s;
        while(sc.hasNext()){
            s = sc.next();
            /*if(s.equals("/"))
                continue;*/
            term = Integer.parseInt(s);
            //term = sc.nextInt();
            addedMinTerms.add(term);
         }
    }
    
    
    public void readDontCares() throws FileNotFoundException{
       
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Get the Don't Cares File");
        File f2 = fileChooser.showOpenDialog(stage1);
        Scanner sc2 = new Scanner(f2);
        int term;
        String s;
        while(sc2.hasNext()){
           s = sc2.next();
           term = Integer.parseInt(s);
           addedDontCares.add(term);
        }
    
}
    public void saveFile() throws FileNotFoundException{
        if(addedMinTerms.isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Warning !");
            alert.setHeaderText("No terms");
            alert.setContentText("You have not entered any terms");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save the File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)", "*.txt"));
        fileChooser.setInitialFileName("Output.txt");
        File f3 = fileChooser.showSaveDialog(stage1);
        try{
        PrintWriter writer = new PrintWriter(f3, "UTF-8");
        writer.println(obj.printFormattedSols().replaceAll("\n", System.lineSeparator()));
        writer.println();
        writer.println(obj.finalString().replaceAll("\n", System.lineSeparator()));
        writer.println("Credits : \n-Mohamed Mashaal \n-Youssef Ali\n@CSED2020".replaceAll("\n", System.lineSeparator()));
        writer.close();
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Warning !");
            alert.setHeaderText("No File");
            alert.setContentText("Wrong Things !!!!!!");
            alert.showAndWait();
            return;
        }
    }
}