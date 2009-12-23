package com.travelex.tgbp.message
{
	[RemoteClass(alias="com.travelex.tgbp.processor.event.SubmissionEvent")]
    [Bindable]
	public class SubmissionEvent
	{
	    public var submissionCount:Number;
	    public var submissionId:String;
	    public var submissionCurrency:String;
	    public var submissionValue:String;
	}
}