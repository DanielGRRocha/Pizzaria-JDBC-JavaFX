package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import model.bo.PizzaBO;
import model.exceptions.ValidationException;
import model.vo.Pizza;




public class PizzaFormController implements Initializable {
	
	private Pizza entity;
	private PizzaBO service;

	private List<InterDataChangeListener> dataChangeListeners = new ArrayList<>(); 
	
	@FXML
	private TextField textFieldId;
	
	@FXML
	private TextField textFieldName;
	
	@FXML
	private TextField textFieldPriceSmallPizza;
	
	@FXML
	private TextField textFieldPriceMediumPizza;
	
	@FXML
	private TextField textFieldPriceBigPizza;

	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorPriceSmallPizza;
	
	@FXML
	private Label labelErrorPriceMediumPizza;
	
	@FXML
	private Label labelErrorPriceBigPizza;
	
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setPizza(Pizza entity) {
		this.entity = entity;
	}
	
	public void setService(PizzaBO service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(InterDataChangeListener listener) {//objetos que implementarem a interface "DataChangeListener" podem se inscrever para receber o evento da classe
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
	
	
	private Pizza getFormData() {
		Pizza obj = new Pizza();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(textFieldId.getText()));
		
		//fazer validação para que o campo não seja vazio
		//name
		if(textFieldName.getText() == null || textFieldName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(textFieldName.getText());
		
		//priceSmallPizza
		if(textFieldPriceSmallPizza.getText() == null || textFieldPriceSmallPizza.getText().trim().equals("")) {
			exception.addError("priceSmallPizza", "Field can't be empty");
		}
		obj.setPriceSmallPizza(Utils.tryParseToDouble(textFieldPriceSmallPizza.getText()));
		
		//priceMediumPizza
		if (textFieldPriceMediumPizza.getText() == null || textFieldPriceMediumPizza.getText().trim().equals("")) {
			exception.addError("priceMediumPizza", "Field can't be empty");
		}
		obj.setPriceMediumPizza(Utils.tryParseToDouble(textFieldPriceMediumPizza.getText()));
		
		//priceBigPizza
		if (textFieldPriceBigPizza.getText() == null || textFieldPriceBigPizza.getText().trim().equals("")) {
			exception.addError("priceBigPizza", "Field can't be empty");
		}
		obj.setPriceBigPizza(Utils.tryParseToDouble(textFieldPriceBigPizza.getText()));

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
		Constraints.setTextFieldDouble(textFieldPriceSmallPizza);
		Constraints.setTextFieldDouble(textFieldPriceMediumPizza);
		Constraints.setTextFieldDouble(textFieldPriceBigPizza);
		
	}
	
	//método updateFormData que irá pegar os dados do seller e popular as caixinhas de texto do formulário (settar)
	public void updateFormData() {
		
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		textFieldId.setText(String.valueOf(entity.getId())); //converte Interger em String
		textFieldName.setText(entity.getName());
		Locale.setDefault(Locale.US);
		textFieldPriceSmallPizza.setText(String.format("%.2f", entity.getPriceSmallPizza()));
		textFieldPriceMediumPizza.setText(String.format("%.2f", entity.getPriceMediumPizza()));
		textFieldPriceBigPizza.setText(String.format("%.2f", entity.getPriceBigPizza()));
		
		
	}
	
	//método responsável por pegar os erros que estão na exceção (ValidationException) e escrevê-los nas tela (label vazio que foi deixado no SceneBuilder)
	private void setErrorMessages(Map<String,String> errors) {//no método do evento do botão save, inserir ValidationException (catch)
		Set<String> fields = errors.keySet();
		
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
		labelErrorPriceSmallPizza.setText((fields.contains("priceSmallPizza") ? errors.get("priceSmallPizza") : ""));
		labelErrorPriceMediumPizza.setText((fields.contains("priceMediumPizza") ? errors.get("priceMediumPizza") : ""));
		labelErrorPriceBigPizza.setText((fields.contains("priceBigPizza") ? errors.get("priceBigPizza") : ""));
		
	}
	
}//class
