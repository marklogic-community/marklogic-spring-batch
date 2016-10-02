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
    
You can also test the execution of your job.  First install your job with the following gradle call.    

    gradle installDist

Next call the generated start script and pass the necessary parameters.  

    $PROJECT_HOME\examples\base\build\install\baseJob\bin\base.bat --config example.YourJobConfig --host oscar --port 8200 --username admin --password admin --output_collections monster123 