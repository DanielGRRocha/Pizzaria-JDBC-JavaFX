package gui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import application.Main;
import gui.listeners.InterDataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.bo.ClientBO;
import model.vo.Client;

public class NewClientListController implements Initializable, InterDataChangeListener {
	
	private ClientBO service;

	@FXML
	private Button btGerarPDF;
	
	@FXML
	private Label label;
	@FXML
	private TextField filterField;

	@FXML
	private TableView<Client> tableViewNewClient;

	@FXML
	private TableColumn<Client, Integer> tableColumnId;

	@FXML
	private TableColumn<Client, String> tableColumnName;
	
	@FXML
	private TableColumn<Client, Date> tableColumnDate;
	

	private ObservableList<Client> obsList;

	
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
		tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		Utils.formatTableColumnDate(tableColumnDate, "dd/MM/yyyy");

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewNewClient.prefHeightProperty().bind(stage.heightProperty());

	}
	
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Client> list = service.findAllNewClient();
		obsList = FXCollections.observableArrayList(list);
		tableViewNewClient.setItems(obsList);
		
		filter();
		
	}
	
	@Override
	public void onDataChanged() {
		updateTableView();
		
	}
	
	
	private void filter() {
		// Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<Client> filteredData = new FilteredList<>(obsList, b -> true);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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

				} else if (String.valueOf(sdf.format(client.getDate())).indexOf(lowerCaseFilter) != -1) {
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
		sortedData.comparatorProperty().bind(tableViewNewClient.comparatorProperty());

		// 5. Add sorted (and filtered) data to the table.
		tableViewNewClient.setItems(sortedData);
	}
	
	private void generatePdf() {
		Document doc = new Document();
		try {
			PdfWriter.getInstance(doc, new FileOutputStream("C:\\Users\\Public\\NovosClientes.pdf"));
			doc.open();
			List<Client> items = tableViewNewClient.getItems();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			doc.add(new Paragraph("Pizzaria do Michelangelo - Lista Registro dos Novos Clientes\n\n"));
			for (Client client : items) {
				
				doc.add(new Paragraph(""));
				doc.add(new Paragraph("Id: "+client.getId()));
				doc.add(new Paragraph("Nome: "+client.getName()));
				doc.add(new Paragraph("Data de registro: "+sdf.format(client.getDate())));
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
