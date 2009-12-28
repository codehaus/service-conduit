// ActionScript file
import mx.collections.XMLListCollection;
import mx.controls.Alert;
import mx.rpc.events.ResultEvent;

[Bindable]
private var serverUrl:String;

[Bindable]
private var xlc:XMLListCollection = new XMLListCollection();

private function retrieveDynamicRules():void {
	startProgressBar('Loading dynamic rules');
    dynamicRuleRetrievalService.send()	
}

private function onDynamicRuleRetrievalServiceResponse(event:ResultEvent):void {
	stopProgressBar(event);
	var doc:XMLDocument = event.result.parentNode;
	var tmp:XML = new XML(doc.toString());
	//mx.controls.Alert.show("XML = " + tmp.toString());		
	xlc.source = tmp.rule;				
}

private function onDynamicRuleRetrievalFailure():void {
	stopProgressBar(null);
	mx.controls.Alert.show("Failure in dynamic rule retrieval service");		
}

public function onDeleteRuleClick(ruleId:Object):void {
	startProgressBar('Deleting dynamic rule');
    //mx.controls.Alert.show(ruleId.toString() + " " + amount.toString() + " " + charge.toString());
    dynamicRuleDeleteService.send({'id':ruleId})
}

private function onDynamicRuleDeleteServiceResponse(event:ResultEvent):void {
	stopProgressBar(event);
    mx.controls.Alert.show("Delete complete, status code " + event.statusCode);
    // Updates UI components with latest server state
    dynamicRuleRetrievalService.send();
}

private function onDynamicRuleDeleteFailure():void {
	stopProgressBar(null);
	mx.controls.Alert.show("Failure in dynamic rule delete service");		
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
