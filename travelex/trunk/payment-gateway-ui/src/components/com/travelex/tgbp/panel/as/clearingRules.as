// ActionScript file
import mx.collections.XMLListCollection;
import mx.controls.Alert;
import mx.rpc.events.ResultEvent;

[Bindable]
private var serverUrl:String;

[Bindable]
private var xlc:XMLListCollection = new XMLListCollection();

private function retrieveConfigRules():void {
	startProgressBar('Loading clearing rules');
	configRuleRetrievalService.send()	
}

private function onConfigRuleRetrievalServiceResponse(event:ResultEvent):void {
	stopProgressBar(event);
	var doc:XMLDocument = event.result.parentNode;
	var tmp:XML = new XML(doc.toString());
	//mx.controls.Alert.show("XML = " + tmp.toString());		
	xlc.source = tmp.rule;				
}

private function onConfigRuleRetrievalFailure():void {
	mx.controls.Alert.show("Failure in config rule retrieval service");		
}

public function onEditRuleClick(ruleId:Object, amount:Object, charge:Object):void {
	startProgressBar('Updating clearing rule');
    //mx.controls.Alert.show(ruleId.toString() + " " + amount.toString() + " " + charge.toString());
    configRuleUpdateService.send({'id':ruleId, 'charge':charge, 'amount':amount})
}

private function onConfigRuleUpdateServiceResponse(event:ResultEvent):void {
	stopProgressBar(event);
    mx.controls.Alert.show("Update complete, status code " + event.statusCode);
}

private function onConfigRuleUpdateFailure():void {
	mx.controls.Alert.show("Failure in config rule update service");		
}

private function startProgressBar(text:String):void
{
	progress.label=text;;
	progress.visible=true;
}

private function stopProgressBar(event:Event):void
{
   var progressTimer:Timer = new Timer(500);
   progressTimer.addEventListener("timer", timerHandler);
   progressTimer.start();       
}

public function timerHandler(event:TimerEvent):void {
	progress.label="Done";
	progress.visible=false;
}
