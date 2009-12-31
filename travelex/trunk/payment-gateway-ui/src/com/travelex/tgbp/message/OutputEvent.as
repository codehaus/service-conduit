package com.travelex.tgbp.message
{
	[RemoteClass(alias="com.travelex.tgbp.output.service.event.OutputEvent")]
    [Bindable]
	public class OutputEvent
	{
		//Are these required??????
	    public var outputCount:Number;
	    public var currencyValues:Array;	      
	    public var mostRecentRoute:String;
	    public var mostRecentFileName:String;
	    public var mostRecentFileContent:String;
	    public var mostRecentSubId:String;
	    //public var sentTo:String;
	    //public var ruleName:String;
	        
	}
}