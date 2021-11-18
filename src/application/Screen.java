package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Screen extends Application {

	private static Stage primaryStage;


	@Override
	public void start(Stage primaryStage) throws Exception {
		setPrimaryStage(primaryStage);
		primaryStage.setTitle("Login");
		primaryStage.show();
		LoginScreen();
		
	}

	public static void LoginScreen() throws IOException {

		Parent root = FXMLLoader.load(Screen.class.getResource("/gui/LoginScreen.fxml"));
		Scene cena = new Scene(root);
		primaryStage.setScene(cena);
	}
	
	public static void OrderListScreen() throws IOException {

		Parent root = FXMLLoader.load(Screen.class.getResource("/gui/OrderList2.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
	}


	public static void main(String[] args) {
		launch();
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void setPrimaryStage(Stage primaryStage) {
		Screen.primaryStage = primaryStage;
	}
	

}
