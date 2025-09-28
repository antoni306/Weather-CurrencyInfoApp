/**
 *
 *  @author Kostuj Antoni S30687
 *
 */

package zad1;




import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Main extends Application {
  public static void main(String[] args) {
    Service s = new Service("Italy");
    String weatherJson = s.getWeather("Rome");
    Double rate1 = s.getRateFor("THB");
    Double rate2 = s.getNBPRate();
    // ...
    // część uruchamiająca GUI
    launch(new String[]{s.getCountry(),s.getCity(),s.getRateForCurr()});
  }
  @Override
  public void start(Stage primaryStage) throws Exception {
    List<String> args=getParameters().getRaw();
    Service service=new Service(args.get(0),args.get(1),args.get(2));
    WebView webView =new WebView();
    WebEngine webEngine=webView.getEngine();
    webEngine.load("https://pl.wikipedia.org/wiki/"+service.getCity());

    SplitPane splitPane=new SplitPane();

    String jsonWeather=service.getWeather(service.getCity());
    TableView<Map.Entry<String,Object>> weatherTable=getTable(jsonWeather,service.getCity());


    Double nbpRate= service.getNBPRate();
    Label nbpLabel=new Label();
    nbpLabel.setText("Kurs NBP: "+service.getCode(service.getCountry())+"->PLN: "+nbpRate.toString());


    nbpLabel.setFont(Font.font("Agency FB Bold",20.0));
    Double rateFor= service.getRateFor(service.getRateForCurr());

    Label rate=new Label();
    rate.setText("Kurs "+service.getCode(service.getCountry())+"->"+service.getRateForCurr()+": "+rateFor);
    rate.setFont(Font.font("Agency FB Bold",20));
    VBox vbox=new VBox();
    vbox.getChildren().addAll(nbpLabel,rate,weatherTable);
    VBox.setVgrow(nbpLabel,Priority.ALWAYS);
    VBox.setVgrow(weatherTable,Priority.ALWAYS);
    VBox.setVgrow(rate,Priority.ALWAYS);
    splitPane.getItems().addAll(webView,vbox);
    splitPane.setDividerPositions(0.5);

    Scene scene=new Scene(splitPane,800,600);

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public TableView<Map.Entry<String,Object>> getTable(String json,String city){
    Gson gson=new Gson();
    Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
    Map<String, Object> map = gson.fromJson(json, mapType);

    ObservableList<Map.Entry<String,Object>> data= FXCollections.observableArrayList(map.entrySet());
    TableView<Map.Entry<String,Object>> table=new TableView<>();

    TableColumn<Map.Entry<String, Object>, String> keyCol = new TableColumn<>("Pogoda: "+city);
    keyCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getKey()));

    TableColumn<Map.Entry<String, Object>, String> valueCol = new TableColumn<>("");
    valueCol.setCellValueFactory(cellData ->{
              String t=cellData.getValue().getKey().toString();
              String value;
              if (t.startsWith("temp") || t.equals("feels_like")){
                  double val=(Math.round(Double.parseDouble( cellData.getValue().getValue().toString()) - 273.15)*100.0)/100.0;

                  value= Double.toString(val).concat(" C");
                  return new SimpleStringProperty( value);
              }else {
                return new SimpleStringProperty(cellData.getValue().getValue().toString());
              }
    });

    table.getColumns().addAll(keyCol, valueCol);
    table.setItems(data);
    table.setItems(data);
    return table;
  }


}
