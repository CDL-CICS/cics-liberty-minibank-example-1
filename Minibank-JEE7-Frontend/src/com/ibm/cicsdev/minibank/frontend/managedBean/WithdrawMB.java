package com.ibm.cicsdev.minibank.frontend.managedBean;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.cicsdev.minibank.frontend.entity.Account;
import com.ibm.cicsdev.minibank.frontend.entity.TransactionPOJO;
import com.ibm.cicsdev.minibank.frontend.util.IConstants;

@Named
@RequestScoped
public class WithdrawMB implements Serializable {

	private static final long serialVersionUID = 1L;
	private @Inject Account sourceAccount;
	private Double moneyAmount;
	private String operationResult;
	private String operationMessage;

	public Account getSourceAccount() {
		return sourceAccount;
	}

	public WithdrawMB() {
		super();
	}

	public String withdraw() {

		TransactionPOJO transPOJO = new TransactionPOJO();
		transPOJO.setSourceAccountId(sourceAccount.getAccountNumber());
		transPOJO.setMoneyAmount(moneyAmount);
		Response response;
		
		try {
			Client client = ClientBuilder.newClient();
			response = client.target(IConstants.URL)
		            .path(IConstants.TRANSEVENT+IConstants.WITHDRAW)
		            .request(MediaType.APPLICATION_JSON)
		            .put(Entity.json(transPOJO));
			client.close();
			System.out.println(response.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			this.setOperationMessage("failed...");
			return "../notificationpages/notification_withdraw_failed";
		}
		if (response.getStatus() == IConstants.CODE_OPERATION_SUCCESS) {
			this.setOperationMessage(IConstants.MESSAGE_WITHDRAW_SUCCESS
					+ response.getEntity().toString());
			return "../notificationpages/notification_withdraw_success";
		} else {
			switch (response.getStatus()) {
			case IConstants.CODE_ACCOUNT_NOT_EXIST:
				this.setOperationMessage(IConstants.MESSAGE_ACCOUNT_NOT_EXIST
						+ sourceAccount.getAccountNumber());
				break;
			case IConstants.CODE_INSUFFIENT_BALANCE:
				this.setOperationMessage(IConstants.MESSAGE_INSUFFIENT_BALANCE
						+ response.getEntity().toString());
				break;
			default:
				this.setOperationMessage(IConstants.MESSAGE_UNKNOWN_FAILED);
				return "../notificationpages/notification_withdraw_failed";
			}
		}
		return "../notificationpages/notification_withdraw_failed";
	}

	public String getOperationResult() {
		return operationResult;
	}

	public void setOperationResult(String operationResult) {
		this.operationResult = operationResult;
	}

	public String getOperationMessage() {
		return operationMessage;
	}

	public void setOperationMessage(String operationMessage) {
		this.operationMessage = operationMessage;
	}

	public Double getMoneyAmount() {
		return moneyAmount;
	}

	public void setMoneyAmount(Double moneyAmount) {
		this.moneyAmount = moneyAmount;
	}
}
