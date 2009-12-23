// ActionScript file
import com.travelex.tgbp.message.GlowHandler;
import com.travelex.tgbp.message.OutputEvent;
import com.travelex.tgbp.message.SubmissionEvent;

import mx.collections.ArrayCollection;
import mx.controls.Image;
import mx.formatters.NumberFormatter;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.IMessage;
	
private static var ALPHA_BASE:Number = 0.2;
	
[Bindable]
private var submissionEvent:SubmissionEvent;	

[Bindable]
private var outputEvent:OutputEvent;	

[Bindable]
private var submisionTotals:ArrayCollection;

[Bindable]
private var outputTotals:ArrayCollection;

[Bindable]
private var routingDecisions:ArrayCollection;
	
private function submissionEventHandler(message:IMessage):void {
	//The flash player is single threaded so there's no need to worry about synchronisation.
	glowImage(submissionReceivedImage);
	submissionEvent = message.body as SubmissionEvent; //Should check that count > current count to deal with out of order event arrival.
		
	var matched:Object = getItem(submisionTotals, submissionEvent.submissionCurrency);
	matched.value += new Number(submissionEvent.submissionValue);
	submisionTotals.refresh();	
}

private function outputEventHandler(message:IMessage):void {
	//The flash player is single threaded so there's no need to worry about synchronisation.
	glowImage(outputSentImage);	
	outputEvent = message.body as OutputEvent;	//Should check that count > current count to deal with out of order event arrival.
	
	var matched:Object = getItem(outputTotals, outputEvent.outputCurrency);
	matched.value += new Number(outputEvent.outputValue);
	outputTotals.refresh();
	
	addRoutingDecision(outputEvent);	
	
}

private function addRoutingDecision(out:OutputEvent): void {
	var decision:String = getTime() +  " Routed " + out.outputCurrency + " " + formatCurrency(out.outputValue) + " to " + out.sentTo + ": " + out.ruleName;
	if(routingDecisions.length == 20) {
		routingDecisions.removeItemAt(0);
	}	
	routingDecisions.addItem(decision);
}

private function getTime(): String {
	var now:Date = new Date();
	return zeroPad(now.hours, 2) + ":" + zeroPad(now.minutes, 2) + ":" + zeroPad(now.seconds, 2)	
}

private function init(): void {	
	initialiseInputValues();
	initialiseOutputValues();
	
	GlowHandler.filterImage(submissionReceivedImage, ALPHA_BASE)
	GlowHandler.filterImage(outputSentImage, ALPHA_BASE)
	
	submissionEventConsumer.subscribe();	
	outputEventConsumer.subscribe();
}

private function initialiseInputValues(): void {
	submissionEvent = new SubmissionEvent();
	submisionTotals = new ArrayCollection();
	
	submissionEvent.submissionCount = 0;
	submissionEvent.submissionId = "";
	submissionEvent.submissionCurrency = "";
	submissionEvent.submissionValue = "";
}

private function initialiseOutputValues(): void {	
	outputEvent = new OutputEvent();
	outputTotals = new ArrayCollection();
	routingDecisions = new ArrayCollection();
	
	outputEvent.outputCount = 0;
	outputEvent.outputCurrency = "";
	outputEvent.outputValue = "";	
}

private function glowImage(image:Image):void
{
   var glow:GlowHandler = new GlowHandler(image); 
   glow.glowImage();      
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

private function createValueLabel(item:Object):String {
	return item.ccy + " " + formatCurrency(item.value);
}

private function formatCurrency(value:Object): String {
	var f:NumberFormatter = new NumberFormatter();
	f.thousandsSeparatorTo="";
	f.precision = 2;
	return f.format(value);
}

public function zeroPad(number:int, width:int):String {
   var ret:String = "" + number;
   while( ret.length < width ) {
       ret= "0" + ret;
   }
   return ret;
}

/* 

Testing only - called by commented out buttons on the system status page

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

private function testOutput(): void {
	var outEvent:OutputEvent = new OutputEvent();
	outEvent.outputCount = 25;
	outEvent.outputCurrency = "USD";
	outEvent.outputValue = "38921.87"
	outEvent.ruleName = "Dynamic Rule 1";
	outEvent.sentTo = "BACS";
	
	var m:AsyncMessage = new AsyncMessage();
	m.body = outEvent;
	outputEventHandler(m);
}
 
*/