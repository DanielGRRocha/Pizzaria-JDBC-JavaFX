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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.bo.AdditionalBO;
import model.bo.ClientBO;
import model.bo.OrderBO;
import model.bo.OrderStatusBO;
import model.bo.PizzaBO;
import model.bo.PizzaSizeBO;
import model.exceptions.ValidationException;
import model.vo.Additional;
import model.vo.Client;
import model.vo.Order;
import model.vo.OrderStatus;
import model.vo.Pizza;
import model.vo.PizzaSize;





public class OrderFormController implements Initializable {//classe Sujeito (emite o evento), instancia a interface DataChangeListener
	
	//criar a dependência (ak instanciar criando um set)
	private Order entity;
	private OrderBO service;
	private ClientBO clientService;
	private PizzaBO pizzaService;
	private PizzaSizeBO pizzaSizeService;
	private AdditionalBO additionalService;
	private OrderStatusBO orderStatusService;

	//a classe em questão vai guardar uma lista de objetos interessados em receber o evento (criar método para adicioná-los na lista)
	private List<InterDataChangeListener> dataChangeListeners = new ArrayList<>(); 
	
	
	//declarações componentes da tela
	@FXML
	private TextField textFieldId;
	
	@FXML
	private DatePicker dpMoment;
	
	@FXML
	private TextField textFieldTotal;
	
	@FXML
	private ComboBox<Client> comboBoxClient;
	
	@FXML
	private ComboBox<Pizza> comboBoxPizza;
	
	@FXML
	private ComboBox<PizzaSize> comboBoxPizzaSize;
	
	@FXML
	private ComboBox<Additional> comboBoxAdditional;
	
	@FXML
	private ComboBox<OrderStatus> comboBoxOrderStatus;
	
	ObservableList<Client> obsListClient;
	
	ObservableList<Pizza> obsListPizza;
	
	ObservableList<PizzaSize> obsListPizzaSize;
	
	ObservableList<Additional> obsListAdditional;
	
	ObservableList<OrderStatus> obsListOrderStatus;
	
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	//set Inventory (agora controlador tem instância do Inventory)
	public void setOrder(Order entity) {
		this.entity = entity;
	}
	//set InventoryBO (agora controlador tem instância da classe de serviços do Inventory)
	public void setServices(OrderBO service, ClientBO clientService, PizzaBO pizzaService, PizzaSizeBO pizzaSizeService, AdditionalBO additionalService, OrderStatusBO orderStatusService) {
		this.service = service;
		this.clientService = clientService;
		this.pizzaService = pizzaService;
		this.pizzaSizeService = pizzaSizeService;
		this.additionalService = additionalService;
		this.orderStatusService = orderStatusService;
	}
	//método para adicionar objetos na lista "dataChangeListeners"
	public void subscribeDataChangeListener(InterDataChangeListener listener) {//objetos que implementarem a interface "DataChangeListener" podem se inscrever para receber o evento da classe
		dataChangeListeners.add(listener);
	}
	
	//método para associar objetos da lista ao comboBox (botar o método no SellerListController) // método que inicializa o comboBox lá em baixo
	public void loadAssociatedObjects() {
		//client
		if (clientService == null) {
			throw new IllegalStateException("ClientBO was null");
		}
		List<Client> listClient = clientService.findAll();
		obsListClient = FXCollections.observableArrayList(listClient);
		comboBoxClient.setItems(obsListClient);
		
		//pizza
		if (pizzaService == null) {
			throw new IllegalStateException("PizzaBO was null");
		}
		List<Pizza> listPizza = pizzaService.findAll();
		obsListPizza = FXCollections.observableArrayList(listPizza);
		comboBoxPizza.setItems(obsListPizza);
		
		//pizzaSize
		if (pizzaSizeService == null) {
			throw new IllegalStateException("PizzaSizeBO was null");
		}
		List<PizzaSize> listPizzaSize = pizzaSizeService.findAll();
		obsListPizzaSize = FXCollections.observableArrayList(listPizzaSize);
		comboBoxPizzaSize.setItems(obsListPizzaSize);
		
		//additional
		if (additionalService == null) {
			throw new IllegalStateException("AdditionalBO was null");
		}
		List<Additional> listAdditional = additionalService.findAll();
		obsListAdditional = FXCollections.observableArrayList(listAdditional);
		comboBoxAdditional.setItems(obsListAdditional);
		
		//orderStatus
		if (orderStatusService == null) {
			throw new IllegalStateException("OrderStatusBO was null");
		}
		List<OrderStatus> listOrderStatus = orderStatusService.findAll();
		obsListOrderStatus = FXCollections.observableArrayList(listOrderStatus);
		comboBoxOrderStatus.setItems(obsListOrderStatus);
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
		
//		initializeComboBoxPizzaSize(); //chamar no "updateFormData"
		Utils.formatComboBox(comboBoxPizzaSize, pizzaSize -> pizzaSize.getName());
		
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
	

	

}//class
