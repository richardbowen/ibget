# ibget
Downloads flex reports from Interactive Brokers. 

I use this with a cron job to download positions at the end of each day.

# usage
Run with command line like below:

    java -jar ibget-1.1-jar-with-dependencies.jar 111111111112222222333333  778899 /data/ib/

Where 
*   arg1 is the Flex Code you get from IB
*   arg2 is the query code
*   arg3 is the location to store the csv file output
    
# build
To create Jar use Maven:

    mvn assembly:single

    
