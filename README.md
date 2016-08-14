To start project you need:
    java 1.8
    scala 1.12
    sbt


To run tests use command 

    sbt test

To build and packag project from sources use command

    sbt assembly

SBT will build thea an app.jar file and put it into folder '/target'
also it will copy there default test file 'instructions.txt'
To run program enter folder '/target' and use command

    java -jar app.jar instructions.txt

    
Assumptions used in task:

I decided to use AKKA for distributed system nodes.
Actors communicate via standard sending of messages, 
if needed it is possible to implement another communication mechanism via REST, RabbitMQ or other.


