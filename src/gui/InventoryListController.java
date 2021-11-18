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
import model.bo.InventoryBO;
import model.vo.Inventory;

public class InventoryListController implements Initializable, InterDataChangeListener { 

	private InventoryBO service;
	
	@FXML
	private Button btGerarPDF;
	
	@FXML
	private Label label;
	@FXML
	private TextField filterField;

	@FXML
	private TableView<Inventory> tableViewInventory;

	@FXML
	private TableColumn<Inventory, Integer> tableColumnId;

	@FXML
	private TableColumn<Inventory, String> tableColumnName;
	
	@FXML
	private TableColumn<Inventory, Integer> tableColumnQuantity;
	
	@FXML
	private TableColumn<Inventory, Inventory> tableColumnEDIT;
	
	@FXML
	private TableColumn<Inventory, Inventory> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Inventory> obsList;

	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Inventory obj = new Inventory();
		createDialogForm(obj,"/gui/InventoryForm.fxml", parentStage);
	}
	
	@FXML
	public void onBtGerarPDF(ActionEvent event) {
		generatePdf();
	}


	public void setInventoryBO(InventoryBO service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewInventory.prefHeightProperty().bind(stage.heightProperty());

	}
	
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Inventory> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewInventory.setItems(obsList);
		
		filter();
		initEditButtons();
		initRemoveButtons();
	}
	

	private void createDialogForm(Inventory obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
		
			InventoryFormController controller = loader.getController();
			controller.setInventory(obj);
			controller.setService(new InventoryBO());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Novo item");
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<Inventory, Inventory>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Inventory obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/InventoryForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Inventory, Inventory>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Inventory obj, boolean empty) {
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

	private void removeEntity(Inventory obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Epa", "Certeza que quer deletar?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch(DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null,e.getMessage(),AlertType.ERROR);
			}
			
		}
	}
	
	private void filter() {
		// Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<Inventory> filteredData = new FilteredList<>(obsList, b -> true);

		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(inventory -> {
				// If filter text is empty, display all persons.

				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (inventory.getName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches name.

				} 
					
				else
					return false; // Does not match.
			});
		});

		// 3. Wrap the FilteredList in a SortedList.
		SortedList<Inventory> sortedData = new SortedList<>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		// Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(tableViewInventory.comparatorProperty());

		// 5. Add sorted (and filtered) data to the table.
		tableViewInventory.setItems(sortedData);
	}
	
	private void generatePdf() {
		Document doc = new Document();
		try {
			PdfWriter.getInstance(doc, new FileOutputStream("C:\\Users\\Public\\Estoque.pdf"));
			doc.open();
			List<Inventory> items = tableViewInventory.getItems();
			
			doc.add(new Paragraph("Pizzaria do Michelangelo - Estoque\n\n"));
			for (Inventory obj : items) {
				
				doc.add(new Paragraph(""));
				doc.add(new Paragraph("Id: "+obj.getId()));
				doc.add(new Paragraph("Nome: "+obj.getName()));
				doc.add(new Paragraph("Quantidade: "+obj.getQuantity()));
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
