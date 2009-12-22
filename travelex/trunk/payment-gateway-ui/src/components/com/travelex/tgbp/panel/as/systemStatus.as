// ActionScript file
import com.travelex.tgbp.message.SystemEvent;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.IMessage;
import flash.filters.ColorMatrixFilter;
	
[Bindable]
private var systemEvent:SystemEvent;	
	
private function messageHandler(message:IMessage):void{
	systemEvent = message.body as SystemEvent;	
}

private function glowImage() : void
{
    var filter : ColorMatrixFilter = new ColorMatrixFilter();
    var matrix:Array = new Array();
     matrix = matrix.concat([1, 0, 0, 0, 0]); // red
    matrix = matrix.concat([0, 1, 0, 0, 0]); // green
    matrix = matrix.concat([0, 0, 1, 0, 0]); // blue 
    matrix = matrix.concat([0, 0, 0, _alpha.value, 0]); //alpha 
    filter.matrix = matrix;
    submissionReceivedImage.filters = [ filter ];
}