//NEEDS PART C AND OWN BUFFER

//get stock for C
var params = {
	articleDescription: "C" /* STRING */
};
var stockC = Things["ERPSimulationDB.DataSource"].getStockForArticle(params).ART_STOCK;

//check if enough stock C
if(stockC >= amount) {
    //check if enough buffer
    if(me.BufferQuantity >= amount) {
		result = true;
    } else {
		result = false;
    }
} else {
	result = false;
}
