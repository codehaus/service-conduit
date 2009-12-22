// ActionScript file
import mx.controls.Alert;

[Bindable]
private var configData:XML;

private function loadConfig():void {
	var configLoader:URLLoader = new URLLoader();
	var req:URLRequest = new URLRequest("config/config.xml");	
	configLoader.dataFormat=URLLoaderDataFormat.TEXT;
	configLoader.addEventListener(Event.COMPLETE, loaderCompleteHandler);
	configLoader.load(req);	
}

private function loaderCompleteHandler(event:Event):void {
	configData = new XML(event.currentTarget.data)	
}

	


