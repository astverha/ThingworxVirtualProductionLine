//NEEDS 1 IRON INGOT AND 1 COPPER INGOT

//GET IRON STOCK
var params = {
	articleDescription: "IRON INGOTS" /* STRING */
};
var ironStock = Things["ERPSimulationDB.DataSource"].getStockForArticle(params);

//GET COPPER STOCK
var params = {
	articleDescription: "COPPER INGOTS" /* STRING */
};
var copperStock = Things["ERPSimulationDB.DataSource"].getStockForArticle(params);

//CHECK IF ENOUGH STOCK
if(ironStock.ART_STOCK >= amount && copperStock.ART_STOCK >= amount){
	result = true;
} else {
    result = false;
}

