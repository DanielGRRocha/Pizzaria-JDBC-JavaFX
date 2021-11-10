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
	
	//criar a dependência (ak instanciar criando um set)
	private Pizza entity;
	private PizzaBO service;
	private PizzaSizeBO pizzaSizeService;
	//a classe em questão vai guardar uma lista de objetos interessados em receber o evento (criar método para adicioná-los na lista)
	private List<InterDataChangeListener> dataChangeListeners = new ArrayList<>(); 
	
	
	//declarações componentes da tela
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
	
	//set Inventory (agora controlador tem instância do Inventory)
	public void setPizza(Pizza entity) {
		this.entity = entity;
	}
	//set InventoryBO (agora controlador tem instância da classe de serviços do Inventory)
	public void setServices(PizzaBO service, PizzaSizeBO pizzaSizeService) {
		this.service = service;
		this.pizzaSizeService = pizzaSizeService;
	}
	//método para adicionar objetos na lista "dataChangeListeners"
	public void subscribeDataChangeListener(InterDataChangeListener listener) {//objetos que implementarem a interface "DataChangeListener" podem se inscrever para receber o evento da classe
		dataChangeListeners.add(listener);
	}
	
	//método para associar objetos da lista ao comboBox (botar o método no SellerListController) // método que inicializa o comboBox lá em baixo
	public void loadAssociatedObjects() {
		if (pizzaSizeService == null) {
			throw new IllegalStateException("PizzaSizeBO was null");
		}
		List<PizzaSize> list = pizzaSizeService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxPizzaSize.setItems(obsList);
	}
	
	
	//eventos/////////////////////////////////////////////////////////////////
	//evento botão salvar
	@FXML
	public void onBtSaveAction(ActionEvent event) { //instanciar o seller e salvar no banco de dados
		if(entity == null){//programação defensiva - verificar se a serviço e entidade estão nulos
			throw new IllegalStateException("Entity was null");
		}
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners(); //autoexplicativo, chama o método que irá notificar os listeners que deu certo e eles irão atualizar a tabela
			//fechar a janela após salvar (adiciona esse parâmentro no método)
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
		
		obj.setId(Utils.tryParseToInt(textFieldId.getText()));//chamar o método para passar String para Integer
		
		//fazer validação para que o campo não seja vazio
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
	
	private void notifyDataChangeListeners() {//método que irá notificar os listeners(método da interface) que deu certo e eles irão atualizar a tabela (será chamado pelas classes)
	//obs: SellerFormController emite o evento. É preciso fazer uma implementação na classe que recebe o evento e executa o método de atualizar a lista (SellerListController)	
		for (InterDataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}
	
	//evento botão cancelar
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		
		Utils.currentStage(event).close();//fechar janela após apertar
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	//colocar as constraints (ou limitações de inserção)
	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldId);
		Constraints.setTextFieldMaxLength(textFieldName, 60);
		Constraints.setTextFieldDouble(textFieldPrice);
		
		initializeComboBoxPizzaSize(); //chamar no "updateFormData"
		
	}
	
	//método updateFormData que irá pegar os dados do seller e popular as caixinhas de texto do formulário (settar)
	public void updateFormData() {
		//programação defensiva: criar um if para verificar entity está valendo nulo
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
	
	//método responsável por pegar os erros que estão na exceção (ValidationException) e escrevê-los nas tela (label vazio que foi deixado no SceneBuilder)
	private void setErrorMessages(Map<String,String> errors) {//no método do evento do botão save, inserir ValidationException (catch)
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
