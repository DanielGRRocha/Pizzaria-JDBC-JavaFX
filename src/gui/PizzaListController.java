package gui;

import java.io.IOException;
import java.net.URL;
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
import model.bo.PizzaBO;
import model.vo.Pizza;


public class PizzaListController implements Initializable, InterDataChangeListener { //o objeto desta classe � Observer (espera emiss�o de sinal das outras opara executar um determinado m�todo)

	// services (depend�ncia) (injetar depend�ncia sem usar a implementa��o da
	// classe. criar m�todo)
	private PizzaBO service;

	//

	@FXML
	private TableView<Pizza> tableViewPizza;

	@FXML
	private TableColumn<Pizza, Integer> tableColumnId;

	@FXML
	private TableColumn<Pizza, String> tableColumnName;
	
	@FXML
	private TableColumn<Pizza, Double> tableColumnPriceSmallPizza;
	
	@FXML
	private TableColumn<Pizza, Double> tableColumnPriceMediumPizza;
	
	@FXML
	private TableColumn<Pizza, Double> tableColumnPriceBigPizza;
	
	@FXML
	private TableColumn<Pizza, Pizza> tableColumnEDIT;
	
	@FXML
	private TableColumn<Pizza, Pizza> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Pizza> obsList; //associoar com tableView

	// m�todos
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Pizza obj = new Pizza();
		createDialogForm(obj,"/gui/PizzaForm.fxml", parentStage);
	}

	// invers�o de controle
	public void setPizzaBO(PizzaBO service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnPriceSmallPizza.setCellValueFactory(new PropertyValueFactory<>("priceSmallPizza"));
		Utils.formatTableColumnDouble(tableColumnPriceSmallPizza, 2);
		tableColumnPriceMediumPizza.setCellValueFactory(new PropertyValueFactory<>("priceMediumPizza"));
		Utils.formatTableColumnDouble(tableColumnPriceMediumPizza, 2);
		tableColumnPriceBigPizza.setCellValueFactory(new PropertyValueFactory<>("priceBigPizza"));
		Utils.formatTableColumnDouble(tableColumnPriceBigPizza, 2);

		// table ir at� o final
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewPizza.prefHeightProperty().bind(stage.heightProperty());

	}
	
	//carregar os objetos em obsList (m�todo respons�vel em acessar o servi�o, carregar os objetos e jogar na ObservableList);
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Pizza> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewPizza.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	
	//janela Form (instanciar a janela de di�logo)
	private void createDialogForm(Pizza obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			//injetar Client obj no controlador na tela de novo cadastro(formul�rio) ANOTA��O: instanciando o FormController � poss�vel chamar os seus m�todos
			PizzaFormController controller = loader.getController(); //pega-se o controlador da tela que foi carregada
			controller.setPizza(obj); //injetar nesse controller o objeto
			controller.setService(new PizzaBO());//injetar BO (inje��o de depend�ncia)
			controller.subscribeDataChangeListener(this);//inscrevendo um listener para receber o evento que chamar� o m�todo "onDataChanged"
			
			controller.updateFormData();//chamar o m�todo que carrega o objeto no formul�rio
			
			
			//instanciar novo stage (stage sobre stage)
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Nova pizza");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL); //n�o pode acessar a janela de tr�s enquanto esta estiver aberta
			dialogStage.showAndWait();
			
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {//veio da interface, ir� atualizar a tabela quando receber o sinal utilizando o m�todo "updateTableView"
		updateTableView();
		
	}
	
	//m�todo do EDIT (chamar no m�todo "updateTableView")
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Pizza, Pizza>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Pizza obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/PizzaForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	//m�todo do REMOVE
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Pizza, Pizza>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Pizza obj, boolean empty) {
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

	private void removeEntity(Pizza obj) {
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
