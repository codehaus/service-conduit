// ActionScript file
import mx.collections.ArrayCollection;
import mx.controls.DataGrid;
import mx.controls.Tree;
import mx.controls.listClasses.ListBase;
import mx.core.DragSource;
import mx.core.UIComponent;
import mx.events.DragEvent;
import mx.managers.DragManager;
import mx.rpc.events.ResultEvent;

[Bindable]
private var dataGridProvider:ArrayCollection = new ArrayCollection();

[Bindable]
private var schemaDescription:String;

private function onDragEnter( event:DragEvent ) : void
{            
    if( event.dragInitiator is Tree ) {
        var ds:DragSource = event.dragSource;
        if( !ds.hasFormat("treeItems") ) return;	//Always "items" or "treeItems"

        var items:Array = ds.dataForFormat("treeItems") as Array;
        for(var i:Number=0; i < items.length; i++) {

            var item:XML = XML(items[i]);
            var javaType:String = item.@javaType;
            if( javaType == "" ) return; //Only allow leaf node drops

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
	var doc:XMLDocument = event.result.parentNode;				
	schemaDescription = doc.toString();
}
