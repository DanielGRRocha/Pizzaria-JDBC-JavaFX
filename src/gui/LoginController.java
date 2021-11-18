package gui;


import gui.util.Alerts;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.bo.UserBO;
import model.vo.User;

public class LoginController {

	@FXML
	private Label erro;
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;

	UserBO userBO = new UserBO();

	public void login(ActionEvent event) throws Exception {

		User user = new User();
		user.setUsername(username.getText());
		user.setPassword(password.getText());

		boolean logged = userBO.login(user);

		if (logged == false) {

			erro.setText("Username ou senha incorretos.");
			erro.setVisible(true);
			username.clear();
			password.clear();
			username.requestFocus();

		} else {

		//Screen.OrderListScreen();
			Utils.currentStage(event).close();
			Alerts.showAlert("Opa", null, "Seja Bem-vindo, "+ user.getUsername() + " :)", AlertType.INFORMATION);
			
		}
	}

	@FXML
	public void usernameKeyPressed(KeyEvent event) {

		username.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER)) {
					password.requestFocus();
					
				}
			}
		});
	}
	
}
