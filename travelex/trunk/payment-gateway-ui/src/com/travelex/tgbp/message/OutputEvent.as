package com.travelex.tgbp.message
{
	[RemoteClass(alias="com.travelex.tgbp.output.service.event.OutputEvent")]
    [Bindable]
	public class OutputEvent
	{
		//Are these required??????
	    public var outputCount:Number;
	    public var outputCurrency:String;	
	    public var outputValue:String;	       
	    public var sentTo:String;
	    public var ruleName:String;
	        
	}
}