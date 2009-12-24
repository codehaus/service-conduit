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
		
	for each(var currencyData:String in submissionEvent.currencyValues) {
		var data:Array = currencyData.split("/"); //e.g. [0] = EUR and [1] = 1000.
		var matched:Object = getItem(submisionTotals, data[0]);
		matched.value = new Number(data[1]);		
	}
	submisionTotals.refresh();	
}

private function outputEventHandler(message:IMessage):void {
	//The flash player is single threaded so there's no need to worry about synchronisation.
	glowImage(outputSentImage);	
	outputEvent = message.body as OutputEvent;	//Should check that count > current count to deal with out of order event arrival.
	
	for each(var currencyData:String in outputEvent.currencyValues) {
		var data:Array = currencyData.split("/"); //e.g. [0] = EUR and [1] = 1000.
		var matched:Object = getItem(outputTotals, data[0]);
		matched.value = new Number(data[1]);		
	}
	outputTotals.refresh();	
	
	//addRoutingDecision(outputEvent);	
	
}

/* private function addRoutingDecision(out:OutputEvent): void {
	var decision:String = getTime() +  " Routed " + out.outputCurrency + " " + formatCurrency(out.outputValue) + " to " + out.sentTo + ": " + out.ruleName;
	if(routingDecisions.length == 20) {
		routingDecisions.removeItemAt(0);
	}	
	routingDecisions.addItem(decision);
} */

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
	submissionEvent.currencyValues = new Array();
	submissionEvent.instructionCount = 0;
}

private function initialiseOutputValues(): void {	
	outputEvent = new OutputEvent();
	outputTotals = new ArrayCollection();
	routingDecisions = new ArrayCollection();
	
	outputEvent.outputCount = 0;
	outputEvent.currencyValues = new Array();
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
{submissionEvent.submissionCurrency} {submissionEvent.submissionValue}
Testing only - called by commented out buttons on the system status page


private function testSubmissionArrival(): void {

	var currencyData:Array = new Array();
	currencyData[0] = "EUR/15000"; 
	currencyData[1] = "JPY/2000.42";
	currencyData[2] = "USD/0.23";
		    
	var sEvent:SubmissionEvent = new SubmissionEvent();
	sEvent.submissionCount = 50;
	sEvent.submissionId = "ABC1234";
	sEvent.instructionCount = 214;
	sEvent.currencyValues = currencyData;
	
	var m:AsyncMessage = new AsyncMessage();
	m.body = sEvent;
	submissionEventHandler(m);
}

private function testOutput(): void {
	var currencyData:Array = new Array();
	currencyData[0] = "EUR/827.50"; 
	currencyData[1] = "JPY/1500.75";
	currencyData[2] = "USD/0.23";
	
	var outEvent:OutputEvent = new OutputEvent();
	outEvent.outputCount = 25;
	outEvent.currencyValues = currencyData;
	outEvent.mostRecentRoute = "BACS";
	
	var m:AsyncMessage = new AsyncMessage();
	m.body = outEvent;
	outputEventHandler(m);
}

*/