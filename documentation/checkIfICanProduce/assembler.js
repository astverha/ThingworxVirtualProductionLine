//GETTING STOCKS OF A, B AND C (Assembler uses 2xA, 2xB and 1xC)
//A
var values = Things["ERPStock"].CreateValues();
values.Article = "A"; 
var params = {
	values: values 
};
var resultA = Things["ERPStock"].GetDataTableEntry(params);

//B
var values = Things["ERPStock"].CreateValues();
values.Article = "B"; 
var params = {
	values: values 
};
var resultB = Things["ERPStock"].GetDataTableEntry(params);

//C
var values = Things["ERPStock"].CreateValues();
values.Article = "C"; 
var params = {
	values: values 
};
var resultC = Things["ERPStock"].GetDataTableEntry(params);

if( (resultA.Stock - 2*amount) > 0 && (resultB.Stock - 2*amount) > 0 && (resultC.Stock - amount) > 0 ){
	result = true;
} else {
    result = false;
}