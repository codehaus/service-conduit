// ActionScript file
import mx.controls.Alert;

private var configData:XML;

public function set config(conf:XML): void {
	configData = conf;
}

protected function getConfigParam(p:String): void {
	Alert.show(configData[p]);	
}

