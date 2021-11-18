package gui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

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
import model.bo.ClientBO;
import model.vo.Client;

public class ClientListController implements Initializable, InterDataChangeListener {
	
	private ClientBO service;

	@FXML
	private Button btGerarPDF;
	
	@FXML
	private Label label;
	@FXML
	private TextField filterField;

	@FXML
	private TableView<Client> tableViewClient;

	@FXML
	private TableColumn<Client, Integer> tableColumnId;

	@FXML
	private TableColumn<Client, String> tableColumnName;
	
	@FXML
	private TableColumn<Client, String> tableColumnCpf;
	
	@FXML
	private TableColumn<Client, String> tableColumnPhone;
	
	@FXML
	private TableColumn<Client, String> tableColumnAddress;
	
	@FXML
	private TableColumn<Client, Client> tableColumnEDIT;
	
	@FXML
	private TableColumn<Client, Client> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Client> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Client obj = new Client();
		createDialogForm(obj,"/gui/ClientForm.fxml", parentStage);
	}
	
	@FXML
	public void onBtGerarPDF(ActionEvent event) {
		generatePdf();
	}


	public void setClientBO(ClientBO service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
		tableColumnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));//igual escrito na classe entidade
		tableColumnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewClient.prefHeightProperty().bind(stage.heightProperty());

	}
	
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Client> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewClient.setItems(obsList);
		
		filter();
		initEditButtons();
		initRemoveButtons();
	}
	
	private void createDialogForm(Client obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			ClientFormController controller = loader.getController(); 
			controller.setClient(obj);
			controller.setService(new ClientBO());
			controller.subscribeDataChangeListener(this);
			
			controller.updateFormData();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Novo cliente");
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
	public void onDataChanged() {
		updateTableView();
		
	}
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Client, Client>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Client obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/ClientForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Client, Client>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Client obj, boolean empty) {
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

	private void removeEntity(Client obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Epa", "Certeza que quer deletar?");
		
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
		FilteredList<Client> filteredData = new FilteredList<>(obsList, b -> true);

		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(client -> {
				// If filter text is empty, display all persons.

				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (client.getName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches name.

				} else if (client.getCpf().indexOf(lowerCaseFilter) != -1) {
					return true;
				} else if (client.getPhone().indexOf(lowerCaseFilter) != -1) {
					return true;
				} 
					
				else
					return false; // Does not match.
			});
		});

		// 3. Wrap the FilteredList in a SortedList.
		SortedList<Client> sortedData = new SortedList<>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		// Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(tableViewClient.comparatorProperty());

		// 5. Add sorted (and filtered) data to the table.
		tableViewClient.setItems(sortedData);
	}
	
	private void generatePdf() {
		Document doc = new Document();
		try {
			PdfWriter.getInstance(doc, new FileOutputStream("C:\\Users\\Public\\Clientes.pdf"));
			doc.open();
			List<Client> items = tableViewClient.getItems();
			
			doc.add(new Paragraph("Pizzaria do Michelangelo - Lista de Clientes\n\n"));
			for (Client client : items) {
				
				doc.add(new Paragraph(""));
				doc.add(new Paragraph("Id: "+client.getId()));
				doc.add(new Paragraph("Nome: "+client.getName()));
				doc.add(new Paragraph("CPF: "+client.getCpf()));
				doc.add(new Paragraph("Telefone: "+client.getPhone()));
				doc.add(new Paragraph("Endereço: "+client.getAddress()));
				doc.add(new Paragraph("\n"));
			}
			
			doc.close();
			Alerts.showAlert("Opa", null, "PDF criado, meu bom!", AlertType.INFORMATION);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}// class
