Reference implementation pertaining to reactive transaction as per the 
Spring Tips (https://youtu.be/9henAE6VUbk) shared by Josh Long

#Steps for enabling replication set in mongodb locally
1. Open powershell window and go to MongoDB bin folder i.e. "C:\Users\Dhaval\scoop\apps\mongodb\4.0.10\bin"
2. Run below command
    .\mongod.exe --port 27017 --dbpath H:\Dhaval\Tech\mongodb\4.0.10\data-1 --replSet rs0
3. Open another powershell window and go to MongoDB bin folder i.e. "C:\Users\Dhaval\scoop\apps\mongodb\4.0.10\bin"
4. Run below command
    .\mongod.exe --port 27018 --dbpath H:\Dhaval\Tech\mongodb\4.0.10\data-2 --replSet rs0
5. Open another powershell window and go to MongoDB bin folder i.e. "C:\Users\Dhaval\scoop\apps\mongodb\4.0.10\bin"
6. Open mongo shell in newly opened window by executing below command
    .\mongo.exe
7. In mongo shell do following
    7.1 rs.initiate() - with this command mongo shell prompt will change to rs0:PRIMARY>
    7.2 On rs0:PRIMARY prompt execute this command - rs.add("localhost:27018");
    Note Response to above will be { “ok” : 1 } meaning addition of mongod instance to the replica set is successful.
    7.3 Check replica status by executing command on mongoshell - rs.status()