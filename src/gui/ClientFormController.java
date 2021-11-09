package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import model.bo.ClientBO;
import model.exceptions.ValidationException;
import model.vo.Client;


public class ClientFormController implements Initializable {//classe Sujeito (emite o evento), instancia a interface DataChangeListener
	
	//criar a dependência (ak instanciar criando um set)
	private Client entity;
	private ClientBO service;
	//a classe em questão vai guardar uma lista de objetos interessados em receber o evento (criar método para adicioná-los na lista)
	private List<InterDataChangeListener> dataChangeListeners = new ArrayList<>(); 
	
	
	//declarações componentes da tela
	@FXML
	private TextField textFieldId;
	
	@FXML
	private TextField textFieldName;
	
	@FXML
	private TextField textFieldCpf;
	
	@FXML
	private TextField textFieldPhone;
	
	@FXML
	private TextField textFieldAddress;

	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorCpf;
	
	@FXML
	private Label labelErrorPhone;
	
	@FXML
	private Label labelErrorAddress;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	//set Client (agora controlador tem instância do Client)
	public void setClient(Client entity) {
		this.entity = entity;
	}
	//set SellerService (agora controlador tem instância da classe de serviços do Client)
	public void setService(ClientBO service) {
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
	
	
	private Client getFormData() {//pegar os dados do formulario
		Client obj = new Client();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(textFieldId.getText()));//chamar o método para passar String para Integer
		
		//fazer validação para que o campo não seja vazio
		//nome
		if(textFieldName.getText() == null || textFieldName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(textFieldName.getText());
		
		//cpf
		if(textFieldCpf.getText() == null || textFieldCpf.getText().trim().equals("")) {
			exception.addError("cpf", "Field can't be empty");
		}
		obj.setCpf(textFieldCpf.getText());
		
		//phone
		if(textFieldPhone.getText() == null || textFieldPhone.getText().trim().equals("")) {
			exception.addError("phone", "Field can't be empty");
		}
		obj.setPhone(textFieldPhone.getText());
		
		//baseSalary
		if(textFieldAddress.getText() == null || textFieldAddress.getText().trim().equals("")) {
			exception.addError("address", "Field can't be empty");
		}
		obj.setAddress(textFieldAddress.getText());
		
		
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
		Constraints.setTextFieldMaxLength(textFieldCpf, 11);
		Constraints.setTextFieldMaxLength(textFieldPhone, 11);
		Constraints.setTextFieldMaxLength(textFieldAddress, 60);
		
		
	}
	
	//método updateFormData que irá pegar os dados do seller e popular as caixinhas de texto do formulário (settar)
	public void updateFormData() {
		//programação defensiva: criar um if para verificar entity está valendo nulo
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		textFieldId.setText(String.valueOf(entity.getId())); //converte Interger em String
		textFieldName.setText(entity.getName());
		textFieldCpf.setText(entity.getCpf());
		textFieldPhone.setText(entity.getPhone());
		textFieldAddress.setText(entity.getAddress());
		
		
	}
	
	//método responsável por pegar os erros que estão na exceção (ValidationException) e escrevê-los nas tela (label vazio que foi deixado no SceneBuilder)
	private void setErrorMessages(Map<String,String> errors) {//no método do evento do botão save, inserir ValidationException (catch)
		Set<String> fields = errors.keySet();
		
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
		labelErrorCpf.setText((fields.contains("cpf") ? errors.get("cpf") : ""));
		labelErrorPhone.setText((fields.contains("phone") ? errors.get("phone") : ""));
		labelErrorAddress.setText((fields.contains("address") ? errors.get("address") : ""));
		
	}
	

}//class
