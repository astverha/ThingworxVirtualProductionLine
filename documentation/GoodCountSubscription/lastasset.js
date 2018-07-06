// GET CURRENT STOCK OF FINISHED PRODUCTS
var values = Things["ERPStock"].CreateValues();
values.Article = "FinishedProduct"; //STRING [Primary Key]
var params = {
	values: values /* INFOTABLE*/
};
var oldStock = Things["ERPStock"].GetDataTableEntry(params).Stock;

//DECREASE BUFFERQUANTITY
me.BufferQuantity = me.BufferQuantity - (eventData.newValue.value - eventData.oldValue.value);

// CALCULATE newStock
var newStock = oldStock + eventData.newValue.value - eventData.oldValue.value;
// UPDATE TO newStock
var values = Things["ERPStock"].CreateValues();
values.Article = "FinishedProduct"; //STRING [Primary Key]
values.Stock = newStock; //INTEGER
var params = {
	sourceType: undefined /* STRING */,
	values: values /* INFOTABLE*/,
	location: undefined /* LOCATION */,
	source: undefined /* STRING */,
	tags: undefined /* TAGS */
};
Things["ERPStock"].UpdateDataTableEntry(params);


