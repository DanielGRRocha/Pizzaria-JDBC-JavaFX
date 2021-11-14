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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.bo.PizzaBO;
import model.exceptions.ValidationException;
import model.vo.Pizza;




public class PizzaFormController implements Initializable {//classe Sujeito (emite o evento), instancia a interface DataChangeListener
	
	//criar a dependência (ak instanciar criando um set)
	private Pizza entity;
	private PizzaBO service;
	//a classe em questão vai guardar uma lista de objetos interessados em receber o evento (criar método para adicioná-los na lista)
	private List<InterDataChangeListener> dataChangeListeners = new ArrayList<>(); 
	
	
	//declarações componentes da tela
	@FXML
	private TextField textFieldId;
	
	@FXML
	private TextField textFieldName;
	
	@FXML
	private TextField textFieldPriceSmallPizza;
	
	@FXML
	private TextField textFieldPriceMediumPizza;
	
	@FXML
	private TextField textFieldPriceBigPizza;

	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorPriceSmallPizza;
	
	@FXML
	private Label labelErrorPriceMediumPizza;
	
	@FXML
	private Label labelErrorPriceBigPizza;
	
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	//set Inventory (agora controlador tem instância do Inventory)
	public void setPizza(Pizza entity) {
		this.entity = entity;
	}
	//set InventoryBO (agora controlador tem instância da classe de serviços do Inventory)
	public void setService(PizzaBO service) {
		this.service = service;
	}
	//método para adicionar objetos na lista "dataChangeListeners"
	public void subscribeDataChangeListener(InterDataChangeListener listener) {//objetos que implementarem a interface "DataChangeListener" podem se inscrever para receber o evento da classe
		dataChangeListeners.add(listener);
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
		
		//priceSmallPizza
		if(textFieldPriceSmallPizza.getText() == null || textFieldPriceSmallPizza.getText().trim().equals("")) {
			exception.addError("priceSmallPizza", "Field can't be empty");
		}
		obj.setPriceSmallPizza(Utils.tryParseToDouble(textFieldPriceSmallPizza.getText()));
		
		//priceMediumPizza
		if (textFieldPriceMediumPizza.getText() == null || textFieldPriceMediumPizza.getText().trim().equals("")) {
			exception.addError("priceMediumPizza", "Field can't be empty");
		}
		obj.setPriceMediumPizza(Utils.tryParseToDouble(textFieldPriceMediumPizza.getText()));
		
		//priceBigPizza
		if (textFieldPriceBigPizza.getText() == null || textFieldPriceBigPizza.getText().trim().equals("")) {
			exception.addError("priceBigPizza", "Field can't be empty");
		}
		obj.setPriceBigPizza(Utils.tryParseToDouble(textFieldPriceBigPizza.getText()));

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
		Constraints.setTextFieldDouble(textFieldPriceSmallPizza);
		Constraints.setTextFieldDouble(textFieldPriceMediumPizza);
		Constraints.setTextFieldDouble(textFieldPriceBigPizza);
		
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
		textFieldPriceSmallPizza.setText(String.format("%.2f", entity.getPriceSmallPizza()));
		textFieldPriceMediumPizza.setText(String.format("%.2f", entity.getPriceMediumPizza()));
		textFieldPriceBigPizza.setText(String.format("%.2f", entity.getPriceBigPizza()));
		
		
	}
	
	//método responsável por pegar os erros que estão na exceção (ValidationException) e escrevê-los nas tela (label vazio que foi deixado no SceneBuilder)
	private void setErrorMessages(Map<String,String> errors) {//no método do evento do botão save, inserir ValidationException (catch)
		Set<String> fields = errors.keySet();
		
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
		labelErrorPriceSmallPizza.setText((fields.contains("priceSmallPizza") ? errors.get("priceSmallPizza") : ""));
		labelErrorPriceMediumPizza.setText((fields.contains("priceMediumPizza") ? errors.get("priceMediumPizza") : ""));
		labelErrorPriceBigPizza.setText((fields.contains("priceBigPizza") ? errors.get("priceBigPizza") : ""));
		
	}
	

}//class
