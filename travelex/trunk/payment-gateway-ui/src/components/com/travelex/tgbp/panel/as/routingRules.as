// ActionScript file
import flash.events.Event;
import flash.net.URLLoader;
import flash.net.URLRequest;
import flash.net.URLRequestMethod;
import flash.net.URLVariables;

import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.controls.DataGrid;
import mx.controls.Tree;
import mx.controls.listClasses.ListBase;
import mx.core.DragSource;
import mx.core.UIComponent;
import mx.events.DragEvent;
import mx.managers.DragManager;
import mx.rpc.events.ResultEvent;
import mx.utils.StringUtil;

[Bindable]
private var dataGridProvider:ArrayCollection = new ArrayCollection();

[Bindable]
private var schemaDescription:String;

[Bindable]
private var serverUrl:String;

private function onDragEnter( event:DragEvent ) : void
{            
    if( event.dragInitiator is Tree ) {
        var ds:DragSource = event.dragSource;
        if( !ds.hasFormat("treeItems") ) return;	//Always "items" or "treeItems"

        var items:Array = ds.dataForFormat("treeItems") as Array;
        for(var i:Number=0; i < items.length; i++) {

            var item:XML = XML(items[i]);
            var javaType:String = item.@javaType;
            if( javaType == "" ) return; //Only allow leaf node (those with a javaType attribute) drops

        }
    } 
  
	DragManager.acceptDragDrop(UIComponent(event.currentTarget)); 
}        

private function onDragOver( event:DragEvent ) : void

{
    if( event.dragInitiator is Tree ) {
        DragManager.showFeedback(DragManager.COPY);
    } else {
		DragManager.showFeedback(DragManager.NONE);
    }
}        

private function onDragExit( event:DragEvent ) : void

{
    var dropTarget:ListBase=ListBase(event.currentTarget);   
	dropTarget.hideDropFeedback(event);
}

private function onGridDragDrop( event:DragEvent ) : void

{
    var ds:DragSource = event.dragSource;
    var dropTarget:DataGrid = DataGrid(event.currentTarget);
    var arr:Array;
    if( ds.hasFormat("treeItems") ) {
        arr = ds.dataForFormat("treeItems") as Array;
    }
    if(arr != null) {
	    for(var i:Number=0; i < arr.length; i++) {
	    	
	    	var fullPath:String = getFullPath(arr[i], "");
	    	
	        var node:XML = XML(arr[i]);
	        var item:Object = new Object();
	        item.fileData = node.@label;
	        item.operator  = "Equals";
	        item.expectedData = "";
	        item.fullPath=fullPath;
	        item.javaType = node.@javaType
	        	        
	        dataGridProvider.addItem(item);
	    }    	
    }

    onDragExit(event); 
}

private function getFullPath(node:XML, fullPath:String):String {
	if(node == null) {
		return fullPath;
	}
	var parent:XML = schemaTree.getParentItem(node);
	return getFullPath(parent, fullPath) + fullPath + "/" + node.@label;
}

private function onTreeDragComplete(event:DragEvent):void {
    event.preventDefault();
}        

private function onSchemaDescriptorService(event:ResultEvent):void {
	stopProgressBar(event);
	var doc:XMLDocument = event.result.parentNode;				
	schemaDescription = doc.toString();
}

private function createRoutingRule():void {		
	var params:URLVariables = getRuleUrlParams();
 	if(params != null) {
 		startProgressBar("Creating rule");
		var createRuleUrl:String = getConfigParam("serverUrl") + "/rules/create.htm";
		
		var req:URLRequest = new URLRequest(createRuleUrl);
		req.method=URLRequestMethod.GET;
		req.data = params;
		
		var ruleLoader:URLLoader = new URLLoader();			
		ruleLoader.addEventListener(Event.COMPLETE, stopProgressBar); 				
		ruleLoader.load(req);
 	}
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
	
private function getRuleUrlParams():URLVariables {
 	if(isEmpty(ruleName.text) || isEmpty(routeTo.text) || 
 		(dataGridProvider == null || dataGridProvider.length < 1)) {
 		Alert.show("Invalid data - can't create rule"); 		
		return null;
	}
	
	var ruleData:String = "<ruleData>";
	for each(var ruleEntry:Object in dataGridProvider) {
		ruleData += "<rule>";
		ruleData += "<path>" + ruleEntry.fullPath + "</path>";
		ruleData += "<operator>" + ruleEntry.operator + "</operator>";
		ruleData += "<type>" + ruleEntry.javaType + "</type>";
		ruleData += "<data>" + ruleEntry.expectedData + "</data>";
		ruleData += "</rule>";
	} 
	ruleData += "</ruleData>";
	
	var params:URLVariables = new URLVariables();
	params.rulename = ruleName.text;
	params.clearingmechanism = routeTo.text;
	params.appliesto = schemaSelection.selectedItem;
	params.ruledata = ruleData;	
	
	return params;		
}	
	
private function isEmpty(value:String):Boolean {
	return value == null || StringUtil.isWhitespace(value);
} 

private function clearRuleData(): void {
	dataGridProvider.removeAll();
	routeTo.text="";
	ruleName.text="";		
}

private function populateTree():void {
	startProgressBar('Loading schema');
	schemaDescriptorService.send({'schema':schemaSelection.selectedLabel})
	schemaDescription = null;
}