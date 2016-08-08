# MarkLogic Spring Batch Base Job

The base job is intended to be a starter template for writing any MarkLogic Spring Batch job.  Just copy and paste this directory into your workspace and modify the classes to your specific batch processing use case.  

 - build.gradle - used to import the MSB jars, include the ml-gradle, java and application gradle plugins
 - gradle.properties - Used to name your artifact id and specify your MarkLogic host settings
 - src/main/java/example/YourJobConfig.java - Sample Job Configuration
 - src/test/java/example/YourJobTest.java - Used to test the Job configuration YourJobConfig

Execute your Java unit tests
    
    gradle test

The following Gradle command will create an executable under install/distributions that can be used to initialize the batch processing program 
   
    gradle distZip