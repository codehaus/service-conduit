// ActionScript file
import mx.collections.ArrayCollection;
import mx.containers.Panel;

[Bindable]
private var dataGridProvider:ArrayCollection = new ArrayCollection();

private var _panelZero:Panel;
private var _panelTwo:Panel;
private var _panelOne:Panel;

public function set panelZero(panel:Panel): void {
	_panelZero = panel;
}

public function set panelOne(panel:Panel): void {
	_panelOne = panel;
}

public function set panelTwo(panel:Panel): void {
	_panelTwo = panel;
}

private function handleMainMenuClick(event:MouseEvent):void {
	dispatchEvent(event);
	_panelZero.visible = (event.currentTarget.selectedIndex == 0);
	_panelOne.visible = (event.currentTarget.selectedIndex == 1);
	_panelTwo.visible = (event.currentTarget.selectedIndex == 2);					
}