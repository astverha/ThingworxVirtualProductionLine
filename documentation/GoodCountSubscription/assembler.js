if(Things[me.NextAsset].BufferQuantity < 10000){
	delta = eventData.newValue.value - eventData.oldValue.value;
    Things[me.NextAsset].BufferQuantity += delta
    
    //GETTING AND UPDATING STOCK VALUES FOR A, B and C
    
    //Getting A
    /*var valuesA = Things["ERPStock"].CreateValues();
    valuesA.Article = "A"; //STRING [Primary Key]
    var params = {
        values: valuesA 
    };
    var oldStockA = Things["ERPStock"].GetDataTableEntry(params);
    
    //Updating A
    var valuesA = Things["ERPStock"].CreateValues();
    valuesA.Article = "A"; //STRING [Primary Key]
    valuesA.Stock = oldStockA.Stock - 2*delta; //INTEGER
    var params = {
        sourceType: undefined ,
        values: valuesA ,
        location: undefined ,
        source: undefined ,
        tags: undefined 
    };
    Things["ERPStock"].UpdateDataTableEntry(params);*/

    //Getting B
    var valuesB = Things["ERPStock"].CreateValues();
    valuesA.Article = "B"; //STRING [Primary Key]
    var params = {
        values: valuesB /* INFOTABLE*/
    };
    var oldStockB = Things["ERPStock"].GetDataTableEntry(params);
    
    //Updating B
    var valuesB = Things["ERPStock"].CreateValues();
    valuesB.Article = "B"; //STRING [Primary Key]
    valuesB.Stock = oldStockB.Stock - 2*delta; //INTEGER
    var params = {
        sourceType: undefined /* STRING */,
        values: valuesB /* INFOTABLE*/,
        location: undefined /* LOCATION */,
        source: undefined /* STRING */,
        tags: undefined /* TAGS */
    };
    Things["ERPStock"].UpdateDataTableEntry(params);
	
	//Getting C
    var valuesC = Things["ERPStock"].CreateValues();
    valuesC.Article = "C"; //STRING [Primary Key]
    var params = {
        values: valuesC /* INFOTABLE*/
    };
    var oldStockC = Things["ERPStock"].GetDataTableEntry(params);
    
    //Updating C
    var valuesC = Things["ERPStock"].CreateValues();
    valuesC.Article = "C"; //STRING [Primary Key]
    valuesC.Stock = oldStockC.Stock - delta; //INTEGER
    var params = {
        sourceType: undefined /* STRING */,
        values: valuesC /* INFOTABLE*/,
        location: undefined /* LOCATION */,
        source: undefined /* STRING */,
        tags: undefined /* TAGS */
    };
    Things["ERPStock"].UpdateDataTableEntry(params);

}