// ActionScript file
import com.travelex.tgbp.message.GlowHandler;
import com.travelex.tgbp.message.SystemEvent;

import mx.controls.Image;
import mx.messaging.messages.IMessage;
	
private static var BASE:Number = 0.2;
private var value:Number = BASE;	
private var valueAdjust:Number = 0.1;
	
[Bindable]
private var systemEvent:SystemEvent;	
	
private function messageHandler(message:IMessage):void{
	systemEvent = message.body as SystemEvent;	
}

public function init(): void {
	consumer.subscribe();
	GlowHandler.filterImage(submissionReceivedImage, value)
	GlowHandler.filterImage(outputSentImage, 0.2)
}

private function glowImage(image:Image):void
{
   var glow:GlowHandler = new GlowHandler(image); 
   glow.glowImage();      
}