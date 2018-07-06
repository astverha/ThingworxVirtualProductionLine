
// GET THE CURRENT STOCK
var values = Things["ERPStock"].CreateValues();
values.Article = "B"; //STRING [Primary Key]
var params = {
	values: values /* INFOTABLE*/
};
// result: INFOTABLE
var result = Things["ERPStock"].GetDataTableEntry(params);

// UPDATE THE STOCK
var values = Things["ERPStock"].CreateValues();
values.Article = "B"; //STRING [Primary Key]
values.Stock = result.Stock + eventData.newValue.value - eventData.oldValue.value; //INTEGER
var params = {
	sourceType: undefined /* STRING */,
	values: values /* INFOTABLE*/,
	location: undefined /* LOCATION */,
	source: undefined /* STRING */,
	tags: undefined /* TAGS */
};
Things["ERPStock"].UpdateDataTableEntry(params);

