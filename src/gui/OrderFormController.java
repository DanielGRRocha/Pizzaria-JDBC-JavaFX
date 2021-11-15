package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
	private Label labelErrorMoment;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setOrder(Order entity) {
		this.entity = entity;
	}
	
	public void setServices(OrderBO service, ClientBO clientService, PizzaBO pizzaService, PizzaSizeBO pizzaSizeService, AdditionalBO additionalService, OrderStatusBO orderStatusService) {
		this.service = service;
		this.clientService = clientService;
		this.pizzaService = pizzaService;
		this.pizzaSizeService = pizzaSizeService;
		this.additionalService = additionalService;
		this.orderStatusService = orderStatusService;
	}

	public void subscribeDataChangeListener(InterDataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	private void notifyDataChangeListeners() {
		for (InterDataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}
	
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
	
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		
		Utils.currentStage(event).close();//fechar janela após apertar
	}
	
	
	private Order getFormData() {
		Order obj = new Order();
		
		ValidationException exception = new ValidationException("Validation error");
		
		//id
		obj.setId(Utils.tryParseToInt(textFieldId.getText()));
		
		//Client
		obj.setClient(comboBoxClient.getValue());
		
		//Pizza
		obj.setPizza(comboBoxPizza.getValue());
		
		//PizzaSize
		obj.setPizzaSize(comboBoxPizzaSize.getValue());
		
		//Additional
		obj.setAdditional(comboBoxAdditional.getValue());
		
		//OrderStatus
		obj.setOrderStatus(comboBoxOrderStatus.getValue());
		
		//Moment
		if(dpMoment.getValue() == null) {
			exception.addError("moment", "Field can't be empty");
		}
		else {
			Instant instant = Instant.from(dpMoment.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setMoment(Date.from(instant));
		}
		
		//price
		if (textFieldTotal.getText() == null || textFieldTotal.getText().trim().equals("")) {
			exception.addError("total", "Field can't be empty");
		}
		obj.setTotal(Utils.tryParseToDouble(textFieldTotal.getText()));
		
		//erros
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}
	

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldId);
		Utils.formatComboBox(comboBoxClient, client -> client.getName());
		Utils.formatComboBox(comboBoxPizza, pizza -> pizza.getName());
		Utils.formatComboBox(comboBoxPizzaSize, pizzaSize -> pizzaSize.getName());
		Utils.formatComboBox(comboBoxAdditional, additional -> additional.getName());
		Utils.formatComboBox(comboBoxOrderStatus, orderStatus -> orderStatus.getName());
		Utils.formatDatePicker(dpMoment, "dd/MM/yyyy");
		Constraints.setTextFieldDouble(textFieldTotal);
		
	}
	
	public void updateFormData() {

		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		//id
		textFieldId.setText(String.valueOf(entity.getId()));
		
		//total
		Locale.setDefault(Locale.US);
		textFieldTotal.setText(String.format("%.2f", entity.getTotal()));
		
		// client
		if (entity.getClient() == null) {
			comboBoxClient.getSelectionModel().selectFirst();
		} else {
			comboBoxClient.setValue(entity.getClient());
		}
		
		// pizza
		if (entity.getPizza() == null) {
			comboBoxPizza.getSelectionModel().selectFirst();
		} else {
			comboBoxPizza.setValue(entity.getPizza());
		}
		
		//pizzaSize
		if(entity.getPizzaSize() == null) {
			comboBoxPizzaSize.getSelectionModel().selectFirst();
		} else {
			comboBoxPizzaSize.setValue(entity.getPizzaSize());
		}
		
		//additional
		if (entity.getAdditional() == null) {
			comboBoxAdditional.getSelectionModel().selectFirst();
		} else {
			comboBoxAdditional.setValue(entity.getAdditional());
		}
		
		//orderStatus
		if (entity.getOrderStatus() == null) {
			comboBoxOrderStatus.getSelectionModel().selectFirst();
		} else {
			comboBoxOrderStatus.setValue(entity.getOrderStatus());
		}
		
		//moment
		if(entity.getMoment() != null) {
			dpMoment.setValue(LocalDate.ofInstant(entity.getMoment().toInstant(), ZoneId.systemDefault()));
		}
		
	}
	
	//método responsável por pegar os erros que estão na exceção (ValidationException) e escrevê-los nas tela (label vazio que foi deixado no SceneBuilder)
	private void setErrorMessages(Map<String,String> errors) {
		Set<String> fields = errors.keySet();
		
		labelErrorMoment.setText((fields.contains("moment") ? errors.get("moment") : ""));
		
	}
	

}//class
