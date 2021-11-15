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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.bo.AdditionalBO;
import model.vo.Additional;


public class AdditionalListController implements Initializable, InterDataChangeListener { //o objeto desta classe é Observer (espera emissão de sinal das outras opara executar um determinado método)

	// services (dependência) (injetar dependência sem usar a implementação da
	// classe. criar método)
	private AdditionalBO service;

	//
	
	@FXML
	private Label label;
	@FXML
	private TextField filterField;

	@FXML
	private TableView<Additional> tableViewAdditional;

	@FXML
	private TableColumn<Additional, Integer> tableColumnId;

	@FXML
	private TableColumn<Additional, String> tableColumnName;
	
	@FXML
	private TableColumn<Additional, Double> tableColumnPrice;
	
	@FXML
	private TableColumn<Additional, Additional> tableColumnEDIT;
	
	@FXML
	private TableColumn<Additional, Additional> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Additional> obsList; //associoar com tableView

	// métodos
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Additional obj = new Additional();
		createDialogForm(obj,"/gui/AdditionalForm.fxml", parentStage);
	}

	// inversão de controle
	public void setAdditionalBO(AdditionalBO service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		Utils.formatTableColumnDouble(tableColumnPrice, 2);

		// table ir até o final
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewAdditional.prefHeightProperty().bind(stage.heightProperty());

	}
	
	//carregar os objetos em obsList (método responsável em acessar o serviço, carregar os objetos e jogar na ObservableList);
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Additional> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewAdditional.setItems(obsList);
		
		filter();
		initEditButtons();
		initRemoveButtons();
	}
	
	//janela Form (instanciar a janela de diálogo)
	private void createDialogForm(Additional obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			//injetar Client obj no controlador na tela de novo cadastro(formulário) ANOTAÇÃO: instanciando o FormController é possível chamar os seus métodos
			AdditionalFormController controller = loader.getController(); //pega-se o controlador da tela que foi carregada
			controller.setAdditional(obj); //injetar nesse controller o objeto
			controller.setService(new AdditionalBO());//injetar BO (injeção de dependência)
			controller.subscribeDataChangeListener(this);//inscrevendo um listener para receber o evento que chamará o método "onDataChanged"
			
			controller.updateFormData();//chamar o método que carrega o objeto no formulário
			
			
			//instanciar novo stage (stage sobre stage)
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Novo adicional");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL); //não pode acessar a janela de trás enquanto esta estiver aberta
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<Additional, Additional>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Additional obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/AdditionalForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	//método do REMOVE
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Additional, Additional>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Additional obj, boolean empty) {
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

	private void removeEntity(Additional obj) {
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
	
	private void filter() {
		// Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<Additional> filteredData = new FilteredList<>(obsList, b -> true);

		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(additional -> {
				// If filter text is empty, display all persons.

				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (additional.getName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches name.
				}
					
				else
					return false; // Does not match.
			});
		});

		// 3. Wrap the FilteredList in a SortedList.
		SortedList<Additional> sortedData = new SortedList<>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		// Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(tableViewAdditional.comparatorProperty());

		// 5. Add sorted (and filtered) data to the table.
		tableViewAdditional.setItems(sortedData);
	}
	

}// class
