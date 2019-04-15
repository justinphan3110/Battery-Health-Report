# Battery-Health-Report
USING R AND JAVA to Get and Analyzed your laptop battery health report. Compare the current with design capacity

The battery capacity will decrease over time. This application will help you to track the current capacity compared to the design capacity

#### :arrow_forward: Click here to got to [Install](#Installing) and [Download](#Download) section

## :notebook: Demo

click Scan


![image](https://user-images.githubusercontent.com/44376091/52910196-02670300-3262-11e9-9962-af9cd8866360.png)


After scan successful you will received the information of your computer. Now you have to option, you can either choose the short result
to get the percentage of design capacity or a details one which will thoroughly analyze the battery health since you bought the laptop.

![image](https://user-images.githubusercontent.com/44376091/52910175-a13f2f80-3261-11e9-87dd-3ef2793c76ea.png)

Choose the Short Result Option:

![image](https://user-images.githubusercontent.com/44376091/52910200-14e13c80-3262-11e9-94e2-2d0d4085091f.png)

Choose the Detail Result Option:

![image](https://user-images.githubusercontent.com/44376091/52910204-2b879380-3262-11e9-8910-1bd53d615fdb.png)

two windows of a point plot and a csv file will pop up give the the data how your battery capacity decrease over time

![image](https://user-images.githubusercontent.com/44376091/52910219-5376f700-3262-11e9-82e5-3b55b2792802.png)



### Prerequisites

I will use cmd commnad to generate a battery-report.html and using Jsoup.jar to parsing this file and read it into a csv file. 

I will then use [Renjin](http://www.renjin.org/about.html) to intergrate [R](https://www.r-project.org/about.html) into [JVM](https://www.geeksforgeeks.org/jvm-works-jvm-architecture/) to clean and analyze this batter data base on each week since the laptop was bought.

![image](https://user-images.githubusercontent.com/44376091/52910233-a05acd80-3262-11e9-930b-1cc06f7f80ea.png)

The html file will give us the data as 

![image](https://user-images.githubusercontent.com/44376091/52910264-16f7cb00-3263-11e9-9dbd-827f9ea8183a.png)

I will then use BufferedReader to read this .html file 
![image](https://user-images.githubusercontent.com/44376091/52910284-4d354a80-3263-11e9-994f-40727c66c95d.png)

After the data has been read. CSVWriter from the [OpenCSV](https://sourceforge.net/projects/opencsv/) can be used to write these data into a .csv file
.csv file can then be used by R language to clean and analyze. 
I use [Renjin](http://www.renjin.org/about.html) instead of [JRI](https://www.rforge.net/JRI/index.html) because Renjin will run on JVM and not require and JRI 
require installed R on the computer to implement. Renjin can be set up in Java IDE as


![image](https://user-images.githubusercontent.com/44376091/52910328-0ac03d80-3264-11e9-8556-9a9399c5e134.png)

engine.eval command just simply pass the command into normal R Terminal to execute the data.frame


![image](https://user-images.githubusercontent.com/44376091/52910353-5a066e00-3264-11e9-82b7-774fd9ce1737.png)

The data after been clean and analyze in BatteryReport.csv, I use [JFreeChart](http://www.jfree.org/jfreechart/) to draw a scatter point plot analyze how the capacity of laptop
battery decrease in terms of weeks since it was first used. 


![image](https://user-images.githubusercontent.com/44376091/52910378-a782db00-3264-11e9-9952-7eac7e00a8b8.png)

Most of the API is build based on javaFX





# Installing
#### :fast_forward:Option 1: Download the [BatteryHealthReport folder](https://github.com/justinphan3110/Battery-Health-Report/tree/master/BatteryHealthReport) and unzip the withJava.zip if your computer have Java (64-bit) installed OR unzip the NoJava.zip if your computer does not have java installed 
###### (:heavy_exclamation_mark:note: keep the jar and the .exe file in the same folder).

![image](https://user-images.githubusercontent.com/44376091/53507163-5a351380-3a85-11e9-8fc9-1ac4db75581e.png)


##### Click on battery.exe to run

![image](https://user-images.githubusercontent.com/44376091/53507369-bf890480-3a85-11e9-8933-4923b5a21d29.png)

###### Note: If you run on the version of WithJava and the application stop working after you click scan, there may be a chance that your laptop is using Java 32-bit. Therefore, you can either change it back to 64 bits or use the NoJava.rar version. [Check JVM 64-bit or 32-bit](https://stackoverflow.com/questions/2062020/how-can-i-tell-if-im-running-in-64-bit-jvm-or-32-bit-jvm-from-within-a-program)



#### :fast_forward:Option 2: I have provide the [code](https://github.com/justinphan3110/Battery-Health-Report/blob/master/BatteryReport.java) and neccessary [lib folder](https://github.com/justinphan3110/Battery-Health-Report/tree/master/lib) to implement this in any IDE. You can make a java class name BatteryReport.java and a folder name lib in the project folder

In this [Lib folder](https://github.com/justinphan3110/Battery-Health-Report/tree/master/lib), copy all of the requirement .jar file to it. You can download them directly from this Repositories or I provide the link to download each of them below

![image](https://user-images.githubusercontent.com/44376091/52917775-5a7f2300-32bd-11e9-8e0c-09a1ebe35275.png)

And add it into Eclipse project library

![image](https://user-images.githubusercontent.com/44376091/52918141-d3807980-32c1-11e9-850f-3a22c52be257.png)

![image](https://user-images.githubusercontent.com/44376091/52918146-ee52ee00-32c1-11e9-9e61-456b435f805a.png)

# Download


#### :arrow_down: [Code](https://github.com/justinphan3110/Battery-Health-Report/blob/master/BatteryReport.java) and :arrow_down: [Lib Folder](https://github.com/justinphan3110/Battery-Health-Report/tree/master/lib)

##### Or you can download each of the dependencies straight from the web for updated

![image](https://user-images.githubusercontent.com/44376091/52910412-0f392600-3265-11e9-8e7b-a26dc0184ba1.png)

:one: [jsoup](https://jsoup.org/download): for the purpose of parsing and reading the html file.

:two: [opencsv](https://sourceforge.net/projects/opencsv/): can be used to write and read csv file 

:three: [Renjin](http://www.renjin.org/downloads.html): using JVM to intergrate Java and R for better data analysize (note: user does not need to have R installed on computer when using Renjin)

:four: [JRI](https://www.rforge.net/JRI/files/): user can used JRI for the familiar if Renjin is still new

:five: [JCommon](https://jar-download.com/artifacts/org.jfree/jcommon) and :six: [JFreeChart](http://www.jfree.org/jfreechart/download.html):  for the purpose of drawing the plot




 
## :link: Built With
* [Renjin](http://www.renjin.org/)
* [Renjin Document and Instruction](http://docs.renjin.org/en/latest/)
* [JSoup](https://jsoup.org/)
* [JFreeChart](http://www.jfree.org/jfreechart/)
* [Capturing results from Renjin](http://docs.renjin.org/en/latest/library/capture.html)
* [Insert command on cmd through Java](https://www.geeksforgeeks.org/java-program-open-command-prompt-insert-commands/)
* [Hash Map](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html)
* [JavaFX](https://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm)
* [JavaFX Dialog](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Dialog.html)

## Author
  [Long Phan](https://github.com/justinphan3110)

