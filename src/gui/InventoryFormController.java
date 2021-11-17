package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.InterDataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.bo.InventoryBO;
import model.exceptions.ValidationException;
import model.vo.Inventory;



public class InventoryFormController implements Initializable {
	
	private Inventory entity;
	private InventoryBO service;

	private List<InterDataChangeListener> dataChangeListeners = new ArrayList<>(); 
	
	
	@FXML
	private TextField textFieldId;
	
	@FXML
	private TextField textFieldName;
	
	@FXML
	private TextField textFieldQuantity;

	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorQuantity;
	
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	

	public void setInventory(Inventory entity) {
		this.entity = entity;
	}
	
	public void setService(InventoryBO service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(InterDataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	
	@FXML
	public void onBtSaveAction(ActionEvent event) { 
		if(entity == null){
			throw new IllegalStateException("Entity was null");
		}
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners(); 
		
			Utils.currentStage(event).close();
			
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	
	private Inventory getFormData() {
		Inventory obj = new Inventory();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(textFieldId.getText()));//chamar o método para passar String para Integer
		
		//fazer validação para que o campo não seja vazio
		//name
		if(textFieldName.getText() == null || textFieldName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(textFieldName.getText());
		
		//quantity
		if(textFieldQuantity.getText() == null || textFieldQuantity.getText().trim().equals("")) {
			exception.addError("quantity", "Field can't be empty");
		}
		obj.setQuantity(Utils.tryParseToInt(textFieldQuantity.getText()));
		
		//erros
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}
	
	private void notifyDataChangeListeners() {
		
		for (InterDataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}
	

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		
		Utils.currentStage(event).close();
	}

	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldId);
		Constraints.setTextFieldMaxLength(textFieldName, 60);
		Constraints.setTextFieldInteger(textFieldQuantity);
		
	}
	
	
	public void updateFormData() {
	
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		textFieldId.setText(String.valueOf(entity.getId()));
		textFieldName.setText(entity.getName());
		textFieldQuantity.setText(String.valueOf(entity.getQuantity()));
		
		
	}
	
	//método responsável por pegar os erros que estão na exceção (ValidationException) e escrevê-los nas tela (label vazio que foi deixado no SceneBuilder)
	private void setErrorMessages(Map<String,String> errors) {//no método do evento do botão save, inserir ValidationException (catch)
		Set<String> fields = errors.keySet();
		
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
		labelErrorQuantity.setText((fields.contains("quantity") ? errors.get("quantity") : ""));
		
	}
	

}//class
