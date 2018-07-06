## Creating the XML file
The first step is creating the XML configuration file. For an example, check out the documentation folder.

## Creating your devices in the Thingworx Manufacturer Apps and ThingWorx Composer
### Thingworx Manufacturer Apps
Create your Line and your different Assets using Thingworx Manufacturer Apps. Provide every asset of it's properties. You only have to provide the properties you added. In the XML file in the documentation folder this is limited to the Temperature property. 

### Run the Java project to connect the virtual things
In the Other Resources folder of the Java project, provide the XML configuration file. Make sure the name of the file corresponds with the name supplied to the ConfigurationAgent in the RunApplication.java class.
Run the project, keep it running for the next steps.

### Thingworx Composer - Binding the properties
Using Thingworx Composer, make sure all assets of your line implement the SimulationThingShape thing shape. Then bind all the properties to their correct remote counterparts. Pay attention you bind GoodCount (remote) to GoodCount (Thingworx) and BadCount to BadCount, mind the casing!

### Thingworx Composer - Create an ERP to manage your stock
Using Thingworx Composer, create an ERP thing implementing the LineV2ERP, which will be referenced to as "ERPStock" from now on. Initialize the stock. Example: if you have an assembler that makes a product out of part A and B, create a data entry for A: Article = "A", Stock = initial stock value, and for B: Article = "B", Stock = initial stock value.

### Thingworx Composer - Override the checkIfCanProduce service
Using Thingworx Composer, override the "checkIfCanProduce" service of the SimulationThingShape thing shape for every asset. In this service, use Javascript to code the logic of producing. Examples can be found in the documentation > checkIfCanProduce folder.

### Thingworx Composer - Create the subscription on GoodCount DataChange
Using Thingworx Composer, create a subscription on the DataChange of the GoodCount property. Use Javascript to code the logic of updating the stock and the BufferQuantity of the NextAsset. Examples can be found in the documentation > GoodCountSubscription folder.

### Stopping the project
You can now stop the running Java project.

### Thingworx Manufacturer Apps - Defining Statuses, Performance Metrics and Alerts
Using Thingworx Manufacturer Apps, define the status for the line and every asset. The easiest way is using pushedStatus. Examples: 
RUNNING: `"Asset__AssetName_:pushedStatus" = 2`
PLANNED_DOWNTIME: `"Asset__AssetName_:pushedStatus" = 3`

Define the performance metrics as follows:
Good Count: `"Asset__AssetName_:GoodCount"`
Total Count: `"Asset__AssetName_:BadCount"+"Asset__AssetName_:GoodCount"`
Ideal Run Rate: `"Asset__AssetName_:idealRunRate"`

## Restarting the simulation
You are now ready to use the simulation. Restart the Java project. You should now see metrics and alerts being triggered in the different Thingworx Manufacturer Apps consoles, as well as a simple GUI to update the production rate of every asset and check some parameters.
