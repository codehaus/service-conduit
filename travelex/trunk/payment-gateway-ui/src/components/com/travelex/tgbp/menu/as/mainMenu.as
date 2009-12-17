// ActionScript file
import mx.collections.ArrayCollection;
import mx.containers.Panel;

[Bindable]
private var dataGridProvider:ArrayCollection = new ArrayCollection();

private var p0:Panel;
private var p2:Panel;
private var p1:Panel;

public function set panelZero(panel:Panel): void {
	p0 = panel;
}

public function set panelOne(panel:Panel): void {
	p1 = panel;
}

public function set panelTwo(panel:Panel): void {
	p2 = panel;
}

private function handleMainMenuClick(event:MouseEvent):void {
	dispatchEvent(event);
	p0.visible = (event.currentTarget.selectedIndex == 0);
	p1.visible = (event.currentTarget.selectedIndex == 1);
	p2.visible = (event.currentTarget.selectedIndex == 2);					
}