package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.bo.AdditionalBO;
import model.bo.ClientBO;
import model.bo.InventoryBO;
import model.bo.PizzaBO;


public class MainViewController implements Initializable {
	
	@FXML
	private MenuItem menuItemClient;
	
	@FXML
	private MenuItem menuItemInventory;
	
	@FXML
	private MenuItem menuItemAdditional;
	
	@FXML
	private MenuItem menuItemPizza;
	
	
	@FXML
	public void onMenuItemClientAction() {
		loadView("/gui/ClientList.fxml", (ClientListController controller) -> {
			controller.setClientBO(new ClientBO());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemInventoryAction() {
		loadView("/gui/InventoryList.fxml", (InventoryListController controller) -> {
			controller.setInventoryBO(new InventoryBO());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemAdditionalAction() {
		loadView("/gui/AdditionalList.fxml", (AdditionalListController controller) -> {
			controller.setAdditionalBO(new AdditionalBO());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemPizzaAction() {
		loadView("/gui/PizzaList.fxml", (PizzaListController controller) -> {
			controller.setPizzaBO(new PizzaBO());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			T controller = loader.getController();
			initializingAction.accept(controller);
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	

}//class
