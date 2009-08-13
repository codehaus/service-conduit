package tests.http;

import javax.ws.rs.QueryParam;

public class BaseUploadRequest {
	
	@QueryParam("cardNumber") private String cardNumber;
	
	public BaseUploadRequest() {
	}

	public BaseUploadRequest(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
    public String getCardNumber() {
        return cardNumber;
    }
	
}
