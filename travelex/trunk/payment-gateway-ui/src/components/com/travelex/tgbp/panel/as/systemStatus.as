// ActionScript file
import com.travelex.tgbp.message.GlowHandler;
import com.travelex.tgbp.message.OutputEvent;
import com.travelex.tgbp.message.SubmissionEvent;

import mx.collections.ArrayCollection;
import mx.controls.Image;
import mx.formatters.NumberFormatter;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.IMessage;
import 	mx.formatters.NumberFormatter;
	
private static var BASE:Number = 0.2;
	
[Bindable]
private var submissionEvent:SubmissionEvent;	

[Bindable]
private var outputEvent:OutputEvent;	

[Bindable]
private var submisionTotalData:ArrayCollection;
	
private function submissionEventHandler(message:IMessage):void {
	//The flash player is single threaded so there's no need to worry about synchronisation.
	glowImage(submissionReceivedImage);
	submissionEvent = message.body as SubmissionEvent; //Should check that count > current count to deal with out of order event arrival.
		
	var matched:Object = getItem(submisionTotalData, submissionEvent.submissionCurrency);
	matched.value += new Number(submissionEvent.submissionValue);
	submisionTotalData.refresh();	
}

private function outputEventHandler(message:IMessage):void {
	//The flash player is single threaded so there's no need to worry about synchronisation.
	glowImage(outputSentImage);	
	outputEvent = message.body as OutputEvent;	//Should check that count > current count to deal with out of order event arrival.
}

private function init(): void {	
	initialiseInputValues();
	initialiseOutputValues();
	
	GlowHandler.filterImage(submissionReceivedImage, BASE)
	GlowHandler.filterImage(outputSentImage, BASE)
	
	submissionEventConsumer.subscribe();	
	outputEventConsumer.subscribe();
}

private function initialiseInputValues(): void {
	submissionEvent = new SubmissionEvent();
	submisionTotalData = new ArrayCollection();
	
	submissionEvent.submissionCount = 0;
	submissionEvent.submissionId = "";
	submissionEvent.submissionCurrency = "";
	submissionEvent.submissionValue = "";
}

private function createvalueLabel(item:Object):String {
	var f:NumberFormatter = new NumberFormatter();
	f.precision = 2;
	return item.ccy + " " + f.format(item.value);
}

private function initialiseOutputValues(): void {	
	outputEvent = new OutputEvent();
	
	outputEvent.outputCount = 0;	
}

private function glowImage(image:Image):void
{
   var glow:GlowHandler = new GlowHandler(image); 
   glow.glowImage();      
}

private function testSubmissionArrival(): void {
	var sEvent:SubmissionEvent = new SubmissionEvent();
	sEvent.submissionCount = 50;
	sEvent.submissionId = "ABC1234";
	sEvent.submissionCurrency = "EUR";
	sEvent.submissionValue = "1000.45";
	
	var m:AsyncMessage = new AsyncMessage();
	m.body = sEvent;
	submissionEventHandler(m);	
}

private function getItem(data:ArrayCollection, currency:String): Object {
	var matched:Object = null;
	for each(var entry:Object in data) {
		if(entry.ccy == currency) {
			matched = entry;				
		}	
	}
	
	if(matched == null) {
		matched = new Object();
		matched.ccy = currency;	
		matched.value = 0;
		data.addItem(matched);		
	}	
	
	return matched;
}