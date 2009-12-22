package com.travelex.tgbp.message
{
	[RemoteClass(alias="com.travelex.tgbp.schema.model.SchemaEventDescriptor")]
    [Bindable]
	public class SystemEvent
	{
	    public var messageId:String;
	    public var someProperty:String;
	    public var anotherProperty:String;	    
	}
}