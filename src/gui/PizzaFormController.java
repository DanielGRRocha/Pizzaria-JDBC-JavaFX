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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.bo.PizzaBO;
import model.bo.PizzaSizeBO;
import model.exceptions.ValidationException;
import model.vo.Pizza;
import model.vo.PizzaSize;





public class PizzaFormController implements Initializable {//classe Sujeito (emite o evento), instancia a interface DataChangeListener
	
	//criar a depend�ncia (ak instanciar criando um set)
	private Pizza entity;
	private PizzaBO service;
	private PizzaSizeBO pizzaSizeService;
	//a classe em quest�o vai guardar uma lista de objetos interessados em receber o evento (criar m�todo para adicion�-los na lista)
	private List<InterDataChangeListener> dataChangeListeners = new ArrayList<>(); 
	
	
	//declara��es componentes da tela
	@FXML
	private TextField textFieldId;
	
	@FXML
	private TextField textFieldName;
	
	@FXML
	private TextField textFieldPrice;
	
	@FXML
	private ComboBox<PizzaSize> comboBoxPizzaSize;
	
	ObservableList<PizzaSize> obsList;

	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorPrice;
	
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	//set Inventory (agora controlador tem inst�ncia do Inventory)
	public void setPizza(Pizza entity) {
		this.entity = entity;
	}
	//set InventoryBO (agora controlador tem inst�ncia da classe de servi�os do Inventory)
	public void setServices(PizzaBO service, PizzaSizeBO pizzaSizeService) {
		this.service = service;
		this.pizzaSizeService = pizzaSizeService;
	}
	//m�todo para adicionar objetos na lista "dataChangeListeners"
	public void subscribeDataChangeListener(InterDataChangeListener listener) {//objetos que implementarem a interface "DataChangeListener" podem se inscrever para receber o evento da classe
		dataChangeListeners.add(listener);
	}
	
	//m�todo para associar objetos da lista ao comboBox (botar o m�todo no SellerListController) // m�todo que inicializa o comboBox l� em baixo
	public void loadAssociatedObjects() {
		if (pizzaSizeService == null) {
			throw new IllegalStateException("PizzaSizeBO was null");
		}
		List<PizzaSize> list = pizzaSizeService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxPizzaSize.setItems(obsList);
	}
	
	
	//eventos/////////////////////////////////////////////////////////////////
	//evento bot�o salvar
	@FXML
	public void onBtSaveAction(ActionEvent event) { //instanciar o seller e salvar no banco de dados
		if(entity == null){//programa��o defensiva - verificar se a servi�o e entidade est�o nulos
			throw new IllegalStateException("Entity was null");
		}
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners(); //autoexplicativo, chama o m�todo que ir� notificar os listeners que deu certo e eles ir�o atualizar a tabela
			//fechar a janela ap�s salvar (adiciona esse par�mentro no m�todo)
			Utils.currentStage(event).close();
			
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	
	private Pizza getFormData() {//pegar os dados do formulario
		Pizza obj = new Pizza();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(textFieldId.getText()));//chamar o m�todo para passar String para Integer
		
		//fazer valida��o para que o campo n�o seja vazio
		//name
		if(textFieldName.getText() == null || textFieldName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(textFieldName.getText());
		
		//price
		if(textFieldPrice.getText() == null || textFieldPrice.getText().trim().equals("")) {
			exception.addError("price", "Field can't be empty");
		}
		obj.setPrice(Utils.tryParseToDouble(textFieldPrice.getText()));
		
		//PizzaSize
		obj.setPizzaSize(comboBoxPizzaSize.getValue());
		
		//erros
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}
	
	private void notifyDataChangeListeners() {//m�todo que ir� notificar os listeners(m�todo da interface) que deu certo e eles ir�o atualizar a tabela (ser� chamado pelas classes)
	//obs: SellerFormController emite o evento. � preciso fazer uma implementa��o na classe que recebe o evento e executa o m�todo de atualizar a lista (SellerListController)	
		for (InterDataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}
	
	//evento bot�o cancelar
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		
		Utils.currentStage(event).close();//fechar janela ap�s apertar
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	//colocar as constraints (ou limita��es de inser��o)
	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldId);
		Constraints.setTextFieldMaxLength(textFieldName, 60);
		Constraints.setTextFieldDouble(textFieldPrice);
		
		initializeComboBoxPizzaSize(); //chamar no "updateFormData"
		
	}
	
	//m�todo updateFormData que ir� pegar os dados do seller e popular as caixinhas de texto do formul�rio (settar)
	public void updateFormData() {
		//programa��o defensiva: criar um if para verificar entity est� valendo nulo
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		textFieldId.setText(String.valueOf(entity.getId())); //converte Interger em String
		textFieldName.setText(entity.getName());
		Locale.setDefault(Locale.US);
		textFieldPrice.setText(String.format("%.2f", entity.getPrice()));
		
		if(entity.getPizzaSize() == null) {
			comboBoxPizzaSize.getSelectionModel().selectFirst();
		} else {
			comboBoxPizzaSize.setValue(entity.getPizzaSize());
		}
		
		
	}
	
	//m�todo respons�vel por pegar os erros que est�o na exce��o (ValidationException) e escrev�-los nas tela (label vazio que foi deixado no SceneBuilder)
	private void setErrorMessages(Map<String,String> errors) {//no m�todo do evento do bot�o save, inserir ValidationException (catch)
		Set<String> fields = errors.keySet();
		
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
		labelErrorPrice.setText((fields.contains("price") ? errors.get("price") : ""));
		
	}
	
	private void initializeComboBoxPizzaSize() {//chamar no InitializeNodes
		Callback<ListView<PizzaSize>, ListCell<PizzaSize>> factory = lv -> new ListCell<PizzaSize>() {
			@Override
			protected void updateItem(PizzaSize item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxPizzaSize.setCellFactory(factory);
		comboBoxPizzaSize.setButtonCell(factory.call(null));
	}
	

}//class
