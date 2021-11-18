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
import model.bo.ClientBO;
import model.exceptions.ValidationException;
import model.vo.Client;


public class ClientFormController implements Initializable {
	

	private Client entity;
	private ClientBO service;

	private List<InterDataChangeListener> dataChangeListeners = new ArrayList<>(); 
	
	
	@FXML
	private TextField textFieldId;
	
	@FXML
	private TextField textFieldName;
	
	@FXML
	private TextField textFieldCpf;
	
	@FXML
	private TextField textFieldPhone;
	
	@FXML
	private TextField textFieldAddress;

	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorCpf;
	
	@FXML
	private Label labelErrorPhone;
	
	@FXML
	private Label labelErrorAddress;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	

	public void setClient(Client entity) {
		this.entity = entity;
	}

	public void setService(ClientBO service) {
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
	
	
	private Client getFormData() {
		Client obj = new Client();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(textFieldId.getText()));//chamar o método para passar String para Integer
		
		//fazer validação para que o campo não seja vazio
		//nome
		if(textFieldName.getText() == null || textFieldName.getText().trim().equals("")) {
			exception.addError("name", "deixe vazio não");
		}
		obj.setName(textFieldName.getText());
		
		//cpf
		if(textFieldCpf.getText() == null || textFieldCpf.getText().trim().equals("")) {
			exception.addError("cpf", "deixe vazio não");
		}
		obj.setCpf(textFieldCpf.getText());
		
		//phone
		if(textFieldPhone.getText() == null || textFieldPhone.getText().trim().equals("")) {
			exception.addError("phone", "deixe vazio não");
		}
		obj.setPhone(textFieldPhone.getText());
		
		//baseSalary
		if(textFieldAddress.getText() == null || textFieldAddress.getText().trim().equals("")) {
			exception.addError("address", "deixe vazio não");
		}
		obj.setAddress(textFieldAddress.getText());
		
		
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
	
	//colocar as constraints (ou limitações de inserção)
	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldId);
		Constraints.setTextFieldMaxLength(textFieldName, 60);
		Constraints.setTextFieldMaxLength(textFieldCpf, 14);
		Constraints.setTextFieldMaxLength(textFieldPhone, 15);
		Constraints.setTextFieldMaxLength(textFieldAddress, 60);
		
		
	}
	
	
	public void updateFormData() {
		
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		textFieldId.setText(String.valueOf(entity.getId()));
		textFieldName.setText(entity.getName());
		textFieldCpf.setText(entity.getCpf());
		textFieldPhone.setText(entity.getPhone());
		textFieldAddress.setText(entity.getAddress());
		
		
	}
	
	//método responsável por pegar os erros que estão na exceção (ValidationException) e escrevê-los nas tela (label vazio que foi deixado no SceneBuilder)
	private void setErrorMessages(Map<String,String> errors) {//no método do evento do botão save, inserir ValidationException (catch)
		Set<String> fields = errors.keySet();
		
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
		labelErrorCpf.setText((fields.contains("cpf") ? errors.get("cpf") : ""));
		labelErrorPhone.setText((fields.contains("phone") ? errors.get("phone") : ""));
		labelErrorAddress.setText((fields.contains("address") ? errors.get("address") : ""));
		
	}
	

}//class
