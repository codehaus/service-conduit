package com.travelex.tgbp.message
{
	import flash.events.Event;
	import flash.filters.ColorMatrixFilter;
	import flash.utils.Timer;
	
	import mx.controls.Image;
	
	public class GlowHandler
	{
		private var base:Number = 0.2;	
		private var step:Number = 0.1;
		
		private var value:Number = base;
		private var valueAdjust:Number = step;
		private var image:Image;
		
		public function GlowHandler(glowImage:Image): void
		{
			image = glowImage;
		}
		
		public function glowImage():void
		{
		   var glowTimer:Timer = new Timer(50, 18);
		   glowTimer.addEventListener("timer", timerHandler);
		   glowTimer.start();       
		}
		
		private function timerHandler(event:Event):void {
			if(value >= 1) {
				valueAdjust = new Number("-" + step);
			} 
			
			value = value + valueAdjust
			if(value < base) {
				return;		
			}
			
			GlowHandler.filterImage(image, value);
		}
		
		public static function filterImage(glowImage:Image, alpha:Number) : void
		{
			var filter : ColorMatrixFilter = new ColorMatrixFilter();
		    var matrix:Array = new Array();
		    matrix = matrix.concat([1, 0, 0, 0, 0]); 		//Red.
		    matrix = matrix.concat([0, 1, 0, 0, 0]); 		//Green.
		    matrix = matrix.concat([0, 0, 1, 0, 0]); 		//Blue.
		    matrix = matrix.concat([0, 0, 0, alpha, 0]);	//Alpha. 
		    filter.matrix = matrix;
		    glowImage.filters = [ filter ];
		}		

	}
}