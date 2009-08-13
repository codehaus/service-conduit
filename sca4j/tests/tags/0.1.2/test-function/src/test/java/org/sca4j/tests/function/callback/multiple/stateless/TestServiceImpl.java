package org.sca4j.tests.function.callback.multiple.stateless;

import org.osoa.sca.annotations.Callback;


public class TestServiceImpl implements TestService {

	@Callback protected TestCallbackService testCallbackService;

	public void sendMessage(String msg) {
       testCallbackService.onCallback("recieved " + msg);
	}

}
