package com.travelex.tgbp.message
{
	[RemoteClass(alias="com.travelex.tgbp.processor.event.SubmissionEvent")]
    [Bindable]
	public class SubmissionEvent
	{
	    public var submissionCount:Number;	    
	    public var currencyValues:Array;
	    public var submissionId:String;
	    public var instructionCount:Number;
	    public var fileName:String;
	    public var fileContent:String;	    
	}
}