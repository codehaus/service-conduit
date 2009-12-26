// ActionScript file
import mx.controls.Alert;

[Bindable]
protected var configData:XML;

public function set config(conf:XML): void {
	configData = conf;
}

protected function getConfigParam(p:String): String {
	return configData[p];
}
