package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.InterDataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.bo.AdditionalBO;
import model.bo.ClientBO;
import model.bo.OrderBO;
import model.bo.OrderStatusBO;
import model.bo.PizzaBO;
import model.bo.PizzaSizeBO;
import model.vo.Additional;
import model.vo.Client;
import model.vo.Order;
import model.vo.OrderStatus;
import model.vo.Pizza;
import model.vo.PizzaSize;




public class OrderListController implements Initializable, InterDataChangeListener { //o objeto desta classe é Observer (espera emissão de sinal das outras opara executar um determinado método)

	// services (dependência) (injetar dependência sem usar a implementação da
	// classe. criar método)
	private OrderBO service;

	//

	@FXML
	private TableView<Order> tableViewOrder;

	@FXML
	private TableColumn<Order, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Order, Client> tableColumnClient;
	
	@FXML
	private TableColumn<Order, Pizza> tableColumnPizza;
	
	@FXML
	private TableColumn<Order, PizzaSize> tableColumnPizzaSize;
	
	@FXML
	private TableColumn<Order, Additional> tableColumnAdditional;
	
	@FXML
	private TableColumn<Order, OrderStatus> tableColumnOrderStatus;
	
	@FXML
	private TableColumn<Order, Date> tableColumnMoment;
	
	@FXML
	private TableColumn<Order, Double> tableColumnTotal;
	
	@FXML
	private TableColumn<Order, Order> tableColumnEDIT;
	
	@FXML
	private TableColumn<Order, Order> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Order> obsList; //associoar com tableView

	// métodos
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Order obj = new Order();
		createDialogForm(obj,"/gui/OrderForm.fxml", parentStage);
	}

	// inversão de controle
	public void setOrderBO(OrderBO service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		
		tableColumnClient.setCellValueFactory((new PropertyValueFactory<>("client")));
		Utils.formatTableColumnSubProperty(tableColumnClient, client -> client.getName());
		
		tableColumnPizza.setCellValueFactory((new PropertyValueFactory<>("pizza")));
		Utils.formatTableColumnSubProperty(tableColumnPizza, pizza -> pizza.getName());
		
		tableColumnPizzaSize.setCellValueFactory((new PropertyValueFactory<>("pizzaSize")));
		Utils.formatTableColumnSubProperty(tableColumnPizzaSize, pizzaSize -> pizzaSize.getName());
		
		tableColumnAdditional.setCellValueFactory((new PropertyValueFactory<>("additional")));
		Utils.formatTableColumnSubProperty(tableColumnAdditional, additional -> additional.getName());
		
		tableColumnOrderStatus.setCellValueFactory((new PropertyValueFactory<>("orderStatus")));
		Utils.formatTableColumnSubProperty(tableColumnOrderStatus, orderStatus -> orderStatus.getName());
		
		tableColumnMoment.setCellValueFactory(new PropertyValueFactory<>("moment"));
		Utils.formatTableColumnDate(tableColumnMoment, "dd/MM/yyyy");
		
		tableColumnTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
		Utils.formatTableColumnDouble(tableColumnTotal, 2);

		// table ir até o final
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewOrder.prefHeightProperty().bind(stage.heightProperty());

	}
	
	//carregar os objetos em obsList (método responsável em acessar o serviço, carregar os objetos e jogar na ObservableList);
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Order> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewOrder.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	
	//janela Form (instanciar a janela de diálogo)
	private void createDialogForm(Order obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			OrderFormController controller = loader.getController();
			controller.setOrder(obj);
			controller.setServices(new OrderBO(), new ClientBO(), new PizzaBO(), new PizzaSizeBO(), new AdditionalBO(), new OrderStatusBO());
			controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			
			controller.updateFormData();
			
			//instanciar novo stage (stage sobre stage)
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Novo pedido");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
			
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {//veio da interface, irá atualizar a tabela quando receber o sinal utilizando o método "updateTableView"
		updateTableView();
		
	}
	
	//método do EDIT (chamar no método "updateTableView")
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Order, Order>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Order obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/OrderForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	//método do REMOVE
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Order, Order>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Order obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Order obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch(DbIntegrityException e) {//deve ser a mesma usada na classe dao
				Alerts.showAlert("Error removing object", null,e.getMessage(),AlertType.ERROR);
			}
			
		}
	}
	

}// class
