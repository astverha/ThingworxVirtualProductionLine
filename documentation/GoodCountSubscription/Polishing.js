//CALCULATE THE DIFFERENCE IN GOODCOUNT
var delta = eventData.newValue.value - eventData.oldValue.value;

//POLISHING NEEDS PART C AND OWN BUFFER, already checked in checkifcanproduce
//POLISHING OUTPUTS FINSHED PRODUCTS (MACHINE)

//time to produce
//get stock C
var params = {
    articleDescription: "C" /* STRING */
};
var stockC = Things["ERPSimulationDB.DataSource"].getStockForArticle(params).ART_STOCK;

//decrease part C stock
var params = {
    articleDescription: "C" /* STRING */,
    newStock: stockC - delta /* INTEGER */
};
var rowsAffected = Things["ERPSimulationDB.DataSource"].updateArticlestock(params);

//decrease own buffer
me.BufferQuantity -= delta;

//get stock MACHINE
var params = {
    articleDescription: "MACHINE" /* STRING */
};
var stockMachine = Things["ERPSimulationDB.DataSource"].getStockForArticle(params).ART_STOCK;

//increase finished products
var params = {
	articleDescription: "MACHINE" /* STRING */,
	newStock: stockMachine+delta /* INTEGER */
};
var affectedRows = Things["ERPSimulationDB.DataSource"].updateArticlestock(params);

